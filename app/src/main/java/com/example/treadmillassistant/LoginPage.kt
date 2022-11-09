package com.example.treadmillassistant

import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.treadmillassistant.backend.localDatabase.UserService
import com.example.treadmillassistant.backend.serverDatabase.serverDatabaseService.ServerUserService
import com.example.treadmillassistant.backend.serverDatabase.serverDatabaseService.StatusCode
import com.example.treadmillassistant.backend.user
import com.example.treadmillassistant.databinding.LoginPageLayoutBinding
import com.example.treadmillassistant.ui.registration.RegisterNamesPage
import java.security.MessageDigest
import kotlin.concurrent.thread

class LoginPage: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = LoginPageLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        binding.signInButton.setOnClickListener { 
            val email = binding.emailInput.text.toString()
            val passwordHash = binding.passwordInput.text.toString()
            thread{
                val responseCode = ServerUserService().checkUserCredentials(email, passwordHash)
                if(responseCode == StatusCode.OK){
                    val serverUser = ServerUserService().getUser(user.ID)
                    if(serverUser.first == StatusCode.OK){
                        user = serverUser.second
                        UserService(this).insertNewUser(user)
                        finishAffinity()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                    else{
                        Looper.prepare()
                        Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                    }

                }
                else{
                    Looper.prepare()
                    Toast.makeText(this, "Wrong username or password!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.register.setOnClickListener {
            val intent = Intent(this, RegisterNamesPage::class.java)
            startActivity(intent)
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
    return digest.fold("") { str, it -> str + "%02x".format(it) }
}