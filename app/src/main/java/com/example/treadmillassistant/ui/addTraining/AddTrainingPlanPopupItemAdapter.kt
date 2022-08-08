package com.example.treadmillassistant.ui.addTraining

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.treadmillassistant.R
import com.example.treadmillassistant.backend.DISTANCE_ROUND_MULTIPLIER
import com.example.treadmillassistant.backend.DURATION_ROUND_MULTIPLIER
import com.example.treadmillassistant.backend.round
import com.example.treadmillassistant.backend.secondsToMinutes
import com.example.treadmillassistant.backend.training.TrainingPlan
import com.example.treadmillassistant.ui.addTraining.AddTraining.Companion.popupWindow
import com.example.treadmillassistant.ui.addTraining.AddTraining.Companion.selectedTrainingPlan
import com.google.android.material.button.MaterialButton

class AddTrainingPlanPopupItemAdapter(private val trainingPlanList: MutableList<TrainingPlan>): RecyclerView.Adapter<AddTrainingPlanPopupItemAdapter.ItemViewHolder>(){

    class ItemViewHolder(view: View): RecyclerView.ViewHolder(view){
        val trainingPlanName: TextView = view.findViewById(R.id.training_plan_name)
        val trainingDistance: TextView = view.findViewById(R.id.training_plan_distance)
        val trainingDuration: TextView = view.findViewById(R.id.training_plan_duration)
        val editButton: MaterialButton = view.findViewById(R.id.training_plan_edit_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder{
        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.training_plan_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int){
        val item = trainingPlanList[position]

        holder.trainingPlanName.text = item.name
        holder.trainingDuration.text = String.format(holder.trainingDuration.context.getString(R.string.duration_minutes),
            round(secondsToMinutes(item.getDuration()), DURATION_ROUND_MULTIPLIER))

        holder.trainingDistance.text = String.format(holder.trainingDistance.context.getString(R.string.distance),
            round(item.getDistance(), DISTANCE_ROUND_MULTIPLIER))

        holder.trainingPlanName.setOnClickListener {
            selectedTrainingPlan = item
            popupWindow.dismiss()
        }
        holder.trainingDuration.setOnClickListener {
            selectedTrainingPlan = item
            popupWindow.dismiss()
        }
        holder.trainingDistance.setOnClickListener {
            selectedTrainingPlan = item
            popupWindow.dismiss()
        }
    }

    override fun getItemCount() = trainingPlanList.size
}