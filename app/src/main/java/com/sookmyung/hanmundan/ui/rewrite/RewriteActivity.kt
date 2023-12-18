package com.sookmyung.hanmundan.ui.rewrite

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import com.google.android.gms.tasks.Task
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import com.sookmyung.hanmundan.R
import com.sookmyung.hanmundan.data.RetrofitInstance
import com.sookmyung.hanmundan.databinding.ActivityRewriteBinding
import com.sookmyung.hanmundan.model.DailyRecord
import com.sookmyung.hanmundan.model.DictionaryResponseDTO
import com.sookmyung.hanmundan.model.Item
import com.sookmyung.hanmundan.util.SnackbarCustom
import com.sookmyung.hanmundan.util.binding.BindingActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class RewriteActivity : BindingActivity<ActivityRewriteBinding>(R.layout.activity_rewrite) {
    private lateinit var deleteDialog: Dialog
    private lateinit var saveDialog: Dialog
    private var moreMeaningState = false
    var saveWritingState = false
    var lastWriting: String? = null
    var currentWriting: String? = null

    private lateinit var databaseReal: DatabaseReference
    private lateinit var currentDocumentId: String
    private lateinit var currentWord: String
    private lateinit var currentDate: String
    private var currentWordSentence: String? = null
    private var currentWordBookmarkState = false
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databaseReal =
            Firebase.database("https://hanmundan-default-rtdb.asia-southeast1.firebasedatabase.app/").reference
        val spf: SharedPreferences =
            applicationContext.getSharedPreferences("user", Context.MODE_PRIVATE)
        userId = spf.getString("userId", "").toString()

        getIntentFromCalender()
        coroutineScope.launch {
            initWord()
            initClick()
        }

        initUI()
        initDialog()

        clickMoreMeaningButton()
        retrofitWork()

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private fun initDialog() {
        deleteDialog = Dialog(this)
        deleteDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        deleteDialog.setContentView(R.layout.dialog_delete_sentence)

        saveDialog = Dialog(this)
        saveDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        saveDialog.setContentView(R.layout.dialog_change_view)
    }

    private fun getIntentFromCalender() {
        val intentFromCalender = intent
        currentWord = intentFromCalender.getStringExtra("word").toString()
        currentWordSentence = intentFromCalender.getStringExtra("sentence")
        currentDate = intentFromCalender.getStringExtra("date").toString()
        currentWordBookmarkState = intentFromCalender.getBooleanExtra("bookmark", false)
    }

    private suspend fun initWord() {
        try {
            val databaseReference = databaseReal.child(userId)
            val dataSnapshot = databaseReference.get().await()
            for (childSnapshot in dataSnapshot.children) {
                val dateInDatabase =
                    childSnapshot.child("date").getValue(String::class.java).toString()
                if (dateInDatabase == currentDate) {
                    currentDocumentId = childSnapshot.key!!
                    break
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

    private fun initUI() {
        binding.tvRewriteWordTitle.text = currentWord
        binding.etRewriteWriting.text =
            Editable.Factory.getInstance().newEditable(currentWordSentence)
    }

    private fun retrofitWork() {
        val service = RetrofitInstance.retrofitService

        service.getDictionary(RetrofitInstance.CLIENT_SECRET, currentWord, "json")
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
                bindMeaningToTextView(binding.tvRewriteWordMeaning1, items, 0)
                bindMeaningToTextView(binding.tvRewriteWordMeaning2, items, 1)
                bindMeaningToTextView(binding.tvRewriteWordMeaning3, items, 2)
                bindMeaningToTextView(binding.tvRewriteWordMeaning4, items, 3)
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
        clickMoreMeaningButton()
        clickSaveButton()
        clickDeleteButton()
    }

    private fun clickMoreMeaningButton() {
        binding.btnRewriteMoreMeaning.setOnClickListener {
            moreMeaningState = !moreMeaningState
            setMeaningVisibility(binding.tvRewriteWordMeaning3, moreMeaningState)
            setMeaningVisibility(binding.tvRewriteWordMeaning4, moreMeaningState)
        }
    }

    private fun setMeaningVisibility(view: View, isVisible: Boolean) {
        view.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    private fun clickSaveButton() {
        binding.btnRewriteSaveWriting.setOnClickListener {
            SnackbarCustom.make(binding.root, "저장되었습니다.").show()
            updateSaveState()
            setDocument(
                DailyRecord(
                    currentWord,
                    currentWriting ?: "",
                    currentDate,
                    currentWordBookmarkState
                )
            )
            lastWriting = currentWriting
        }
    }

    private fun updateSaveState() {
        currentWriting = binding.etRewriteWriting.text.toString()
        saveWritingState = lastWriting == currentWriting
    }

    private fun setDocument(data: DailyRecord) {
        databaseReal.child(userId).child(currentDocumentId).setValue(data)
            .addOnSuccessListener {
                Log.e("hmm", "setDocument success")
            }
            .addOnFailureListener {
                Log.e("hmm", "setDocument fail")
            }
    }

    private fun clickDeleteButton() {
        binding.ivRewriteDelete.setOnClickListener {
            showDialog()
        }
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            updateSaveState()
            if (!saveWritingState) {
                showDialogBackPressed()
            } else {
                finish()
            }
        }
    }

    private fun showDialogBackPressed() {
        saveDialog.setCancelable(false)
        saveDialog.show()
        saveDialog.findViewById<MaterialTextView>(R.id.btn_dialog_no)
            .setOnClickListener { saveDialog.dismiss() }
        saveDialog.findViewById<MaterialTextView>(R.id.btn_dialog_yes)
            .setOnClickListener {
                finish()
            }
    }

    private fun showDialog() {
        deleteDialog.setCancelable(false)
        deleteDialog.show()
        deleteDialog.findViewById<MaterialTextView>(R.id.tv_dialog_no)
            .setOnClickListener { deleteDialog.dismiss() }
        deleteDialog.findViewById<MaterialTextView>(R.id.tv_dialog_yes)
            .setOnClickListener { doDelete() }
    }

    private fun doDelete() {
        binding.etRewriteWriting.text = Editable.Factory.getInstance().newEditable("")
        currentWordBookmarkState = false
        setDocument(
            DailyRecord(
                currentWord,
                binding.etRewriteWriting.text.toString(),
                currentDate,
                currentWordBookmarkState
            )
        )
        finish()
    }
}