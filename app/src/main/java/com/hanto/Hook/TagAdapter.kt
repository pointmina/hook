package com.hanto.Hook

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TagAdapter(private val tags: Array<String>) : RecyclerView.Adapter<TagAdapter.TagViewHolder>() {

    inner class TagViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val textView : TextView = view.findViewById(R.id.tv_tag_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tag, parent, false)

        return TagViewHolder(view)
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        holder.textView.text = tags[position]
    }

    override fun getItemCount() = tags.size
}


//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
//        val inflater = LayoutInflater.from(parent.context)
//        val binding = ItemTagBinding.inflate(inflater, parent, false)
//        return TagViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
//        val tag = [position]
//        holder.bind(tag)
//    }
//
//    override fun getItemCount(): Int {
//        return items.size
//    }
//
//    fun setItems(newItems: List<String>) {
//        tags = newItems
//        notifyDataSetChanged()
//    }
//}
//
//class TagViewHolder(private val binding: ItemTagBinding) :
//    RecyclerView.ViewHolder(binding.root) {
//
//    fun bind(tag: String) {
//        with(binding) {
//            tvTagName.text = tag
//        }
//    }
//
//    companion object {
//        fun from(parent: ViewGroup): TagViewHolder {
//            val inflater = LayoutInflater.from(parent.context)
//            val binding = ItemTagBinding.inflate(inflater, parent, false)
//            return TagViewHolder(binding)
//        }
//    }
//}
