package com.example.flagsincity.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.flagsincity.database.HistoryAction
import com.example.flagsincity.databinding.ListItemHistoryActionBinding

class HistoryActionAdapter: androidx.recyclerview.widget.ListAdapter<HistoryAction, HistoryActionAdapter.ViewHolder>(HistoryActionDiffCallback()){

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ListItemHistoryActionBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: HistoryAction) {
            binding.action = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemHistoryActionBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}


class HistoryActionDiffCallback : DiffUtil.ItemCallback<HistoryAction>() {

    override fun areItemsTheSame(oldItem: HistoryAction, newItem: HistoryAction): Boolean {
        return oldItem.actionId == newItem.actionId
    }


    override fun areContentsTheSame(oldItem: HistoryAction, newItem: HistoryAction): Boolean {
        return oldItem == newItem
    }


}