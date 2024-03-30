package com.hanto.payapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hanto.payapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    //호출될 때마다 현재에 저장되어 있는 값이 반환된다.
    private  val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    //onViewCreated => 실제 onCreateView에서 inflate 성공을 하게 되면 호출이 되는 callback
    //라이브러리로 화면 구현

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //네비게이션 그래프 액션 내부 컨트롤러가 이 네비게이션 라이브러리 내부에서 실제로 화면에 이동을 제어하는 기능을 하고 있는 컨트롤러
        //기존에는 액션의 아이디로 전달하고 있었는데 safeargs는 action을 함수로 제공됨
        binding.viewCardArea.setOnClickListener{
            val action = HomeFragmentDirections.actionHomeToPaymentMethodFragment()
//            findNavController().navigate(R.id.action_home_to_paymentMethodFragment)
            findNavController().navigate(action)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}