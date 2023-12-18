package com.sookmyung.hanmundan.ui.myPage

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputFilter
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import com.sookmyung.hanmundan.R
import com.sookmyung.hanmundan.databinding.ActivityChangeNicknameBinding
import com.sookmyung.hanmundan.util.SnackbarCustom
import com.sookmyung.hanmundan.util.binding.BindingActivity
import com.sookmyung.hanmundan.util.hideKeyboard
import java.util.regex.Pattern

class ChangeNickNameActivity :
    BindingActivity<ActivityChangeNicknameBinding>(R.layout.activity_change_nickname) {
    private lateinit var databaseReal: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val spf: SharedPreferences =
            applicationContext.getSharedPreferences("user", Context.MODE_PRIVATE)

        databaseReal =
            Firebase.database("https://hanmundan-default-rtdb.asia-southeast1.firebasedatabase.app/").reference

        val filterAlphaNumSpace = arrayOf(InputFilter { source, start, end, dest, dstart, dend ->
            val ps = Pattern.compile("^[ㄱ-ㅣ가-힣a-zA-Z0-9]+$")
            if (!ps.matcher(source).matches()) {
                ""
            } else source
        }, InputFilter.LengthFilter(6))
        binding.etChangeNicknameNameInput.filters = filterAlphaNumSpace

        binding.root.setOnClickListener { view ->
            hideKeyboard(view)
        }

        binding.ivChangeNicknameFeather.setOnClickListener { view ->
            hideKeyboard(view)
            val etText = binding.etChangeNicknameNameInput.text.toString()
            if (etText.isEmpty()) {
                SnackbarCustom.make(binding.root, "당신의 이름을 알고 싶어요.").show()
            } else {
                val success = spf.edit().putString("nickname", etText).commit()
                if (success) {
                    SnackbarCustom.make(binding.root, "성공적으로 이름이 바뀌었어요!").show()
                    finish()
                }
            }
        }
    }
}