package pro.roldex.golosovania

import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object VoteUiFormatter {
    private val serverFormatters = listOf(
        DateTimeFormatter.ISO_LOCAL_DATE_TIME,
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
    )

    fun deadlineText(lastDate: String): String {
        val date = parse(lastDate) ?: return "Срок: $lastDate"
        val now = LocalDateTime.now()

        if (date.isBefore(now)) {
            return "Завершено"
        }

        val duration = Duration.between(now, date)
        val days = duration.toDays()
        val hours = duration.minusDays(days).toHours()
        val minutes = duration.minusDays(days).minusHours(hours).toMinutes()

        val parts = buildList {
            if (days > 0) {
                add("$days д.")
            }
            if (hours > 0 || days > 0) {
                add("$hours ч.")
            }
            add("$minutes мин.")
        }.joinToString(" ")

        return "Закроется через $parts"
    }

    fun detailsText(lastDate: String, choicesCount: Int): String {
        val date = parse(lastDate)
        val formattedDate = if (date != null) {
            date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
        } else {
            lastDate
        }

        return "Вариантов: $choicesCount | До: $formattedDate"
    }

    fun isClosed(lastDate: String): Boolean {
        val date = parse(lastDate) ?: return false
        return !date.isAfter(LocalDateTime.now())
    }

    private fun parse(value: String): LocalDateTime? {
        for (formatter in serverFormatters) {
            try {
                return LocalDateTime.parse(value, formatter)
            } catch (_: DateTimeParseException) {
            }
        }

        return null
    }
}
