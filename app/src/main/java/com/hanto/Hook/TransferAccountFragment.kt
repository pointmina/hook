package com.hanto.Hook

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hanto.Hook.databinding.FragmentAccountTransferBinding


class TransferAccountFragment : Fragment() {
    //참조를 관리함
    private var _binding: FragmentAccountTransferBinding? = null

    //호출될 때마다 현재에 저장되어 있는 값이 반환된다.
    private val binding get() = _binding!!

    //온크리에이트에서 바인딩을 초기화한다.
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountTransferBinding.inflate(inflater, container, false)
        return binding.root
    }

    //온크리에이트뷰 함수가 호출됐을때 생성된 뷰는 온뷰크리에이트 라이프사이클 콜백의 인자로 들어오게됨
    //뷰와 관련된 설정은 onViewCreated 함수의 본문에서 해야한다.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}