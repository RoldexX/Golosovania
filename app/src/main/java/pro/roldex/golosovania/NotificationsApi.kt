package pro.roldex.golosovania

import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path

interface NotificationsApi {
    @GET("notifications")
    fun getNotifications(): Call<List<NotificationResponse>>

    @DELETE("notifications/{notificationId}")
    fun deleteNotification(
        @Path("notificationId") notificationId: Long
    ): Call<DeleteNotificationResponse>
}
