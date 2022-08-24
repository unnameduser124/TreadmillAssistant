package com.example.treadmillassistant.ui.addTraining

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.treadmillassistant.R
import com.example.treadmillassistant.backend.DISTANCE_ROUND_MULTIPLIER
import com.example.treadmillassistant.backend.DURATION_ROUND_MULTIPLIER
import com.example.treadmillassistant.backend.round
import com.example.treadmillassistant.backend.secondsToMinutes
import com.example.treadmillassistant.backend.training.TrainingPlan
import com.example.treadmillassistant.ui.EditTrainingPlan
import com.example.treadmillassistant.ui.addTraining.AddTraining.Companion.popupWindow
import com.example.treadmillassistant.ui.addTraining.AddTraining.Companion.selectedTrainingPlan
import com.google.android.material.button.MaterialButton

class AddTrainingPlanPopupItemAdapter(private val trainingPlanList: MutableList<TrainingPlan>, private val fromTraining: Boolean, private val trainingID: Long = -1): RecyclerView.Adapter<AddTrainingPlanPopupItemAdapter.ItemViewHolder>(){

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

        val listener = View.OnClickListener{
            selectedTrainingPlan = item
            popupWindow.dismiss()
        }

        holder.trainingDistance.setOnClickListener(listener)
        holder.trainingPlanName.setOnClickListener(listener)
        holder.trainingDuration.setOnClickListener(listener)

        holder.editButton.setOnClickListener {
            val intent = Intent(holder.editButton.context, EditTrainingPlan::class.java)
            intent.putExtra("ID", item.ID)
            intent.putExtra("fromTraining", fromTraining)
            if(trainingID!=-1L){
                intent.putExtra("trainingID", trainingID)
            }
            popupWindow.dismiss()
            startActivity(holder.editButton.context, intent, null)
        }
    }

    override fun getItemCount() = trainingPlanList.size
}