package com.example.lessons.Screens.Profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.lessons.Screens.Raspis.PersonInfo
import com.example.lessons.Screens.Raspis.ScheduleScreen
import com.example.lessons.ui.theme.WorkForPerson

@Composable
fun Raspisanie(
    navController: NavController,
    viewModel: WorkForPerson
) {
    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf("Расписание", "Профиль")
    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = {
                            when (index) {
                                0 -> Icon(Icons.Filled.Face, contentDescription = item)
                                1 -> Icon(Icons.Filled.Person, contentDescription = item)
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
                0 -> ScheduleScreen(groupId = 0,viewModel = WorkForPerson())
                1 -> PersonInfo(viewModel = WorkForPerson())
            }
        }
    }
}