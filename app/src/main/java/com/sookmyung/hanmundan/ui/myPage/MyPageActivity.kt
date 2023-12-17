package com.sookmyung.hanmundan.ui.myPage

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Window
import com.google.android.material.textview.MaterialTextView
import com.sookmyung.hanmundan.R
import com.sookmyung.hanmundan.databinding.ActivityMyPageBinding
import com.sookmyung.hanmundan.util.binding.BindingActivity

class MyPageActivity : BindingActivity<ActivityMyPageBinding>(R.layout.activity_my_page) {
    private lateinit var resignDialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initClickListener()
        resignDialog = Dialog(this)
        resignDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        resignDialog.setContentView(R.layout.dialog_frame)
    }

    private fun initClickListener(){
        initTextClickListener()
        initchangeNicknameClickListener()
        initResignClickListener()
    }

    private fun initResignClickListener() {
        binding.tvMyPageMenuResign.setOnClickListener {
            showDialog()
        }
    }

    private fun initTextClickListener() {
        binding.tvMyPageMenuPasswordChange.setOnClickListener{
            val intentToPasswordChange = Intent(this, ChangePasswordActivity::class.java)
            startActivity(intentToPasswordChange)
        }
    }

    private fun initchangeNicknameClickListener() {
        binding.tvMyPageMenuNicknameChange.setOnClickListener {
            val intentToChangeNickname = Intent(this, ChangeNickNameActivity::class.java)
            startActivity(intentToChangeNickname)
        }
    }

    private fun showDialog() {
        resignDialog.setCancelable(false)
        resignDialog.show()
        val question = resignDialog.findViewById<MaterialTextView>(R.id.tv_dialog_change_view)
        question.text = "정말로 잉크를 엎지르시겠어요?"
        resignDialog.findViewById<MaterialTextView>(R.id.btn_dialog_no)
            .setOnClickListener { resignDialog.dismiss() }
        resignDialog.findViewById<MaterialTextView>(R.id.btn_dialog_yes)
            .setOnClickListener { finish() }
    }
}