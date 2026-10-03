package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.auth.LockoutState
import com.example.ui.theme.AxisDanger
import com.example.ui.theme.AxisWarning

@Composable
fun LoginScreen(
    lockoutState: LockoutState,
    onLoginSubmit: (String, String, Boolean, (String) -> Unit) -> Unit,
    onQuickDemoLogin: (Boolean) -> Unit,
    onResetLockout: () -> Unit
) {
    var username by remember { mutableStateOf("kabarnet_bm") }
    var password by remember { mutableStateOf("demo1234") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    var showForgotDialog by remember { mutableStateOf(false) }
    var showRequestAccountDialog by remember { mutableStateOf(false) }
    var showStudentViewDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 480.dp)
                .verticalScroll(scrollState)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Campus Hero Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box {
                    Image(
                        painter = painterResource(id = R.drawable.boarding_hero_1791014005407),
                        contentDescription = "Boarding Campus",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.35f))
                    )
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("A6", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                        Column {
                            Text(
                                text = "The Axis 6",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Boarding Management System",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Main Glass Login Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // School Identity
                    Text(
                        text = "Kabarnet Senior School",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Knowledge is Power",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 2.dp, bottom = 18.dp)
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Sign in to Portal",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 14.dp)
                    )

                    // Error Alert
                    AnimatedVisibility(visible = errorMessage != null) {
                        errorMessage?.let {
                            Surface(
                                color = Color(0xFFFEF2F2),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ErrorOutline,
                                        contentDescription = null,
                                        tint = AxisDanger,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = it,
                                        color = Color(0xFF991B1B),
                                        fontSize = 13.sp,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }

                    // Lockout Alert (15-min countdown)
                    AnimatedVisibility(visible = lockoutState.isLocked) {
                        val minutes = lockoutState.remainingSeconds / 60
                        val seconds = lockoutState.remainingSeconds % 60
                        Surface(
                            color = Color(0xFFFFFBEB),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.LockClock,
                                        contentDescription = null,
                                        tint = AxisWarning,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Account temporarily locked",
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF92400E),
                                        fontSize = 13.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Too many failed attempts. Try again in %02d:%02d".format(minutes, seconds),
                                    color = Color(0xFF92400E),
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                TextButton(
                                    onClick = onResetLockout,
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("Reset Lockout (Testing Override)", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }

                    // Username Field
                    OutlinedTextField(
                        value = username,
                        onValueChange = {
                            username = it
                            errorMessage = null
                        },
                        label = { Text("Username") },
                        placeholder = { Text("Enter your username") },
                        leadingIcon = {
                            Icon(Icons.Outlined.Person, contentDescription = null)
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("username_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Password Field
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            errorMessage = null
                        },
                        label = { Text("Password") },
                        placeholder = { Text("Enter your password") },
                        leadingIcon = {
                            Icon(Icons.Outlined.Lock, contentDescription = null)
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = { passwordVisible = !passwordVisible },
                                modifier = Modifier.testTag("password_toggle_btn")
                            ) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password"
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("password_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Options Row: Remember Me & Forgot Password
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = rememberMe,
                                onCheckedChange = { rememberMe = it },
                                modifier = Modifier.testTag("remember_me_checkbox")
                            )
                            Text("Remember me", style = MaterialTheme.typography.bodySmall)
                        }

                        TextButton(
                            onClick = { showForgotDialog = true },
                            modifier = Modifier.testTag("forgot_password_btn")
                        ) {
                            Text("Forgot password?", style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Sign In Button
                    Button(
                        onClick = {
                            isLoading = true
                            errorMessage = null
                            onLoginSubmit(username, password, rememberMe) { err ->
                                isLoading = false
                                errorMessage = err
                            }
                        },
                        enabled = !lockoutState.isLocked && !isLoading,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("sign_in_submit_btn")
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text("Sign in", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Quick Demo Credentials section
                    Text(
                        text = "Quick Demo Accounts:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(
                            onClick = {
                                username = "kabarnet_bm"
                                password = "demo1234"
                                onQuickDemoLogin(false)
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).testTag("demo_bm_btn")
                        ) {
                            Text("Boarding Master", fontSize = 11.sp, maxLines = 1)
                        }

                        OutlinedButton(
                            onClick = {
                                username = "mr_kabutiei"
                                password = "super admin"
                                onQuickDemoLogin(true)
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).testTag("demo_admin_btn")
                        ) {
                            Text("Master Admin", fontSize = 11.sp, maxLines = 1)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Alternate Actions
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        FilledTonalButton(
                            onClick = { showRequestAccountDialog = true },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Request Account", fontSize = 12.sp)
                        }

                        FilledTonalButton(
                            onClick = { showStudentViewDialog = true },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Student Portal", fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Footer
            Text(
                text = "© 2026 Kabarnet Senior School\nPowered by The Axis 6 Boarding Management System",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                fontSize = 11.sp
            )
        }
    }

    // Dialog: Forgot password
    if (showForgotDialog) {
        AlertDialog(
            onDismissRequest = { showForgotDialog = false },
            title = { Text("Password Recovery") },
            text = {
                Text("Please contact the school Boarding Master or The Axis 6 technical support at:\n\nsupport@theaxis6.com\n+254 700 000 000\n\nAdministrative password resets require school headteacher verification.")
            },
            confirmButton = {
                TextButton(onClick = { showForgotDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    // Dialog: Request Account
    if (showRequestAccountDialog) {
        AlertDialog(
            onDismissRequest = { showRequestAccountDialog = false },
            title = { Text("Request Staff Account") },
            text = {
                Text("Boarding staff accounts (Matrons, Housemasters, Sanatorium Nurses, and Cleaners Supervisors) are provisioned directly by the Master Admin (Mr. Kabutiei).\n\nPlease submit your National ID and Staff Payroll Number to the boarding administration office.")
            },
            confirmButton = {
                TextButton(onClick = { showRequestAccountDialog = false }) {
                    Text("Understood")
                }
            }
        )
    }

    // Dialog: Student Portal View
    if (showStudentViewDialog) {
        AlertDialog(
            onDismissRequest = { showStudentViewDialog = false },
            title = { Text("Student & Parent Portal") },
            text = {
                Text("Student kiosk mode allows enrolled students to view their bed assignments, current inspection scores for their dormitory, and the upcoming meal timetable.\n\nLog in using your official Boarding Master credentials to manage student profiles and records.")
            },
            confirmButton = {
                TextButton(onClick = { showStudentViewDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}
