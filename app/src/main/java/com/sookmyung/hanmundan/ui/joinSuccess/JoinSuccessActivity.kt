package com.sookmyung.hanmundan.ui.joinSuccess

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.sookmyung.hanmundan.R
import com.sookmyung.hanmundan.databinding.ActivityJoinSuccessBinding
import com.sookmyung.hanmundan.ui.main.MainActivity
import com.sookmyung.hanmundan.util.binding.BindingActivity


class JoinSuccessActivity :
    BindingActivity<ActivityJoinSuccessBinding>(R.layout.activity_join_success) {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val spf: SharedPreferences = applicationContext.getSharedPreferences("user",Context.MODE_PRIVATE)
        val nickname = spf.getString("nickname", "")
        binding.tvJoinSuccessNickname.text = "$nickname 님"

        Handler(Looper.getMainLooper()).postDelayed({
            val intentToMain = Intent(this, MainActivity::class.java)
            startActivity(intentToMain)
            finish()
        }, 1000)
    }
}