package com.example.treadmillassistant.ui.settings

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.treadmillassistant.MainActivity
import com.example.treadmillassistant.backend.localDatabase.UserService
import com.example.treadmillassistant.backend.serverDatabase.databaseClasses.ServerUser
import com.example.treadmillassistant.backend.serverDatabase.serverDatabaseService.ServerUserService
import com.example.treadmillassistant.backend.serverDatabase.serverDatabaseService.StatusCode
import com.example.treadmillassistant.backend.user
import com.example.treadmillassistant.databinding.ChangeUserDataLayoutBinding
import kotlin.concurrent.thread

class ChangeUserData: AppCompatActivity() {

    private lateinit var binding: ChangeUserDataLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ChangeUserDataLayoutBinding.inflate(layoutInflater)

        binding.changeFirstNameInput.setText(user.firstName)
        binding.changeLastNameInput.setText(user.lastName)
        binding.changeUsernameInput.setText(user.username)

        binding.changeUserDataButton.setOnClickListener {
            var fName = ""
            var lName = ""
            var username = ""
            if(binding.changeFirstNameInput.text.toString()!=user.firstName){
                fName = binding.changeFirstNameInput.text.toString()
            }
            if(binding.changeLastNameInput.text.toString()!=user.lastName){
                lName = binding.changeLastNameInput.text.toString()
            }
            if(binding.changeUsernameInput.text.toString()!=user.username){
                username = binding.changeUsernameInput.text.toString()
            }

            val updatedValues = ServerUser(fName, lName, username, -1, -1.0, "", "")

            thread{
                val response = ServerUserService().updateUser(updatedValues, user.ID)
                if(response == StatusCode.OK){
                    val serverUser = ServerUserService().getUser(user.ID)
                    if(serverUser.first == StatusCode.OK){
                        user = serverUser.second
                        UserService(this).updateUser(user, user.ID)
                        finishAffinity()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
        }

        setContentView(binding.root)
    }
}