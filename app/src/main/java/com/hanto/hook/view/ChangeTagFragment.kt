package com.hanto.hook.view

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.hanto.hook.databinding.FragmentChangeTagBinding

class ChangeTagFragment : DialogFragment() {

    private var _binding: FragmentChangeTagBinding? = null
    private val binding get() = _binding!!

    private var selectedTag: String? = null // 선택된 태그의 이름을 저장할 변수 추가

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

        val selectedTag = arguments?.getString("selectedTag")
        binding.tvChangeTagName.text = selectedTag?.let { Editable.Factory.getInstance().newEditable(it) }

        val changeTagName = binding.tvChangeTagName
        changeTagName.setOnClickListener {
            showKeyboardAndFocus(changeTagName)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun showKeyboardAndFocus(editText: EditText) {
        editText.requestFocus()
        val imm = activity?.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }
}
