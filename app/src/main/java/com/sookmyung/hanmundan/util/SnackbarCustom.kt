package com.sookmyung.hanmundan.util

import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.sookmyung.hanmundan.R

class SnackbarCustom(view: View, private val message: String) {
    companion object {
        fun make(view: View, message: String) = SnackbarCustom(view, message)
    }

    private val context = view.context
    private val snackBar = Snackbar.make(view, "", 5000)
    private val snackBarView =
        LayoutInflater.from(context).inflate(R.layout.snackbar_layout, null, false)
    private val snackBarLayout = snackBar.view as Snackbar.SnackbarLayout
    private var snackBarText = snackBarView.findViewById<TextView>(R.id.tv_snackbar_custom)

    init {
        initView()
        initData()
    }

    private fun initView() {
        with(snackBarLayout) {
            removeAllViews()
            setPadding(20, 20, 20, 20)
            setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
            addView(snackBarView, 0)
        }
    }

    private fun initData() {
        snackBarText.text = message
    }

    fun show() {
        snackBar.show()
    }
}