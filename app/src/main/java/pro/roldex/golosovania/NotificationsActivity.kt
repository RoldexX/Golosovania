package pro.roldex.golosovania

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import pro.roldex.golosovania.databinding.ActivityNotificationsBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationsBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var adapter: NotificationsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationsBinding.inflate(layoutInflater)
        sessionManager = SessionManager(this)
        setContentView(binding.root)

        adapter = NotificationsAdapter { item -> confirmDelete(item.id) }
        binding.notificationsRecycler.layoutManager = LinearLayoutManager(this)
        binding.notificationsRecycler.adapter = adapter
        binding.notificationsRecycler.setPadding(20, 20, 20, 20)
        binding.notificationsRecycler.clipToPadding = false

        binding.burger.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        loadNotifications()
    }

    private fun loadNotifications() {
        ApiClient.notificationsApi(this).getNotifications()
            .enqueue(object : Callback<List<NotificationResponse>> {
                override fun onResponse(
                    call: Call<List<NotificationResponse>>,
                    response: Response<List<NotificationResponse>>
                ) {
                    if (response.isSuccessful) {
                        adapter.submitList(response.body().orEmpty())
                    } else if (response.code() == 401) {
                        sessionManager.clearSession()
                        redirectToMain()
                    } else {
                        toast(ApiErrorParser.message(response) { getString(R.string.request_failed, it) })
                    }
                }

                override fun onFailure(call: Call<List<NotificationResponse>>, t: Throwable) {
                    toast(getString(R.string.network_error, t.message ?: getString(R.string.request_failed, -1)))
                }
            })
    }

    private fun confirmDelete(notificationId: Long) {
        AlertDialog.Builder(this)
            .setTitle(R.string.delete_notification_title)
            .setMessage(R.string.delete_notification_message)
            .setPositiveButton(R.string.delete) { _, _ -> deleteNotification(notificationId) }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun deleteNotification(notificationId: Long) {
        ApiClient.notificationsApi(this).deleteNotification(notificationId)
            .enqueue(object : Callback<DeleteNotificationResponse> {
                override fun onResponse(
                    call: Call<DeleteNotificationResponse>,
                    response: Response<DeleteNotificationResponse>
                ) {
                    if (response.isSuccessful) {
                        loadNotifications()
                    } else if (response.code() == 401) {
                        sessionManager.clearSession()
                        redirectToMain()
                    } else {
                        toast(ApiErrorParser.message(response) { getString(R.string.request_failed, it) })
                    }
                }

                override fun onFailure(call: Call<DeleteNotificationResponse>, t: Throwable) {
                    toast(getString(R.string.network_error, t.message ?: getString(R.string.request_failed, -1)))
                }
            })
    }

    private fun redirectToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
