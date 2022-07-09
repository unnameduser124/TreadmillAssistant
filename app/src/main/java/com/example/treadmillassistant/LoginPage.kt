package com.example.treadmillassistant

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.treadmillassistant.backend.loggedIn
import com.example.treadmillassistant.backend.user
import com.example.treadmillassistant.databinding.LoginPageLayoutBinding
import java.security.MessageDigest

class LoginPage: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = LoginPageLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        binding.signInButton.setOnClickListener { 
            val username = binding.usernameInput.text.toString()
            val passwordHash = hashMessage(binding.passwordInput.text.toString())
            if((username==user.email && passwordHash == user.password) || binding.passwordInput.text.toString() == ""){
                loggedIn = true
                finishAffinity()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            else{
                Toast.makeText(this, "Wrong username or password!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBackPressed() {
        finishAffinity()
        finish()
    }
}

fun hashMessage(message: String): String {
    val bytes = message.toByteArray()
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(bytes)
    return digest.fold("", { str, it -> str + "%02x".format(it) })
}