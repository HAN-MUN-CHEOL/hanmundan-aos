package com.sookmyung.hanmundan.ui.join

import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sookmyung.hanmundan.databinding.ActivityJoinBinding
import com.sookmyung.hanmundan.ui.joinSuccess.JoinSuccessActivity
import java.util.regex.Pattern

class JoinActivity : AppCompatActivity() {
    val binding: ActivityJoinBinding by lazy {
        ActivityJoinBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val etText = binding.etJoinNameInput.text.toString()

        /** 문자열필터(EditText Filter) */
        val filterAlphaNumSpace = arrayOf(InputFilter { source, start, end, dest, dstart, dend ->
            /*
                [요약 설명]
                1. 정규식 패턴 ^[a-z] : 영어 소문자 허용
                2. 정규식 패턴 ^[A-Z] : 영어 대문자 허용
                3. 정규식 패턴 ^[ㄱ-ㅣ가-힣] : 한글 허용
                4. 정규식 패턴 ^[0-9] : 숫자 허용
                5. 정규식 패턴 ^[ ] or ^[\\s] : 공백 허용
            */
            val ps = Pattern.compile("^[ㄱ-ㅣ가-힣a-zA-Z0-9]+$")
            if (!ps.matcher(source).matches()) {
                ""
            } else source
        }, InputFilter.LengthFilter(6))
        binding.etJoinNameInput.filters = filterAlphaNumSpace

        binding.ivJoinNameCheck.setOnClickListener {
            if (etText.isEmpty()) {
                Toast.makeText(this, "당신의 이름을 알고 싶어요.", Toast.LENGTH_LONG).show()
            } else {
                val intentToSuccess = Intent(this, JoinSuccessActivity::class.java)
                startActivity(intentToSuccess)
                finish()
            }
        }
    }
}