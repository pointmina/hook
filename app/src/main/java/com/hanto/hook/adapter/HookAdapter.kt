package com.hanto.hook.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.hanto.hook.databinding.ItemHookBinding
import com.hanto.hook.model.Hook
import com.hanto.hook.api.SuccessResponse
import com.hanto.hook.model.Tag

class HookAdapter(
    private var hooks: ArrayList<Hook>,
    private var tag: List<Tag>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<HookAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onClick(position: Int)
        fun onOptionButtonClick(position: Int)
    }

    inner class ViewHolder(val binding: ItemHookBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(hook: Hook) {
            binding.tvTitle.text = hook.title
            binding.tvUrlLink.text = hook.url
            binding.hookID.text = hook.id.toString()

            if (!hook.description.isNullOrEmpty()) {
                binding.tvTagDescription.visibility = View.VISIBLE
                binding.tvTagDescription.text = hook.description
            } else {
                binding.tvTagDescription.visibility = View.GONE
            }

            //tags에 값이 있는 경우에만 RecyclerView에 어댑터 설정
            hook.tags?.let { tags ->
                val validTags = tags.filter { !it.displayName.isNullOrEmpty() }
                if (validTags.isNotEmpty()) {
                    binding.rvTagContainer.apply {
                        layoutManager = FlexboxLayoutManager(binding.root.context).apply {
                            flexDirection = FlexDirection.ROW
                            justifyContent = JustifyContent.FLEX_START
                        }
                        adapter = TagHomeAdapter(validTags.map { it.displayName!! }, hook)
                        visibility = View.VISIBLE
                    }
                } else {
                    binding.rvTagContainer.visibility = View.GONE
                }
            } ?: run {
                binding.rvTagContainer.visibility = View.GONE
            }
        }

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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHookBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val hook = hooks[position]
        holder.bind(hook)
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return hooks.size
    }

    fun getItem(position: Int): Hook {
        return hooks[position]
    }

    fun updateData(response: SuccessResponse) {
        this.tag = response.tag
        this.hooks = response.hooks
        notifyDataSetChanged() // 데이터셋 변경을 알림
    }
}
