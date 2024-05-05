import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.hanto.hook.adapter.TagHomeAdapter
import com.hanto.hook.databinding.ItemHookBinding
import com.hanto.hook.model.Hook

class HookAdapter(
    val context: Context,
    val dataSet: List<Hook>,
    val listener: OnItemClickListener
) : RecyclerView.Adapter<HookAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onClick(position: Int)
        fun onOptionButtonClick(position: Int)
    }

    inner class ViewHolder(private val binding: ItemHookBinding) :
        RecyclerView.ViewHolder(binding.root) {

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
            binding.hook = hook
            binding.executePendingBindings()

            hook.title?.let { title ->
                binding.tvTitle.text = title
                binding.tvTitle.visibility = View.VISIBLE
            } ?: run {
                binding.tvTitle.visibility = View.GONE
            }

            // url이 null이 아닌 경우에만 TextView에 설정
            hook.url?.let { url ->
                binding.tvUrlLink.text = url
                binding.tvUrlLink.visibility = View.VISIBLE
            } ?: run {
                binding.tvUrlLink.visibility = View.GONE
            }

            // description이 null이 아닌 경우에만 TextView에 설정
            hook.description?.let { description ->
                binding.tvTagDescription.text = description
                binding.tvTagDescription.visibility = View.VISIBLE
            } ?: run {
                binding.tvTagDescription.visibility = View.GONE
            }

            // tag가 null이 아닌 경우에만 RecyclerView에 어댑터 설정
            hook.tag?.let { tag ->
                val flexboxLayoutManager = FlexboxLayoutManager(context)
                flexboxLayoutManager.flexDirection = FlexDirection.ROW
                flexboxLayoutManager.justifyContent = JustifyContent.FLEX_START
                binding.rvTagContainer.layoutManager = flexboxLayoutManager
                binding.rvTagContainer.adapter = TagHomeAdapter(tag)
                binding.rvTagContainer.visibility = View.VISIBLE
            } ?: run {
                binding.rvTagContainer.visibility = View.GONE
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
