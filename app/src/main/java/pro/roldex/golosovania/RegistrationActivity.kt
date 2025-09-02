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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val etName = binding.name
        val etSurname = binding.surname
        val etEmail = binding.editTextEmail
        val etUsername = binding.editTextEmail
        val etLogin = binding.editTextEmail
        val etPassword = binding.editTextPassword

        binding.signUpBtn.setOnClickListener {
            val login = etLogin.text.toString()
            val password = etPassword.text.toString()
            val email = etEmail.text.toString()
            val username = etUsername.text.toString()
            val name = etName.text.toString()
            val surname = etSurname.text.toString()
            if (login.isNotEmpty() && password.isNotEmpty()) {
                registerUser(login, password, email, username, name, surname)
            } else {
                Toast.makeText(this@RegistrationActivity, "Заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun registerUser(login: String, password: String, email: String, username: String, name: String, surname: String) {
        val call = ApiClient.authApi.register(RegisterRequest(login, password, email, username, name, surname))
        call.enqueue(object : Callback<AuthResponse> {
            override fun onResponse (call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@RegistrationActivity, "Вы успешно зарегисрировались!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@RegistrationActivity, LoginActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this@RegistrationActivity, "Ошибка регистрации", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure (call: Call<AuthResponse>, t: Throwable) {
                Toast.makeText(this@RegistrationActivity, "Ошибка сети ${t.message}", Toast.LENGTH_SHORT).show()
            }

        })
    }
}