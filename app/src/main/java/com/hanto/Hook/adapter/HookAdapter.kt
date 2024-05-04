package com.hanto.Hook.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.hanto.Hook.databinding.ItemHookBinding
import com.hanto.Hook.model.Hook
//commit test
class HookAdapter(val context: Context, val dataSet: List<Hook>, val listener : OnItemClickListener) :
    RecyclerView.Adapter<HookAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onClick(position: Int)
        fun onLongClick(position: Int): Boolean // 반환 값을 Boolean으로 합니다. (long click 이벤트 처리 여부 반환)
    }

    //뷰홀더 클래스
    inner class ViewHolder(val binding: ItemHookBinding): RecyclerView.ViewHolder(binding.root){

        init {
            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onClick(position)
                }
            }

            itemView.setOnLongClickListener{
                val position = bindingAdapterPosition
                Log.d("제발", ": 제발제발")
                if (position != RecyclerView.NO_POSITION){
                    return@setOnLongClickListener listener.onLongClick(position)
                }
                false
            }
        }

        fun bind(hook: Hook){
            with(binding){
                tvTitle.text = hook.title
                tvUrlLink.text = hook.url
                tvTagDescription.text = hook.description

                rvTagContainer.apply {
                    adapter = TagHomeAdapter(hook.tag)
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
        return ViewHolder(ItemHookBinding.inflate(LayoutInflater.from(parent.context),parent,false))

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }
}