package com.sookmyung.hanmundan.ui.calender

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.sookmyung.hanmundan.R
import com.sookmyung.hanmundan.databinding.ActivityCalenderBinding
import com.sookmyung.hanmundan.ui.bookmark.BookmarkActivity
import com.sookmyung.hanmundan.ui.main.MainActivity
import com.sookmyung.hanmundan.ui.myPage.MyPageActivity
import com.sookmyung.hanmundan.ui.rewrite.RewriteActivity
import com.sookmyung.hanmundan.util.binding.BindingActivity
import java.util.Calendar
import kotlin.properties.Delegates

class CalenderActivity : BindingActivity<ActivityCalenderBinding>(R.layout.activity_calender),
    NavigationView.OnNavigationItemSelectedListener {
    private val viewModel: CalenderViewModel by viewModels()
    private var date by Delegates.notNull<Long>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.vm = viewModel
        val headerNavigation =
            binding.nvMenu.getHeaderView(0).findViewById<ImageView>(R.id.iv_navigation_navi)
        val btnMenuClose = headerNavigation.findViewById<ImageView>(R.id.iv_navigation_navi)
        date = binding.cvCalender.date

        initNavi(btnMenuClose)
        initClickListener()
    }

    private fun initClickListener() {
        binding.btnCalenderRetirement.setOnClickListener {
            val intentToRetirement = Intent(this, RewriteActivity::class.java)
            startActivity(intentToRetirement)
        }
        binding.ivCalenderBookmark.setOnClickListener { viewModel.updateBookmark() }
        initCalenderDateClickListener()
    }

    private fun initCalenderDateClickListener() {
        date = binding.cvCalender.date
        binding.cvCalender.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)

            if (selectedDate.timeInMillis != date) {
                date = selectedDate.timeInMillis
                Log.e("kang", "$year $month $dayOfMonth")
            }
        }
    }

    private fun initNavi(btnMenuClose: ImageView) {
        binding.nvMenu.setNavigationItemSelectedListener(this)
        binding.ivCalenderNavi.setOnClickListener {
            binding.dlCalender.openDrawer(GravityCompat.END)
        }
        btnMenuClose.setOnClickListener {
            binding.dlCalender.closeDrawers()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_main -> {
                val intentToMain = Intent(this, MainActivity::class.java)
                startActivity(intentToMain)
                finish()
                binding.dlCalender.closeDrawers()
            }

            R.id.menu_item_calendar -> {
                binding.dlCalender.closeDrawers()
            }

            R.id.menu_item_bookmark -> {
                val intentToBookmark = Intent(this, BookmarkActivity::class.java)
                startActivity(intentToBookmark)
                finish()
                binding.dlCalender.closeDrawers()
            }

            R.id.menu_item_my_page -> {
                val intentToMyPage = Intent(this, MyPageActivity::class.java)
                startActivity(intentToMyPage)
                finish()
                binding.dlCalender.closeDrawers()
            }
        }
        return false
    }
}
