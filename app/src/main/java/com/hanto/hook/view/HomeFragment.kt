package com.hanto.hook.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
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
import com.hanto.hook.viewmodel.MainViewModel
import com.hanto.hook.viewmodel.ViewModelFactory
import okhttp3.internal.notify

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
            loadData()
            binding.swipeLayout.isRefreshing = false
        }  // 새로 고침

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
                } // 디폴트 어댑터 선언, 아이템 누르면 디테일 뷰로 이동

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
        // 아이템 사이에 구분선 넣는 데코

        binding.rvHome.adapter = hookAdapter // rv 에 어댑터 붙이기

        // loadData()
    }

    private fun loadData() {
        /*binding.sfLoading.startShimmer() 자동 시작 */
        hookViewModel.loadFindMyHooks()
        hookViewModel.hookData.observe(viewLifecycleOwner) { hookData ->
            if (hookData != null) {
                hookAdapter.updateData(hookData)
                val shimmerContainer = binding.sfLoading
                shimmerContainer.stopShimmer()
                shimmerContainer.visibility = View.GONE

/*                val successToast = Toast.makeText(requireActivity(),"${hookData.count}개의 훅을 불러왔습니다.",Toast.LENGTH_SHORT)
                successToast.show()
                Handler(Looper.getMainLooper()).postDelayed({
                    successToast.cancel()
                }, 500)*/

            } else {
                hookViewModel.hookData.observe(viewLifecycleOwner) { errorData ->
                    if (errorData != null) {
                        Toast.makeText(requireActivity(), "${errorData.result?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

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
            hookViewModel.deleteSuccessData.observe(viewLifecycleOwner) { deleteSuccessData ->
                if (deleteSuccessData != null) {
                    loadData()
                    val deleteSuccessToast = Toast.makeText(
                        requireActivity(),
                        "${deleteSuccessData.result?.message}",
                        Toast.LENGTH_SHORT
                    )
                    deleteSuccessToast.show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        deleteSuccessToast.cancel()
                    }, 500)
                } else {
                    hookViewModel.deleteSuccessData.observe(viewLifecycleOwner) { errorData ->
                        if (errorData != null) {
                            val deleteErrorToast = Toast.makeText(
                                requireActivity(),
                                "${selectedItem.title} 삭제 실패",
                                Toast.LENGTH_SHORT
                            )
                            deleteErrorToast.show()
                            Handler(Looper.getMainLooper()).postDelayed({
                                deleteErrorToast.cancel()
                            }, 500)
                        }
                    }
                }
            }
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        Handler(Looper.getMainLooper()).postDelayed({
            loadData()
        }, 500)
    }
}

