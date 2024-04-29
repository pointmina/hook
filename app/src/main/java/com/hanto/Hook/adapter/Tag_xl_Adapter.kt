package com.hanto.Hook.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hanto.Hook.view.SelectedTagActivity
import com.hanto.Hook.databinding.ItemTagXlBinding

class Tag_xl_Adapter(private val tags: List<String>) :
    RecyclerView.Adapter<Tag_xl_Adapter.TagViewHolder>() {

    inner class TagViewHolder(private val binding: ItemTagXlBinding) :
        RecyclerView.ViewHolder(binding.root) {

        //항목 누르면 activity넘어가기
        init {
            binding.root.setOnClickListener {
                val context = binding.root.context
                val tag = tags[adapterPosition]
                val intent = Intent(context, SelectedTagActivity::class.java)
                intent.putExtra("selectedTag", tag)
                context.startActivity(intent)
            }
        }

        val textView: TextView = binding.tvTagNameXl
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val binding = ItemTagXlBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TagViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        holder.textView.text = tags[position]
    }

    override fun getItemCount() = tags.size
}
