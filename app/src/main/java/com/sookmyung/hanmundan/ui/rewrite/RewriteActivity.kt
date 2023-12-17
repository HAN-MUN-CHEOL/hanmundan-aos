package com.sookmyung.hanmundan.ui.rewrite

import android.app.Dialog
import android.os.Bundle
import android.view.Window
import android.widget.ImageView
import com.google.android.material.textview.MaterialTextView
import com.sookmyung.hanmundan.R
import com.sookmyung.hanmundan.databinding.ActivityRewriteBinding
import com.sookmyung.hanmundan.util.binding.BindingActivity

class RewriteActivity : BindingActivity<ActivityRewriteBinding>(R.layout.activity_rewrite) {
    private lateinit var deletedialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        deletedialog = Dialog(this)
        deletedialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        deletedialog.setContentView(R.layout.dialog_delete_sentence)
        findViewById<ImageView>(R.id.iv_rewrite_delete).setOnClickListener { showDialog() }
    }

    private fun showDialog() {
        deletedialog.setCancelable(false)
        deletedialog.show()
        deletedialog.findViewById<MaterialTextView>(R.id.tv_dialog_no)
            .setOnClickListener { deletedialog.dismiss() }
        deletedialog.findViewById<MaterialTextView>(R.id.tv_dialog_yes)
            .setOnClickListener { finish() }
    }
}