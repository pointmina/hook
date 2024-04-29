package com.hanto.Hook.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.hanto.Hook.databinding.ItemSelectedTagHookListBinding
import com.hanto.Hook.model.Hook

class SelectedTagHookListAdapter(
    private val items: List<Hook>
) : RecyclerView.Adapter<SelectedTagHookListAdapter.SelectedTagHookViewHolder>() {

    // 뷰홀더 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedTagHookViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSelectedTagHookListBinding.inflate(inflater, parent, false)
        return SelectedTagHookViewHolder(binding)
    }

    // 데이터와 뷰 바인딩
    override fun onBindViewHolder(holder: SelectedTagHookViewHolder, position: Int) {
        holder.bind(items[position])
    }

    // 아이템 개수 반환
    override fun getItemCount(): Int = items.size

    // 뷰홀더 클래스
    inner class SelectedTagHookViewHolder(private val binding: ItemSelectedTagHookListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        //추후에 수정해야됨 특정 태그를 포함하고 있는거만 띄워야함
        fun bind(hook: Hook) {
            with(binding) {
                tvTitle.text = hook.title
                tvUrlLink.text = hook.url
                tvTagDescription.text = hook.description

                // RecyclerView에 TagAdapter와 LayoutManager 설정
                rvTagContainer.apply {
                    // 기존 adapter 대신에 새로운 TagAdapter 객체를 생성하여 사용
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
}

















