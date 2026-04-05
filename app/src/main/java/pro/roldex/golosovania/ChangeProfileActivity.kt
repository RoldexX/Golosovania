package pro.roldex.golosovania

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pro.roldex.golosovania.databinding.ActivityChangeProfileBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangeProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangeProfileBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeProfileBinding.inflate(layoutInflater)
        sessionManager = SessionManager(this)
        setContentView(binding.root)

        binding.burger.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
            finish()
        }

        binding.saveChangeProfile.setOnClickListener {
            val request = UpdateUserProfileRequest(
                name = binding.textEditName.text.toString().trim(),
                surname = binding.textEditSurname.text.toString().trim(),
                email = binding.textEditEmail.text.toString().trim(),
                username = binding.textEditUsername.text.toString().trim()
            )

            if (
                request.name.isBlank() ||
                request.surname.isBlank() ||
                request.email.isBlank() ||
                request.username.isBlank()
            ) {
                toast(getString(R.string.fill_all_fields))
            } else {
                updateProfile(request)
            }
        }

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
                            binding.textEditName.setText(user.name)
                            binding.textEditSurname.setText(user.surname)
                            binding.textEditEmail.setText(user.email)
                            binding.textEditUsername.setText(user.username)
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

    private fun updateProfile(request: UpdateUserProfileRequest) {
        ApiClient.authApi(this).updateCurrentUser(request)
            .enqueue(object : Callback<UpdateUserProfileResponse> {
                override fun onResponse(
                    call: Call<UpdateUserProfileResponse>,
                    response: Response<UpdateUserProfileResponse>
                ) {
                    if (response.isSuccessful) {
                        toast(getString(R.string.profile_updated))
                        startActivity(Intent(this@ChangeProfileActivity, ProfileActivity::class.java))
                        finish()
                    } else if (response.code() == 401) {
                        sessionManager.clearSession()
                        redirectToMain()
                    } else {
                        toast(ApiErrorParser.message(response) { getString(R.string.request_failed, it) })
                    }
                }

                override fun onFailure(call: Call<UpdateUserProfileResponse>, t: Throwable) {
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
