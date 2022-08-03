package com.example.treadmillassistant.ui.home.trainingTab

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.treadmillassistant.backend.*
import com.example.treadmillassistant.backend.workout.Workout
import com.example.treadmillassistant.backend.workout.WorkoutPhase
import com.example.treadmillassistant.backend.workout.WorkoutStatus
import com.example.treadmillassistant.databinding.TrainingTabBinding
import com.example.treadmillassistant.ui.home.OnStartClickedListener
import com.example.treadmillassistant.ui.home.PageViewModel
import com.google.android.material.button.MaterialButton

import java.util.*

class TrainingTabPlaceholderFragment: Fragment(), OnStartClickedListener {
    private lateinit var pageViewModel: PageViewModel

    private var workout: Workout = Workout()
    private var treadmill: Treadmill = Treadmill()
    private lateinit var binding: TrainingTabBinding
    private var fromCalendar = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProvider(this).get(PageViewModel::class.java).apply {
            setIndex(arguments?.getInt(SECTION_NUMBER) ?: 1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = TrainingTabBinding.inflate(layoutInflater)
        startClicked = this
        if(user.treadmillList.isEmpty()){
            treadmill=Treadmill()
        }
        else{
            treadmill = user.treadmillList.first()
        }

        if(workout.ID!=0L){

        }
        else{
            if(user.workoutSchedule.workoutList.isEmpty()){
                user.workoutSchedule.workoutList.sortBy { it.ID }
                workout = Workout(
                    treadmill = treadmill, workoutTime = Calendar.getInstance(),
                    workoutStatus = WorkoutStatus.Upcoming, ID = 1)
                user.workoutSchedule.sortCalendar()
            }
            else{user.workoutSchedule.workoutList.sortBy { it.ID }
                workout = Workout(
                    treadmill = treadmill, workoutTime = Calendar.getInstance(),
                    workoutStatus = WorkoutStatus.Upcoming, ID = user.workoutSchedule.workoutList.last().ID+1)
                user.workoutSchedule.sortCalendar()

            }

        }



        binding.finishTrainingButton.isGone = true
        hideTrainingItems(binding)

        binding.startTrainingButton.setOnClickListener {
            if(workout.workoutPlan.workoutPhaseList.isEmpty()){
                if(workout.workoutStatus != WorkoutStatus.InProgress && workout.workoutStatus != WorkoutStatus.Finished && workout.workoutStatus!=WorkoutStatus.Paused){
                    showGenericWorkoutItems(binding)
                    workout.startWorkout()
                    binding.speedDisplay.text = "${round(treadmill.getSpeed(), SPEED_ROUND_MULTIPLIER)}"
                    binding.tiltDisplay.text = "${round(treadmill.getTilt(), TILT_ROUND_MULTIPLIER)}"
                    binding.paceTextView.text = "${round((SECONDS_IN_MINUTE.toDouble()/treadmill.getSpeed()), PACE_ROUND_MULTIPLIER)}'"
                    (it as MaterialButton).text = "stop"
                    runTimer(binding, workout)
                }
                else if(workout.workoutStatus == WorkoutStatus.InProgress){
                    (it as MaterialButton).text = "resume"
                    binding.finishTrainingButton.isGone = false
                    workout.pauseWorkout()
                }
                else{
                    (it as MaterialButton).text = "stop"
                    binding.finishTrainingButton.isGone = true
                    workout.resumeWorkout()
                    runTimer(binding, workout)
                }
            }
            else{
                if(workout.workoutStatus != WorkoutStatus.InProgress && workout.workoutStatus != WorkoutStatus.Finished && workout.workoutStatus!=WorkoutStatus.Paused){
                    showTrainingItems(binding)
                    workout.startWorkout()
                    binding.speedDisplay.text = "${round(treadmill.getSpeed(), SPEED_ROUND_MULTIPLIER)}"
                    binding.tiltDisplay.text = "${round(treadmill.getTilt(), TILT_ROUND_MULTIPLIER)}"
                    binding.paceTextView.text = "${round((SECONDS_IN_MINUTE.toDouble()/treadmill.getSpeed()), PACE_ROUND_MULTIPLIER)}'"
                    binding.progressTextView.text = "${round(
                        workout.getCurrentMoment().toDouble()/workout.getTotalDuration().toDouble(),
                        PERCENTAGE_ROUND_MULTIPLIER)}%"
                    (it as MaterialButton).text = "stop"
                    runTimer(binding, workout)
                }

            }
        }

        binding.finishTrainingButton.setOnClickListener{
            if(workout.workoutStatus==WorkoutStatus.Paused){
                workout.finishWorkout()
                user.workoutSchedule.addNewWorkout(workout)
                user.workoutSchedule.workoutList.sortBy { it.ID }
                workout = Workout(treadmill = treadmill, workoutStatus = WorkoutStatus.Upcoming, ID = user.workoutSchedule.workoutList.last().ID+1)
                user.workoutSchedule.sortCalendar()
                hideTrainingItems(binding)
                it.isGone = true
                binding.startTrainingButton.text = "start"
            }
        }

        binding.speedUpButton.setOnClickListener{
            if(workout.workoutStatus==WorkoutStatus.InProgress){
                workout.speedUp()
                binding.speedDisplay.text = "${round(treadmill.getSpeed(), SPEED_ROUND_MULTIPLIER)}"
                binding.paceTextView.text = "${round((SECONDS_IN_MINUTE.toDouble()/treadmill.getSpeed()), PACE_ROUND_MULTIPLIER)}'"
            }
        }
        binding.speedDownButton.setOnClickListener{
            if(workout.workoutStatus==WorkoutStatus.InProgress){
                workout.speedDown()
                binding.speedDisplay.text = "${round(treadmill.getSpeed(), SPEED_ROUND_MULTIPLIER)}"
                binding.paceTextView.text = "${round((SECONDS_IN_MINUTE.toDouble()/treadmill.getSpeed()), PACE_ROUND_MULTIPLIER)}'"
            }

        }

        binding.tiltUpButton.setOnClickListener{
            if(workout.workoutStatus==WorkoutStatus.InProgress){
                workout.tiltUp()
                binding.tiltDisplay.text = "${round(treadmill.getTilt(), TILT_ROUND_MULTIPLIER)}"
            }

        }
        binding.tiltDownButton.setOnClickListener{
            if(workout.workoutStatus==WorkoutStatus.InProgress){
                workout.tiltDown()
                binding.tiltDisplay.text = "${round(treadmill.getTilt(), TILT_ROUND_MULTIPLIER)}"
            }

        }


        return binding.root
    }

    private fun hideTrainingItems(binding: TrainingTabBinding){
        binding.distanceTextView.isGone = true
        binding.paceTextView.isGone = true
        binding.timeTextView.isGone = true
        binding.progressTextView.isGone = true
        binding.distanceLabel.isGone = true
        binding.paceLabel.isGone = true
        binding.timeLabel.isGone = true
        binding.progressLabel.isGone = true
        binding.caloriesLabel.isGone = true
        binding.caloriesTextView.isGone = true

    }
    fun showTrainingItems(binding: TrainingTabBinding){
        binding.distanceTextView.isGone = false
        binding.paceTextView.isGone = false
        binding.timeTextView.isGone = false
        binding.progressTextView.isGone = false
        binding.distanceLabel.isGone = false
        binding.paceLabel.isGone = false
        binding.timeLabel.isGone = false
        binding.progressLabel.isGone = false
        binding.caloriesLabel.isGone = false
        binding.caloriesTextView.isGone = false
    }

    private fun hideGenericWorkoutItems(binding: TrainingTabBinding){
        binding.distanceTextView.isGone = true
        binding.paceTextView.isGone = true
        binding.timeTextView.isGone = true
        binding.distanceLabel.isGone = true
        binding.paceLabel.isGone = true
        binding.timeLabel.isGone = true
        binding.caloriesLabel.isGone = true
        binding.caloriesTextView.isGone = true
    }
    private fun showGenericWorkoutItems(binding: TrainingTabBinding){
        binding.distanceTextView.isGone = false
        binding.paceTextView.isGone = false
        binding.timeTextView.isGone = false
        binding.distanceLabel.isGone = false
        binding.paceLabel.isGone = false
        binding.timeLabel.isGone = false
        binding.caloriesLabel.isGone = false
        binding.caloriesTextView.isGone = false
    }


    private fun runTimer(binding: TrainingTabBinding, workout: Workout){
        if(fromCalendar){
            val handler = Handler()
            var runnableCode = object: Runnable {
                override fun run() {
                    if(workout.workoutStatus == WorkoutStatus.InProgress){
                        binding.timeTextView.text = "${workout.getCurrentMoment()} s"
                        binding.distanceTextView.text = "${workout.getCurrentDistance()} km"
                        binding.caloriesTextView.text = "${calculateCaloriesForOngoingWorkout(workout.getCurrentMoment())} kcal"
                        binding.progressTextView.text = "${round(
                            workout.getCurrentMoment().toDouble()/workout.getTotalDuration().toDouble(), 
                            PERCENTAGE_ROUND_MULTIPLIER)}%"
                        if(workout.getCurrentPhase().speed!=treadmill.getSpeed() || workout.getCurrentPhase().tilt!=treadmill.getTilt()){
                            treadmill.setSpeed(workout.getCurrentPhase().speed)
                            treadmill.setTilt(workout.getCurrentPhase().tilt)
                            binding.speedDisplay.text = "${round(treadmill.getSpeed(), SPEED_ROUND_MULTIPLIER)}"
                            binding.paceTextView.text = "${round((SECONDS_IN_MINUTE.toDouble()/treadmill.getSpeed()), PACE_ROUND_MULTIPLIER)}'"
                            binding.tiltDisplay.text = "${round(treadmill.getTilt(), TILT_ROUND_MULTIPLIER)}"
                        }
                        if(workout.getCurrentMoment()>=workout.getTotalDuration()){
                            workout.finishWorkout()
                            binding.startTrainingButton.text = "workout finished, start new?"
                        }

                        handler.postDelayed(this, 250)
                    }
                }
            }
            handler.post(runnableCode)
        }
        else{
            val handler = Handler()
            var runnableCode = object: Runnable {
                override fun run() {
                    if(workout.workoutStatus == WorkoutStatus.InProgress){
                        binding.timeTextView.text = "${workout.getCurrentMoment()} s"
                        binding.distanceTextView.text = "${workout.getCurrentDistance()} km"
                        binding.caloriesTextView.text = "${calculateCaloriesForOngoingWorkout(workout.getCurrentMoment())} kcal"
                        handler.postDelayed(this, 250)
                    }
                }
            }
            handler.post(runnableCode)
        }

    }



    companion object{

        private const val SECTION_NUMBER = "section number"

        @JvmStatic
        fun newInstance(sectionNumber: Int): TrainingTabPlaceholderFragment {
            return TrainingTabPlaceholderFragment().apply{
                arguments = Bundle().apply{
                    putInt(SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }

    override fun onStartClicked(trainingID: Long) {
        val workoutFound = user.workoutSchedule.getWorkout(trainingID)
        if(workoutFound!=null){
            workout = workoutFound
            treadmill = workout.treadmill
            fromCalendar = true
            binding.startTrainingButton.callOnClick()
        }
    }
}