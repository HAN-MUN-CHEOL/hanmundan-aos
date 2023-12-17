package com.sookmyung.hanmundan.util

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import com.sookmyung.hanmundan.R

@Suppress("DEPRECATION")
class ToastCustom(context: Context) {
    private var context: Context? = context
    private var toast: Toast? = null

    @SuppressLint("InflateParams")
    fun showToast(content: String) {
        if (toast != null) {
            toast!!.cancel()
            toast = null
        }
        val inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = inflater.inflate(R.layout.toast_layout, null)

        val textView = layout.findViewById<TextView>(R.id.tv_toast_custom)

        textView.text = content

        toast = Toast(context)
        toast!!.duration = Toast.LENGTH_LONG
        toast!!.view = layout
        toast!!.show()
    }
}

