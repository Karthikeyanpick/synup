package com.example.synup.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.synup.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var mToolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        setActionBar()

        titleTv.text = "synup"

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.container,
                    MainFragment()
                )
                .commitNow()
        }
    }

    /**
     * To setup the actionbar for the screen
     */
    private fun setActionBar() {
        mToolbar = findViewById(R.id.toolbar)

        if (mToolbar != null) {
            setSupportActionBar(mToolbar)
        }
    }


}