package com.example.treadmillassistant

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.treadmillassistant.backend.localDatabase.TrainingDatabaseService
import com.example.treadmillassistant.backend.serverDatabase.serverDatabaseService.ServerTrainingService
import com.example.treadmillassistant.backend.serverDatabase.serverDatabaseService.StatusCode
import com.example.treadmillassistant.backend.tempUser
import com.example.treadmillassistant.backend.training.TrainingCalendar
import com.example.treadmillassistant.backend.user
import com.example.treadmillassistant.databinding.ProfilePageLayoutBinding
import kotlin.concurrent.thread

class ProfilePage: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ProfilePageLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.emailTextView.text = user.email
        binding.usernameTextView.text = user.username
        binding.weightTextView.text = String.format(getString(R.string.weight), user.weight)
        binding.ageTextView.text = user.age.toString()

        val trainingCal = TrainingCalendar()
        val trainingsLoaded: MutableLiveData<Boolean> by lazy{
            MutableLiveData<Boolean>()
        }
        thread{
            val allTrainingsPair = ServerTrainingService().getAllTrainings()
            if(allTrainingsPair.first== StatusCode.OK){
                trainingCal.trainingList = allTrainingsPair.second

                trainingsLoaded.postValue(true)
            }
        }

        val observer = Observer<Boolean>{
            if(it){
                binding.totalCaloriesTrainingValue.text = String.format(getString(R.string.calories), trainingCal.getTotalCalories())
                binding.totalDistanceValue.text = String.format(getString(R.string.distance), trainingCal.getTotalDistance())
                binding.totalDurationValue.text = String.format(getString(R.string.duration_hours), trainingCal.getTotalDuration())
                binding.totalTrainingNumberValue.text = "${trainingCal.trainingList.size}"
                binding.longestDistanceTrainingValue.text = String.format(getString(R.string.distance), trainingCal.getLongestDistance())
                binding.longestDurationTrainingValue.text = String.format(getString(R.string.duration_hours), trainingCal.getLongestDuration())
                binding.fullNameTextView.text = String.format(getString(R.string.full_name), user.firstName, user.lastName)
            }
        }
        trainingsLoaded.observe(this, observer)


        binding.logOutButton.setOnClickListener{
            TrainingDatabaseService(this).clearDatabase()
            tempUser = null
            val intent = Intent(this, LoginPage::class.java)
            finishAffinity()
            startActivity(intent)
        }
    }
}