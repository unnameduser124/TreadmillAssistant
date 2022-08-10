package com.example.treadmillassistant

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.treadmillassistant.backend.User
import com.example.treadmillassistant.backend.localDatabase.TrainingDatabaseService
import com.example.treadmillassistant.backend.tempUser
import com.example.treadmillassistant.backend.user
import com.example.treadmillassistant.databinding.ProfilePageLayoutBinding

class ProfilePage: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ProfilePageLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.emailTextView.text = user.email

        binding.totalDistanceValue.text = String.format(getString(R.string.distance), user.getTotalDistance())
        binding.totalDurationValue.text = String.format(getString(R.string.duration_hours), user.getTotalDuration())
        binding.totalTrainingNumberValue.text = "${user.trainingSchedule.trainingList.size}"
        binding.longestDistanceTrainingValue.text = String.format(getString(R.string.distance), user.getLongestDistance())
        binding.longestDurationTrainingValue.text = String.format(getString(R.string.duration_hours), user.getLongestDuration())
        binding.fullNameTextView.text = String.format(getString(R.string.full_name), user.firstName, user.lastName)
        binding.usernameTextView.text = user.username
        binding.weightTextView.text = String.format(getString(R.string.weight), user.weight)
        binding.ageTextView.text = user.age.toString()
        binding.totalCaloriesTrainingValue.text = String.format(getString(R.string.calories), user.getTotalCalories())

        //doesn't do anything right now TODO("Make it clear tables in database")
        binding.logOutButton.setOnClickListener{
            TrainingDatabaseService(this).clearDatabase()
            tempUser = null
            val intent = Intent(this, LoginPage::class.java)
            finishAffinity()
            startActivity(intent)
        }
    }
}