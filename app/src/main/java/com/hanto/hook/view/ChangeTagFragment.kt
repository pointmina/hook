package com.hanto.hook.view

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.DialogFragment
import com.hanto.hook.databinding.FragmentChangeTagBinding
import androidx.lifecycle.ViewModelProvider
<<<<<<< HEAD
import com.hanto.hook.api.ApiServiceManager
import com.hanto.hook.viewmodel.MainViewModel
import com.hanto.hook.viewmodel.ViewModelFactory


=======
import com.hanto.hook.R
import com.hanto.hook.api.ApiServiceManager
import com.hanto.hook.viewmodel.MainViewModel
import com.hanto.hook.viewmodel.ViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.hanto.hook.api.ErrorResponse
import com.hanto.hook.api.SuccessResponse
import androidx.lifecycle.Observer




>>>>>>> ac08e78431b0c55b0d9a4965df466ed97cdbc5e6
class ChangeTagFragment(private val onTagUpdated: (String) -> Unit) : DialogFragment() {

    private var _binding: FragmentChangeTagBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MainViewModel
    private var selectedTagId: Int = -1

//    private var selectedTag: String? = null // 선택된 태그의 이름을 저장할 변수 추가

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangeTagBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val apiServiceManager = ApiServiceManager()
        val viewModelFactory = ViewModelFactory(apiServiceManager)
<<<<<<< HEAD
        viewModel = ViewModelProvider(requireActivity(), viewModelFactory)[MainViewModel::class.java]
=======
        viewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(MainViewModel::class.java)
>>>>>>> ac08e78431b0c55b0d9a4965df466ed97cdbc5e6

        val selectedTag = arguments?.getString("selectedTag")
        selectedTagId = arguments?.getInt("selectedTagId", -1) ?: -1

<<<<<<< HEAD
        binding.tvChangeTagName.text =
            selectedTag?.let { Editable.Factory.getInstance().newEditable(it) }

//        val changeTagName = binding.tvChangeTagName
//        changeTagName.setOnClickListener {
//            showKeyboardAndFocus(changeTagName)
//        }

        binding.btnChangeTagName.setOnClickListener {
            val newTagName = binding.tvChangeTagName.text.toString()
            if (newTagName.isNotEmpty() && selectedTagId != -1) {
                updateTagName(selectedTagId, newTagName)
            } else {
                Toast.makeText(requireContext(), "태그 이름을 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }
=======
        binding.tvChangeTagName.text = selectedTag?.let { Editable.Factory.getInstance().newEditable(it) }
>>>>>>> ac08e78431b0c55b0d9a4965df466ed97cdbc5e6

//        val changeTagName = binding.tvChangeTagName
//        changeTagName.setOnClickListener {
//            showKeyboardAndFocus(changeTagName)
//        }

        binding.btnChangeTagName.setOnClickListener{
            val newTagName = binding.tvChangeTagName.text.toString()
            if (newTagName.isNotEmpty() && selectedTagId != -1) {
                updateTagName(selectedTagId, newTagName)
            } else {
                Toast.makeText(requireContext(), "태그 이름을 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }

        val changeTagName = binding.tvChangeTagName
        changeTagName.setOnClickListener {
            showKeyboardAndFocus(changeTagName)
        }
<<<<<<< HEAD
        viewModel.successData.observe(viewLifecycleOwner) { successResponse ->
=======
        viewModel.successData.observe(viewLifecycleOwner, Observer { successResponse ->
>>>>>>> ac08e78431b0c55b0d9a4965df466ed97cdbc5e6
            successResponse?.let {
                Toast.makeText(requireContext(), "태그 이름이 수정되었습니다.", Toast.LENGTH_SHORT).show()
                onTagUpdated(binding.tvChangeTagName.text.toString()) // 수정된 태그 이름을 반환
                dismiss()
            }
<<<<<<< HEAD
        }

        viewModel.errorData.observe(viewLifecycleOwner) { errorResponse ->
            errorResponse?.let {
                Toast.makeText(
                    requireContext(),
                    "태그 이름 수정에 실패했습니다: ${it.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
=======
        })

        viewModel.errorData.observe(viewLifecycleOwner, Observer { errorResponse ->
            errorResponse?.let {
                Toast.makeText(requireContext(), "태그 이름 수정에 실패했습니다: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        })
>>>>>>> ac08e78431b0c55b0d9a4965df466ed97cdbc5e6
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun showKeyboardAndFocus(editText: EditText) {
        editText.requestFocus()
        val imm =
            activity?.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }
<<<<<<< HEAD

=======
>>>>>>> ac08e78431b0c55b0d9a4965df466ed97cdbc5e6
    private fun updateTagName(tagId: Int, newTagName: String) {
        viewModel.loadUpdateTag(tagId, newTagName)
    }
}
