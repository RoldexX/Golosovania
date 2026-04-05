package pro.roldex.golosovania

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pro.roldex.golosovania.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        sessionManager = SessionManager(this)
        setContentView(binding.root)

        if (!sessionManager.getToken().isNullOrBlank()) {
            openGeneralScreen()
            finish()
            return
        }

        binding.signInBtn.setOnClickListener {
            val login = binding.EmailAddress.text.toString().trim()
            val password = binding.Password.text.toString()

            if (login.isBlank() || password.isBlank()) {
                toast(getString(R.string.fill_all_fields))
            } else {
                loginUser(login, password)
            }
        }
    }

    private fun loginUser(login: String, password: String) {
        ApiClient.authApi(this).login(LoginRequest(login, password))
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    if (response.isSuccessful) {
                        val token = response.body()?.token
                        if (token.isNullOrBlank()) {
                            toast(getString(R.string.empty_token_response))
                            return
                        }

                        sessionManager.saveToken(token)
                        toast(getString(R.string.login_successful))
                        openGeneralScreen()
                        finish()
                    } else {
                        toast(ApiErrorParser.message(response) { getString(R.string.request_failed, it) })
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    toast(getString(R.string.network_error, t.message ?: getString(R.string.request_failed, -1)))
                }
            })
    }

    private fun openGeneralScreen() {
        startActivity(Intent(this, GeneralActivity::class.java))
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
