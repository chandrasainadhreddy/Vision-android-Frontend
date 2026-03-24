package com.SIMATS.binocularvision.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.SIMATS.binocularvision.ui.theme.BinocularvisionTheme
import com.SIMATS.binocularvision.ui.viewmodels.AuthState
import com.SIMATS.binocularvision.ui.viewmodels.AuthViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAccountScreen(
    onNavigateBack: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    onCreateAccountClick: () -> Unit = {},
    viewModel: AuthViewModel = viewModel()
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var agreeToTerms by remember { mutableStateOf(false) }

    // Error states for individual fields
    var fullNameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val authState by viewModel.authState

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                snackbarHostState.showSnackbar((authState as AuthState.Success).message)
                onCreateAccountClick()
                viewModel.resetState()
            }
            is AuthState.Error -> {
                snackbarHostState.showSnackbar((authState as AuthState.Error).message)
            }
            else -> {}
        }
    }

    // Validation logic based on provided conditions
    fun validate(): Boolean {
        fullNameError = null
        emailError = null
        phoneError = null
        passwordError = null
        confirmPasswordError = null

        val name = fullName.trim()
        val mail = email.trim()
        val phone = phoneNumber.trim()

        // Username
        if (name.isEmpty()) {
            fullNameError = "Username required"
            return false
        }
        if (!name.matches(Regex("^[A-Za-z]+(?: [A-Za-z]+)*$"))) {
            fullNameError = "Only letters allowed"
            return false
        }

        // Email
        if (mail.isEmpty()) {
            emailError = "Email required"
            return false
        }
        if (!mail.matches(Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"))) {
            emailError = "Invalid email"
            return false
        }

        // Phone
        if (phone.isEmpty()) {
            phoneError = "Phone required"
            return false
        }
        if (!phone.matches(Regex("^\\d{10}$"))) {
            phoneError = "Must be 10 digits"
            return false
        }

        // Password
        if (password.isEmpty()) {
            passwordError = "Password required"
            return false
        }
        if (!password.matches(Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&^#()_\\-+=|\\\\/.,:;~`])[A-Za-z\\d@$!%*?&^#()_\\-+=|\\\\/.,:;~`]{8,}$"))) {
            passwordError = "Weak password"
            return false
        }

        // Confirm Password
        if (confirmPassword.isEmpty()) {
            confirmPasswordError = "Confirm password required"
            return false
        }
        if (password != confirmPassword) {
            confirmPasswordError = "Passwords do not match"
            return false
        }

        if (!agreeToTerms) {
            scope.launch { snackbarHostState.showSnackbar("Please agree to the terms and conditions") }
            return false
        }

        return true
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Surface(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with Back Arrow and Title
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF1E293B)
                        )
                    }
                    Text(
                        text = "Account",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Help us personalize your experience.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF64748B),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Full Name Field
                AccountInputField(
                    label = "Full Name",
                    value = fullName,
                    onValueChange = {
                        fullName = it
                        fullNameError = null
                    },
                    placeholder = "John Doe",
                    leadingIcon = Icons.Default.Person,
                    errorText = fullNameError
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Email Address Field
                AccountInputField(
                    label = "Email Address",
                    value = email,
                    onValueChange = {
                        email = it
                        emailError = null
                    },
                    placeholder = "your.email@example.com",
                    leadingIcon = Icons.Default.Email,
                    keyboardType = KeyboardType.Email,
                    errorText = emailError
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Phone Number Field
                AccountInputField(
                    label = "Phone Number",
                    value = phoneNumber,
                    onValueChange = {
                        phoneNumber = it
                        phoneError = null
                    },
                    placeholder = "10-digit mobile number",
                    leadingIcon = Icons.Default.Phone,
                    keyboardType = KeyboardType.Phone,
                    errorText = phoneError
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Password Field
                AccountInputField(
                    label = "Password",
                    value = password,
                    onValueChange = {
                        password = it
                        passwordError = null
                    },
                    placeholder = "Min. 8 characters",
                    leadingIcon = Icons.Default.Lock,
                    isPassword = true,
                    helperText = "At least 8 characters involving uppercase, lowercase, numbers, and symbols.",
                    errorText = passwordError
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Confirm Password Field
                AccountInputField(
                    label = "Confirm Password",
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        confirmPasswordError = null
                    },
                    placeholder = "Re-enter password",
                    leadingIcon = Icons.Default.Lock,
                    isPassword = true,
                    errorText = confirmPasswordError
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Terms Checkbox
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { agreeToTerms = !agreeToTerms },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = agreeToTerms,
                        onCheckedChange = { agreeToTerms = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFF2563EB),
                            uncheckedColor = Color(0xFFCBD5E1)
                        )
                    )
                    Text(
                        text = "I agree to the Terms of Service and Privacy Policy",
                        fontSize = 13.sp,
                        color = Color(0xFF64748B)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Create Account Button
                if (authState is AuthState.Loading) {
                    CircularProgressIndicator(color = Color(0xFF2563EB))
                } else {
                    Button(
                        onClick = {
                            if (validate()) {
                                viewModel.register(fullName.trim(), email.trim(), phoneNumber.trim(), password, confirmPassword)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2563EB)
                        )
                    ) {
                        Text(
                            text = "Create Account",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Already have an account? Login
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Already have an account? ",
                        color = Color(0xFF64748B),
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Login",
                        color = Color(0xFF2563EB),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable { onLoginClick() }
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun AccountInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    helperText: String? = null,
    errorText: String? = null
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val skyBlue = Color(0xFF87CEEB)
    val customTextSelectionColors = TextSelectionColors(
        handleColor = skyBlue,
        backgroundColor = skyBlue.copy(alpha = 0.4f)
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B),
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color(0xFF94A3B8)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            isError = errorText != null,
            leadingIcon = {
                Icon(leadingIcon, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(20.dp))
            },
            trailingIcon = if (isPassword) {
                {
                    val image = if (passwordVisible)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff

                    val description = if (passwordVisible) "Hide password" else "Show password"

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = description, tint = Color(0xFF94A3B8))
                    }
                }
            } else null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (errorText != null) Color.Red else Color.Transparent,
                unfocusedBorderColor = if (errorText != null) Color.Red else Color.Transparent,
                focusedContainerColor = Color(0xFFF1F5F9),
                unfocusedContainerColor = Color(0xFFF1F5F9),
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                cursorColor = Color.Black,
                selectionColors = customTextSelectionColors
            ),
            singleLine = true,
            visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
        )
        if (errorText != null) {
            Text(
                text = errorText,
                fontSize = 11.sp,
                color = Color.Red,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp),
                lineHeight = 16.sp
            )
        } else if (helperText != null) {
            Text(
                text = helperText,
                fontSize = 11.sp,
                color = Color(0xFF64748B),
                modifier = Modifier.padding(start = 4.dp, top = 4.dp),
                lineHeight = 16.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateAccountScreenPreview() {
    BinocularvisionTheme {
        CreateAccountScreen()
    }
}
