package com.example.lessons.Models

data class ScheduleResponse(
    val result: ResultData?,
)

data class ResultData(
    val main: List<ScheduleItem>?,
    val change: List<ScheduleItem>?,
    val bell: List<BellItem>?,
)

data class ScheduleItem(
    val id: Int,
    val para: Int,
    val lesson: Lesson?,            // из paraTo
    val teachers: List<Teacher>?,   // из paraTo
    val cabinet: Cabinet?,
    val group: Groupaspis?,
    val type: String,               // "main" или "change"
    val paraFrom: ParaDetail? = null, // для изменённых пар
    val paraTo: ParaDetail? = null,    // для изменённых пар
    val noteData: NoteData? = null,
    val parallel: Paralel? = null,
)

data class Paralel(
    val id: Int,
    val name: String,
    val instrumental_case: String,
    val full_name: String,
)

data class NoteData(
    val text: String?,
    val paras: List<Any>?,
    val is_cancel: Boolean?,
    val teacher: TeacherDetail,
)

data class TeacherDetail(
    val id: Int,
    val name: String,
    val instrumental_case: String,
)

data class ParaDetail(
    val lesson: Lesson?,
    val teachers: List<Teacher>?,
    val subgroup: Int?,
    val para: Int?,
    val hours: Int?,
)

data class Lesson(
    val id: Int,
    val name: String,
    val instrumental_case: String,
)

data class Teacher(
    val id: Int,
    val name: String,
    val full_name: String?,
)

data class Cabinet(
    val id: Int,
    val name: String,
)

data class Groupaspis(
    val id: Int,
    val name: String,
)

data class BellItem(
    val para: Int,
    val start_time: String,
    val end_time: String,
)
