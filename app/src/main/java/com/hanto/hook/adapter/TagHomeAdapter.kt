package com.hanto.hook.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hanto.hook.databinding.ItemHomeTagBinding

class TagHomeAdapter(private val tags: List<String?>) :
    RecyclerView.Adapter<TagHomeAdapter.TagViewHolder>() {

    inner class TagViewHolder(val binding: ItemHomeTagBinding) : RecyclerView.ViewHolder(binding.root) {
        val textView = binding.tvTagName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        return TagViewHolder(ItemHomeTagBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        holder.textView.text = tags!![position]
    }

    override fun getItemCount() = tags!!.size
}

