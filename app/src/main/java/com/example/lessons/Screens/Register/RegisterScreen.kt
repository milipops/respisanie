package com.example.lessons.Screens.Register

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role.Companion.Checkbox
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.lessons.Methods.PhoneNumberVisualTransformation
import com.example.lessons.Methods.formatPhoneNumber
import com.example.lessons.ui.theme.CustomOutlinedTextField
import kotlinx.coroutines.delay

@Composable
fun RegisterScreen(navController: NavController) {
    val viewModel: RegisterViewModel = viewModel()
    val state by viewModel.uiState
    val result by viewModel.resultState.collectAsState()
    var isChecked by remember { mutableStateOf(false) }

    val backgroundColor = Color(0xFF0066FF)
    val buttonColor = Color.White
    val textColor = Color.White

    val phoneVisualTransformation = remember {
        PhoneNumberVisualTransformation()
    }

    LaunchedEffect(result) {
        if (result is ResultState.Success) {
            delay(2000)
            navController.navigate("login") {
                popUpTo("register") { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-55).dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Регистрация",
                style = MaterialTheme.typography.headlineMedium,
                color = textColor
            )

            Spacer(modifier = Modifier.height(28.dp))

            CustomOutlinedTextField(
                value = state.name,
                onValueChange = { viewModel.updateState(state.copy(name = it)) },
                label = "Имя",
                textColor = textColor
            )

            Spacer(modifier = Modifier.height(12.dp))

            CustomOutlinedTextField(
                value = state.email,
                onValueChange = {
                    viewModel.updateState(state.copy(email = it))
                },
                label = "Email",
                isError = !state.isEmailValid,
                textColor = textColor,
                keyboardType = KeyboardType.Email
            )

            if (!state.isEmailValid && state.email.isNotEmpty()) {
                Text("Некорректный email", color = Color.Red)
            }

            Spacer(modifier = Modifier.height(12.dp))

            CustomOutlinedTextField(
                value = state.password,
                onValueChange = { viewModel.updateState(state.copy(password = it)) },
                label = "Пароль",
                visualTransformation = PasswordVisualTransformation(),
                textColor = textColor,
                keyboardType = KeyboardType.Password
            )

            Spacer(modifier = Modifier.height(12.dp))

            CustomOutlinedTextField(
                value = state.confirmPassword,
                onValueChange = { viewModel.updateState(state.copy(confirmPassword = it)) },
                label = "Подтвердите пароль",
                visualTransformation = PasswordVisualTransformation(),
                textColor = textColor,
                keyboardType = KeyboardType.Password
            )

            if (state.password.isNotEmpty() &&
                state.confirmPassword.isNotEmpty() &&
                state.password != state.confirmPassword
            ) {
                Text("Пароли не совпадают", color = Color.Red)
            }

            Spacer(modifier = Modifier.height(12.dp))

            CustomOutlinedTextField(
                value = state.phone,
                onValueChange = {
                    val digits = it.filter { ch -> ch.isDigit() }.take(11)
                    viewModel.updateState(state.copy(phone = digits))
                },
                label = "Номер телефона",
                visualTransformation = phoneVisualTransformation,
                textColor = textColor,
                keyboardType = KeyboardType.Phone
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = { isChecked = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color.White,
                        uncheckedColor = Color.White
                    )
                )
                Text(
                    text = "Соглашаюсь с условиями политики конфиденциальности",
                    modifier = Modifier.padding(start = 8.dp),
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        ) {
            when (result) {
                is ResultState.Error -> {
                    Spacer(modifier = Modifier.height(1.dp))
                    Text((result as ResultState.Error).message,
                        color = Color.Red,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(7.dp))
                }
                is ResultState.Success -> {
                    Text("Регистрация успешна!", color = Color.Green)
                    Spacer(modifier = Modifier.height(8.dp))
                    CircularProgressIndicator(color = Color.White)
                }
                ResultState.Loading -> {
                    CircularProgressIndicator(color = Color.White)
                }
                else -> {}
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { viewModel.register() },
                enabled = result !is ResultState.Loading && result !is ResultState.Success && isChecked,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
            ) {
                Text(
                    text = "Зарегистрироваться",
                    color = backgroundColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = { navController.navigate("login") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(2.dp, Color.White),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
            ) {
                Text(
                    text = "Уже есть аккаунт? Войти",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
