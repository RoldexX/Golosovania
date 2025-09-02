package pro.roldex.golosovania

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import pro.roldex.golosovania.databinding.ActivityGeneralBinding

class GeneralActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGeneralBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGeneralBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.burger.setOnClickListener {
            val intent = Intent(this@GeneralActivity, ProfileActivity::class.java)
            startActivity(intent)
        }


    }
}