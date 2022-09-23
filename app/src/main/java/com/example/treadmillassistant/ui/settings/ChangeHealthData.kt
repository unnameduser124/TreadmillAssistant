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
import com.example.treadmillassistant.databinding.ChangeHealthDataLayoutBinding
import kotlin.concurrent.thread

class ChangeHealthData: AppCompatActivity() {

    private lateinit var binding: ChangeHealthDataLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ChangeHealthDataLayoutBinding.inflate(layoutInflater)

        binding.changeAgeInput.setText(user.age.toString())
        binding.changeWeightInput.setText(user.weight.toString())

        binding.changeHealthDataButton.setOnClickListener {
            val age = binding.changeAgeInput.text.toString().toInt()
            val weight = binding.changeWeightInput.text.toString().toDouble()

            val updatedValues = ServerUser("", "", "", age, weight, "", "")

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