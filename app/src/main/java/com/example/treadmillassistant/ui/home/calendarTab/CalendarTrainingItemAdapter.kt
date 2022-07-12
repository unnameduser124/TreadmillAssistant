package com.example.treadmillassistant.ui.home.calendarTab

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.example.treadmillassistant.R
import com.example.treadmillassistant.backend.workout.Workout
import com.example.treadmillassistant.backend.workout.WorkoutStatus
import com.google.android.material.button.MaterialButton
import java.util.*

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
        if(item.workoutTime.minutes<10 && item.workoutTime.hours<10){
            holder.timeView.text = "0${item.workoutTime.hours}:0${item.workoutTime.minutes}"
        }
        else if(item.workoutTime.minutes<10 && item.workoutTime.hours>10){
            holder.timeView.text = "${item.workoutTime.hours}:0${item.workoutTime.minutes}"
        }
        else if(item.workoutTime.minutes>10 && item.workoutTime.hours<10){
            holder.timeView.text = "0${item.workoutTime.hours}:${item.workoutTime.minutes}"
        }
        else{
            holder.timeView.text = "${item.workoutTime.hours}:${item.workoutTime.minutes}"
        }
        holder.durationView.text = "Duration: ${item.workoutDuration/60} minutes"

        val date = Date(Calendar.getInstance().get(Calendar.YEAR), Date().month, Date().date, 0, 0, 0)
        if(item.workoutTime.before(date) || item.workoutStatus!=WorkoutStatus.Upcoming){
            holder.startButton.isGone = true
        }

    }

    override fun getItemCount() = dataset.size

}