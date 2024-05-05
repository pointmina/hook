package com.hanto.hook.view

import android.app.Application
import android.util.Log
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.util.Utility

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // 다른 초기화 코드들
        var keyHash = Utility.getKeyHash(this)
        Log.i("GlobalApplication", "$keyHash")

        // Kakao SDK 초기화
        KakaoSdk.init(this, "9e323bbf72e71bd53ab75f947101fa0b")
    }
}