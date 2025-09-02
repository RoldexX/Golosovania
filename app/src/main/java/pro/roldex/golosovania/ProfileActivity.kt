package pro.roldex.golosovania

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import pro.roldex.golosovania.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.changeProfileDataBtn.setOnClickListener {
            val intent = Intent(this@ProfileActivity, ChangeProfileActivity::class.java)
            startActivity(intent)
        }

        binding.changePasswordBtn.setOnClickListener {
            val intent = Intent(this@ProfileActivity, ChangePasswordActivity::class.java)
            startActivity(intent)
        }

        binding.burger.setOnClickListener {
            val intent = Intent(this@ProfileActivity, GeneralActivity::class.java)
            startActivity(intent)
        }



        binding.buttonExit.setOnClickListener {
            val intent = Intent(this@ProfileActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }
}