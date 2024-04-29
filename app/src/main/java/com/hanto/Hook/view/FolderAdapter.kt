package com.hanto.Hook.view

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hanto.Hook.databinding.ItemFolderBinding

class FolderAdapter(private val folders: Array<String>) :
    RecyclerView.Adapter<FolderAdapter.FolderViewHolder>() {

    inner class FolderViewHolder(private val binding: ItemFolderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val textView: TextView = binding.tvGridFolder
        val imgView : ImageView = binding.ivGridFolder
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val binding = ItemFolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FolderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        holder.textView.text = folders[position]
//        holder.textView.background.setBack
    }

    override fun getItemCount(): Int = folders.size
}
