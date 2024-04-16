package com.hanto.Hook
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.DialogFragment
import com.hanto.Hook.databinding.FragmentChangeTagBinding


class ChangeTagFragment : DialogFragment() {

    private var _binding: FragmentChangeTagBinding? = null
    private val binding get() = _binding!!

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
