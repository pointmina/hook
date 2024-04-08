package com.hanto.Hook

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.os.Handler

@Suppress("splash_screen")
@SuppressLint("CustomSplashScreen")
class SplashView : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        // 타이머가 끝나면 내부 실행
        Handler().postDelayed(Runnable {
            // 앱의 LoginActivity로 넘어가기
            val intent = Intent(this@SplashView, LoginActivity::class.java)
            startActivity(intent)
            // 현재 액티비티 닫기
            finish()
        }, 500) // 0.5초
    }
}
