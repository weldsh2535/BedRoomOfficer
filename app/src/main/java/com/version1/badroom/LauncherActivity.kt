package com.version1.badroom

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import com.version1.badroom.prefreces.Constants


class LauncherActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val i: Intent
        val pref = getSharedPreferences(Constants.APPLICATION_TAG, MODE_PRIVATE)
        val username = pref.getString("username", "")?.toString()
        val password = pref.getString("password", "")?.toString()
        if (username!!.isEmpty() && password!!.isEmpty())
      {
            i = Intent(this@LauncherActivity, LoginActivity::class.java)
            startActivity(i)
        } else {
            i = Intent(this@LauncherActivity, MainActivity::class.java)
            startActivity(i)
        }
        finish()
    }
}