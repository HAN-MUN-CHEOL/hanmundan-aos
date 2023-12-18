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
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.google.android.gms.tasks.Task
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import com.sookmyung.hanmundan.R
import com.sookmyung.hanmundan.data.RetrofitInstance
import com.sookmyung.hanmundan.data.RetrofitInstance.CLIENT_SECRET
import com.sookmyung.hanmundan.databinding.ActivityMainBinding
import com.sookmyung.hanmundan.model.DailyRecord
import com.sookmyung.hanmundan.model.DictionaryResponseDTO
import com.sookmyung.hanmundan.model.Item
import com.sookmyung.hanmundan.ui.bookmark.BookmarkActivity
import com.sookmyung.hanmundan.ui.calender.CalenderActivity
import com.sookmyung.hanmundan.ui.myPage.MyPageActivity
import com.sookmyung.hanmundan.util.SnackbarCustom
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var dialog: Dialog
    var saveWritingState = false
    var lastWriting: String? = null
    var currentWriting: String? = null
    private var moreMeaningState = false
    private var bookmarkState = false

    private lateinit var databaseReal: DatabaseReference
    private val dateInPhone = getCurrentDate()
    private lateinit var dateInDatabase: String
    private lateinit var todayDocumentId: String
    private lateinit var todayWord: String
    private var todayWordSentence: String? = null
    private var todayWordBookmarkState: Boolean = false
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        mainActivity = this

        FirebaseApp.initializeApp(this)
        val headerNavigation = binding.nvMainMenu.getHeaderView(0)
        val btnMenuClose = headerNavigation.findViewById<ImageView>(R.id.iv_navigation_navi)
        val textMenuNickname =
            headerNavigation.findViewById<TextView>(R.id.tv_navigation_header_name)
        val navigationView = binding.nvMainMenu
        val spf: SharedPreferences =
            applicationContext.getSharedPreferences("user", Context.MODE_PRIVATE)
        val nickname = spf.getString("nickname", "")

        databaseReal =
            Firebase.database("https://hanmundan-default-rtdb.asia-southeast1.firebasedatabase.app/").reference

        coroutineScope.launch {
            initWord()
            initUI()
            retrofitWork()
            initClick()
        }

        navigationView.setNavigationItemSelectedListener(this)
        initMenuNickname(textMenuNickname, nickname)
        initDrawer(btnMenuClose)

        dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_change_view)

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private suspend fun initWord() {
        try {
            val databaseReference = databaseReal.child("hanmundan")
            val dataSnapshot = databaseReference.get().await()
            for (childSnapshot in dataSnapshot.children) {
                dateInDatabase = childSnapshot.child("date").getValue(String::class.java).toString()
                if (dateInDatabase == dateInPhone) {
                    todayDocumentId = childSnapshot.key!!
                    todayWord = childSnapshot.child("word").getValue(String::class.java)!!
                    todayWordSentence =
                        childSnapshot.child("sentence").getValue(String::class.java) ?: ""
                    bookmarkState =
                        childSnapshot.child("bookmark").getValue(Boolean::class.java) ?: false
                    break
                }
            }
        } catch (exception: Exception) {
            Log.d("hmm", "Error getting data: ", exception)
        }
    }

    // Firebase의 Task를 Deferred로 변환하는 확장 함수
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

    private fun getCurrentDate(): String {
        val currentDate = System.currentTimeMillis()
        val date = Date(currentDate)
        val dateFormat = SimpleDateFormat("yyyy.MM.dd").format(date)
        val todayDate = dateFormat.format(date)
        return todayDate
    }

    private fun initUI() {
        binding.tvMainWordTitle.text = todayWord
        binding.etMainWriting.text = Editable.Factory.getInstance().newEditable(todayWordSentence)
        bookmarkState = todayWordBookmarkState
    }

    private fun retrofitWork() {
        val service = RetrofitInstance.retrofitService

        service.getDictionary(CLIENT_SECRET, todayWord, "json") // 여기에 받아온 단어
            .enqueue(object : Callback<DictionaryResponseDTO> {
                override fun onResponse(
                    call: Call<DictionaryResponseDTO>,
                    response: Response<DictionaryResponseDTO>
                ) {
                    handleDictionaryResponse(response.body())
                }

                override fun onFailure(call: Call<DictionaryResponseDTO>, t: Throwable) {
                    Log.d("kang", t.message.toString())
                }
            })
    }

    private fun handleDictionaryResponse(result: DictionaryResponseDTO?) {
        result?.let {
            val items = it.channel.item
            if (items.isNotEmpty()) {
                bindMeaningToTextView(binding.tvMainWordMeaning1, items, 0)
                bindMeaningToTextView(binding.tvMainWordMeaning2, items, 1)
                bindMeaningToTextView(binding.tvMainWordMeaning3, items, 2)
                bindMeaningToTextView(binding.tvMainWordMeaning4, items, 3)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun bindMeaningToTextView(textView: TextView, items: List<Item>, index: Int) {
        if (index < items.size) {
            val meaning = items[index].sense[0].definition
            textView.text = "${index + 1}. $meaning"
        }
    }

    private fun initClick() {
        clickBookmarkButton()
        clickMoreMeaningButton()
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

    private fun clickMoreMeaningButton() {
        binding.tvMainMoreMeaning.setOnClickListener {
            moreMeaningState = !moreMeaningState
            setMeaningVisibility(binding.tvMainWordMeaning3, moreMeaningState)
            setMeaningVisibility(binding.tvMainWordMeaning4, moreMeaningState)
        }
    }

    private fun setMeaningVisibility(view: View, isVisible: Boolean) {
        view.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    private fun clickBookmarkButton() {
        binding.ivMainBlankedBookmark.setOnClickListener {
            bookmarkState = !bookmarkState
            if (bookmarkState) {
                binding.ivMainBlankedBookmark.setImageResource(R.drawable.ic_bookmark_fill)
                SnackbarCustom.make(binding.root, "책갈피를 끼웠습니다.").show()
            } else {
                binding.ivMainBlankedBookmark.setImageResource(R.drawable.ic_bookmark_blank)
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

    private fun clickSaveButton() {
        binding.tvMainSaveWriting.setOnClickListener {
            updateWritingValue()
            updateSaveWritingState()
            if (saveWritingState) {
                SnackbarCustom.make(binding.root, "저장되었습니다.").show()
                setDocument(
                    DailyRecord(
                        todayWord,
                        binding.etMainWriting.text.toString(),
                        dateInDatabase,
                        bookmarkState
                    )
                )
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

    private fun setDocument(data: DailyRecord) {
        databaseReal.child("hanmundan").child(todayDocumentId).setValue(data)
            .addOnSuccessListener {
                Log.e("hmm", "setDocument success")
            }
            .addOnFailureListener {
                Log.e("hmm", "setDocument fail")
            }
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

    companion object{
        var mainActivity : MainActivity? = null
    }
}
