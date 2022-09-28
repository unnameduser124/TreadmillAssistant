package com.example.treadmillassistant.ui.home.calendarTab

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.treadmillassistant.backend.localDatabase.NotificationService
import com.example.treadmillassistant.backend.serverDatabase.serverDatabaseService.ServerTrainingPlanService
import com.example.treadmillassistant.backend.serverDatabase.serverDatabaseService.ServerTrainingService
import com.example.treadmillassistant.backend.serverDatabase.serverDatabaseService.StatusCode
import com.example.treadmillassistant.backend.training.Training
import com.example.treadmillassistant.backend.user
import com.example.treadmillassistant.databinding.CalendarTabBinding
import com.example.treadmillassistant.ui.ScheduledTrainingNotification
import com.example.treadmillassistant.ui.home.PageViewModel
import com.example.treadmillassistant.ui.notificationID
import com.example.treadmillassistant.ui.trainingNotificationTime
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

class CalendarPlaceholderFragment: Fragment() {
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
    ): View {
        val binding = CalendarTabBinding.inflate(layoutInflater)

        var dataset = mutableListOf<Training>()
        var itemAdapter = CalendarTrainingItemAdapter(dataset)

        val loaded: MutableLiveData<Boolean> by lazy{
            MutableLiveData<Boolean>(false)
        }
        thread{
            reloadTrainingList(Calendar.getInstance())
            loaded.postValue(true)
        }
        val observer = Observer<Boolean>{
            if(it){
                dataset = user.trainingSchedule.trainingList
                chosenDay = Calendar.getInstance()
                itemAdapter = CalendarTrainingItemAdapter(dataset)
                val linearLayoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
                binding.dayTrainingsList.layoutManager = linearLayoutManager
                binding.dayTrainingsList.adapter = itemAdapter
                binding.dayTrainingsList.setHasFixedSize(true)
                trainingList = binding.dayTrainingsList
            }
        }
        loaded.observe(viewLifecycleOwner, observer)


        binding.trainingScheduleCalendar.setOnDateChangeListener { _, year, month, day ->
            loaded.value = false
            thread{
                val newCalendar = Calendar.getInstance()
                newCalendar.set(Calendar.YEAR, year)
                newCalendar.set(Calendar.MONTH, month)
                newCalendar.set(Calendar.DAY_OF_MONTH, day)
                reloadTrainingList(newCalendar)
                dataset = user.trainingSchedule.trainingList
                itemAdapter = CalendarTrainingItemAdapter(dataset)
                chosenDay = newCalendar
                loaded.postValue(true)
            }
            val loadedObserver = Observer<Boolean>{
                if(it){
                    binding.dayTrainingsList.adapter = itemAdapter
                }

            }
            loaded.observe(viewLifecycleOwner, loadedObserver)
        }

        return binding.root
    }

    private fun scheduleNotification(dateCal: Calendar, trainingID: Long) {
        dateCal.set(Calendar.SECOND, 0)

        val notificationCal = Calendar.getInstance()
        notificationCal.set(
            dateCal.get(Calendar.YEAR),
            dateCal.get(Calendar.MONTH),
            dateCal.get(Calendar.DAY_OF_MONTH),
            dateCal.get(Calendar.HOUR_OF_DAY),
            dateCal.get(Calendar.MINUTE)-5,
            0
        )
        val notificationIntent = Intent(requireContext(), ScheduledTrainingNotification::class.java)
        val sdfTime = SimpleDateFormat("HH:mm", Locale.ROOT)
        val trainingTime = sdfTime.format(dateCal.time)
        val newNotificationID = NotificationService(requireContext()).insertNewNotification(trainingID)
        notificationIntent.putExtra(trainingNotificationTime, trainingTime.toString())
        notificationIntent.putExtra(notificationID, newNotificationID.toInt())


        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            newNotificationID.toInt(),
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val time = notificationCal.timeInMillis
        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
    }

    private fun reloadTrainingList(newCalendar: Calendar): Boolean {
        user.trainingSchedule.trainingList.clear()
        val allTrainingsPair = ServerTrainingService().getTrainingsForDay(newCalendar, 0, 10, requireContext())
        allTrainingsPair.second.forEach {
            val plan = ServerTrainingPlanService().getTrainingPlan(it.trainingPlan.ID)
            if(plan.first == StatusCode.OK){
                it.trainingPlan = plan.second
            }
            user.trainingSchedule.trainingList.add(it)
        }
        if(allTrainingsPair.first == StatusCode.OK){
            return true
        }
        else if(allTrainingsPair.first != StatusCode.NotFound){
            Looper.prepare()
            Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show()
        }
        return false
    }

    companion object{

        private const val SECTION_NUMBER = "section number"
        lateinit var trainingList: RecyclerView
        var chosenDay: Calendar = Calendar.getInstance()

        @JvmStatic
        fun newInstance(sectionNumber: Int): CalendarPlaceholderFragment{
            return CalendarPlaceholderFragment().apply{
                arguments = Bundle().apply{
                    putInt(SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }

}