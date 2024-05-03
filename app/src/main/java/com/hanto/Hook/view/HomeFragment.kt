package com.hanto.Hook.view

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
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.hanto.Hook.R
import com.hanto.Hook.databinding.FragmentHomeBinding
import com.hanto.apitest.HookViewModel
import com.hanto.Hook.adapter.HookAdapter
import com.hanto.Hook.model.Hook

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HookViewModel
    private var currentSelectedItem: Hook? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)


        //뷰모델 생성 & 데이터 로드
        viewModel = ViewModelProvider(this)[HookViewModel::class.java]
        viewModel.getAllData()

        //훅추가
        binding.ivAppbarAddHook.setOnClickListener {
            val action_a = HomeFragmentDirections.actionNavigationHomeToAddHookActivity()
            findNavController().navigate(action_a)
        }

        //세팅
        binding.ivSetting.setOnClickListener {
            val action_s = HomeFragmentDirections.actionNavigationHomeToSettingActivity()
            findNavController().navigate(action_s)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //LiveData 관찰자 설정
        viewModel.result.observe(viewLifecycleOwner, Observer { itemList ->
            HookAdapter(requireContext(), itemList, object : HookAdapter.OnItemClickListener {

                // 아이템 단일 클릭 시 이벤트 처리
                override fun onClick(position: Int) {
                    // 'itemList'에서 선택한 위치의 Hook 객체 가져오기
                    val selectedHook = itemList[position]

                    // Intent 생성하고 HookDetailActivity로 이동하는 로직을 실행
                    Intent(requireContext(), HookDetailActivity::class.java).apply {
                        putExtra("item_title", selectedHook.title)
                        putExtra("item_url", selectedHook.url)
                        putExtra("item_description", selectedHook.description)
                        startActivity(this)
                    }
                }

                // 아이템 롱 클릭 시 이벤트 처리
                override fun onLongClick(position: Int): Boolean {
                    // 현재 선택된 아이템 업데이트
                    currentSelectedItem = itemList[position]
                    // BottomSheetDialog 보여주기
                    showBottomSheetDialog(currentSelectedItem)
                    // 이벤트가 처리됐음을 나타냄
                    return true
                }
            }).also { hookAdapter ->
                // RecyclerView 어댑터 설정
                binding.rvHome.adapter = hookAdapter
            }

            // DividerItemDecoration
            val dividerItemDecoration =
                DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
            ResourcesCompat.getDrawable(resources, R.drawable.divider, null)?.let {
                dividerItemDecoration.setDrawable(it)
            }
            binding.rvHome.addItemDecoration(dividerItemDecoration)

        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showBottomSheetDialog(selectedItem: Hook?) {
        selectedItem?.let { item ->  // selectedItem이 null이 아닌 경우에만 로직 실행
            val dialog = BottomSheetDialog(requireContext(), R.style.CustomBottomSheetDialogTheme)
            val view = layoutInflater.inflate(R.layout.dialog_home, null)
            dialog.setContentView(view)
            dialog.setCancelable(true)

            val btonWeb = view.findViewById<Button>(R.id.bt_onWeb)
            btonWeb.setOnClickListener {
                // selectedItem을 사용하여 Intent 생성
                Intent(requireContext(), WebviewActivity::class.java).also { intent ->
                    intent.putExtra(
                        WebviewActivity.EXTRA_URL,
                        selectedItem?.url
                    )  // 'selectedItem?.url'은 null 가능성을 고려하여 null 안전 호출을 사용합니다.
                    startActivity(intent)
                }
                dialog.dismiss()
            }


            val btHookShare = view.findViewById<Button>(R.id.bt_HookShare)
            btHookShare.setOnClickListener {
                // 전체 앱을 띄우는 게 보안 문제 때문에 막혔음
                dialog.dismiss()
            }

            val btHookDelete = view.findViewById<Button>(R.id.bt_HookDelete)
            btHookDelete.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }
    }


}
