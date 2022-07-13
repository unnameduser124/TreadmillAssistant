package com.example.treadmillassistant

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.treadmillassistant.backend.User
import com.example.treadmillassistant.backend.loggedIn
import com.example.treadmillassistant.backend.user
import com.example.treadmillassistant.backend.workout.WorkoutCalendar
import com.example.treadmillassistant.backend.workoutCalendar
import com.example.treadmillassistant.databinding.RegisterPageLayoutBinding

class RegisterPage: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = RegisterPageLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerButton.setOnClickListener{
            val firstName = binding.firstNameInput.text.toString()
            val lastName = binding.lastNameInput.text.toString()
            val username = binding.usernameInput.text.toString()
            val email = binding.emailInput.text.toString()
            val password = hashMessage(binding.passwordInput.text.toString())
            var age = 0
            if(binding.ageInput.text.toString()!=""){
                age = binding.ageInput.text.toString().toInt()
            }
            var weight = 0.0
            if(binding.weightInput.text.toString()!=""){

                weight = binding.weightInput.text.toString().toDouble()
            }
            
            if(firstName!="" && lastName!="" && username!="" && email!="" && password!="" && age!=0 && weight!=0.0 ) {
                user = User(workoutCalendar, email, password,firstName,lastName, username, age, weight)
                loggedIn = true
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            else{
                Toast.makeText(this, "Fill in the fields!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}