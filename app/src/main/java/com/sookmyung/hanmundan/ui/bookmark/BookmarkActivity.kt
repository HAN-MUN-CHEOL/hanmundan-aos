package com.sookmyung.hanmundan.ui.bookmark

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.sookmyung.hanmundan.R
import com.sookmyung.hanmundan.databinding.ActivityBookmarkBinding
import com.sookmyung.hanmundan.model.DailyRecord
import com.sookmyung.hanmundan.ui.calender.CalenderActivity
import com.sookmyung.hanmundan.ui.main.MainActivity
import com.sookmyung.hanmundan.ui.myPage.MyPageActivity
import com.sookmyung.hanmundan.util.binding.BindingActivity
import com.sookmyung.hanmundan.util.hideKeyboard

class BookmarkActivity : BindingActivity<ActivityBookmarkBinding>(R.layout.activity_bookmark),
    NavigationView.OnNavigationItemSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val item = ArrayList<DailyRecord>()
        item.add(DailyRecord("하루", "하루하루 지나가면 바람결에 실려오는 나의 꿈들이 나를 채우고", "2023.10.12", false))
        item.add(DailyRecord("하늘", "하늘을 날아가는 기분이야", "2023.10.12", false))
        item.add(DailyRecord("하루살이", "이제 난 하루살이 하루하루 내일도 잃어버린 채", "2023.10.12", false))

        val recyclerView: RecyclerView = binding.rvBookmarkList
        val adapter = RecyclerViewAdapter(item)
        recyclerView.adapter = adapter

        val headerNavigation = binding.nvMenu.getHeaderView(0)
        val btnMenuClose = headerNavigation.findViewById<ImageView>(R.id.iv_navigation_navi)
        val navigationView = binding.nvMenu
        val textMenuNickname =
            headerNavigation.findViewById<TextView>(R.id.tv_navigation_header_name)
        val spf: SharedPreferences =
            applicationContext.getSharedPreferences("user", Context.MODE_PRIVATE)
        val nickname = spf.getString("nickname", "")
        navigationView.setNavigationItemSelectedListener(this)
        initMenuNickname(textMenuNickname, nickname)
        initNavi(btnMenuClose)
        initHideKeyBoard()
    }

    @SuppressLint("SetTextI18n")
    private fun initMenuNickname(textMenuNickname: TextView, nickname: String?) {
        textMenuNickname.text = "$nickname 님"
    }

    private fun initNavi(btnMenuClose: ImageView) {
        binding.nvMenu.setNavigationItemSelectedListener(this)
        binding.ivBookmarkNavi.setOnClickListener {
            binding.dlBookmark.openDrawer(GravityCompat.END)
        }
        btnMenuClose.setOnClickListener {
            binding.dlBookmark.closeDrawers()
        }
    }

    private fun initHideKeyBoard() {
        binding.root.setOnClickListener { view ->
            hideKeyboard(view)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_main -> {
                val intentToMain = Intent(this, MainActivity::class.java)
                startActivity(intentToMain)
                finish()
                binding.dlBookmark.closeDrawers()
            }

            R.id.menu_item_calendar -> {
                val intentToCalender = Intent(this, CalenderActivity::class.java)
                startActivity(intentToCalender)
                finish()
                binding.dlBookmark.closeDrawers()
            }

            R.id.menu_item_bookmark -> {
                binding.dlBookmark.closeDrawers()
            }

            R.id.menu_item_my_page -> {
                val intentToMyPage = Intent(this, MyPageActivity::class.java)
                startActivity(intentToMyPage)
                finish()
                binding.dlBookmark.closeDrawers()
            }
        }
        return false
    }
}