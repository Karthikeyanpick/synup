package com.example.synup.ui.intro

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.synup.ui.main.MainActivity
import com.example.synup.ui.util.launchActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        launchActivity<MainActivity>().also { finish() }
    }
}