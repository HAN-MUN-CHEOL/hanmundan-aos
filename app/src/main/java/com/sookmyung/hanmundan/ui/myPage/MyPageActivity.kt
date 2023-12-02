package com.sookmyung.hanmundan.ui.myPage

import android.content.Intent
import android.os.Bundle
import com.sookmyung.hanmundan.R
import com.sookmyung.hanmundan.databinding.ActivityMyPageBinding
import com.sookmyung.hanmundan.util.binding.BindingActivity

class MyPageActivity : BindingActivity<ActivityMyPageBinding>(R.layout.activity_my_page) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initTextClickListener()
    }

    private fun initTextClickListener() {
        changeNicknameClickListener()
    }

    private fun changeNicknameClickListener() {
        binding.tvMyPageMenuNicknameChange.setOnClickListener {
            val intentToChangeNickname = Intent(this, ChangeNickNameActivity::class.java)
            startActivity(intentToChangeNickname)
        }
    }
}