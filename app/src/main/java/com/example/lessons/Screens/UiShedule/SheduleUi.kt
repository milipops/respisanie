package com.example.lessons.Screens.UiShedule

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.lessons.Models.BellItem
import com.example.lessons.Models.ScheduleItem
import com.example.lessons.Models.Teacher
import com.example.lessons.ui.theme.WorkForPerson


@Composable
fun ScheduleList(
    viewModel: WorkForPerson,
    bells: List<BellItem>,
) {
    val schedule by viewModel.scheduleState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        if (schedule.isEmpty()) {
            item {
                Text(
                    text = "Расписание не найдено",
                    modifier = Modifier.padding(16.dp),
                    color = Color.White
                )
            }
        } else {
            val groupedByPara = schedule.groupBy { it.para }

            for ((para, lessons) in groupedByPara) {
                val bell = bells.find { it.para == para }

                item {
                    if (para == 0) {
                        // Заметки без времени и номера пары
                        lessons.forEach {
                            ScheduleCard(item = it, bells = bells, showTimeAndPara = false)
                        }
                    } else {
                        // Пары с отдельной карточкой времени
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            // Левая часть — номер пары и время
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(8.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF90CAF9)),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(
                                        text = "Пара №$para",
                                        color = Color.Black
                                    )
                                    Text(
                                        text = "${bell?.start_time ?: "--:--"} – ${bell?.end_time ?: "--:--"}",
                                        color = Color.Black
                                    )
                                }
                            }

                            // Правая часть — расписание без повтора номера и времени
                            Column(modifier = Modifier.weight(3f)) {
                                lessons.forEach {
                                    ScheduleCard(item = it, bells = bells, showTimeAndPara = false)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ScheduleCard(
    item: ScheduleItem,
    bells: List<BellItem>,
    showTimeAndPara: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val isCancel = "remove" in item.type
    val backgroundColor = if (item.type == "change")
        Color(0xFFFFF9C4) else Color(0xFFE1F5FE)
    val bell = bells.firstOrNull { it.para == item.para }

    Card(
        modifier = modifier.padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            if (showTimeAndPara && item.para != 0) {
                Text(text = "Пара №${item.para}", style = MaterialTheme.typography.titleMedium)
                bell?.let {
                    Text(text = "Время: ${it.start_time} – ${it.end_time}")
                }
            }

            item.noteData?.let { note ->
                val hasNote = note.text?.isNotBlank() == true
                val hasParas = !note.paras.isNullOrEmpty()
                val hasTeacher = note.teacher?.name?.isNotBlank() == true
                val hasCancel = note.is_cancel?.toString()?.isNotBlank() == true
                val isAllEmpty = !hasNote && !hasParas && !hasCancel

                if (!isAllEmpty) {
                    Column {
                        if (hasNote) {
                            Text(text = "Примечание: ${note.text}")
                        }
                        if (hasParas) {
                            val parasFormatted = note.paras
                                ?.mapNotNull {
                                    when (it) {
                                        is Number -> it.toInt()
                                        is String -> it.toIntOrNull()
                                        else -> null
                                    }
                                }
                                ?.joinToString(",")
                            Text(text = "Пары: $parasFormatted")
                        }
                        if (hasTeacher) {
                            Text(text = "Преподаватель: ${note.teacher.name}")
                        }
                        if (hasCancel) {
                            Text(text = "Отмена: ${note.is_cancel}")
                        }
                    }
                }
            }

            if (isCancel) {
                Text(text = "Занятие отменено", color = Color.Red)
            } else {
                item.lesson?.name?.takeIf { it.isNotBlank() }?.let {
                    Text(text = "Предмет: $it")
                }
                item.cabinet?.name?.takeIf { it.isNotBlank() }?.let {
                    Text(text = "Аудитория: $it")
                }
                item.teachers?.joinToString(", ") { it.name }
                    ?.takeIf { it.isNotBlank() }?.let {
                        Text(text = "Преподаватель: $it")
                    }

                item.parallel?.let { parallel ->
                    parallel.name?.takeIf { it.isNotBlank() }?.let {
                        Text(text = "Параллель: $it")
                    }
                    parallel.full_name?.takeIf { it.isNotBlank() }?.let {
                        Text(text = it)
                    }
                    parallel.instrumental_case?.takeIf { it.isNotBlank() }?.let {
                        Text(text = it)
                    }
                }
            }
        }
    }
}






