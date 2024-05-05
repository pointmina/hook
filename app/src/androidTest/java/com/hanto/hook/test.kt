package com.hanto.hook.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.hanto.apitest.HookViewModel
import com.hanto.hook.adapter.HookAdapter
import com.hanto.hook.R
import com.hanto.hook.databinding.FragmentHomeBinding
import com.hanto.hook.model.Hook

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HookViewModel
    private var currentSelectedItem: Hook? = null  // 여기서 Item 타입은 가정한 것입니다. 실제 타입을 사용하세요.

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.ivAppbarAddHook.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_addHookActivity)
        }

        binding.ivSetting.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_settingActivity)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ViewModel 초기화
        viewModel = ViewModelProvider(this)[HookViewModel::class.java]

        // 데이터 로드
        viewModel.getAllData()

        // LiveData 관찰자 설정
        viewModel.result.observe(viewLifecycleOwner) { itemList ->
            // 다음 코드 블록에서 hookAdapter는 HookAdapter의 인스턴스입니다.
            HookAdapter(requireContext(), itemList, object : HookAdapter.OnItemClickListener {
                // 아이템 단일 클릭 시 이벤트 처리
                override fun onClick(position: Int) {
                    // 'itemList'에서 선택한 위치의 Hook 객체 가져오기
                    val selectedHook = itemList[position]

                    // Intent 생성하고 URL을 Extra로 추가
                    Intent(requireContext(), WebviewActivity::class.java).also { intent ->
                        intent.putExtra(WebviewActivity.EXTRA_URL, selectedHook.url)  // 'selectedHook.url'은 Hook 객체의 URL 속성입니다.
                        startActivity(intent)
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
        }
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
                val intent = Intent(requireContext(), HookDetailActivity::class.java).apply {
                    putExtra("item_title", item.title)  // selectedItem을 사용
                    putExtra("item_url", item.url)
                    putExtra("item_description", item.description)
                }
                startActivity(intent)
                dialog.dismiss()
            }

            // 버튼 클릭 리스너 설정 - 예시
            val btHookShare = view.findViewById<Button>(R.id.bt_HookShare)
            btHookShare.setOnClickListener {
                // Get the title and URL of the selected item
                val itemTitle = selectedItem?.title ?: ""
                val itemUrl = selectedItem.url ?: ""

                // Construct the content to be shared
                val shareContent = "내 Hook을 공유했어요.\n링크를 확인해주세요!"

                // Create the share intent
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "$shareContent\n\n$itemUrl")
                    type = "text/plain"
                }

                // Show the app chooser to the user
                val chooserTitle = "친구에게 공유하기"
                startActivity(Intent.createChooser(shareIntent, chooserTitle))
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
