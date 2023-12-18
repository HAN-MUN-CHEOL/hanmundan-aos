package com.sookmyung.hanmundan.ui.rewrite

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.textview.MaterialTextView
import com.sookmyung.hanmundan.R
import com.sookmyung.hanmundan.data.RetrofitInstance
import com.sookmyung.hanmundan.databinding.ActivityRewriteBinding
import com.sookmyung.hanmundan.model.DictionaryResponseDTO
import com.sookmyung.hanmundan.model.Item
import com.sookmyung.hanmundan.util.binding.BindingActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RewriteActivity : BindingActivity<ActivityRewriteBinding>(R.layout.activity_rewrite) {
    private lateinit var deletedialog: Dialog
    private var moreMeaningState = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        deletedialog = Dialog(this)
        deletedialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        deletedialog.setContentView(R.layout.dialog_delete_sentence)
        findViewById<ImageView>(R.id.iv_rewrite_delete).setOnClickListener { showDialog() }
        clickMoreMeaningButton()
        retrofitWork()
    }

    private fun retrofitWork() {
        val service = RetrofitInstance.retrofitService

        service.getDictionary(RetrofitInstance.CLIENT_SECRET, "역사", "json")
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

    private fun showDialog() {
        deletedialog.setCancelable(false)
        deletedialog.show()
        deletedialog.findViewById<MaterialTextView>(R.id.tv_dialog_no)
            .setOnClickListener { deletedialog.dismiss() }
        deletedialog.findViewById<MaterialTextView>(R.id.tv_dialog_yes)
            .setOnClickListener { finish() }
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
}