package com.hanto.Hook.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.hanto.Hook.databinding.ItemHookBinding
import com.hanto.Hook.model.Hook


class HookAdapter(
    val context: Context,
    val dataSet: List<Hook>,
    val listener: OnItemClickListener
) : RecyclerView.Adapter<HookAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onClick(position: Int)
        fun onOptionButtonClick(position: Int)
    }

    inner class ViewHolder(val binding: ItemHookBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            // 항목 전체를 클릭했을 때
            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onClick(position)
                }
            }

            // 옵션 버튼 클릭했을 때
            binding.btOption.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onOptionButtonClick(position)
                }
            }
        }

        fun bind(hook: Hook) {
            with(binding) {
                tvTitle.text = hook.title
                tvUrlLink.text = hook.url
                tvTagDescription.text = hook.description

                // 태그 RecyclerView 설정
                rvTagContainer.apply {
                    adapter = TagHomeAdapter(hook.tag)
                    layoutManager =
                        FlexboxLayoutManager(context).apply {
                            flexDirection = FlexDirection.ROW
                            justifyContent = JustifyContent.FLEX_START
                        }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHookBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }
}
