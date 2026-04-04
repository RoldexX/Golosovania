package pro.roldex.golosovania

data class NotificationResponse(
    val id: Long,
    val text: String,
    val date: String
)

data class DeleteNotificationResponse(
    val success: Boolean
)
