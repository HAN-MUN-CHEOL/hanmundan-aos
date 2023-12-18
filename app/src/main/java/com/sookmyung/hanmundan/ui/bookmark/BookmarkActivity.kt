package com.sookmyung.hanmundan.ui.bookmark

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.GravityCompat
import com.google.android.gms.tasks.Task
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import com.sookmyung.hanmundan.R
import com.sookmyung.hanmundan.databinding.ActivityBookmarkBinding
import com.sookmyung.hanmundan.model.DailyRecord
import com.sookmyung.hanmundan.ui.calender.CalenderActivity
import com.sookmyung.hanmundan.ui.main.MainActivity
import com.sookmyung.hanmundan.ui.myPage.MyPageActivity
import com.sookmyung.hanmundan.util.binding.BindingActivity
import com.sookmyung.hanmundan.util.hideKeyboard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class BookmarkActivity : BindingActivity<ActivityBookmarkBinding>(R.layout.activity_bookmark),
    NavigationView.OnNavigationItemSelectedListener {

    private lateinit var databaseReal: DatabaseReference
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private lateinit var userId: String
    private var itemList = mutableListOf<DailyRecord>()
    private val adapter: RecyclerViewAdapter?
        get() = binding.rvBookmarkList.adapter as? RecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        databaseReal =
            Firebase.database("https://hanmundan-default-rtdb.asia-southeast1.firebasedatabase.app/").reference
        coroutineScope.launch {
            initWord()
            initAdapter()
        }

        val headerNavigation = binding.nvMenu.getHeaderView(0)
        val btnMenuClose = headerNavigation.findViewById<ImageView>(R.id.iv_navigation_navi)
        val navigationView = binding.nvMenu
        val textMenuNickname =
            headerNavigation.findViewById<TextView>(R.id.tv_navigation_header_name)

        val spf: SharedPreferences =
            applicationContext.getSharedPreferences("user", Context.MODE_PRIVATE)
        val nickname = spf.getString("nickname", "")
        userId = spf.getString("userId", "").toString()

        navigationView.setNavigationItemSelectedListener(this)
        initMenuNickname(textMenuNickname, nickname)
        initNavi(btnMenuClose)
        initHideKeyBoard()

        binding.rvBookmarkList.adapter = RecyclerViewAdapter { pos, item ->
            val dailyRecord = item.copy(bookmark = !(item.bookmark))
            adapter?.updateItemChange(pos)
            setDocument(
                DailyRecord(
                    dailyRecord.word,
                    dailyRecord.sentence,
                    dailyRecord.date,
                    dailyRecord.bookmark
                )
            )
        }
        searchEvent()
    }

    private fun searchEvent() {
        binding.etBookmarkSearch.setOnKeyListener { _, keyCode, _ ->
            when (keyCode) {
                KeyEvent.KEYCODE_ENTER -> {
                    coroutineScope.launch {
                        findWord()
                    }
                    return@setOnKeyListener true
                }

                KeyEvent.KEYCODE_DEL -> {
                    binding.etBookmarkSearch.text.clear()
                    adapter?.submitList(itemList)
                    return@setOnKeyListener true
                }
            }
            return@setOnKeyListener false
        }
    }

    private fun setDocument(data: DailyRecord) {
        databaseReal.child(userId).child(data.date.replace(Regex("\\."), "")).setValue(data)
            .addOnSuccessListener {
                Log.e("hmm", "setDocument success")
            }
            .addOnFailureListener {
                Log.e("hmm", "setDocument fail")
            }
    }

    private fun initAdapter() {
        adapter?.submitList(itemList)
    }

    private suspend fun initWord() {
        try {
            val databaseReference = databaseReal.child(userId)
            val dataSnapshot = databaseReference.get().await()

            for (childSnapshot in dataSnapshot.children) {
                val isBookmarked = childSnapshot.child("bookmark").getValue(Boolean::class.java)
                if (isBookmarked == true) {
                    val word = childSnapshot.child("word").getValue(String::class.java)!!
                    val sentence =
                        childSnapshot.child("sentence").getValue(String::class.java) ?: ""
                    val date = childSnapshot.child("date").getValue(String::class.java) ?: ""
                    itemList.add(DailyRecord(word, sentence, date, isBookmarked))
                }
            }
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

    private suspend fun findWord() {
        try {
            val databaseReference = databaseReal.child(userId)
            val dataSnapshot = databaseReference.get().await()
            for (childSnapshot in dataSnapshot.children) {
                val keyword = binding.etBookmarkSearch.text.toString()
                val word = childSnapshot.child("word").getValue(String::class.java).toString()
                if (keyword == word) {
                    val sentence =
                        childSnapshot.child("sentence").getValue(String::class.java) ?: ""
                    val date = childSnapshot.child("date").getValue(String::class.java) ?: ""
                    val isBookmarked =
                        childSnapshot.child("bookmark").getValue(Boolean::class.java) ?: true
                    val result = mutableListOf<DailyRecord>()
                    result.add(DailyRecord(word, sentence, date, isBookmarked))
                    adapter?.submitList(result)
                    break
                }
            }
        } catch (exception: Exception) {
            Log.d("hmm", "Error getting data: ", exception)
        }
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

