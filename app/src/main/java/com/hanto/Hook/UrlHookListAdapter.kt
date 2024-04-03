package com.hanto.Hook

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hanto.Hook.databinding.ItemUrlHookBinding


class TransferAccountListAdapter(
    //어뎁터를 생성을 할때 그 목록 데이터를 받아오도록 구현을 한다.
    private val items: List<Account>
) : RecyclerView.Adapter<UrlHookItemViewHolder>() {


    //반환타입이 뷰홀더
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UrlHookItemViewHolder {
        val binding =
            ItemUrlHookBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UrlHookItemViewHolder(binding.root)
    }

    //첫번째 인자가 뷰홀더
    //생성된 뷰홀더는 온바인드 뷰홀더의 인자로 전달 받을 수 있는데
    //bind라는 단어가 의미하는 것처럼 뷰홀더에 어뎁터가 관리하고 있는
    //데이터를 연결하는 동작을 수행한다.
    //위치에 따라 특정 데이터를 꺼내와야함 => position
    override fun onBindViewHolder(holder: UrlHookItemViewHolder, position: Int) {
        holder.bind(items[position])
        //items : 어뎁터가 관리하는 데이터
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
//리사이클러의 뷰홀더는 생성자 파라미터로 필수적으로 전달을 해야하는 것이 있다.

//바인드 함수에서 바인딩 클래스에서 이미지 뷰랑 이름이랑 계좌 정보를 그리는 데
//필요한 데이터를 전달받아야함
//데이터를 그룹핑해서 클래스로 정의할 수 있는지?
//목록형 레이아웃을 나타내기 위해서 사전에 레이아웃 구현
class UrlHookItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(account: Account) {
    }
}






















