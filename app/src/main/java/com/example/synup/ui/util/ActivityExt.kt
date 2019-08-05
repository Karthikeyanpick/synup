package com.example.synup.ui.util

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat


inline fun <reified T : Activity> Activity.launchActivity(option: ActivityOptionsCompat? = null) {

    val intent = Intent(this, T::class.java)
    if (option == null) {
        startActivity(intent)
    } else {
        ActivityCompat.startActivity(this, intent, option.toBundle())
    }
}



inline fun <reified T : Activity> Activity.launchActivity(id: String, name: String? = "") {
    val intent = Intent(this, T::class.java)
    intent.putExtra("id", id)
    intent.putExtra("name", id)
    startActivity(intent)
}

inline fun <reified T : Activity> Activity.launchActivity(key: String, value: Boolean) {
    val intent = Intent(this, T::class.java)
    intent.putExtra(key, value)
    startActivity(intent)
}

inline fun <reified T : Activity> Activity.launchActivity(bundle: Bundle) {
    val intent = Intent(this, T::class.java)
    intent.putExtras(bundle)
    startActivity(intent)
}

fun AppCompatActivity.replaceFragmentInActivity(
    fragment: androidx.fragment.app.Fragment,
    frameId: Int
) {
    supportFragmentManager.transact {
        replace(frameId, fragment)
//        addToBackStack(null)
    }
}

/**
 * Runs a FragmentTransaction, then calls commit().
 */
private inline fun androidx.fragment.app.FragmentManager.transact(action: androidx.fragment.app.FragmentTransaction.() -> Unit) {
    beginTransaction().apply {
        action()
    }.commit()
}

fun AppCompatActivity.setupActionBar(@IdRes toolbarId: Int, action: ActionBar.() -> Unit) {
    setSupportActionBar(findViewById(toolbarId))
    supportActionBar?.run {
        action()
    }
}