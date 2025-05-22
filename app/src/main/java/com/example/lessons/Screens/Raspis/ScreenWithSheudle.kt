package com.example.lessons.Screens.Raspis


import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.material3.*
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lessons.Models.BellItem
import com.example.lessons.Screens.UiShedule.ScheduleList
import com.example.lessons.ui.theme.WorkForPerson
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScheduleScreen(
    groupId: Int,
    viewModel: WorkForPerson
) {
    val backgroundColor = Color(0xFF5DA8FF)
    val inputColor = Color(0xFFA2C7FF)
    val buttonColor = Color(0xFF003366)
    val textColor = Color.White

    var selectedGroupId by remember { mutableStateOf(groupId) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    var showDatePicker by remember { mutableStateOf(false) }

    var expanded by remember { mutableStateOf(false) }

    val groupes by viewModel.Group.collectAsState()
    val imageUrl = remember { mutableStateOf("") }
    var groupText by remember { mutableStateOf("") }

    LaunchedEffect(groupId) {
        val url = viewModel.getUrlImage(groupId.toString())
        imageUrl.value = url

        val card = groupes.find { it.id == groupId }
        groupText = card?.Name ?: "Неизвестно"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    TextField(
                        value = groupText,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Группа") },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            containerColor = inputColor,
                            focusedLabelColor = textColor,
                            unfocusedLabelColor = textColor,
                        ),
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        if (groupes.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("Группы не найдены") },
                                onClick = {}
                            )
                        } else {
                            groupes.forEach { group ->
                                DropdownMenuItem(
                                    text = { Text(group.Name ?: "Неизвестно") },
                                    onClick = {
                                        groupText = group.Name ?: "Неизвестно"
                                        selectedGroupId = group.id
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            OutlinedTextField(
                value = selectedDate.format(dateFormatter),
                onValueChange = {},
                label = { Text("Дата", color = textColor) },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = inputColor,
                    focusedLabelColor = textColor,
                    unfocusedLabelColor = textColor,
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Выбор даты",
                        tint = textColor
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .clickable { showDatePicker = true },
                readOnly = true
            )
            Button(
                onClick = {
                    val epochDate = selectedDate
                        .atStartOfDay(ZoneId.systemDefault())
                        .toEpochSecond()

                    if (selectedGroupId != 0) {
                        viewModel.loadSchedule(selectedGroupId, epochDate)
                    } else {
                        println("Ошибка: группа не выбрана или не найдена")
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonColor,
                    contentColor = textColor
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Смотреть")
            }
            val bells = remember {
                listOf(
                    BellItem(para = 1, start_time = "08:00", end_time = "09:30"),
                    BellItem(para = 2, start_time = "09:50", end_time = "11:20"),
                    BellItem(para = 3, start_time = "11:50", end_time = "13:20"),
                    BellItem(para = 4, start_time = "13:30", end_time = "15:00"),
                    BellItem(para = 5, start_time = "15:10", end_time = "16:40"),
                    BellItem(para = 6, start_time = "16:50", end_time = "18:20")
                )
            }

            ScheduleList(viewModel = viewModel, bells = bells)

        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Button(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            selectedDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
