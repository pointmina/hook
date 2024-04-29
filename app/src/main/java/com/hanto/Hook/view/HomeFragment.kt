package com.hanto.Hook.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.hanto.Hook.R
import com.hanto.Hook.adapter.UrlHookListAdapter
import com.hanto.Hook.databinding.FragmentHomeBinding
import com.hanto.apitest.HookViewModel
import com.hassan.hook.adapter.HookAdapter

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private  lateinit var viewModel: HookViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        binding.rvHome.adapter = UrlHookListAdapter(getDummyData())

        viewModel = ViewModelProvider(this).get(HookViewModel::class.java)
        viewModel.getAllData()

        viewModel.result.observe(viewLifecycleOwner, Observer {
            val hookAdpater = HookAdapter(requireContext(), it)
            binding.rvHome.adapter = hookAdpater
        })
        // DividerItemDecoration에 대한 설정을 수정
        val dividerItemDecoration =
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        ResourcesCompat.getDrawable(resources, R.drawable.divider, null)?.let {
            dividerItemDecoration.setDrawable(it)
        }

        binding.rvHome.addItemDecoration(dividerItemDecoration)


        binding.ivAppbarAddHook.setOnClickListener {
            val action = HomeFragmentDirections.actionNavigationHomeToAddHookActivity()
            findNavController().navigate(action)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
