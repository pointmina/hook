package com.hanto.hook.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hanto.hook.databinding.ItemTagBinding

@Suppress("DEPRECATION")
class TagListAdapter(private val context: Context, private val tagMap: LinkedHashMap<String, Boolean>) :
    RecyclerView.Adapter<TagListAdapter.TagViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val binding = ItemTagBinding.inflate(LayoutInflater.from(context), parent, false)
        return TagViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        val tag = tagMap.keys.elementAt(position)
        holder.bind(tag, tagMap[tag] ?: false)
    }

    override fun getItemCount(): Int {
        return tagMap.size
    }

    inner class TagViewHolder(private val binding: ItemTagBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            // 아이템 뷰 클릭 시 체크 상태를 변경
            binding.root.setOnClickListener {
                val tag = tagMap.keys.elementAt(adapterPosition)
                val currentCheckedState = tagMap[tag] ?: false
                tagMap[tag] = !currentCheckedState
                notifyItemChanged(adapterPosition)
            }
        }

        fun bind(tag: String, isChecked: Boolean) {
            binding.tvTagName.text = tag
            binding.checkbox.isChecked = isChecked
        }
    }
}
