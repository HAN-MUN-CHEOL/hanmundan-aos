package com.sookmyung.hanmundan.ui.myPage

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.Window
import android.widget.ImageView
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textview.MaterialTextView
import com.sookmyung.hanmundan.R
import com.sookmyung.hanmundan.databinding.ActivityMyPageBinding
import com.sookmyung.hanmundan.ui.bookmark.BookmarkActivity
import com.sookmyung.hanmundan.ui.calender.CalenderActivity
import com.sookmyung.hanmundan.ui.main.MainActivity
import com.sookmyung.hanmundan.util.binding.BindingActivity
import com.sookmyung.hanmundan.util.hideKeyboard

class MyPageActivity : BindingActivity<ActivityMyPageBinding>(R.layout.activity_my_page),
    NavigationView.OnNavigationItemSelectedListener {
    private lateinit var resignDialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initClickListener()
        val headerNavigation =
            binding.nvMenu.getHeaderView(0).findViewById<ImageView>(R.id.iv_navigation_navi)
        val btnMenuClose = headerNavigation.findViewById<ImageView>(R.id.iv_navigation_navi)

        initNavi(btnMenuClose)
    }

    private fun initNavi(btnMenuClose: ImageView) {
        resignDialog = Dialog(this)
        resignDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        resignDialog.setContentView(R.layout.dialog_frame)

        binding.nvMenu.setNavigationItemSelectedListener(this)

        binding.ivMyPageNavi.setOnClickListener {
            binding.dlMyPage.openDrawer(GravityCompat.END)
        }
        btnMenuClose.setOnClickListener {
            binding.dlMyPage.closeDrawers()
        }
    }

    private fun initClickListener() {
        initTextClickListener()
        initChangeNicknameClickListener()
        initResignClickListener()
        initHideKeyBoard()
    }

    private fun initHideKeyBoard() {
        binding.root.setOnClickListener { view ->
            hideKeyboard(view)
        }
    }

    private fun initResignClickListener() {
        binding.tvMyPageMenuResign.setOnClickListener {
            showDialog()
        }
    }

    private fun initTextClickListener() {
        binding.tvMyPageMenuPasswordChange.setOnClickListener {
            val intentToPasswordChange = Intent(this, ChangePasswordActivity::class.java)
            startActivity(intentToPasswordChange)
        }
    }

    private fun initChangeNicknameClickListener() {
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_main -> {
                val intentToMain = Intent(this, MainActivity::class.java)
                startActivity(intentToMain)
                finish()
                binding.dlMyPage.closeDrawers()
            }

            R.id.menu_item_calendar -> {
                val intentToCalender = Intent(this, CalenderActivity::class.java)
                startActivity(intentToCalender)
                finish()
                binding.dlMyPage.closeDrawers()
            }

            R.id.menu_item_bookmark -> {
                val intentToBookmark = Intent(this, BookmarkActivity::class.java)
                startActivity(intentToBookmark)
                finish()
                binding.dlMyPage.closeDrawers()
            }

            R.id.menu_item_my_page -> {
                binding.dlMyPage.closeDrawers()
            }
        }
        return false
    }

}