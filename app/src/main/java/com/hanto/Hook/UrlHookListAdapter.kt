package com.hanto.Hook

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
                tvTitle.text = hook.urlTitle
                tvUrlLink.text = hook.urlLink
                tvTagDescription.text = hook.urlDescription
                rvTagContainer.apply {
                    adapter = TagAdapter(hook.tags)
                    layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                }
                // 이미지 리소스를 설정하는 코드가 주석 처리되어 있습니다.
                // gvTagFolderContainer.setImageResource(hook.folderResourceId)
            }
        }

    }
}



















