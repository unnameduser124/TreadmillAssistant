package com.example.treadmillassistant.ui.home.calendarTab

import android.content.Intent
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.treadmillassistant.R
import com.example.treadmillassistant.backend.workout.Workout
import com.google.android.material.button.MaterialButton
import java.util.*
import java.util.concurrent.TimeUnit

class CalendarTrainingItemAdapter(private val dataset: List<Workout>): RecyclerView.Adapter<CalendarTrainingItemAdapter.ItemViewHolder>(){

    class ItemViewHolder(view: View): RecyclerView.ViewHolder(view){
        val timeView: TextView = view.findViewById(R.id.training_time)
        val durationView: TextView = view.findViewById(R.id.training_duration)
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
        holder.durationView.text = "Duration: ${item.workoutDuration} minutes"

    }

    override fun getItemCount() = dataset.size

}