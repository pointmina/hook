package com.hanto.hook.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.hanto.hook.R
import com.hanto.hook.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {



    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setBottomNavigation()

    }

    private fun setBottomNavigation() {
        //액티비티 홈 안의 bottom_navigation_home에 접근 요기는 fragment에 관한 접근이기때문에 viewBinding x
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.container_home) as NavHostFragment

        val navController = navHostFragment.navController
        binding.bottomNavigationHome.setupWithNavController(navController)
    }

    var backPressedTime: Long = 0
    override fun onBackPressed() {
        if (backPressedTime + 3000 > System.currentTimeMillis()) {
            super.onBackPressed()
            finish() //액티비티 종료
        } else {
            Toast.makeText(
                applicationContext, "한 번 더 뒤로가기 버튼을 누르면 종료됩니다.",
                Toast.LENGTH_SHORT
            ).show()
        }
        backPressedTime = System.currentTimeMillis()
    }
}