package com.sookmyung.hanmundan.ui.joinSuccess

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.sookmyung.hanmundan.R
import com.sookmyung.hanmundan.databinding.ActivityJoinSuccessBinding
import com.sookmyung.hanmundan.ui.main.MainActivity
import com.sookmyung.hanmundan.util.binding.BindingActivity


class JoinSuccessActivity : BindingActivity<ActivityJoinSuccessBinding>(R.layout.activity_join_success) {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intentFromJoin = intent
        val nickname = intentFromJoin.getStringExtra("nickname")

        binding.tvJoinSuccessNickname.text = "$nickname 님"

        Handler(Looper.getMainLooper()).postDelayed({
            val intentToMain = Intent(this, MainActivity::class.java)
            intentToMain.putExtra("nickname", nickname)
            startActivity(intentToMain)
            finish()
        }, 1000)
    }
}