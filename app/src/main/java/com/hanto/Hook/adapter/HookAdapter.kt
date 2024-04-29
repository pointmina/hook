package com.hassan.hook.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.hanto.Hook.adapter.TagAdapter
import com.hanto.Hook.databinding.ItemUrlHookBinding
import com.hanto.Hook.model.Hook

class HookAdapter(val context: Context, val dataSet: List<Hook>) : RecyclerView.Adapter<HookAdapter.ViewHolder>() {

    //뷰홀더 클래스
    inner class ViewHolder(val binding: ItemUrlHookBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(hook: Hook){
            with(binding){
                tvTitle.text = hook.title
                tvUrlLink.text = hook.url
                tvTagDescription.text = hook.description

                rvTagContainer.apply {
                    adapter = TagAdapter(hook.tag)
                    val recyclerView = binding.rvTagContainer
                    layoutManager =
                        FlexboxLayoutManager(context)
                    (layoutManager as FlexboxLayoutManager).flexDirection = FlexDirection.ROW
                    (layoutManager as FlexboxLayoutManager).justifyContent =
                        JustifyContent.FLEX_START
                    recyclerView.layoutManager = layoutManager
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemUrlHookBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }
}