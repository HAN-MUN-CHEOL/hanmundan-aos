package com.sookmyung.hanmundan.ui.lockSetting

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import com.sookmyung.hanmundan.R
import com.sookmyung.hanmundan.databinding.ActivityLockBinding
import com.sookmyung.hanmundan.ui.joinSuccess.JoinSuccessActivity
import com.sookmyung.hanmundan.util.binding.BindingActivity

class LockSettingActivity : BindingActivity<ActivityLockBinding>(R.layout.activity_lock) {
    private var password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val spf: SharedPreferences =
            applicationContext.getSharedPreferences("user", Context.MODE_PRIVATE)

        binding.tvLockTitle.text = "자물쇠 설정하기"
        binding.tvLockGuide.text = "당신의 글을 비밀스럽게 보관해보세요."

        binding.btnLock0.setOnClickListener { updatePassword("0", spf) }
        binding.btnLock1.setOnClickListener { updatePassword("1", spf) }
        binding.btnLock2.setOnClickListener { updatePassword("2", spf) }
        binding.btnLock3.setOnClickListener { updatePassword("3", spf) }
        binding.btnLock4.setOnClickListener { updatePassword("4", spf) }
        binding.btnLock5.setOnClickListener { updatePassword("5", spf) }
        binding.btnLock6.setOnClickListener { updatePassword("6", spf) }
        binding.btnLock7.setOnClickListener { updatePassword("7", spf) }
        binding.btnLock8.setOnClickListener { updatePassword("8", spf) }
        binding.btnLock9.setOnClickListener { updatePassword("9", spf) }
        binding.btnLockDeleteNumber.setOnClickListener { deleteLastDigit() }
    }

    private fun updatePassword(digit: String, spf: SharedPreferences) {
        password += digit
        changePasswordDot()
        if (password.length == 4) {
            val success = spf.edit().putString("password", password).commit()
            if (success) {
                val intentToSuccess = Intent(this, JoinSuccessActivity::class.java)
                startActivity(intentToSuccess)
                finish()
            }
        }
    }

    private fun deleteLastDigit() {
        if (password.isNotEmpty()) {
            password = password.substring(0, password.length - 1)
        }
        changePasswordDot()
    }

    private fun changePasswordDot() {
        binding.ivLockPin1.setImageResource(getDotResource(0))
        binding.ivLockPin2.setImageResource(getDotResource(1))
        binding.ivLockPin3.setImageResource(getDotResource(2))
        binding.ivLockPin4.setImageResource(getDotResource(3))
    }

    private fun getDotResource(index: Int): Int {
        val dot80ResourceId = R.drawable.shape_gray_dot_80
        val whiteDotResourceId = R.drawable.shape_white_dot

        return if (index < password.length) whiteDotResourceId else dot80ResourceId
    }
}