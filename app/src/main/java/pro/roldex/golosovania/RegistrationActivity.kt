package pro.roldex.golosovania

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pro.roldex.golosovania.databinding.ActivityRegistrationBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        sessionManager = SessionManager(this)
        setContentView(binding.root)

        binding.signUpBtn.setOnClickListener {
            val request = RegisterRequest(
                name = binding.name.text.toString().trim(),
                surname = binding.surname.text.toString().trim(),
                login = binding.editTextLogin.text.toString().trim(),
                password = binding.editTextPassword.text.toString(),
                email = binding.editTextEmail.text.toString().trim(),
                username = binding.editTextUsername.text.toString().trim()
            )

            if (
                request.name.isBlank() ||
                request.surname.isBlank() ||
                request.login.isBlank() ||
                request.password.isBlank() ||
                request.email.isBlank() ||
                request.username.isBlank()
            ) {
                toast(getString(R.string.fill_all_fields))
            } else {
                registerUser(request)
            }
        }
    }

    private fun registerUser(request: RegisterRequest) {
        ApiClient.authApi(this).register(request)
            .enqueue(object : Callback<RegisterResponse> {
                override fun onResponse(
                    call: Call<RegisterResponse>,
                    response: Response<RegisterResponse>
                ) {
                    if (response.isSuccessful) {
                        val token = response.body()?.token
                        if (token.isNullOrBlank()) {
                            toast(getString(R.string.empty_token_response))
                            return
                        }

                        sessionManager.saveToken(token)
                        toast(getString(R.string.registration_successful))
                        startActivity(Intent(this@RegistrationActivity, GeneralActivity::class.java))
                        finish()
                    } else {
                        toast(ApiErrorParser.message(response) { getString(R.string.request_failed, it) })
                    }
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    toast(getString(R.string.network_error, t.message ?: getString(R.string.request_failed, -1)))
                }
            })
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
