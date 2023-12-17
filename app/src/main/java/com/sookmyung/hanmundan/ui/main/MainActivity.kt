package com.sookmyung.hanmundan.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.sookmyung.hanmundan.R
import com.sookmyung.hanmundan.databinding.ActivityMainBinding
import com.sookmyung.hanmundan.ui.bookmark.BookmarkActivity
import com.sookmyung.hanmundan.ui.calender.CalenderActivity
import com.sookmyung.hanmundan.ui.myPage.MyPageActivity
import com.sookmyung.hanmundan.util.SnackbarCustom
import com.sookmyung.hanmundan.util.hideKeyboard

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val headerNavigation = binding.nvMainMenu.getHeaderView(0)
        val btnMenuClose = headerNavigation.findViewById<ImageView>(R.id.iv_navigation_navi)
        val textMenuNickname = headerNavigation.findViewById<TextView>(R.id.tv_navigation_header_name)
        val intentFromJoinSuccess = intent
        val nickname = intentFromJoinSuccess.getStringExtra("nickname")
        val navigationView = binding.nvMainMenu
        var bookmarkState = false
        var moreMeaningState = false

        binding.ivMainBlankedBookmark.setOnClickListener {
            if (!bookmarkState) {
                binding.ivMainBlankedBookmark.setImageResource(R.drawable.ic_bookmark_fill)
                SnackbarCustom.make(binding.root, "책갈피를 끼웠습니다.").show()
                bookmarkState = true
            } else {
                binding.ivMainBlankedBookmark.setImageResource(R.drawable.ic_bookmark_blank)
                SnackbarCustom.make(binding.root, "책갈피를 뺐습니다.").show()
                bookmarkState = false
            }
        }

        binding.tvMainMoreMeaning.setOnClickListener {
            if (!moreMeaningState) {
                binding.tvMainWordMeaning.text =
                    "1. 인류 사회의 변천과 흥망의 과정. 또는 그 기록.\n\n2. 어떠한 사물이나 사실이 존재해 온 연혁.\n\n3. 자연 현상이 변하여 온 자취.\n\n4. 역으로 쓰는 건물."
                binding.tvMainMoreMeaning.text = "접기"
                moreMeaningState = true
            } else {
                binding.tvMainWordMeaning.text =
                    "1. 인류 사회의 변천과 흥망의 과정. 또는 그 기록.\n\n2. 어떠한 사물이나 사실이 존재해 온 연혁."
                binding.tvMainMoreMeaning.text = "더 보기"
                moreMeaningState = false
            }
        }

        navigationView.setNavigationItemSelectedListener(this)

        textMenuNickname.text = "$nickname 님"

        binding.ivMainNavi.setOnClickListener {
            binding.dlMain.openDrawer(GravityCompat.END)
        }

        btnMenuClose.setOnClickListener {
            binding.dlMain.closeDrawers()
        }

        binding.root.setOnClickListener { view ->
            hideKeyboard(view)
        }

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_main -> {
                binding.dlMain.closeDrawers()
            }

            R.id.menu_item_calendar -> {
                val intentToCalender = Intent(this, CalenderActivity::class.java)
                startActivity(intentToCalender)
                binding.dlMain.closeDrawers()
            }

            R.id.menu_item_bookmark -> {
                val intentToBookmark = Intent(this, BookmarkActivity::class.java)
                startActivity(intentToBookmark)
                binding.dlMain.closeDrawers()
            }

            R.id.menu_item_my_page -> {
                val intentToMyPage = Intent(this, MyPageActivity::class.java)
                startActivity(intentToMyPage)
                binding.dlMain.closeDrawers()
            }
        }
        return false
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (binding.dlMain.isDrawerOpen(GravityCompat.END)) {
                binding.dlMain.closeDrawers()
            } else {
                finish()
            }
        }
    }
}
