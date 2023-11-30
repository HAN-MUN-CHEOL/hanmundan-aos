package com.sookmyung.hanmundan.ui.join

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sookmyung.hanmundan.databinding.ActivityJoinBinding
import com.sookmyung.hanmundan.ui.joinSuccess.JoinSuccessActivity

class JoinActivity : AppCompatActivity() {
    val binding: ActivityJoinBinding by lazy {
        ActivityJoinBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.ibJoinNameCheck.setOnClickListener {
            val intentToSuccess = Intent(this, JoinSuccessActivity::class.java)
            startActivity(intentToSuccess)
            finish()
        }
    }
}