package com.example.treadmillassistant.ui.home.calendarTab

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.example.treadmillassistant.R
import com.example.treadmillassistant.backend.DURATION_ROUND_MULTIPLIER
import com.example.treadmillassistant.backend.round
import com.example.treadmillassistant.backend.secondsToMinutes
import com.example.treadmillassistant.backend.workout.Workout
import com.example.treadmillassistant.backend.workout.WorkoutStatus
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.coroutineContext

class CalendarTrainingItemAdapter(private val dataset: List<Workout>): RecyclerView.Adapter<CalendarTrainingItemAdapter.ItemViewHolder>(){

    class ItemViewHolder(view: View): RecyclerView.ViewHolder(view){
        val timeView: TextView = view.findViewById(R.id.training_time)
        val durationView: TextView = view.findViewById(R.id.training_duration)
        val startButton: MaterialButton = view.findViewById(R.id.start_training_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder{
        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.calendar_tab_training_list_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int){
        val item = dataset[position]

        val dateFormat = SimpleDateFormat("kk:mm")
        val workoutTime = dateFormat.format(item.workoutTime.time)
        holder.timeView.text = "$workoutTime"
        holder.durationView.text = "Duration: ${round(secondsToMinutes(item.getTotalDuration()), DURATION_ROUND_MULTIPLIER)} minutes"

        val date = Date(Calendar.getInstance().get(Calendar.YEAR), Date().month, Date().date, 0, 0, 0)
        if(item.workoutTime.before(date) || item.workoutStatus!=WorkoutStatus.Upcoming){
            holder.startButton.isGone = true
        }

    }

    override fun getItemCount() = dataset.size

}