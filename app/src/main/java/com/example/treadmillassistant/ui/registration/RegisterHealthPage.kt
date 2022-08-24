package com.example.treadmillassistant.ui.registration

import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.treadmillassistant.MainActivity
import com.example.treadmillassistant.backend.*
import com.example.treadmillassistant.backend.localDatabase.UserService
import com.example.treadmillassistant.backend.serverDatabase.databaseClasses.ServerUser
import com.example.treadmillassistant.backend.serverDatabase.serverDatabaseService.ServerUserService
import com.example.treadmillassistant.backend.serverDatabase.serverDatabaseService.StatusCode
import com.example.treadmillassistant.databinding.RegisterPageHealthLayoutBinding
import kotlin.concurrent.thread

class RegisterHealthPage: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = RegisterPageHealthLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var firstName = intent.getStringExtra("firstName")
        if(firstName==null){
            firstName=""
        }
        var lastName = intent.getStringExtra("lastName")
        if(lastName==null){
            lastName=""
        }
        var username = intent.getStringExtra("username")
        if(username==null){
            username=""
        }
        var email = intent.getStringExtra("email")
        if(email==null){
            email=""
        }
        var password = intent.getStringExtra("password")
        if(password==null){
            password=""
        }

        binding.registerButton.setOnClickListener {
            var age = 0
            if(binding.ageInput.text.toString()!=""){
                age = binding.ageInput.text.toString().toInt()
            }
            var weight = 0.0
            if(binding.weightInput.text.toString()!=""){
                weight = binding.weightInput.text.toString().toDouble()
            }

            if(firstName!="" && lastName!="" && username!="" && email!="" && password!="" && age!=0 && weight!=0.0) {
                thread{
                    //user = User(trainingCalendar, email, password,firstName,lastName, username, age, weight)
                    val newUser = ServerUser(firstName, lastName, username, age, weight, email, password)
                    val responseCode = ServerUserService().createUser(newUser)
                    println(responseCode)
                    if(responseCode==StatusCode.Created){
                        val serverUser = ServerUserService().getUser(user.ID)
                        user = User(serverUser.second[0], user.ID)
                        UserService(this).insertNewUser(user)
                        val intent = Intent(this, MainActivity::class.java)
                        finishAffinity()
                        startActivity(intent)
                    }
                    else{
                        Looper.prepare()
                        Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show()
                    }

                }
            }
            else{
                Toast.makeText(this, "Fill in the fields!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}