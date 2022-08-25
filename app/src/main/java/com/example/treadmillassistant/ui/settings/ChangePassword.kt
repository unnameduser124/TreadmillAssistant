package com.example.treadmillassistant.ui.settings

import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.treadmillassistant.MainActivity
import com.example.treadmillassistant.backend.serverDatabase.serverDatabaseService.ServerUserService
import com.example.treadmillassistant.backend.serverDatabase.serverDatabaseService.StatusCode
import com.example.treadmillassistant.backend.user
import com.example.treadmillassistant.databinding.ChangePasswordLayoutBinding
import com.example.treadmillassistant.hashMessage
import kotlin.concurrent.thread

class ChangePassword: AppCompatActivity() {

    private lateinit var binding: ChangePasswordLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ChangePasswordLayoutBinding.inflate(layoutInflater)

        binding.changePasswordButton.setOnClickListener {
            thread{
                val oldPassword = binding.oldPasswordInput.text.toString()
                val credentialsConfirmation = ServerUserService().checkUserCredentials(user.email, oldPassword)
                if(credentialsConfirmation == StatusCode.OK){
                    if(binding.newPasswordInput.text.toString().length<8 || binding.newPasswordInput.text.toString().length > 20){
                        Looper.prepare()
                        Toast.makeText(this, "Password has to be between 8 and 20 characters long!", Toast.LENGTH_SHORT).show()
                        return@thread
                    }
                    val newPassword = hashMessage(binding.newPasswordInput.text.toString())
                    val confirmPassword = hashMessage(binding.confirmPasswordInput.text.toString())
                    if(newPassword == confirmPassword){
                        val changePasswordResponseCode = ServerUserService().updateUserPassword(newPassword, user.ID)
                        if(changePasswordResponseCode == StatusCode.OK){
                            Looper.prepare()
                            Toast.makeText(this, "Password changed!", Toast.LENGTH_SHORT).show()
                            finishAffinity()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        }
                    }
                }
                else{
                    Looper.prepare()
                    Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show()
                }

            }
        }

        setContentView(binding.root)
    }
}