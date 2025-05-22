package com.example.lessons.Screens.Raspis

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import com.example.lessons.Models.UserManager
import com.example.lessons.Screens.Register.RegisterScreen
import com.example.lessons.ui.theme.WorkForPerson


@Composable
fun PersonInfo(
    viewModel: WorkForPerson,
    navController: NavController

) {
    val backgroundColor = Color(0xFF5DA8FF)
    val inputColor = Color(0xFFA2C7FF)
    val buttonColor = Color(0xFF003366)
    val textColor = Color.White

    val context = LocalContext.current
    val userData by viewModel.currentUser.collectAsState()
    val currentUser by UserManager.currentUser

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        selectedImageUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // Блок с аватаром
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clickable { imagePickerLauncher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            when {
                selectedImageUri != null -> {
                    Image(
                        painter = rememberImagePainter(selectedImageUri),
                        contentDescription = "Выбранный аватар",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                !currentUser?.image_url.isNullOrEmpty() -> {
                    AsyncImage(
                        model = currentUser?.image_url,
                        contentDescription = "User avatar",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.AddCircle,
                contentDescription = "Изменить фото",
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .background(Color.White, CircleShape)
                    .padding(8.dp),
                tint = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Карточки с информацией
        UserInfoCard(
            title = "Имя",
            value = currentUser?.name ?: "Не указано",
        )

        UserInfoCard(
            title = "Почта",
            value = currentUser?.email ?: "Не указано",
        )

        UserInfoCard(
            title = "Телефон",
            value = currentUser?.phone ?: "Не указано",
        )

        // Кнопки
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Button(
                onClick = {
                    selectedImageUri?.let { uri ->
                        viewModel.uploadAvatar(uri, context)
                    }
                    Toast.makeText(context, "Данные сохранены", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
            ) {
                Text("Сохранить", fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = {
                    viewModel.signOut()
                    navController.navigate("login")
                    Toast.makeText(context, "Выход", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Выйти из аккаунта", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    currentUser?.id?.let { userId ->
                        viewModel.deleteAccount(userId,context){
                            Toast.makeText(context, "Данные удалены", Toast.LENGTH_SHORT).show()
                            navController.navigate("login")
                        }
                    } ?: run {
                        Toast.makeText(
                            context,
                            "Ошибка: пользователь не найден",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Удалить аккаунта", fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun UserInfoCard(title: String, value: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFA2C7FF))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontSize = 14.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

