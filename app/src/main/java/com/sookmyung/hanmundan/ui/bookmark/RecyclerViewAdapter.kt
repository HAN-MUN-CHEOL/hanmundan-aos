package com.sookmyung.hanmundan.ui.bookmark

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sookmyung.hanmundan.R

class RecyclerViewAdapter(private val items: ArrayList<Data>) :
    RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.word.text = item.word
        holder.sentence.text = item.sentence
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflatedView =
            LayoutInflater.from(parent.context).inflate(R.layout.bookmark_list_item, parent, false)
        return ViewHolder(inflatedView)
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private var view: View = v
        val sentence = view.findViewById<TextView>(R.id.tv_bookmark_list_sentence)
        val word = view.findViewById<TextView>(R.id.tv_bookmark_list_word)
    }
}
