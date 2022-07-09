package com.example.treadmillassistant

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.treadmillassistant.backend.loggedIn
import com.example.treadmillassistant.backend.user
import com.example.treadmillassistant.databinding.ProfilePageLayoutBinding

class ProfilePage: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ProfilePageLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.emailTextView.text = user.email

        var totalDistance = 0.0
        var longestDistance = 0.0
        var longestDuration = 0
        var totalDuration = 0
        user.workoutSchedule.workoutList.forEach {
            totalDistance+=it.workoutPlan.getTotalDistance()
            totalDuration+=it.workoutPlan.getTotalDuration()
            if(it.workoutPlan.getTotalDistance()>longestDistance){

                longestDistance = it.workoutPlan.getTotalDistance()
            }
            if(it.workoutPlan.getTotalDuration()>longestDuration){
                longestDuration = it.workoutPlan.getTotalDuration()
            }
        }
        val durationInHours = Math.round((totalDuration.toDouble()/3600.toDouble())*10.0)/10.0

        binding.totalDistanceValue.text = "${Math.round(totalDistance*10.0)/10.0}km"
        binding.totalDurationValue.text = "${durationInHours}h"
        binding.totalWorkoutNumberValue.text = "${user.workoutSchedule.workoutList.size}"
        binding.longestDistanceWorkoutValue.text = "${Math.round(longestDistance*10.0)/10.0}km"
        binding.longestDurationWorkoutValue.text = "${Math.round((longestDuration.toDouble()/3600.toDouble())*10.0)/10.0}h"

        binding.logOutButton.setOnClickListener{
            loggedIn = false
            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
        }
    }
}