package com.sookmyung.hanmundan.ui.myPage

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.GravityCompat
import com.google.android.gms.tasks.Task
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import com.sookmyung.hanmundan.R
import com.sookmyung.hanmundan.databinding.ActivityMyPageBinding
import com.sookmyung.hanmundan.ui.bookmark.BookmarkActivity
import com.sookmyung.hanmundan.ui.calender.CalenderActivity
import com.sookmyung.hanmundan.ui.main.MainActivity
import com.sookmyung.hanmundan.util.binding.BindingActivity
import com.sookmyung.hanmundan.util.hideKeyboard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class MyPageActivity : BindingActivity<ActivityMyPageBinding>(R.layout.activity_my_page),
    NavigationView.OnNavigationItemSelectedListener {
    private lateinit var resignDialog: Dialog
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private lateinit var databaseReal: DatabaseReference
    private lateinit var sentenceInDB: String
    private var featherCount = 0;

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initClickListener()
        val headerNavigation = binding.nvMenu.getHeaderView(0)
        val btnMenuClose = headerNavigation.findViewById<ImageView>(R.id.iv_navigation_navi)
        val navigationView = binding.nvMenu
        val textMenuNickname =
            headerNavigation.findViewById<TextView>(R.id.tv_navigation_header_name)
        val spf: SharedPreferences =
            applicationContext.getSharedPreferences("user", Context.MODE_PRIVATE)
        val nickname = spf.getString("nickname", "")
        binding.tvMyPageNickname.text = "${nickname}님"
        initDialog()
        navigationView.setNavigationItemSelectedListener(this)
        initMenuNickname(textMenuNickname, nickname)
        initNavi(btnMenuClose)

        FirebaseApp.initializeApp(this)
        databaseReal =
            Firebase.database("https://hanmundan-default-rtdb.asia-southeast1.firebasedatabase.app/").reference

        coroutineScope.launch {
            countFeather()
        }
    }

    @SuppressLint("SetTextI18n")
    private suspend fun countFeather() {
        try {
            val databaseReference = databaseReal.child("hanmundan")
            val dataSnapshot = databaseReference.get().await()
            for (childSnapshot in dataSnapshot.children) {
                sentenceInDB =
                    childSnapshot.child("sentence").getValue(String::class.java).toString()
                if (sentenceInDB.isNullOrBlank()) continue
                else featherCount++
            }
            binding.tvMyPageFeatherCount.text = "${featherCount}개"
        } catch (exception: Exception) {
            Log.d("hmm", "Error getting data: ", exception)
        }
    }

    private suspend fun <T> Task<T>.await(): T {
        return suspendCancellableCoroutine { continuation ->
            addOnSuccessListener { result ->
                continuation.resume(result)
            }
            addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initMenuNickname(textMenuNickname: TextView, nickname: String?) {
        textMenuNickname.text = "$nickname 님"
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        val spf: SharedPreferences =
            applicationContext.getSharedPreferences("user", Context.MODE_PRIVATE)
        val nickname = spf.getString("nickname", "")
        val headerNavigation = binding.nvMenu.getHeaderView(0)
        val textMenuNickname =
            headerNavigation.findViewById<TextView>(R.id.tv_navigation_header_name)

        binding.tvMyPageNickname.text = "${nickname}님"
        initMenuNickname(textMenuNickname, nickname)
    }

    private fun initNavi(btnMenuClose: ImageView) {
        binding.nvMenu.setNavigationItemSelectedListener(this)

        binding.ivMyPageNavi.setOnClickListener {
            binding.dlMyPage.openDrawer(GravityCompat.END)
        }
        btnMenuClose.setOnClickListener {
            binding.dlMyPage.closeDrawers()
        }
    }

    private fun initDialog() {
        resignDialog = Dialog(this)
        resignDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        resignDialog.setContentView(R.layout.dialog_frame)
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
            val intentToPasswordChange = Intent(this, CheckPasswordActivity::class.java)
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