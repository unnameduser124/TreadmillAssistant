package com.example.treadmillassistant.ui.trainingDetails

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.treadmillassistant.R
import com.example.treadmillassistant.backend.*
import com.example.treadmillassistant.backend.workout.WorkoutPhase

class TrainingDetailsPhaseItemAdapter(private val dataset: List<WorkoutPhase>): RecyclerView.Adapter<TrainingDetailsPhaseItemAdapter.ItemViewHolder>(){

    class ItemViewHolder(view: View): RecyclerView.ViewHolder(view){
        val duration = view.findViewById<TextView>(R.id.training_details_phase_duration)
        val speed = view.findViewById<TextView>(R.id.training_details_phase_speed)
        val distance = view.findViewById<TextView>(R.id.training_details_phase_distance)
        val tilt = view.findViewById<TextView>(R.id.training_details_phase_tilt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder{
        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.individual_training_page_phase_list_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int){
        val item = dataset[position]

        holder.duration.text = "${round(secondsToMinutes(item.duration), DURATION_ROUND_MULTIPLIER)} min"
        holder.speed.text = "${round(item.speed, SPEED_ROUND_MULTIPLIER)} km/h"
        holder.distance.text = "${round(secondsToHoursNotRounded(item.duration)*item.speed, DISTANCE_ROUND_MULTIPLIER)} km"
        holder.tilt.text = "${round(item.tilt, TILT_ROUND_MULTIPLIER)}"
    }

    override fun getItemCount() = dataset.size

}