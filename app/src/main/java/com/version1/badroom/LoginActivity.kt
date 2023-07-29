package com.version1.badroom

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.version1.badroom.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.welcome.hasSelection()
        val login = binding.login
        val loading = binding.loading
        login.setOnClickListener {
            loading.visibility = View.VISIBLE

            loginStorage()
            checkUsernamePassword( binding.username.text.toString(), binding.password.text.toString())
        }
    }
    private fun loginStorage(){
        val sharedPreferences = getSharedPreferences("my_app", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("username", binding.username.text.toString())
        editor.putString("password",binding.password.text.toString())
        editor.putString("role","officer")
        editor.apply()
    }

    private fun checkUsernamePassword(username:String,password:String) {
        db.collection("accounts")
            .whereEqualTo("username", username)
            .whereEqualTo("password", password)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    if (document.data["role"] == "officer") {
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                    }else{
                        Toast.makeText(applicationContext,"እባኮ እንደገና ያስገቡ!!", Toast.LENGTH_SHORT).show()
                    }
                    }
                }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents.", exception)
            }
    }

}
