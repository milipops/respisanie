package com.example.lessons.Methods

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import kotlin.math.min

// Функция для форматирования номера телефона
fun formatPhoneNumber(digits: String): String {
    val cleanDigits = digits.filter { it.isDigit() }.take(11)
    if (cleanDigits.isEmpty()) return cleanDigits

    return buildString {
        when {
            cleanDigits.startsWith("7") || cleanDigits.startsWith("8") -> {
                append("+7")
                val remaining = cleanDigits.drop(1)
                if (remaining.isNotEmpty()) {
                    append(" (${remaining.take(3)}")
                    if (remaining.length > 3) append(") ${remaining.drop(3).take(3)}")
                    if (remaining.length > 6) append("-${remaining.drop(6).take(2)}")
                    if (remaining.length > 8) append("-${remaining.drop(8).take(2)}")
                }
            }
            else -> append(cleanDigits) // Для номеров не начинающихся с 7 или 8
        }
    }
}
// VisualTransformation для отображения маски телефона
class PhoneNumberVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        return try {
            val formatted = formatPhoneNumber(text.text)
            TransformedText(
                AnnotatedString(formatted),
                PhoneNumberOffsetMapping
            )
        } catch (e: Exception) {
            // В случае ошибки возвращаем исходный текст без преобразования
            TransformedText(text, OffsetMapping.Identity)
        }
    }
}

// Для корректного позиционирования курсора
object PhoneNumberOffsetMapping : OffsetMapping {
    override fun originalToTransformed(offset: Int): Int {
        return when {
            offset <= 0 -> 0
            offset == 1 -> min(2, 2) // +7
            offset <= 4 -> min(offset + 3, 6) // (XXX)
            offset <= 7 -> min(offset + 5, 11) // XXX
            offset <= 9 -> min(offset + 6, 14) // -XX
            else -> min(offset + 7, 16) // -XX
        }
    }

    override fun transformedToOriginal(offset: Int): Int {
        return when {
            offset <= 2 -> min(offset, 1) // +7
            offset <= 6 -> min(offset - 3, 4) // (XXX)
            offset <= 11 -> min(offset - 5, 7) // XXX
            offset <= 14 -> min(offset - 6, 9) // -XX
            else -> min(offset - 7, 11) // -XX
        }
    }
}