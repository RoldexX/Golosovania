package pro.roldex.golosovania

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import pro.roldex.golosovania.databinding.ActivityChangePasswordBinding

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.burger.setOnClickListener {
            val intent = Intent(this@ChangePasswordActivity, ProfileActivity::class.java)
            startActivity(intent)
        }

        binding.saveChangePassword.setOnClickListener {
            val intent = Intent(this@ChangePasswordActivity, ProfileActivity::class.java)
            startActivity(intent)
        }


    }
}