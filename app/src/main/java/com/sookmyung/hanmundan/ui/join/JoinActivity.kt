package com.sookmyung.hanmundan.ui.join

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputFilter
import androidx.appcompat.app.AppCompatActivity
import com.sookmyung.hanmundan.databinding.ActivityJoinBinding
import com.sookmyung.hanmundan.ui.lockSetting.LockSettingActivity
import com.sookmyung.hanmundan.util.SnackbarCustom
import com.sookmyung.hanmundan.util.hideKeyboard
import java.util.regex.Pattern

class JoinActivity : AppCompatActivity() {
    val binding: ActivityJoinBinding by lazy {
        ActivityJoinBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val spf: SharedPreferences =
            applicationContext.getSharedPreferences("user", Context.MODE_PRIVATE)

        val filterAlphaNumSpace = arrayOf(InputFilter { source, start, end, dest, dstart, dend ->
            val ps = Pattern.compile("^[ㄱ-ㅣ가-힣a-zA-Z0-9]+$")
            if (!ps.matcher(source).matches()) {
                ""
            } else source
        }, InputFilter.LengthFilter(6))
        binding.etJoinNameInput.filters = filterAlphaNumSpace

        binding.root.setOnClickListener { view ->
            hideKeyboard(view)
        }

        binding.ivJoinNameCheck.setOnClickListener { view ->
            hideKeyboard(view)
            val etText = binding.etJoinNameInput.text.toString()
            if (etText.isEmpty()) {
                SnackbarCustom.make(binding.root, "당신의 이름을 알고 싶어요.").show()
            } else {
                val success = spf.edit().putString("nickname", etText).commit()
                if (success) {
                    val intentToLockSetting = Intent(this, LockSettingActivity::class.java)
                    startActivity(intentToLockSetting)
                    finish()
                }
            }
        }
    }
}