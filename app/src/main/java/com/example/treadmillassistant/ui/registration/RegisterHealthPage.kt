package com.example.treadmillassistant.ui.registration

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.treadmillassistant.MainActivity
import com.example.treadmillassistant.backend.*
import com.example.treadmillassistant.databinding.RegisterPageHealthLayoutBinding

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
                user = User(trainingCalendar, email, password,firstName,lastName, username, age, weight)
                user.treadmillList.add(Treadmill())
                user.trainingPlanList = trainingPlanList
                val intent = Intent(this, MainActivity::class.java)
                finishAffinity()
                startActivity(intent)
            }
            else{
                Toast.makeText(this, "Fill in the fields!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}