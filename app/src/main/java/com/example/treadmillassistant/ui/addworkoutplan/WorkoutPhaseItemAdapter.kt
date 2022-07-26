package com.example.treadmillassistant.ui.addworkoutplan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.example.treadmillassistant.R
import com.example.treadmillassistant.backend.workout.WorkoutPhase
import com.google.android.material.textfield.TextInputEditText

class WorkoutPhaseItemAdapter(private val phaseList: MutableList<WorkoutPhase>,
                              private val totalDuration: TextView,
                              private val totalDistance: TextView): RecyclerView.Adapter<WorkoutPhaseItemAdapter.ItemViewHolder>(){

    class ItemViewHolder(view: View): RecyclerView.ViewHolder(view){
        val durationInput = view.findViewById<TextInputEditText>(R.id.phase_duration_input)
        val speedInput = view.findViewById<TextInputEditText>(R.id.phase_speed_input)
        val tiltInput = view.findViewById<TextInputEditText>(R.id.phase_tilt_input)
        val removeButton = view.findViewById<Button>(R.id.phase_remove_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder{
        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.phase_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int){
        val item = phaseList[position]

        holder.durationInput.setText("${item.duration}")
        holder.speedInput.setText("${item.speed}")
        holder.tiltInput.setText("${item.tilt}")

        holder.removeButton.setOnClickListener {
            if(position<phaseList.size){
                phaseList.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, phaseList.size)
                setDuration()
                setDistance()
            }
        }

        holder.durationInput.addTextChangedListener {
            if(it.toString()!=""){
                item.duration = it.toString().toInt()
            }
            else{
                item.duration = 0
            }
            setDuration()
            setDistance()
        }
        holder.speedInput.addTextChangedListener {
            if(it.toString()!=""){
                item.speed = it.toString().toDouble()
            }
            else{
                item.speed = 0.0
            }
            setDistance()
        }
        holder.tiltInput.addTextChangedListener {
            if(it.toString()!="" && it.toString()!="-"){
                item.tilt = it.toString().toDouble()
            }
            else{
                item.tilt = 0.0
            }
        }
    }

    override fun getItemCount() = phaseList.size


    private fun setDistance(){
        var distance = 0.0
        phaseList.forEach {
            distance += (it.duration.toDouble()/3600.0)*it.speed
        }
        println(distance)
        totalDistance.text = "Total duration: ${Math.round(distance*100.0)/100.0} km"
    }

    private fun setDuration(){
        var duration = 0
        phaseList.forEach{
            duration += it.duration
        }
        totalDuration.text = "Total duration: ${Math.round((duration.toDouble()/60.toDouble())*10.0)/10.0} min"
    }
}