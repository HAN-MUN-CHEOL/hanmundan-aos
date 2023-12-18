package com.sookmyung.hanmundan.ui.calender

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.view.GravityCompat
import com.google.android.gms.tasks.Task
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import com.sookmyung.hanmundan.R
import com.sookmyung.hanmundan.databinding.ActivityCalenderBinding
import com.sookmyung.hanmundan.model.DailyRecord
import com.sookmyung.hanmundan.ui.bookmark.BookmarkActivity
import com.sookmyung.hanmundan.ui.main.MainActivity
import com.sookmyung.hanmundan.ui.myPage.MyPageActivity
import com.sookmyung.hanmundan.ui.rewrite.RewriteActivity
import com.sookmyung.hanmundan.util.SnackbarCustom
import com.sookmyung.hanmundan.util.binding.BindingActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.properties.Delegates

class CalenderActivity : BindingActivity<ActivityCalenderBinding>(R.layout.activity_calender),
    NavigationView.OnNavigationItemSelectedListener {
    private val viewModel: CalenderViewModel by viewModels()
    private var date by Delegates.notNull<Long>()
    private var realDate: String = getCurrentDate()

    private lateinit var databaseReal: DatabaseReference
    private lateinit var dateInDatabase: String
    private lateinit var todayDocumentId: String
    private lateinit var todayWord: String
    private var todayWordSentence: String? = null
    private var todayWordBookmarkState: Boolean = false
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var bookmarkState = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.vm = viewModel
        val headerNavigation = binding.nvMenu.getHeaderView(0)
        val btnMenuClose = headerNavigation.findViewById<ImageView>(R.id.iv_navigation_navi)
        val navigationView = binding.nvMenu
        val textMenuNickname =
            headerNavigation.findViewById<TextView>(R.id.tv_navigation_header_name)
        val spf: SharedPreferences =
            applicationContext.getSharedPreferences("user", Context.MODE_PRIVATE)
        val nickname = spf.getString("nickname", "")

        date = binding.cvCalender.date
        navigationView.setNavigationItemSelectedListener(this)
        initMenuNickname(textMenuNickname, nickname)
        initNavi(btnMenuClose)
        initClickListener()

        FirebaseApp.initializeApp(this)
        databaseReal =
            Firebase.database("https://hanmundan-default-rtdb.asia-southeast1.firebasedatabase.app/").reference

        coroutineScope.launch {
            initWord()
            initUI()
            initClick()
        }
    }

    private fun initUI() {
        binding.tvCalenderWordTitle.text = todayWord
        binding.tvCalenderSentence.text =
            Editable.Factory.getInstance().newEditable(todayWordSentence)
        bookmarkState = todayWordBookmarkState
    }

    private fun initClick() {
        clickBookmarkButton()
    }

    private fun clickBookmarkButton() {
        binding.ivCalenderBookmark.setOnClickListener {
            bookmarkState = !bookmarkState
            if (bookmarkState) {
                binding.ivCalenderBookmark.setImageResource(R.drawable.ic_bookmark_fill)
                SnackbarCustom.make(binding.root, "책갈피를 끼웠습니다.").show()
            } else {
                binding.ivCalenderBookmark.setImageResource(R.drawable.ic_bookmark_blank)
                SnackbarCustom.make(binding.root, "책갈피를 뺐습니다.").show()
            }
            setDocument(
                DailyRecord(
                    todayWord,
                    todayWordSentence ?: "",
                    dateInDatabase,
                    bookmarkState
                )
            )
        }
    }

    private fun setDocument(data: DailyRecord) {
        databaseReal.child("hanmundan").child(todayDocumentId).setValue(data)
            .addOnSuccessListener {
                Log.e("hmm", "setDocument success")
            }
            .addOnFailureListener {
                Log.e("hmm", "setDocument fail")
            }
    }

    private suspend fun initWord() {
        try {
            val databaseReference = databaseReal.child("hanmundan")
            val dataSnapshot = databaseReference.get().await()
            for (childSnapshot in dataSnapshot.children) {
                dateInDatabase = childSnapshot.child("date").getValue(String::class.java).toString()
                if (dateInDatabase == realDate) {
                    todayDocumentId = childSnapshot.key!!
                    todayWord = childSnapshot.child("word").getValue(String::class.java)!!
                    todayWordSentence =
                        childSnapshot.child("sentence").getValue(String::class.java) ?: ""
                    bookmarkState =
                        childSnapshot.child("bookmark").getValue(Boolean::class.java) ?: false
                    viewModel.updateDailyRecord(
                        DailyRecord(
                            todayWord,
                            todayWordSentence ?: "",
                            realDate,
                            bookmarkState
                        )
                    )
                    break
                }
            }
        } catch (exception: Exception) {
            Log.d("hmm", "Error getting data: ", exception)
        }
    }

    private fun getCurrentDate(): String {
        val currentDate = System.currentTimeMillis()
        val date = Date(currentDate)
        val dateFormat = SimpleDateFormat("yyyy.MM.dd").format(date)
        val todayDate = dateFormat.format(date)
        return todayDate
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

    private fun initClickListener() {
        binding.btnCalenderRetirement.setOnClickListener {
            val intentToRetirement = Intent(this, RewriteActivity::class.java)
            intentToRetirement.putExtra("word", todayWord)
            intentToRetirement.putExtra("sentence", todayWordSentence)
            intentToRetirement.putExtra("date", realDate)
            intentToRetirement.putExtra("bookmark", todayWordBookmarkState)
            startActivity(intentToRetirement)
        }
        binding.ivCalenderBookmark.setOnClickListener { viewModel.updateBookmark() }
        initCalenderDateClickListener()
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

        initMenuNickname(textMenuNickname, nickname)
    }

    private fun initCalenderDateClickListener() {
        date = binding.cvCalender.date
        binding.cvCalender.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)

            if (selectedDate.timeInMillis != date) {
                date = selectedDate.timeInMillis
                realDate = "${year}.${month + 1}.${dayOfMonth}"
                Log.e("kang", "$year $month $dayOfMonth")
            }
            coroutineScope.launch {
                initWord()
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
