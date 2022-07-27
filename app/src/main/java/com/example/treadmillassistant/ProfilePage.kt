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

        binding.totalDistanceValue.text = "${user.getTotalDistance()}km"
        binding.totalDurationValue.text = "${user.getTotalDuration()}h"
        binding.totalWorkoutNumberValue.text = "${user.workoutSchedule.workoutList.size}"
        binding.longestDistanceWorkoutValue.text = "${user.getLongestDistance()}km"
        binding.longestDurationWorkoutValue.text = "${user.getLongestDuration()}h"
        binding.fullNameTextView.text = "${user.firstName} ${user.lastName}"
        binding.usernameTextView.text = "${user.username}"
        binding.weightTextView.text = "${user.weight} kg"
        binding.ageTextView.text = "${user.age}"
        binding.totalCaloriesWorkoutValue.text = "${user.getTotalCalories()} kcal"

        binding.logOutButton.setOnClickListener{
            loggedIn = false
            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
        }
    }
}