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
import com.example.lessons.Models.ScheduleItem
import com.example.lessons.ui.theme.WorkForPerson


@Composable
fun ScheduleList(viewModel: WorkForPerson) {
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

            for ((_, lessons) in groupedByPara) {
                val leftColumnItems = lessons.filter { it.type !in
                        listOf("change", "groupNote", "replace", "add", "parallel") }
                val rightColumnItems = lessons.filter { it.type in
                        listOf("change", "groupNote", "replace", "add", "parallel") }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            leftColumnItems.forEach {
                                ScheduleCard(item = it)
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            rightColumnItems.forEach {
                                ScheduleCard(item = it)
                            }
                        }
                    }
                }
            }
        }
    }
}





@Composable
fun ScheduleCard(item: ScheduleItem, modifier: Modifier = Modifier) {
    val isCancel = "remove" in item.type
    val backgroundColor = if (item.type == "change")
        Color(0xFFFFF9C4) else Color(0xFFE1F5FE)
    val paraText = "Пара №${item.para}" + if (item.type == "change") " (замена)"  else ""

    Card(
        modifier = modifier.padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = paraText, style = MaterialTheme.typography.titleMedium)

            item.noteData?.text?.takeIf { !it.isNullOrBlank() }?.let {
                Text(text = "Примечание: ${item.noteData?.text ?: "Неизвестно"}")
                Text(text = " ${item.noteData?.paras ?: "Неизвестно"}")
                Text(text = " ${item.noteData?.is_cancel ?: "Неизвестно"}")
                Text(text = " ${item.noteData?.teacher ?: "Неизвестно"}")



            }
            if (isCancel) {
                Text(text = " Занятие отменено", color = Color.Red)
            } else {
                Text(text = "Предмет: ${item.lesson?.name ?: "Неизвестно"}")
                Text(text = "Аудитория: ${item.cabinet?.name ?: "Не указана"}")
                Text(
                    text = "Преподаватель: ${
                        item.teachers?.joinToString(", ") { it.name } ?: "—"
                    }"
                )

                item.parallel?.name?.takeIf { it.isNotBlank() }?.let {
                    Text(text = "Параллель: $it")
                }
                item.parallel?.full_name?.takeIf { it.isNotBlank() }?.let {
                    Text(text = ": $it")
                }

                item.parallel?.instrumental_case?.takeIf { it.isNotBlank() }?.let {
                    Text(text = ": $it")
                }

            }
        }
    }
}






