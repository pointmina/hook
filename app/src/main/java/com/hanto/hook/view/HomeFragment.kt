package com.hanto.hook.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.hanto.hook.R
import com.hanto.hook.databinding.FragmentHomeBinding
import com.hanto.hook.adapter.HookAdapter
import com.hanto.hook.api.ApiServiceManager
import com.hanto.hook.model.Hook
import com.hanto.hook.urlHandler.PageDetailsDialog
import com.hanto.hook.viewmodel.MainViewModel
import com.hanto.hook.viewmodel.ViewModelFactory

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var hookAdapter: HookAdapter
    private val apiServiceManager by lazy { ApiServiceManager() }
    private val viewModelFactory by lazy { ViewModelFactory(apiServiceManager) }
    private val hookViewModel: MainViewModel by lazy {
        ViewModelProvider(
            this,
            viewModelFactory
        ).get(MainViewModel::class.java)
    }

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

        val btSetting = view.findViewById<ImageButton>(R.id.bt_setting)
        btSetting.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_settingActivity)
        }  // 환경 설정 버튼

        binding.swipeLayout.setOnRefreshListener {
            hookViewModel.loadFindMyHooks()
            binding.swipeLayout.isRefreshing = false
        }  // 새로 고침

        // 60~82: 디폴트 어댑터 선언
        hookAdapter = HookAdapter(
            hooks = ArrayList(),
            tag = ArrayList(),
            object : HookAdapter.OnItemClickListener {
                override fun onClick(position: Int) {
                    val selectedHook = hookAdapter.getItem(position)
                    Intent(requireContext(), HookDetailActivity::class.java).apply {
                        putExtra("item_id", selectedHook.id.toString())
                        putExtra("item_title", selectedHook.title)
                        putExtra("item_url", selectedHook.url)
                        putExtra("item_description", selectedHook.description)
                        selectedHook.tags?.map { it.displayName }?.let {
                            putStringArrayListExtra("item_tag_list", ArrayList(it))
                        }
                        startActivity(this)
                    }
                } // 아이템 누르면 디테일 뷰로 이동

                override fun onOptionButtonClick(position: Int) {
                    val selectedHook = hookAdapter.getItem(position)
                    showBottomSheetDialog(selectedHook)
                } // 점 세 개 버튼 -> dialog 열기
            })

        val dividerItemDecoration =
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        ResourcesCompat.getDrawable(resources, R.drawable.divider, null)?.let {
            dividerItemDecoration.setDrawable(it)
        }
        binding.rvHome.addItemDecoration(dividerItemDecoration)
        // 85~90: rv 각 아이템 사이에 구분선 넣는 데코

        binding.rvHome.adapter = hookAdapter // rv 에 어댑터 붙이기

        hookViewModel.loadFindMyHooks() // fragment 진입 -> 데이터 로딩

        val shimmerContainer = binding.sfLoading
        hookViewModel.hookData.observe(viewLifecycleOwner) { hookData ->
            if (hookData != null) {
                hookAdapter.updateData(hookData)
                shimmerContainer.stopShimmer() // shimmer 는 원래 자동 시작 ... hookData 오면 stop
                shimmerContainer.visibility = View.GONE
//                Toast.makeText(requireActivity(), "${hookData.count}개의 훅이 업데이트 됐어요.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireActivity(), "불러오기 실패", Toast.LENGTH_SHORT).show()
            }
        }
        val createHookLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                hookViewModel.loadFindMyHooks()  // 새 훅이 추가되었을 때 목록을 새로고침
            }
        }


    }

    // 111~132: 바텀 시트 dialog
    private fun showBottomSheetDialog(selectedItem: Hook) {
        val dialog = BottomSheetDialog(requireContext(), R.style.CustomBottomSheetDialogTheme)
        val view = layoutInflater.inflate(R.layout.bottom_dialog_home, null)
        dialog.setContentView(view)
        dialog.setCancelable(true)

        val btonWeb = view.findViewById<Button>(R.id.bt_onWeb)
        btonWeb.setOnClickListener {
            Intent(requireContext(), WebviewActivity::class.java).also { intent ->
                intent.putExtra(WebviewActivity.EXTRA_URL, selectedItem.url)
                startActivity(intent)
            }
            dialog.dismiss()
        }

        val btHookDelete = view.findViewById<Button>(R.id.bt_HookDelete)
        btHookDelete.setOnClickListener {
            selectedItem.id?.let { it1 -> hookViewModel.loadDeleteHook(it1) }
            dialog.dismiss()
            Toast.makeText(requireActivity(),"삭제 완료!", Toast.LENGTH_SHORT).show()
        }
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

