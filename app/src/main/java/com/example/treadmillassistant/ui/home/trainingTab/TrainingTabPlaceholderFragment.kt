package com.example.treadmillassistant.ui.home.trainingTab

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.treadmillassistant.R
import com.example.treadmillassistant.backend.*
import com.example.treadmillassistant.backend.training.GenericTraining
import com.example.treadmillassistant.backend.training.PlannedTraining
import com.example.treadmillassistant.backend.training.Training
import com.example.treadmillassistant.backend.training.TrainingStatus
import com.example.treadmillassistant.databinding.TrainingTabBinding
import com.example.treadmillassistant.ui.home.OnStartClickedListener
import com.example.treadmillassistant.ui.home.PageViewModel
import com.google.android.material.button.MaterialButton

import java.util.*

class TrainingTabPlaceholderFragment: Fragment(), OnStartClickedListener {
    private lateinit var pageViewModel: PageViewModel

    private var training: Training = GenericTraining()
    private var treadmill: Treadmill = Treadmill()
    private lateinit var binding: TrainingTabBinding

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
    ): View {
        binding = TrainingTabBinding.inflate(layoutInflater)
        startClicked = this

        if(treadmill.ID==0L){
            treadmill = if(user.treadmillList.isEmpty()){
                Treadmill()
            } else{
                user.treadmillList.first()
            }
        }

        newGenericTraining()

        binding.finishTrainingButton.isGone = true
        hideTrainingItems()

        binding.startTrainingButton.setOnClickListener {
            if(training is GenericTraining){
                if(training.trainingStatus != TrainingStatus.InProgress && training.trainingStatus!=TrainingStatus.Paused){
                    showGenericTrainingItems()
                    training.startTraining()
                    updateSteeringDisplays()
                    (it as MaterialButton).text = getString(R.string.stop)
                    runTimer(training)
                }
                else if(training.trainingStatus == TrainingStatus.InProgress){
                    (it as MaterialButton).text = getString(R.string.resume)
                    binding.finishTrainingButton.isGone = false
                    training.pauseTraining()
                }
                else{
                    (it as MaterialButton).text = getString(R.string.stop)
                    binding.finishTrainingButton.isGone = true
                    training.resumeTraining()
                    runTimer(training)
                }
            }
            else{
                if(training.trainingStatus != TrainingStatus.InProgress && training.trainingStatus != TrainingStatus.Finished && training.trainingStatus!=TrainingStatus.Paused){
                    showTrainingItems()
                    training.startTraining()
                    updateSteeringDisplays()
                    updateStatusDisplays()
                    (it as MaterialButton).text = getString(R.string.stop)
                    runTimer(training)
                }
                else if(training.trainingStatus == TrainingStatus.InProgress){
                    (it as MaterialButton).text = getString(R.string.resume)
                    binding.finishTrainingButton.isGone = false
                    training.pauseTraining()
                }
                else{
                    (it as MaterialButton).text = getString(R.string.stop)
                    binding.finishTrainingButton.isGone = true
                    training.resumeTraining()
                    runTimer(training)
                }

            }
        }

        binding.finishTrainingButton.setOnClickListener{ finishTrainingButton ->
            if(training.trainingStatus==TrainingStatus.Paused){
                training.finishTraining()
                if(training is PlannedTraining){
                    unfinishPhases(training)
                    training.trainingStatus = TrainingStatus.Upcoming
                }
                newGenericTraining()
                hideTrainingItems()
                finishTrainingButton.isGone = true
                binding.startTrainingButton.text = getString(R.string.start)
            }
        }

        binding.speedUpButton.setOnClickListener{
            if(training is PlannedTraining){
                Toast.makeText(this.context, "Can't change in planned training!", Toast.LENGTH_SHORT).show()
            }
            else if(training.trainingStatus==TrainingStatus.InProgress){
                training.speedUp()
                updateSteeringDisplays()
            }
        }
        binding.speedDownButton.setOnClickListener{
            if(training is PlannedTraining){
                Toast.makeText(this.context, "Can't change in planned training!", Toast.LENGTH_SHORT).show()
            }
            else if(training.trainingStatus==TrainingStatus.InProgress){
                training.speedDown()
                updateSteeringDisplays()
            }

        }

        binding.tiltUpButton.setOnClickListener{
            if(training is PlannedTraining){
                Toast.makeText(this.context, "Can't change in planned training!", Toast.LENGTH_SHORT).show()
            }
            else if(training.trainingStatus==TrainingStatus.InProgress){
                training.tiltUp()
                binding.tiltDisplay.text = "${round(treadmill.getTilt(), TILT_ROUND_MULTIPLIER)}"
            }

        }
        binding.tiltDownButton.setOnClickListener{
            if(training is PlannedTraining){
                Toast.makeText(this.context, "Can't change in planned training!", Toast.LENGTH_SHORT).show()
            }
            else if(training.trainingStatus==TrainingStatus.InProgress){
                training.tiltDown()
                binding.tiltDisplay.text = "${round(treadmill.getTilt(), TILT_ROUND_MULTIPLIER)}"
            }

        }


        return binding.root
    }

    private fun newGenericTraining() {
        if (training.ID == 0L) {
            if (user.trainingSchedule.trainingLists.isEmpty()) {
                user.trainingSchedule.trainingLists.sortBy { it.ID }
                training = GenericTraining(
                    treadmill = treadmill, trainingTime = Calendar.getInstance(),
                    trainingStatus = TrainingStatus.Upcoming, ID = 1
                )
                user.trainingSchedule.sortCalendar()
            } else {
                user.trainingSchedule.trainingLists.sortBy { it.ID }
                training = GenericTraining(
                    treadmill = treadmill,
                    trainingTime = Calendar.getInstance(),
                    trainingStatus = TrainingStatus.Upcoming,
                    ID = user.trainingSchedule.trainingLists.last().ID + 1
                )
                user.trainingSchedule.sortCalendar()

            }
        }
    }
    private fun hideTrainingItems(){
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
    private fun showTrainingItems(){
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

    private fun showGenericTrainingItems(){
        binding.distanceTextView.isGone = false
        binding.paceTextView.isGone = false
        binding.timeTextView.isGone = false
        binding.distanceLabel.isGone = false
        binding.paceLabel.isGone = false
        binding.timeLabel.isGone = false
        binding.caloriesLabel.isGone = false
        binding.caloriesTextView.isGone = false
    }


    private fun runTimer(currentTraining: Training){
        if(currentTraining is PlannedTraining){
            val handler = Handler()
            var runnableCode = object: Runnable {
                override fun run() {
                    if(currentTraining.trainingStatus == TrainingStatus.InProgress){
                        updateStatusDisplays()

                        if(currentTraining.getCurrentPhase().speed!=treadmill.getSpeed() || currentTraining.getCurrentPhase().tilt!=treadmill.getTilt()){
                            changePhases()
                        }

                        if(currentTraining.getCurrentMoment()>=currentTraining.getTotalDuration()){
                            currentTraining.finishTraining()
                            binding.startTrainingButton.text = getString(R.string.training_finished)
                            newGenericTraining()
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
                    if(currentTraining.trainingStatus == TrainingStatus.InProgress){
                        updateStatusDisplays()
                        handler.postDelayed(this, 250)
                    }
                }
            }
            handler.post(runnableCode)
        }

    }

    private fun updateSteeringDisplays(){
        binding.speedDisplay.text = "${round(treadmill.getSpeed(), SPEED_ROUND_MULTIPLIER)}"
        binding.tiltDisplay.text = "${round(treadmill.getTilt(), TILT_ROUND_MULTIPLIER)}"
        binding.paceTextView.text = String.format(getString(R.string.pace),
            round((SECONDS_IN_MINUTE.toDouble()/treadmill.getSpeed()), PACE_ROUND_MULTIPLIER))
    }

    fun updateStatusDisplays(){
        binding.timeTextView.text = String.format(getString(R.string.duration_seconds), training.getCurrentMoment())
        binding.distanceTextView.text = String.format(getString(R.string.distance), training.getCurrentDistance())
        binding.caloriesTextView.text = String.format(getString(R.string.calories),
            calculateCaloriesForOngoingTraining(training.getCurrentMoment()))
        if(training is PlannedTraining){
            binding.progressTextView.text = String.format(getString(R.string.percentage), round(
                (training.getCurrentMoment().toDouble()/training.getTotalDuration().toDouble())*100.0,
                PERCENTAGE_ROUND_MULTIPLIER))
        }
    }

    fun changePhases(){
        treadmill.setSpeed((training as PlannedTraining).getCurrentPhase().speed)
        treadmill.setTilt((training as PlannedTraining).getCurrentPhase().tilt)
        if(training.trainingPlan.trainingPhaseList.indexOf((training as PlannedTraining).getCurrentPhase())>0){
            training.trainingPlan.trainingPhaseList[training.trainingPlan.trainingPhaseList.indexOf((training as PlannedTraining).getCurrentPhase()
            )-1].isFinished = true
        }
        training.lastPhaseStart = Calendar.getInstance().timeInMillis
        updateSteeringDisplays()
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
        val trainingFound = user.trainingSchedule.getTraining(trainingID)
        if(trainingFound!=null){
            training = trainingFound
            treadmill = training.treadmill
            if(training.trainingStatus == TrainingStatus.InProgress){
                training.trainingStatus = TrainingStatus.Upcoming
            }
            binding.startTrainingButton.callOnClick()
        }
    }
}