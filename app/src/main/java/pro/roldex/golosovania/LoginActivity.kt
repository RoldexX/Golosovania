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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val etLogin = binding.EmailAddress
        val etPassword = binding.Password

        binding.signInBtn.setOnClickListener {
            val login = etLogin.text.toString()
            val password = etPassword.text.toString()
            if (login.isNotEmpty() && password.isNotEmpty()) {
                loginUser(login, password)
            } else {
                Toast.makeText(this@LoginActivity, "Заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun loginUser(login: String, password: String) {
        val call = ApiClient.authApi.login(LoginRequest(login, password))
        call.enqueue(object : Callback<AuthResponse> {
            override fun onResponse (call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@LoginActivity, "Успешный вход!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@LoginActivity, GeneralActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this@LoginActivity, "Ошибка входа", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure (call: Call<AuthResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Ошибка сети ${t.message}", Toast.LENGTH_SHORT).show()
            }

        })
    }
}