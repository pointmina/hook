package com.hanto.hook.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hanto.hook.databinding.ItemHomeTagBinding

class TagHomeAdapter(private val tags: List<String?>) :
    RecyclerView.Adapter<TagHomeAdapter.TagViewHolder>() {

    inner class TagViewHolder(val binding: ItemHomeTagBinding)
        : RecyclerView.ViewHolder(binding.root) {
        val textView = binding.tvTagName

        fun bind(tag: String) {
            textView.text = tag
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        return TagViewHolder(ItemHomeTagBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        val tag = tags[position]
        tag?.let { holder.bind(it) } // Nullable 체크 후 사용
    }

    override fun getItemCount(): Int {
        return tags.filterNotNull().size // Nullable 항목 필터링 후 크기 반환
    }
}
