package com.hanto.hook.view

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.hanto.hook.R
import com.hanto.hook.databinding.FragmentHomeBinding
import com.hanto.hook.adapter.HookAdapter
import com.hanto.hook.api.ApiServiceManager
import com.hanto.hook.api.SuccessResponse
import com.hanto.hook.model.Hook
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
        )[MainViewModel::class.java]
    }

    private var nickname: String = "Hook 사용자"

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

        hookAdapter = HookAdapter(
            hooks = ArrayList(),
            tag = ArrayList(),
            object : HookAdapter.OnItemClickListener {
                override fun onClick(position: Int) {
                    val selectedHook = hookAdapter.getItem(position)
                    webIntent(selectedHook.url!!)
                    //showCountDownDialog(selectedHook)
                }

                override fun onOptionButtonClick(position: Int) {
                    val selectedHook = hookAdapter.getItem(position)
                    showBottomSheetDialog(selectedHook)
                } // 점 세 개 버튼 -> dialog 열기

                override fun onLongClick(position: Int) {
                    loadNickName()
                    Handler(Looper.getMainLooper()).postDelayed({
                        val selectedHook = hookAdapter.getItem(position)
                        val clipboard =
                            requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val pasteText =
                            "${nickname} 님이 훅을 공유했어요! \n\n${selectedHook.title} \n${selectedHook.url}"
                        val clip = ClipData.newPlainText("label", pasteText)
                        clipboard.setPrimaryClip(clip)
                    }, 150) // Ripple 애니메이션이 대략 완료될 시간
                }
            })

        val dividerItemDecoration =
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        ResourcesCompat.getDrawable(resources, R.drawable.divider, null)?.let {
            dividerItemDecoration.setDrawable(it)
        }
        binding.rvHome.addItemDecoration(dividerItemDecoration)
        // 아이템 사이에 구분선 넣는 데코

        binding.rvHome.adapter = hookAdapter // rv 에 어댑터 붙이기
    }

    // ============== 이하 override fun 영역 ============================
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    // ============= 이하 ~ 끝까지 private fun 영역 ==============================

    private fun loadNickName() {
        hookViewModel.loadGetMyInfo()
        hookViewModel.userData.observe(this) { user ->
            nickname = user?.user?.nickname!!
        }
        hookViewModel.userData.removeObservers(this)
    }

    private fun loadData() {
        hookViewModel.loadFindMyHooks()
        hookViewModel.hookData.observe(viewLifecycleOwner) { hookData ->
            if (hookData != null) {
                hookAdapter.updateData(hookData)
                val shimmerContainer = binding.sfLoading
                shimmerContainer.stopShimmer()
                shimmerContainer.visibility = View.GONE
            } else {
                hookViewModel.hookData.observe(viewLifecycleOwner) { errorData ->
                    if (errorData != null) {
                        Toast.makeText(
                            requireActivity(),
                            "${errorData.result?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            hookViewModel.hookData.removeObservers(this)
            hookViewModel.errorData.removeObservers(this)
        }
    }

    private fun showCountDownDialog(selectedHook: Hook) {

        val inflater = LayoutInflater.from(requireContext())
        val dialogView = inflater.inflate(R.layout.design_alert_dialog, null)

        // 메시지 텍스트 뷰 설정
        val messageTextView = dialogView.findViewById<TextView>(R.id.alertMessage)

        fun createSpannableMessage(secondsRemaining: Long): SpannableString {
            val message = "${secondsRemaining + 1}초 후에 자동으로 \n${selectedHook.url} \n주소로 이동합니다..."
            val spannable = SpannableString(message)

            val start = selectedHook.url?.let { message.indexOf(it) }
            val end = start?.plus(selectedHook.url!!.length)

            spannable.setSpan(
                ForegroundColorSpan(Color.parseColor("#6230F2")),
                start!!,
                end!!,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            return spannable
        }

        // 초기 메시지 설정
        messageTextView.text = createSpannableMessage(3)

        var countDownTimer: CountDownTimer? = null

        // AlertDialog 설정
        val dialogBuilder = AlertDialog.Builder(requireContext()).apply {
            setView(dialogView)
            setPositiveButton("바로 이동") { dialog, _ ->
                countDownTimer?.cancel()
                dialog.dismiss()
                selectedHook.url?.let { webIntent(it) }
            }
        }

        val dialog = dialogBuilder.create()

        dialog.setOnDismissListener {
            countDownTimer?.cancel()
        }

        // CountDownTimer 시작
        countDownTimer = object : CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                messageTextView.text = createSpannableMessage(secondsRemaining)
            }

            override fun onFinish() {
                dialog.dismiss()
                selectedHook.url?.let { webIntent(it) }
            }
        }.start()

        dialog.show()
    }

    private fun webIntent(url: String) {
        Intent(requireContext(), WebviewActivity::class.java).also { intent ->
            intent.putExtra(WebviewActivity.EXTRA_URL, url)
            startActivity(intent)
        }
    }

    private fun showBottomSheetDialog(selectedItem: Hook) {
        val dialog = BottomSheetDialog(requireContext(), R.style.CustomBottomSheetDialogTheme)
        val view = layoutInflater.inflate(R.layout.bottom_dialog_home, null)
        dialog.setContentView(view)
        dialog.setCancelable(true)

        val btonWeb = view.findViewById<Button>(R.id.bt_onWeb)
        btonWeb.setOnClickListener {
            Intent(requireContext(), HookDetailActivity::class.java).also { intent ->
                intent.putExtra("item_id", selectedItem.id.toString())
                intent.putExtra("item_title", selectedItem.title)
                intent.putExtra("item_url", selectedItem.url)
                intent.putExtra("item_description", selectedItem.description)
                selectedItem.tags?.map { it.displayName }?.let {
                    intent.putStringArrayListExtra("item_tag_list", ArrayList(it))
                }
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
                hookViewModel.deleteSuccessData.removeObservers(this)
                hookViewModel.errorData.removeObservers(this)
            }
            dialog.dismiss()
        }
        dialog.show()
    }
}