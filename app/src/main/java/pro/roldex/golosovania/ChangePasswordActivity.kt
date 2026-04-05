package pro.roldex.golosovania

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pro.roldex.golosovania.databinding.ActivityChangePasswordBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        sessionManager = SessionManager(this)
        setContentView(binding.root)

        binding.burger.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
            finish()
        }

        binding.saveChangePassword.setOnClickListener {
            val oldPassword = binding.oldPassword.text.toString()
            val newPassword = binding.newPassword.text.toString()
            val repeatPassword = binding.new2Password.text.toString()

            when {
                oldPassword.isBlank() || newPassword.isBlank() || repeatPassword.isBlank() ->
                    toast(getString(R.string.fill_all_fields))

                newPassword != repeatPassword ->
                    toast(getString(R.string.new_passwords_do_not_match))

                else -> updatePassword(oldPassword, newPassword)
            }
        }
    }

    private fun updatePassword(oldPassword: String, newPassword: String) {
        ApiClient.authApi(this).updatePassword(UpdatePasswordRequest(oldPassword, newPassword))
            .enqueue(object : Callback<UpdatePasswordResponse> {
                override fun onResponse(
                    call: Call<UpdatePasswordResponse>,
                    response: Response<UpdatePasswordResponse>
                ) {
                    if (response.isSuccessful) {
                        toast(getString(R.string.password_updated))
                        startActivity(Intent(this@ChangePasswordActivity, ProfileActivity::class.java))
                        finish()
                    } else if (response.code() == 401) {
                        sessionManager.clearSession()
                        redirectToMain()
                    } else {
                        toast(ApiErrorParser.message(response) { getString(R.string.request_failed, it) })
                    }
                }

                override fun onFailure(call: Call<UpdatePasswordResponse>, t: Throwable) {
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
