package com.sookmyung.hanmundan.ui.main

import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.sookmyung.hanmundan.R
import com.sookmyung.hanmundan.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val headerNavigation = binding.nvMenu.getHeaderView(0)
        val btnMenuClose = headerNavigation.findViewById<ImageView>(R.id.iv_navigation_navi)

        binding.ivMainNavi.setOnClickListener {
            binding.dlMain.openDrawer(GravityCompat.END)
        }

        btnMenuClose.setOnClickListener {
            binding.dlMain.closeDrawers()
        }

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_main -> {
                return true
            }

            R.id.menu_item_calendar -> {
                return true
            }

            R.id.menu_item_bookmark -> {
                return true
            }

            R.id.menu_item_my_page -> {
                return true
            }
        }
        return super.onOptionsItemSelected(item)
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
