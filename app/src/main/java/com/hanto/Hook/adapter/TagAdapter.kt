package com.hanto.Hook.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hanto.Hook.R

class TagAdapter(private val tags: Array<String>?) :
    RecyclerView.Adapter<TagAdapter.TagViewHolder>() {

    inner class TagViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.tv_tag_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tag, parent, false)

        return TagViewHolder(view)
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        holder.textView.text = tags!![position]
    }

    override fun getItemCount() = tags!!.size
}

