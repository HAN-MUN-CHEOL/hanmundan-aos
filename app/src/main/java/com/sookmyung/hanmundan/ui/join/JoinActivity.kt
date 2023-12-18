package com.sookmyung.hanmundan.ui.join

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputFilter
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import com.sookmyung.hanmundan.databinding.ActivityJoinBinding
import com.sookmyung.hanmundan.model.DailyRecord
import com.sookmyung.hanmundan.ui.lockSetting.LockSettingActivity
import com.sookmyung.hanmundan.util.SnackbarCustom
import com.sookmyung.hanmundan.util.hideKeyboard
import java.text.SimpleDateFormat
import java.util.Date
import java.util.regex.Pattern

class JoinActivity : AppCompatActivity() {
    val binding: ActivityJoinBinding by lazy {
        ActivityJoinBinding.inflate(layoutInflater)
    }

    private lateinit var databaseReal: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        databaseReal =
            Firebase.database("https://hanmundan-default-rtdb.asia-southeast1.firebasedatabase.app/").reference

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
                spf.edit().putString("userId", generateRandomUserId(15)).apply()
                val success = spf.edit().putString("nickname", etText).commit()
                if (success) {
                    val userId = spf.getString("userId", "")
                    databaseReal.child(userId ?: "").child(getDateId())
                        .setValue(DailyRecord("자유", "", getCurrentDate(), false))
                    val intentToLockSetting = Intent(this, LockSettingActivity::class.java)
                    startActivity(intentToLockSetting)
                    finish()
                }
            }
        }
    }

    fun generateRandomUserId(length: Int): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..length)
            .map { chars.random() }
            .joinToString("")
    }

    @SuppressLint("SimpleDateFormat")
    private fun getDateId(): String {
        val currentDate = System.currentTimeMillis()
        val date = Date(currentDate)
        val dateFormat = SimpleDateFormat("yyyyMMdd").format(date)
        return dateFormat.format(date)
    }

    @SuppressLint("SimpleDateFormat")
    private fun getCurrentDate(): String {
        val currentDate = System.currentTimeMillis()
        val date = Date(currentDate)
        val dateFormat = SimpleDateFormat("yyyy.MM.dd").format(date)
        return dateFormat.format(date)
    }
}