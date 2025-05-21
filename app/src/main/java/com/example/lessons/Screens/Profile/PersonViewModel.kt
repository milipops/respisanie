package com.example.lessons.Screens.Profile

import android.app.Person
import  androidx.lifecycle.viewmodel.compose.viewModel
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import com.example.lessons.Models.UserInfoCard
import com.example.lessons.Models.UserManager.currentUser
import com.example.lessons.ui.theme.WorkForPerson
import kotlinx.coroutines.flow.StateFlow


@Composable
fun PersonInfo(viewModel: ViewModel)
{
    val context = LocalContext.current

    val viewModel: WorkForPerson = viewModel()
    val userData by viewModel.currentUser.collectAsState()
    val currentUser = userData.firstOrNull()

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher для выбора изображения
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        selectedImageUri = uri
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp)
    ) {
        // Аватар пользователя с возможностью выбора нового
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clickable { imagePickerLauncher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clickable { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageUri != null) {
                    Image(
                        painter = rememberImagePainter(selectedImageUri),
                        contentDescription = "Выбранный аватар",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else if (!currentUser?.image_url.isNullOrEmpty()) {
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

            Spacer(modifier = Modifier.height(16.dp))

            UserInfoCard(
                title = "Имя",
                value = currentUser?.name ?: "Не указано"
            )


            UserInfoCard(
                title = "Почта",
                value = currentUser?.email ?: "Не указано"
            )
            UserInfoCard(
                title = "Телефон",
                value = currentUser?.phone ?: "Не указано"
            )

            Button(
                onClick = {
                    // Сохраняем выбранное изображение
                    selectedImageUri?.let { uri ->
                        viewModel.uploadAvatar(uri, context)
                    } ?: run {
                        Toast.makeText(context, "Данные сохранены", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF80CBC4))
            ) {
                Text("Сохранить", fontSize = 18.sp)
            }
        }
    }
}
