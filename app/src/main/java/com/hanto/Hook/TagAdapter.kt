package com.hanto.Hook

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hanto.Hook.databinding.ItemTagBinding

class TagAdapter(private var items: List<String>) : RecyclerView.Adapter<TagViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTagBinding.inflate(inflater, parent, false)
        return TagViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        val tag = items[position]
        holder.bind(tag)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setItems(newItems: List<String>) {
        items = newItems
        notifyDataSetChanged()
    }
}

class TagViewHolder(private val binding: ItemTagBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(tag: String) {
        with(binding) {
            tvTagName.text = tag
        }
    }

    companion object {
        fun from(parent: ViewGroup): TagViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemTagBinding.inflate(inflater, parent, false)
            return TagViewHolder(binding)
        }
    }
}
