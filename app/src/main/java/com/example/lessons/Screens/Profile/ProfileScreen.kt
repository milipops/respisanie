package com.example.lessons.Screens.Profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.lessons.Screens.Raspis.Raspis
import com.example.lessons.ui.theme.WorkForPerson

@Composable
fun Raspisanie(navController: NavController) {
    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf("Расписание", "Заметки", "Профиль")
    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = {
                            when (index) {
                                0 -> Icon(Icons.Filled.Face, contentDescription = item)
                                1 -> Icon(Icons.Filled.Edit, contentDescription = item)
                                2 -> Icon(Icons.Filled.Person, contentDescription = item)
                                else -> Icon(Icons.Filled.Info, contentDescription = item)
                            }
                        },
                        label = { Text(item) },
                        selected = selectedItem == index,
                        onClick = { selectedItem = index }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when (selectedItem) {
                0 -> Raspis(navController)
                1 -> Text("Экран заметок", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.align(
                    Alignment.Center))
                2 -> { val personViewModel: WorkForPerson = viewModel()
                    PersonInfo(viewModel = personViewModel)
                }
            }
        }
    }
}