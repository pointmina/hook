package com.hanto.Hook.view

import android.content.ClipboardManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hanto.Hook.databinding.ActivityWriteHookBinding

class WriteHookActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWriteHookBinding // 뷰 바인딩
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWriteHookBinding.inflate(layoutInflater) // inflate 메소드 호출
        val view = binding.root //root View는 레이아웃에서 가장 바깥쪽의 View Container
        setContentView(view) // 레이아웃 바인딩 완료

        binding.btBack.setOnClickListener{
            finish()
        }

        // 버튼에 클릭 리스너를 설정합니다.
        binding.btPasteLink.setOnClickListener {
            // 클립보드 매니저를 가져옵니다.
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager

            // 클립보드에 현재 저장된 항목을 조회합니다.
            if (clipboard.hasPrimaryClip()) {
                val clipData = clipboard.primaryClip
                if (clipData != null && clipData.itemCount > 0) {
                    // 클립보드의 첫 번째 항목의 텍스트 데이터를 가져옵니다.
                    val item = clipData.getItemAt(0)
                    val pasteData = item.text

                    // 가져온 데이터가 URL인지 검증합니다. 간단한 예로, http/https로 시작하는 텍스트를 URL로 가정합니다.
                    if (pasteData != null && (pasteData.startsWith("http://") || pasteData.startsWith("https://"))) {
                        // URL을 EditText에 붙여넣습니다.
                        binding.tvHandedUrl.setText(pasteData)
                        Toast.makeText(this, "가장 최근에 복사한 URL을 가져왔어요!", Toast.LENGTH_SHORT).show()
                    } else {
                        // URL이 아니거나 클립보드에 텍스트가 없는 경우의 처리를 합니다.
                        Toast.makeText(this, "클립보드에 유효한 URL이 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                // 클립보드에 아무런 데이터도 없는 경우의 처리를 합니다.
                Toast.makeText(this, "클립보드가 비어 있습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        // 글자수 세는 거 //
        binding.tvLimit1.text = "${binding.tvHandedTitle.text.length} / 50"
        binding.tvLimit2.text = "${binding.tvHandedDesc.text.length} / 80"


        binding.tvHandedTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    binding.tvLimit1.text = "${s.length} / 50"
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        binding.tvHandedDesc.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    binding.tvLimit2.text = "${s.length} / 80"
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }
}