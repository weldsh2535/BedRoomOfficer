package com.version1.badroom

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
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

        val username = binding.username
        val password = binding.password
        val login = binding.login
        val loading = binding.loading
        login.setOnClickListener {
            loading.visibility = View.VISIBLE
            login()
//            val loadingView: View = LayoutInflater.from(this@LoginActivity).inflate(R.layout.loadinglayout, null)
//            val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this@LoginActivity)
//            builder.setView(loadingView)
//            builder.setCancelable(false)
//            builder.create()?.show()
            checkUsernamePassword(username.text.toString(), password.text.toString())
            //   loading.visibility = View.GONE
            //       builder.create()?.dismiss()
        }
    }
    private fun login() {

        val user = hashMapOf(
            "username" to binding.username.text.toString(),
            "password" to binding.password.text.toString()
        )
        // Add a new document with a generated ID
        db.collection("accounts")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d("weldsh", "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("weldsh", "Error adding document", e)
            }
    }
    private fun checkUsernamePassword(username:String,password:String) {
        db.collection("accounts")
            .whereEqualTo("username", username)
            .whereEqualTo("password", password)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    if (document.data["role"] == "admin") {
                        val intent = Intent(this@LoginActivity,MainActivity::class.java)
                        startActivity(intent)

                    }
                    else{
                        val intent = Intent(this@LoginActivity,MainActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents.", exception)
            }
    }

}
