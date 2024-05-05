package com.hanto.hook.view

import HookAdapter
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.hanto.hook.R
import com.hanto.hook.databinding.FragmentHomeBinding
import com.hanto.hook.model.Hook
import com.hanto.apitest.HookViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HookViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)


        // Viewmodel 생성 및 데이터 로드
        viewModel = ViewModelProvider(this)[HookViewModel::class.java]
        viewModel.getAllData()

        // 훅 추가 버튼 클릭 시
        binding.ivAppbarAddHook.setOnClickListener {
            val action_a = HomeFragmentDirections.actionNavigationHomeToAddHookActivity()
            findNavController().navigate(action_a)
        }

        // 세팅 버튼 클릭 시
        binding.ivSetting.setOnClickListener {
            val action_s = HomeFragmentDirections.actionNavigationHomeToSettingActivity()
            findNavController().navigate(action_s)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // DividerItemDecoration 설정
        val dividerItemDecoration =
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        ResourcesCompat.getDrawable(resources, R.drawable.divider, null)?.let {
            dividerItemDecoration.setDrawable(it)
        }
        binding.rvHome.addItemDecoration(dividerItemDecoration)


        binding.refreshLayout.setOnRefreshListener {
            // 데이터 새로고침
            viewModel.getAllData()
            binding.refreshLayout.isRefreshing = false // 새로고침 완료 후 인디케이터 정지
        }


        // LiveData 관찰자 설정
        viewModel.result.observe(viewLifecycleOwner, Observer { itemList ->
            HookAdapter(requireContext(), itemList, object : HookAdapter.OnItemClickListener {

                override fun onClick(position: Int) {
                    // 아이템 클릭 시 이벤트 처리
                    val selectedHook = itemList[position]
                    Intent(requireContext(), HookDetailActivity::class.java).apply {
                        putExtra("item_title", selectedHook.title)
                        putExtra("item_url", selectedHook.url)
                        putExtra("item_description", selectedHook.description)
                        startActivity(this)
                    }
                }

                override fun onOptionButtonClick(position: Int) {
                    // 옵션 버튼 클릭 시 이벤트 처리
                    val selectedItem = itemList[position]
                    showBottomSheetDialog(selectedItem)
                }
            }).also { hookAdapter ->
                // RecyclerView 어댑터 설정
                binding.rvHome.adapter = hookAdapter
                // LayoutManager 설정
                binding.rvHome.layoutManager = LinearLayoutManager(requireContext())

            }

        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun showBottomSheetDialog(selectedItem: Hook?) {
        selectedItem?.let { item ->
            val dialog = BottomSheetDialog(requireContext(), R.style.CustomBottomSheetDialogTheme)
            val view = layoutInflater.inflate(R.layout.dialog_home, null)
            dialog.setContentView(view)
            dialog.setCancelable(true)

            val btonWeb = view.findViewById<Button>(R.id.bt_onWeb)
            btonWeb.setOnClickListener {
                Intent(requireContext(), WebviewActivity::class.java).also { intent ->
                    intent.putExtra(WebviewActivity.EXTRA_URL, selectedItem?.url)
                    startActivity(intent)
                }
                dialog.dismiss()
            }

            val btHookShare = view.findViewById<Button>(R.id.bt_HookShare)
            btHookShare.setOnClickListener {
                // Share 기능 구현
                dialog.dismiss()
            }

            val btHookDelete = view.findViewById<Button>(R.id.bt_HookDelete)
            btHookDelete.setOnClickListener {
                // Delete 기능 구현
                dialog.dismiss()
            }

            dialog.show()
        }
    }
}
