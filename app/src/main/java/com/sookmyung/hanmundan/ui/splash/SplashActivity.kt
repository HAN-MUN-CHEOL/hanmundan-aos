package com.sookmyung.hanmundan.ui.splash

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.sookmyung.hanmundan.R
import com.sookmyung.hanmundan.databinding.ActivitySplashBinding
import com.sookmyung.hanmundan.ui.join.JoinActivity
import com.sookmyung.hanmundan.ui.lock.LockActivity
import com.sookmyung.hanmundan.util.binding.BindingActivity

class SplashActivity : BindingActivity<ActivitySplashBinding>(R.layout.activity_splash) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val spf: SharedPreferences =
            applicationContext.getSharedPreferences("user", Context.MODE_PRIVATE)
        val nickname = spf.getString("userId", "")

        if (nickname.isNullOrEmpty()) {
            Handler(Looper.getMainLooper()).postDelayed({
                val intentToJoin = Intent(this, JoinActivity::class.java)
                startActivity(intentToJoin)
                finish()
            }, 2000)

        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                val intentToLock = Intent(this, LockActivity::class.java)
                startActivity(intentToLock)
                finish()
            }, 2000)

        }
    }
}