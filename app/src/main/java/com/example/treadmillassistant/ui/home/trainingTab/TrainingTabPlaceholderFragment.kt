package com.example.treadmillassistant.ui.home.trainingTab

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.treadmillassistant.R
import com.example.treadmillassistant.backend.*
import com.example.treadmillassistant.backend.serverDatabase.databaseClasses.ServerTraining
import com.example.treadmillassistant.backend.serverDatabase.serverDatabaseService.ServerTrainingService
import com.example.treadmillassistant.backend.training.GenericTraining
import com.example.treadmillassistant.backend.training.PlannedTraining
import com.example.treadmillassistant.backend.training.Training
import com.example.treadmillassistant.backend.training.TrainingStatus
import com.example.treadmillassistant.databinding.TrainingTabBinding
import com.example.treadmillassistant.ui.home.OnStartClickedListener
import com.example.treadmillassistant.ui.home.PageViewModel
import com.example.treadmillassistant.ui.home.calendarTab.CalendarPlaceholderFragment.Companion.chosenDay
import com.example.treadmillassistant.ui.home.calendarTab.CalendarPlaceholderFragment.Companion.trainingList
import com.example.treadmillassistant.ui.home.calendarTab.CalendarTrainingItemAdapter
import com.google.android.material.button.MaterialButton
import java.util.*
import kotlin.concurrent.thread

class TrainingTabPlaceholderFragment: Fragment(), OnStartClickedListener {
    private lateinit var pageViewModel: PageViewModel

    private lateinit var binding: TrainingTabBinding
    private var error = 0

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

        //creating generic treadmill and training if none is provided
        if(treadmill.ID==0L){
            treadmill = if(user.treadmillList.isEmpty()){
                Treadmill()
            } else{
                user.treadmillList.first()
            }
        }
        if(training.ID == 0L){
            newGenericTraining()
        }

        binding.finishTrainingButton.isGone = true


        hideTrainingItems()
        if(training.trainingStatus == TrainingStatus.InProgress){
            if(training is GenericTraining){
                showGenericTrainingItems()
                updateSteeringDisplays()
                binding.startTrainingButton.text = getString(R.string.stop)
                runTimer()
            }
            else{
                showTrainingItems()
                updateSteeringDisplays()
                updateStatusDisplays()
                binding.startTrainingButton.text = getString(R.string.stop)
                runTimer()
            }
        }
        else if(training.trainingStatus == TrainingStatus.Paused){
            binding.startTrainingButton.text = getString(R.string.resume)
            binding.finishTrainingButton.isGone = false
        }

        binding.startTrainingButton.setOnClickListener {
            if(training is GenericTraining){
                if(training.trainingStatus != TrainingStatus.InProgress && training.trainingStatus!=TrainingStatus.Paused){
                    showGenericTrainingItems()
                    training.startTraining()
                    updateSteeringDisplays()
                    (it as MaterialButton).text = getString(R.string.stop)
                    runTimer()
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
                    runTimer()
                }
            }
            else{
                if(training.trainingStatus != TrainingStatus.InProgress && training.trainingStatus != TrainingStatus.Finished && training.trainingStatus!=TrainingStatus.Paused){
                    showTrainingItems()
                    training.startTraining()
                    updateSteeringDisplays()
                    updateStatusDisplays()
                    (it as MaterialButton).text = getString(R.string.stop)
                    runTimer()
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
                    runTimer()
                }

            }
        }

        binding.finishTrainingButton.setOnClickListener{ finishTrainingButton ->
            if(training.trainingStatus==TrainingStatus.Paused){
                training.finishTraining(binding.finishTrainingButton.context)
                if(training is PlannedTraining){
                    unfinishPhases(training)
                    training.trainingStatus = TrainingStatus.Upcoming
                }
                val created: MutableLiveData<Boolean> by lazy{
                    MutableLiveData<Boolean>(false)
                }
                if(training is GenericTraining){
                    thread{
                        val code = ServerTrainingService().createTraining(ServerTraining(training))
                        println(code)
                        user.trainingSchedule.trainingList.clear()
                        val allTrainingsPair = ServerTrainingService().getTrainingsForDay(chosenDay, 0, 10)
                        allTrainingsPair.second.forEach {
                            user.trainingSchedule.trainingList.add(PlannedTraining(it))
                        }
                        created.postValue(true)
                    }
                }
                val observer = androidx.lifecycle.Observer<Boolean>{
                    if(it){
                        val dataset = user.trainingSchedule.trainingList
                        trainingList.adapter = CalendarTrainingItemAdapter(dataset)
                        newGenericTraining()
                        finishTrainingButton.isGone = true
                        binding.startTrainingButton.text = getString(R.string.start_new)
                    }
                }
                created.observe(viewLifecycleOwner, observer)
            }
        }

        speedButtonListeners()

        tiltButtonListeners()

        return binding.root
    }

    private fun newGenericTraining() {
        training = GenericTraining(
            treadmill = treadmill, trainingTime = Calendar.getInstance(),
            trainingStatus = TrainingStatus.Upcoming, ID = user.trainingSchedule.getNewID()
        )
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


    private fun runTimer(){
        if(training is PlannedTraining){
            val handler = Handler(Looper.getMainLooper())
            val runnableCode = object: Runnable {
                override fun run() {
                    if(training.trainingStatus == TrainingStatus.InProgress){
                        updateStatusDisplays()

                        if((training as PlannedTraining).getCurrentPhase().speed!=treadmill.getSpeed()
                            || (training as PlannedTraining).getCurrentPhase().tilt!=treadmill.getTilt()){
                            changePhases()
                        }

                        if(training.getCurrentMoment()>=training.getTotalDuration()){
                            training.finishTraining(binding.finishTrainingButton.context)
                            val updated: MutableLiveData<Boolean> by lazy{
                                MutableLiveData<Boolean>(false)
                            }
                            thread{
                                ServerTrainingService().updateTraining(ServerTraining(training), training.ID)
                                updated.postValue(true)
                            }
                            val observer = androidx.lifecycle.Observer<Boolean>{
                                if(it){
                                    binding.startTrainingButton.text = getString(R.string.training_finished)
                                    trainingList.adapter = CalendarTrainingItemAdapter(user.trainingSchedule.getTrainingsForDate(chosenDay))
                                    newGenericTraining()
                                }
                            }
                            updated.observe(viewLifecycleOwner, observer)
                        }
                        handler.postDelayed(this, 100)
                    }
                }
            }
            handler.post(runnableCode)
        }
        else{
            val handler = Handler(Looper.getMainLooper())
            val runnableCode = object: Runnable {
                override fun run() {
                    if(training.trainingStatus == TrainingStatus.InProgress){
                        updateStatusDisplays()
                        handler.postDelayed(this, 100)
                    }
                }
            }
            handler.post(runnableCode)
        }

    }

    private fun updateSteeringDisplays(){
        try{
            binding.speedDisplay.text = "${round(treadmill.getSpeed(), SPEED_ROUND_MULTIPLIER)}"
            binding.tiltDisplay.text = "${round(treadmill.getTilt(), TILT_ROUND_MULTIPLIER)}"
            binding.paceTextView.text = String.format(getString(R.string.pace),
                round((SECONDS_IN_MINUTE.toDouble()/treadmill.getSpeed()), PACE_ROUND_MULTIPLIER))
        }
        catch(exception: IllegalStateException){
            println(exception.message)
            error++
            if(error<5){
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({ updateStatusDisplays() }, 100)
            }

        }
    }

    fun updateStatusDisplays(){
        try{
            setUpDurationView(binding.timeTextView, training.getCurrentMoment())

            binding.distanceTextView.text = String.format(getString(R.string.distance), training.getCurrentDistance())
            binding.caloriesTextView.text = String.format(getString(R.string.calories),
                calculateCaloriesForOngoingTraining(training.getCurrentMoment()))
            if(training is PlannedTraining){
                binding.progressTextView.text = String.format(getString(R.string.percentage), round(
                    (training.getCurrentMoment().toDouble()/training.getTotalDuration().toDouble())*100.0,
                    PERCENTAGE_ROUND_MULTIPLIER))
            }
        }
        catch(exception: IllegalStateException){
            println(exception.message)
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

    private fun speedButtonListeners(){
        binding.speedUpButton.setOnClickListener{
            if(training is PlannedTraining){
                Toast.makeText(this.context, "Can't change in planned training!", Toast.LENGTH_SHORT).show()
            }
            else if(training.trainingStatus==TrainingStatus.InProgress){
                training.speedUp()
                updateSteeringDisplays()
            }
        }

        binding.speedUpButton.setOnLongClickListener {

            if(training is PlannedTraining){
                Toast.makeText(this.context, "Can't change in planned training!", Toast.LENGTH_SHORT).show()
            }
            else if(training.trainingStatus==TrainingStatus.InProgress){
                val runnableCode = object: Runnable {
                    override fun run(){
                        Thread{
                            if(binding.speedUpButton.isPressed){
                                training.speedUp()
                                updateSteeringDisplays()
                                Handler(Looper.getMainLooper()).postDelayed(this, 200)
                            }
                        }.run()
                    }
                }
                Handler(Looper.getMainLooper()).post(runnableCode)
            }

            true
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

        binding.speedDownButton.setOnLongClickListener {
            if(training is PlannedTraining){
                Toast.makeText(this.context, "Can't change in planned training!", Toast.LENGTH_SHORT).show()
            }
            else if(training.trainingStatus==TrainingStatus.InProgress){
                val runnableCode = object: Runnable {
                    override fun run(){
                        Thread{
                            if(binding.speedDownButton.isPressed){
                                training.speedDown()
                                updateSteeringDisplays()
                                Handler(Looper.getMainLooper()).postDelayed(this, 200)
                            }
                        }.run()
                    }
                }
                Handler(Looper.getMainLooper()).post(runnableCode)
            }

            true
        }
    }

    private fun tiltButtonListeners(){
        binding.tiltUpButton.setOnClickListener{
            if(training is PlannedTraining){
                Toast.makeText(this.context, "Can't change in planned training!", Toast.LENGTH_SHORT).show()
            }
            else if(training.trainingStatus==TrainingStatus.InProgress){
                training.tiltUp()
                binding.tiltDisplay.text = "${round(treadmill.getTilt(), TILT_ROUND_MULTIPLIER)}"
            }

        }

        binding.tiltUpButton.setOnLongClickListener {
            if(training is PlannedTraining){
                Toast.makeText(this.context, "Can't change in planned training!", Toast.LENGTH_SHORT).show()
            }
            else if(training.trainingStatus==TrainingStatus.InProgress){
                val runnableCode = object: Runnable {
                    override fun run(){
                        Thread{
                            if(binding.tiltUpButton.isPressed){
                                training.tiltUp()
                                updateSteeringDisplays()
                                Handler(Looper.getMainLooper()).postDelayed(this, 200)
                            }
                        }.run()
                    }
                }
                Handler(Looper.getMainLooper()).post(runnableCode)
            }

            true
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

        binding.tiltDownButton.setOnLongClickListener {
            if(training is PlannedTraining){
                Toast.makeText(this.context, "Can't change in planned training!", Toast.LENGTH_SHORT).show()
            }
            else if(training.trainingStatus==TrainingStatus.InProgress){
                val runnableCode = object: Runnable {
                    override fun run(){
                        Thread{
                            if(binding.tiltDownButton.isPressed){
                                training.tiltDown()
                                updateSteeringDisplays()
                                Handler(Looper.getMainLooper()).postDelayed(this, 200)
                            }
                        }.run()
                    }
                }
                Handler(Looper.getMainLooper()).post(runnableCode)
            }

            true
        }
    }

    companion object{

        private const val SECTION_NUMBER = "section number"
        var training: Training = GenericTraining()
        var treadmill: Treadmill = Treadmill()

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