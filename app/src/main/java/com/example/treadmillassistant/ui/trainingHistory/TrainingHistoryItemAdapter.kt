package com.example.treadmillassistant.ui.trainingHistory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.treadmillassistant.R
import com.example.treadmillassistant.backend.*
import com.example.treadmillassistant.backend.workout.Workout
import java.text.SimpleDateFormat

class TrainingHistoryItemAdapter(private val dataset: List<Workout>): RecyclerView.Adapter<TrainingHistoryItemAdapter.ItemViewHolder>(){

    class ItemViewHolder(view: View): RecyclerView.ViewHolder(view){
        val duration = view.findViewById<TextView>(R.id.history_item_duration_text)
        val avgSpeed = view.findViewById<TextView>(R.id.history_item_avg_speed_text)
        val calories = view.findViewById<TextView>(R.id.history_item_calories_text)
        val date = view.findViewById<TextView>(R.id.history_item_date_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder{
        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.history_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int){
        val item = dataset[position]

        val dateFormat = SimpleDateFormat("dd.MM.${item.workoutTime.year}")
        val workoutTime = dateFormat.format(item.workoutTime.time)

        holder.duration.text = "${round(secondsToMinutes(item.getTotalDuration()), DURATION_ROUND_MULTIPLIER)} min"
        holder.avgSpeed.text = "${round(item.getAverageSpeed(), SPEED_ROUND_MULTIPLIER)} km/h"
        holder.calories.text = "${calculateCaloriesForWorkout(item)} kcal"
        holder.date.text = "$workoutTime"

    }

    override fun getItemCount() = dataset.size

}