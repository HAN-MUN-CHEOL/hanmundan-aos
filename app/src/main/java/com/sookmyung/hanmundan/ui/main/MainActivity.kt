package com.sookmyung.hanmundan.ui.main

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textview.MaterialTextView
import com.sookmyung.hanmundan.R
import com.sookmyung.hanmundan.databinding.ActivityMainBinding
import com.sookmyung.hanmundan.ui.bookmark.BookmarkActivity
import com.sookmyung.hanmundan.ui.calender.CalenderActivity
import com.sookmyung.hanmundan.ui.myPage.MyPageActivity
import com.sookmyung.hanmundan.util.SnackbarCustom


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var dialog: Dialog
    var saveWritingState = false
    var lastWriting: String? = null
    var currentWriting: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val headerNavigation = binding.nvMainMenu.getHeaderView(0)
        val btnMenuClose = headerNavigation.findViewById<ImageView>(R.id.iv_navigation_navi)
        val textMenuNickname =
            headerNavigation.findViewById<TextView>(R.id.tv_navigation_header_name)
        val navigationView = binding.nvMainMenu
        var bookmarkState = false
        var moreMeaningState = false
        val spf: SharedPreferences = applicationContext.getSharedPreferences("user", Context.MODE_PRIVATE)
        val nickname = spf.getString("nickname","")

        initClick(bookmarkState, moreMeaningState)
        navigationView.setNavigationItemSelectedListener(this)
        initMenuNickname(textMenuNickname, nickname)
        initDrawer(btnMenuClose)

        dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_change_view)

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private fun initClick(bookmarkState: Boolean, moreMeaningState: Boolean) {
        clickBookmarkButton(bookmarkState)
        clickMoreMeaningButton(moreMeaningState)
        clickSaveButton()
    }

    @SuppressLint("SetTextI18n")
    private fun initMenuNickname(textMenuNickname: TextView, nickname: String?) {
        textMenuNickname.text = "$nickname 님"
    }

    private fun initDrawer(btnMenuClose: ImageView) {
        binding.ivMainNavi.setOnClickListener {
            binding.dlMain.openDrawer(GravityCompat.END)
        }

        btnMenuClose.setOnClickListener {
            binding.dlMain.closeDrawers()
        }
    }

    private fun clickMoreMeaningButton(moreMeaningState: Boolean) {
        var moreMeaningState1 = moreMeaningState
        binding.tvMainMoreMeaning.setOnClickListener {
            if (!moreMeaningState1) {
                binding.tvMainWordMeaning.text =
                    "1. 인류 사회의 변천과 흥망의 과정. 또는 그 기록.\n\n2. 어떠한 사물이나 사실이 존재해 온 연혁.\n\n3. 자연 현상이 변하여 온 자취.\n\n4. 역으로 쓰는 건물."
                binding.tvMainMoreMeaning.text = "접기"
                moreMeaningState1 = true
            } else {
                binding.tvMainWordMeaning.text =
                    "1. 인류 사회의 변천과 흥망의 과정. 또는 그 기록.\n\n2. 어떠한 사물이나 사실이 존재해 온 연혁."
                binding.tvMainMoreMeaning.text = "더 보기"
                moreMeaningState1 = false
            }
        }
    }

    private fun clickBookmarkButton(bookmarkState: Boolean) {
        var bookmarkState1 = bookmarkState
        binding.ivMainBlankedBookmark.setOnClickListener {
            if (!bookmarkState1) {
                binding.ivMainBlankedBookmark.setImageResource(R.drawable.ic_bookmark_fill)
                SnackbarCustom.make(binding.root, "책갈피를 끼웠습니다.").show()
                bookmarkState1 = true
            } else {
                binding.ivMainBlankedBookmark.setImageResource(R.drawable.ic_bookmark_blank)
                SnackbarCustom.make(binding.root, "책갈피를 뺐습니다.").show()
                bookmarkState1 = false
            }
        }
    }

    private fun clickSaveButton() {
        binding.tvMainSaveWriting.setOnClickListener {
            updateWritingValue()
            updateSaveWritingState()
            if (saveWritingState) {
                SnackbarCustom.make(binding.root, "저장되었습니다.").show()
            }
        }
    }

    private fun updateWritingValue() {
        if (lastWriting == null) {
            lastWriting = binding.etMainWriting.text.toString()
            currentWriting = lastWriting
        } else {
            lastWriting = currentWriting
            currentWriting = binding.etMainWriting.text.toString()
        }
    }

    private fun updateSaveWritingState() {
        binding.etMainWriting.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                updateWritingValue()
                saveWritingState = lastWriting == currentWriting
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                updateWritingValue()
                saveWritingState = lastWriting == currentWriting
            }

            override fun afterTextChanged(s: Editable) {
                updateWritingValue()
                saveWritingState = lastWriting == currentWriting
            }
        })
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_main -> {
                binding.dlMain.closeDrawers()
            }

            R.id.menu_item_calendar -> {
                val intentToCalender = Intent(this, CalenderActivity::class.java)
                showDialogOrChangeActivity(intentToCalender)
            }

            R.id.menu_item_bookmark -> {
                val intentToBookmark = Intent(this, BookmarkActivity::class.java)
                showDialogOrChangeActivity(intentToBookmark)
            }

            R.id.menu_item_my_page -> {
                val intentToMyPage = Intent(this, MyPageActivity::class.java)
                showDialogOrChangeActivity(intentToMyPage)
            }
        }
        return false
    }

    private fun showDialogOrChangeActivity(intent: Intent) {
        updateSaveWritingState()
        if (!saveWritingState) {
            dialog.setCancelable(false)
            dialog.show()
            dialog.findViewById<MaterialTextView>(R.id.btn_dialog_no)
                .setOnClickListener { dialog.dismiss() }
            dialog.findViewById<MaterialTextView>(R.id.btn_dialog_yes)
                .setOnClickListener {
                    startActivity(intent)
                    Handler(Looper.getMainLooper()).postDelayed({
                        binding.dlMain.closeDrawers()
                        dialog.dismiss()
                        binding.etMainWriting.text =
                            Editable.Factory.getInstance().newEditable(lastWriting ?: "")
                    }, 500)
                }
        } else {
            startActivity(intent)
            Handler(Looper.getMainLooper()).postDelayed({
                binding.dlMain.closeDrawers()
            }, 500)
        }
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (binding.dlMain.isDrawerOpen(GravityCompat.END)) {
                binding.dlMain.closeDrawers()
            }
            if (!saveWritingState) {
                showDialogBackPressed()
            } else {
                finish()
            }
        }
    }

    private fun showDialogBackPressed() {
        dialog.setCancelable(false)
        dialog.show()
        dialog.findViewById<MaterialTextView>(R.id.btn_dialog_no)
            .setOnClickListener { dialog.dismiss() }
        dialog.findViewById<MaterialTextView>(R.id.btn_dialog_yes)
            .setOnClickListener {
                finish()
                binding.etMainWriting.text =
                    Editable.Factory.getInstance().newEditable(lastWriting)
            }
    }
}
