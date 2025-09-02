package pro.roldex.golosovania

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import pro.roldex.golosovania.databinding.ActivityChangeProfileBinding

class ChangeProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangeProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.burger.setOnClickListener {
            val intent = Intent(this@ChangeProfileActivity, ProfileActivity::class.java)
            startActivity(intent)
        }

        binding.saveChangeProfile.setOnClickListener {
            val intent = Intent(this@ChangeProfileActivity, ProfileActivity::class.java)
            startActivity(intent)
        }


    }
}