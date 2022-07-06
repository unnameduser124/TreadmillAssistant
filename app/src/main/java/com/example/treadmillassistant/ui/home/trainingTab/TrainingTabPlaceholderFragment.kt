package com.example.treadmillassistant.ui.home.trainingTab

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.treadmillassistant.backend.Treadmill
import com.example.treadmillassistant.backend.lastPhaseStart
import com.example.treadmillassistant.backend.trainingRunning
import com.example.treadmillassistant.backend.trainingStartTime
import com.example.treadmillassistant.backend.workout.Workout
import com.example.treadmillassistant.backend.workout.WorkoutPhase
import com.example.treadmillassistant.databinding.TrainingTabBinding
import com.example.treadmillassistant.ui.home.PageViewModel
import com.example.treadmillassistant.ui.home.calendarTab.CalendarPlaceholderFragment
import com.google.android.material.button.MaterialButton
import java.util.*

class TrainingTabPlaceholderFragment: Fragment() {
    private lateinit var pageViewModel: PageViewModel

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
        val binding = TrainingTabBinding.inflate(layoutInflater)
        val treadmill = Treadmill()
        val workout = Workout(treadmill = treadmill)

        hideTrainingItems(binding)

        binding.startTrainingButton.setOnClickListener {
            if(!trainingRunning){
                lastPhaseStart = Calendar.getInstance().timeInMillis
                showGenericWorkoutItems(binding)
                treadmill.setSpeed(10.0)
                treadmill.setTilt(0.0)
                binding.speedDisplay.text = "${Math.round(treadmill.getSpeed()*10.0)/10.0}"
                binding.tiltDisplay.text = "${Math.round(treadmill.getTilt()*10.0)/10.0}"
                binding.paceTextView.text = "${Math.round((60.0/treadmill.getSpeed())*10.0)/10.0}'"
                (it as MaterialButton).text = "stop"
                trainingStartTime = Calendar.getInstance().timeInMillis
                runTimer(binding, workout, treadmill)
            }
            else{
                hideTrainingItems(binding)
                (it as MaterialButton).text = "start"
            }

        }

        binding.speedUpButton.setOnClickListener{
            val lastPhaseTimeInSeconds = (Calendar.getInstance().timeInMillis/1000 - lastPhaseStart/1000).toInt()
            val phase = WorkoutPhase(lastPhaseTimeInSeconds, treadmill.getSpeed(), treadmill.getTilt(), 0, workout.workoutPlan.workoutPhaseList.size)
            workout.workoutPlan.addNewPhase(phase)
            lastPhaseStart = Calendar.getInstance().timeInMillis
            treadmill.increaseSpeed()
            binding.speedDisplay.text = "${Math.round(treadmill.getSpeed()*10.0)/10.0}"
            binding.paceTextView.text = "${Math.round((60.0/treadmill.getSpeed())*10.0)/10.0}'"

        }
        binding.speedDownButton.setOnClickListener{
            val lastPhaseTimeInSeconds = (Calendar.getInstance().timeInMillis/1000 - lastPhaseStart/1000).toInt()
            val phase = WorkoutPhase(lastPhaseTimeInSeconds, treadmill.getSpeed(), treadmill.getTilt(), 0, workout.workoutPlan.workoutPhaseList.size)
            workout.workoutPlan.addNewPhase(phase)
            lastPhaseStart = Calendar.getInstance().timeInMillis
            treadmill.decreaseSpeed()
            binding.speedDisplay.text = "${Math.round(treadmill.getSpeed()*10.0)/10.0}"
            binding.paceTextView.text = "${Math.round((60.0/treadmill.getSpeed())*10.0)/10.0}'"
        }

        binding.tiltUpButton.setOnClickListener{
            val lastPhaseTimeInSeconds = (Calendar.getInstance().timeInMillis/1000 - lastPhaseStart/1000).toInt()
            val phase = WorkoutPhase(lastPhaseTimeInSeconds, treadmill.getSpeed(), treadmill.getTilt(), 0, workout.workoutPlan.workoutPhaseList.size)
            workout.workoutPlan.addNewPhase(phase)
            lastPhaseStart = Calendar.getInstance().timeInMillis
            treadmill.increaseTilt()
            binding.tiltDisplay.text = "${Math.round(treadmill.getTilt()*10.0)/10.0}"
        }
        binding.tiltDownButton.setOnClickListener{
            val lastPhaseTimeInSeconds = (Calendar.getInstance().timeInMillis/1000 - lastPhaseStart/1000).toInt()
            val phase = WorkoutPhase(lastPhaseTimeInSeconds, treadmill.getSpeed(), treadmill.getTilt(), 0, workout.workoutPlan.workoutPhaseList.size)
            workout.workoutPlan.addNewPhase(phase)
            lastPhaseStart = Calendar.getInstance().timeInMillis
            treadmill.decreaseTilt()
            binding.tiltDisplay.text = "${Math.round(treadmill.getTilt()*10.0)/10.0}"
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

    }
    private fun showTrainingItems(binding: TrainingTabBinding){
        binding.distanceTextView.isGone = false
        binding.paceTextView.isGone = false
        binding.timeTextView.isGone = false
        binding.progressTextView.isGone = false
        binding.distanceLabel.isGone = false
        binding.paceLabel.isGone = false
        binding.timeLabel.isGone = false
        binding.progressLabel.isGone = false
    }

    private fun hideGenericWorkoutItems(binding: TrainingTabBinding){
        binding.distanceTextView.isGone = true
        binding.paceTextView.isGone = true
        binding.timeTextView.isGone = true
        binding.distanceLabel.isGone = true
        binding.paceLabel.isGone = true
        binding.timeLabel.isGone = true
    }
    private fun showGenericWorkoutItems(binding: TrainingTabBinding){
        binding.distanceTextView.isGone = false
        binding.paceTextView.isGone = false
        binding.timeTextView.isGone = false
        binding.distanceLabel.isGone = false
        binding.paceLabel.isGone = false
        binding.timeLabel.isGone = false
    }


    private fun runTimer(binding: TrainingTabBinding, workout: Workout, treadmill: Treadmill){
        val handler = Handler()
        var runnableCode = object: Runnable {
            override fun run() {
                    if(binding.startTrainingButton.text=="stop"){
                        val timeInSeconds = Calendar.getInstance().timeInMillis/1000 - trainingStartTime/1000
                        binding.timeTextView.text = "$timeInSeconds"
                        val lastPhaseTimeInHours = (Calendar.getInstance().timeInMillis/1000 - lastPhaseStart/1000).toDouble()/3600.0
                        binding.distanceTextView.text = "${Math.round((workout.workoutPlan.getTotalDistance()+ lastPhaseTimeInHours*treadmill.getSpeed())*100.0)/100.0} km"
                        handler.postDelayed(this, 250)
                    }
                }
            }
        handler.post(runnableCode)
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

}