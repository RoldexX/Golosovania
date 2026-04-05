package pro.roldex.golosovania

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pro.roldex.golosovania.databinding.ActivityProfileBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        sessionManager = SessionManager(this)
        setContentView(binding.root)

        binding.changeProfileDataBtn.setOnClickListener {
            startActivity(Intent(this, ChangeProfileActivity::class.java))
        }

        binding.changePasswordBtn.setOnClickListener {
            startActivity(Intent(this, ChangePasswordActivity::class.java))
        }

        binding.myGolosovania.setOnClickListener {
            val intent = Intent(this, GeneralActivity::class.java)
            intent.putExtra(GeneralActivity.EXTRA_MODE, GeneralActivity.MODE_MY)
            startActivity(intent)
        }

        binding.participatedVotesBtn.setOnClickListener {
            val intent = Intent(this, GeneralActivity::class.java)
            intent.putExtra(GeneralActivity.EXTRA_MODE, GeneralActivity.MODE_PARTICIPATED)
            startActivity(intent)
        }

        binding.notificationsBtn.setOnClickListener {
            startActivity(Intent(this, NotificationsActivity::class.java))
        }

        binding.burger.setOnClickListener {
            startActivity(Intent(this, GeneralActivity::class.java))
        }

        binding.buttonExit.setOnClickListener {
            logout()
        }
    }

    override fun onResume() {
        super.onResume()
        loadProfile()
    }

    private fun loadProfile() {
        ApiClient.authApi(this).getCurrentUser()
            .enqueue(object : Callback<UserProfileResponse> {
                override fun onResponse(
                    call: Call<UserProfileResponse>,
                    response: Response<UserProfileResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { user ->
                            binding.textView4.text = "${user.name} ${user.surname}".trim()
                        }
                    } else if (response.code() == 401) {
                        sessionManager.clearSession()
                        redirectToMain()
                    } else {
                        toast(ApiErrorParser.message(response) { getString(R.string.request_failed, it) })
                    }
                }

                override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
                    toast(getString(R.string.network_error, t.message ?: getString(R.string.request_failed, -1)))
                }
            })
    }

    private fun logout() {
        ApiClient.authApi(this).logout().enqueue(object : Callback<LogoutResponse> {
            override fun onResponse(
                call: Call<LogoutResponse>,
                response: Response<LogoutResponse>
            ) {
                sessionManager.clearSession()
                redirectToMain()
            }

            override fun onFailure(call: Call<LogoutResponse>, t: Throwable) {
                sessionManager.clearSession()
                redirectToMain()
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
