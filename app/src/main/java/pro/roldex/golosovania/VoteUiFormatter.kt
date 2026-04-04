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
        val date = parse(lastDate) ?: return "Deadline: $lastDate"
        val now = LocalDateTime.now()

        if (date.isBefore(now)) {
            return "Closed"
        }

        val duration = Duration.between(now, date)
        val days = duration.toDays()
        val hours = duration.minusDays(days).toHours()
        val minutes = duration.minusDays(days).minusHours(hours).toMinutes()

        return buildString {
            append("Closes in ")
            if (days > 0) {
                append(days).append("d ")
            }
            if (hours > 0 || days > 0) {
                append(hours).append("h ")
            }
            append(minutes).append("m")
        }
    }

    fun detailsText(lastDate: String, choicesCount: Int): String {
        val date = parse(lastDate)
        val formattedDate = if (date != null) {
            date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
        } else {
            lastDate
        }

        return "Choices: $choicesCount | Until: $formattedDate"
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
