package com.hanto.hook.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hanto.hook.databinding.ItemTagTagBinding
import com.hanto.hook.model.Tag

class TagAdapter(
    private var tags: ArrayList<Tag>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<TagAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onClick(position: Int)
    }

    inner class ViewHolder(val binding: ItemTagTagBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(tag: Tag) {
            val displayNameWithHash = "#${tag.displayName}"
            binding.tvTagNameXl.text = displayNameWithHash
        }

        init {
            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onClick(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTagTagBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tag = tags[position]
        holder.bind(tag)
    }

    override fun getItemCount(): Int {
        return tags.size
    }

    fun getItem(position: Int): Tag {
        return tags[position]
    }

    fun updateData(newTags: List<Tag>) {
        tags.clear()
        tags.addAll(newTags)
        notifyDataSetChanged()
    }
}