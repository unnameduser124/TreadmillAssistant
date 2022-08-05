package com.example.treadmillassistant.ui.trainingDetails

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.treadmillassistant.R
import com.example.treadmillassistant.backend.*
import com.example.treadmillassistant.backend.training.TrainingPhase

class TrainingDetailsPhaseItemAdapter(private val dataset: List<TrainingPhase>): RecyclerView.Adapter<TrainingDetailsPhaseItemAdapter.ItemViewHolder>(){

    class ItemViewHolder(view: View): RecyclerView.ViewHolder(view){
        val duration: TextView = view.findViewById(R.id.training_details_phase_duration)
        val speed: TextView = view.findViewById(R.id.training_details_phase_speed)
        val distance: TextView = view.findViewById(R.id.training_details_phase_distance)
        val tilt: TextView = view.findViewById(R.id.training_details_phase_tilt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder{
        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.individual_training_page_phase_list_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int){
        val item = dataset[position]

        holder.duration.text = String.format(holder.duration.context.getString(R.string.duration_minutes),
            round(secondsToMinutes(item.duration), DURATION_ROUND_MULTIPLIER))
        holder.speed.text = String.format(holder.speed.context.getString(R.string.speed), round(item.speed, SPEED_ROUND_MULTIPLIER))
        holder.distance.text = String.format(holder.distance.context.getString(R.string.distance),
            round(secondsToHoursNotRounded(item.duration)*item.speed, DISTANCE_ROUND_MULTIPLIER))
        holder.tilt.text = round(item.tilt, TILT_ROUND_MULTIPLIER).toString()
    }

    override fun getItemCount() = dataset.size

}