package com.sookmyung.hanmundan.ui.myPage

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.sookmyung.hanmundan.R
import com.sookmyung.hanmundan.databinding.ActivityChangePasswordBinding
import com.sookmyung.hanmundan.util.SnackbarCustom
import com.sookmyung.hanmundan.util.binding.BindingActivity

class CheckPasswordActivity :
    BindingActivity<ActivityChangePasswordBinding>(R.layout.activity_change_password) {
    private var password = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val spf: SharedPreferences =
            applicationContext.getSharedPreferences("user", Context.MODE_PRIVATE)

        binding.tvChangePasswordTitle.text = "자물쇠 열기"
        binding.tvChangePasswordGuide.text = "기존에 사용하시던 암호를 입력해주세요"

        binding.btnChangePassword0.setOnClickListener { updatePassword("0", spf) }
        binding.btnChangePassword1.setOnClickListener { updatePassword("1", spf) }
        binding.btnChangePassword2.setOnClickListener { updatePassword("2", spf) }
        binding.btnChangePassword3.setOnClickListener { updatePassword("3", spf) }
        binding.btnChangePassword4.setOnClickListener { updatePassword("4", spf) }
        binding.btnChangePassword5.setOnClickListener { updatePassword("5", spf) }
        binding.btnChangePassword6.setOnClickListener { updatePassword("6", spf) }
        binding.btnChangePassword7.setOnClickListener { updatePassword("7", spf) }
        binding.btnChangePassword8.setOnClickListener { updatePassword("8", spf) }
        binding.btnChangePassword9.setOnClickListener { updatePassword("9", spf) }
        binding.btnChangePasswordDeleteNumber.setOnClickListener { deleteLastDigit() }
    }

    private fun updatePassword(digit: String, spf: SharedPreferences) {
        password += digit
        changePasswordDot()
        if (password.length == 4) {
            val pw = spf.getString("password", "")
            if (password == pw) {
                val success = spf.edit().putString("password", password).commit()
                if (success) {
                    val intentToChangePassword = Intent(this, ChangePasswordActivity::class.java)
                    startActivity(intentToChangePassword)
                    finish()
                }
            } else {
                SnackbarCustom.make(binding.root, "비밀번호가 틀렸어요!").show()
                Handler(Looper.getMainLooper()).postDelayed({
                    password = ""
                    changePasswordDot()
                }, 500)
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
        binding.ivChangePasswordPin1.setImageResource(getDotResource(0))
        binding.ivChangePasswordPin2.setImageResource(getDotResource(1))
        binding.ivChangePasswordPin3.setImageResource(getDotResource(2))
        binding.ivChangePasswordPin4.setImageResource(getDotResource(3))
    }

    private fun getDotResource(index: Int): Int {
        val dotGrayResourceId = R.drawable.shape_gray_dot
        val greenDotResourceId = R.drawable.shape_green_dot

        return if (index < password.length) greenDotResourceId else dotGrayResourceId
    }
}