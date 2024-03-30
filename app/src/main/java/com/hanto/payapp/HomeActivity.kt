package com.hanto.payapp

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toolbar
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hanto.payapp.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    @SuppressLint("CommitTransaction")

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setBottomNavigation()

//        findViewById<BottomNavigationView>(R.id.bottom_navigation_home).setupWithNavController(
//            navController
//        )

//        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_home)
//        bottomNavigationView.setOnItemSelectedListener { item ->
//            when (item.itemId) {
//                R.id.navigation_home -> {
//                    //프레그먼트로 전환, 커밋 필수;
//                    val transaction = supportFragmentManager.beginTransaction()
//                    transaction.replace(R.id.container_home, HomeFragment())
//                    transaction.commit()
//                    true
//                }
//
//                R.id.navigation_transfer -> {
//                    //프레그먼트로 전환
//                    val transaction = supportFragmentManager.beginTransaction()
//                    transaction.replace(R.id.container_home, TransferFragment())
//                    transaction.commit()
//                    true
//                }
//                else -> false
//            }
//        }
    }

    private fun setBottomNavigation() {
        //액티비티 홈 안의 bottom_navigation_home에 접근 요기는 fragment에 관한 접근이기때문에 viewBinding x
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.container_home) as NavHostFragment

        val navController = navHostFragment.navController
        binding.bottomNavigationHome.setupWithNavController(navController)
    }
}