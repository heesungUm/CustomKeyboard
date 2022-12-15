package com.heesungum.customkeyboard.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.heesungum.customkeyboard.databinding.ItemClipboardBinding



class ClipboardAdapter(
    private val clipboardList: List<String>,
    private val onItemClick: (Int) -> Unit,
    private val onDeleteClick: (Int) -> Unit,
) : RecyclerView.Adapter<ClipboardAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ItemClipboardBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onItemClick,
            onDeleteClick,
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(clipboardList[clipboardList.size - 1 - position])
    }

    override fun getItemCount(): Int = clipboardList.size

    class ItemViewHolder(
        private val binding: ItemClipboardBinding,
        onItemClick: (Int) -> Unit,
        onDeleteClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                onItemClick.invoke(adapterPosition)
            }
            binding.deleteBtn.setOnClickListener {
                onDeleteClick.invoke(adapterPosition)
            }
        }

        fun bind(clipboardText: String) {
            binding.clipboardTv.text = clipboardText
        }
    }
}