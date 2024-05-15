package com.hanto.hook.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hanto.hook.databinding.ItemHomeTagBinding
import com.hanto.hook.model.Hook
import com.hanto.hook.view.HookDetailActivity

class TagHomeAdapter(private val tags: List<String?>, private val selectedHook: Hook) :
    RecyclerView.Adapter<TagHomeAdapter.TagViewHolder>() {

    inner class TagViewHolder(val binding: ItemHomeTagBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val textView = binding.tvTagName

        fun bind(tag: String) {
            textView.text = tag
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val binding = ItemHomeTagBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TagViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        val tag = tags[position]
        tag?.let { holder.bind(it) } // Nullable 체크 후 사용

        // 태그 클릭 이벤트 설정
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            // 클릭한 태그에 관련된 작업 수행
            // 여기에 HookDetailActivity로 이동하는 코드를 추가합니다.
            Intent(context, HookDetailActivity::class.java).apply {
                // HomeFragment에서 받아온 정보들을 함께 전달합니다.
                putExtra("item_title", selectedHook.title)
                putExtra("item_url", selectedHook.url)
                putExtra("item_description", selectedHook.description)
                // 클릭한 태그와 관련된 정보도 함께 전달합니다.
                selectedHook.tags?.map { it.displayName }?.let {
                    putStringArrayListExtra("item_tag_list", ArrayList(it))
                }
                context.startActivity(this)
            }
        }
    }

    override fun getItemCount(): Int {
        return tags.filterNotNull().size // Nullable 항목 필터링 후 크기 반환
    }
}
