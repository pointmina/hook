package com.hanto.Hook.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.hanto.Hook.model.Hook
import com.hanto.Hook.databinding.ItemUrlHookBinding

class UrlHookListAdapter(
    private val items: List<Hook>
) : RecyclerView.Adapter<UrlHookListAdapter.UrlHookItemViewHolder>() {

    // 뷰홀더 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UrlHookItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemUrlHookBinding.inflate(inflater, parent, false)
        return UrlHookItemViewHolder(binding)
    }

    // 데이터와 뷰 바인딩
    override fun onBindViewHolder(holder: UrlHookItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    // 아이템 개수 반환
    override fun getItemCount(): Int = items.size

    // 뷰홀더 클래스
    inner class UrlHookItemViewHolder(private val binding: ItemUrlHookBinding) :
        RecyclerView.ViewHolder(binding.root) {

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

















