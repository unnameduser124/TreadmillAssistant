package com.example.treadmillassistant.ui.addTraining

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.treadmillassistant.R
import com.example.treadmillassistant.backend.*
import com.example.treadmillassistant.backend.training.TrainingPlan
import com.example.treadmillassistant.ui.EditTrainingPlan
import com.example.treadmillassistant.ui.EditTreadmill
import com.example.treadmillassistant.ui.addTraining.AddTraining.Companion.popupWindow
import com.example.treadmillassistant.ui.addTraining.AddTraining.Companion.selectedTrainingPlan
import com.example.treadmillassistant.ui.addTraining.AddTraining.Companion.selectedTreadmill
import com.example.treadmillassistant.ui.addTraining.AddTraining.Companion.treadmillPopup
import com.example.treadmillassistant.ui.editTraining.EditTraining
import com.google.android.material.button.MaterialButton

class AddTreadmillPopupItemAdapter(private val trainingPlanList: MutableList<Treadmill>, private val fromTraining: Boolean, private val trainingID: Long = -1): RecyclerView.Adapter<AddTreadmillPopupItemAdapter.ItemViewHolder>(){

    class ItemViewHolder(view: View): RecyclerView.ViewHolder(view){
        val treadmillName: TextView = view.findViewById(R.id.training_plan_name)
        val editButton: MaterialButton = view.findViewById(R.id.training_plan_edit_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder{
        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.training_plan_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int){
        val item = trainingPlanList[position]

        holder.treadmillName.text = item.name

        holder.treadmillName.setOnClickListener {
            selectedTreadmill = item
            EditTraining.selectedTreadmill = item
            treadmillPopup.dismiss()
            EditTraining.treadmillPopup.dismiss()
        }

        holder.editButton.setOnClickListener {
            val intent = Intent(holder.editButton.context, EditTreadmill::class.java)
            intent.putExtra("ID", item.ID)
            intent.putExtra("fromTraining", fromTraining)
            if(!fromTraining){
                intent.putExtra("fromEditTraining", true)
            }
            if(trainingID!=-1L){
                intent.putExtra("id", trainingID)
            }
            EditTraining.treadmillPopup.dismiss()
            treadmillPopup.dismiss()
            startActivity(holder.editButton.context, intent, null)
        }
    }

    override fun getItemCount() = trainingPlanList.size
}