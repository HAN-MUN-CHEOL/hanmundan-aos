package com.sookmyung.hanmundan.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.sookmyung.hanmundan.R
import com.sookmyung.hanmundan.databinding.ActivityMainBinding
import com.sookmyung.hanmundan.ui.calender.CalenderActivity
import com.sookmyung.hanmundan.ui.myPage.MyPageActivity

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
        val navigationView = binding.nvMainMenu
        var bookmarkState = false
        var moreMeaningState = false

        binding.ivMainBlankedBookmark.setOnClickListener {
            if (!bookmarkState) {
                binding.ivMainBlankedBookmark.setImageResource(R.drawable.ic_bookmark_fill)
                Toast.makeText(this, "책갈피를 끼웠습니다.", Toast.LENGTH_SHORT).show()
                bookmarkState = true
            } else {
                binding.ivMainBlankedBookmark.setImageResource(R.drawable.ic_bookmark_blank)
                Toast.makeText(this, "책갈피를 뺐습니다.", Toast.LENGTH_SHORT).show()
                bookmarkState = false
            }
        }

        binding.tvMainMoreMeaning.setOnClickListener {
            if (!moreMeaningState) {
                binding.tvMainWordMeaning.text =
                    getString(R.string.main_word_meaning) + "\n\n" + getString(R.string.main_word_meaning_add)
                binding.tvMainMoreMeaning.text = "접기"
                moreMeaningState = true
            } else {
                binding.tvMainWordMeaning.text = getString(R.string.main_word_meaning)
                binding.tvMainMoreMeaning.text = "더 보기"
                moreMeaningState = false
            }
        }

        navigationView.setNavigationItemSelectedListener(this)

        binding.ivMainNavi.setOnClickListener {
            binding.dlMain.openDrawer(GravityCompat.END)
        }

        btnMenuClose.setOnClickListener {
            binding.dlMain.closeDrawers()
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
                return true
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
