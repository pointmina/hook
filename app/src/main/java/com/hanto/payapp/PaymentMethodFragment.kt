package com.hanto.payapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hanto.payapp.databinding.FragmentHomeBinding
import com.hanto.payapp.databinding.FragmentPaymentMethodBinding

class PaymentMethodFragment :Fragment() {
    private var _binding: FragmentPaymentMethodBinding? = null

    //호출될 때마다 현재에 저장되어 있는 값이 반환된다.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentMethodBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}