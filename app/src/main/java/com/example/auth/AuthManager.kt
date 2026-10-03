package com.example.auth

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UserSession(
    val id: String,
    val username: String,
    val displayName: String,
    val role: String, // "Boarding Master", "Master Admin", "Housemaster"
    val schoolName: String = "Kabarnet Senior School",
    val schoolMotto: String = "Knowledge is Power",
    val initials: String = "JM"
)

data class LockoutState(
    val isLocked: Boolean = false,
    val remainingSeconds: Long = 0,
    val failedAttempts: Int = 0
)

class AuthManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("axis6_auth_prefs", Context.MODE_PRIVATE)

    private val _currentUser = MutableStateFlow<UserSession?>(null)
    val currentUser: StateFlow<UserSession?> = _currentUser.asStateFlow()

    private val _lockout = MutableStateFlow(LockoutState())
    val lockout: StateFlow<LockoutState> = _lockout.asStateFlow()

    init {
        checkSavedSession()
        updateLockoutStatus()
    }

    private fun checkSavedSession() {
        val savedUser = prefs.getString("saved_username", null)
        val savedRole = prefs.getString("saved_role", null)
        val savedName = prefs.getString("saved_display_name", null)
        val remember = prefs.getBoolean("remember_me", false)

        if (remember && savedUser != null && savedRole != null && savedName != null) {
            val initials = savedName.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("").ifEmpty { "BM" }
            _currentUser.value = UserSession(
                id = savedUser,
                username = savedUser,
                displayName = savedName,
                role = savedRole,
                initials = initials
            )
        }
    }

    fun updateLockoutStatus() {
        val lockUntil = prefs.getLong("locked_until_ms", 0L)
        val now = System.currentTimeMillis()
        if (lockUntil > now) {
            val remainingSec = (lockUntil - now) / 1000
            val attempts = prefs.getInt("failed_attempts", 3)
            _lockout.value = LockoutState(isLocked = true, remainingSeconds = remainingSec, failedAttempts = attempts)
        } else {
            if (lockUntil != 0L) {
                prefs.edit().remove("locked_until_ms").putInt("failed_attempts", 0).apply()
            }
            val attempts = prefs.getInt("failed_attempts", 0)
            _lockout.value = LockoutState(isLocked = false, remainingSeconds = 0, failedAttempts = attempts)
        }
    }

    fun login(username: String, pass: String, rememberMe: Boolean): Result<UserSession> {
        updateLockoutStatus()
        if (_lockout.value.isLocked) {
            val minutes = _lockout.value.remainingSeconds / 60
            val seconds = _lockout.value.remainingSeconds % 60
            return Result.failure(Exception("Account temporarily locked. Try again in %02d:%02d".format(minutes, seconds)))
        }

        val u = username.trim()
        val p = pass.trim()

        val isBM = (u == "kabarnet_bm" && p == "demo1234")
        val isMaster = (u == "mr_kabutiei" && p == "super admin")

        if (isBM || isMaster) {
            // Successful login
            prefs.edit().putInt("failed_attempts", 0).remove("locked_until_ms").apply()
            _lockout.value = LockoutState(isLocked = false, remainingSeconds = 0, failedAttempts = 0)

            val session = if (isMaster) {
                UserSession(
                    id = "admin_01",
                    username = "mr_kabutiei",
                    displayName = "Mr. Kabutiei",
                    role = "Master Admin",
                    initials = "MK"
                )
            } else {
                UserSession(
                    id = "bm_01",
                    username = "kabarnet_bm",
                    displayName = "John Mwangi",
                    role = "Boarding Master",
                    initials = "JM"
                )
            }

            if (rememberMe) {
                prefs.edit()
                    .putBoolean("remember_me", true)
                    .putString("saved_username", session.username)
                    .putString("saved_role", session.role)
                    .putString("saved_display_name", session.displayName)
                    .apply()
            } else {
                prefs.edit().remove("remember_me").remove("saved_username").apply()
            }

            _currentUser.value = session
            return Result.success(session)
        } else {
            // Failed attempt
            val currentAttempts = prefs.getInt("failed_attempts", 0) + 1
            if (currentAttempts >= 3) {
                val lockDurationMs = 15 * 60 * 1000L // 15 minutes lockout
                val lockUntil = System.currentTimeMillis() + lockDurationMs
                prefs.edit()
                    .putInt("failed_attempts", currentAttempts)
                    .putLong("locked_until_ms", lockUntil)
                    .apply()
                _lockout.value = LockoutState(isLocked = true, remainingSeconds = 15 * 60, failedAttempts = currentAttempts)
                return Result.failure(Exception("Too many failed attempts. Account locked for 15:00."))
            } else {
                prefs.edit().putInt("failed_attempts", currentAttempts).apply()
                _lockout.value = LockoutState(isLocked = false, remainingSeconds = 0, failedAttempts = currentAttempts)
                val remaining = 3 - currentAttempts
                return Result.failure(Exception("Incorrect username or password. $remaining attempt(s) remaining."))
            }
        }
    }

    fun quickDemoLogin(asMaster: Boolean) {
        val session = if (asMaster) {
            UserSession(
                id = "admin_01",
                username = "mr_kabutiei",
                displayName = "Mr. Kabutiei",
                role = "Master Admin",
                initials = "MK"
            )
        } else {
            UserSession(
                id = "bm_01",
                username = "kabarnet_bm",
                displayName = "John Mwangi",
                role = "Boarding Master",
                initials = "JM"
            )
        }
        _currentUser.value = session
    }

    fun switchRole(newRole: String) {
        val current = _currentUser.value ?: return
        _currentUser.value = current.copy(role = newRole)
    }

    fun logout() {
        prefs.edit().remove("saved_username").remove("saved_role").remove("remember_me").apply()
        _currentUser.value = null
    }

    fun resetLockout() {
        prefs.edit().putInt("failed_attempts", 0).remove("locked_until_ms").apply()
        _lockout.value = LockoutState(isLocked = false, remainingSeconds = 0, failedAttempts = 0)
    }
}
