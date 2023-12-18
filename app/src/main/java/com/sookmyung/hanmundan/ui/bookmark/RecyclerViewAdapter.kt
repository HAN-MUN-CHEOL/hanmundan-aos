package com.sookmyung.hanmundan.ui.bookmark

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sookmyung.hanmundan.databinding.ItemBookmarkDailyRecordBinding
import com.sookmyung.hanmundan.model.DailyRecord
import com.sookmyung.hanmundan.util.ItemDiffCallback

class RecyclerViewAdapter(
    private val clickListener: ItemClickListener<DailyRecord>
) : ListAdapter<DailyRecord, RecyclerViewAdapter.DailyRecordViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyRecordViewHolder {
        val itemBookmarkDailyRecordBinding =
            ItemBookmarkDailyRecordBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return DailyRecordViewHolder(itemBookmarkDailyRecordBinding)
    }

    override fun onBindViewHolder(holder: DailyRecordViewHolder, position: Int) {
        val item = getItem(position)
        holder.onBind(item, clickListener)
    }

    fun updateItemChange(position: Int) {
        val item = getItem(position)
        item.bookmark = !item.bookmark
        notifyItemChanged(position)
    }

    class DailyRecordViewHolder(
        val binding: ItemBookmarkDailyRecordBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun onBind(data: DailyRecord, itemClickListener: ItemClickListener<DailyRecord>) {
            binding.data = data
            Log.e("kang", "data is $data")
            binding.root.setOnClickListener {
                itemClickListener.onClick(absoluteAdapterPosition, data)
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = ItemDiffCallback<DailyRecord>(
            onItemsTheSame = { old, new -> old.date == new.date },
            onContentsTheSame = { old, new -> old == new }
        )
    }
}

fun interface ItemClickListener<T> {
    fun onClick(pos: Int, item: T)
}

