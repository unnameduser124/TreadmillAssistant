package com.example.treadmillassistant.ui.addTrainingPlan

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.example.treadmillassistant.R
import com.example.treadmillassistant.backend.*
import com.example.treadmillassistant.backend.training.TrainingPhase
import com.example.treadmillassistant.ui.EditTrainingPlan.Companion.removedPhaseIDs
import com.example.treadmillassistant.ui.InputFilterMinMax
import com.google.android.material.textfield.TextInputEditText
import java.lang.NumberFormatException

class TrainingPhaseItemAdapter(private val phaseList: MutableList<TrainingPhase>,
                               private val totalDuration: TextView,
                               private val totalDistance: TextView): RecyclerView.Adapter<TrainingPhaseItemAdapter.ItemViewHolder>(){

    class ItemViewHolder(view: View): RecyclerView.ViewHolder(view){
        val durationInput: TextInputEditText = view.findViewById(R.id.phase_duration_input)
        val speedInput: TextInputEditText = view.findViewById(R.id.phase_speed_input)
        val tiltInput: TextInputEditText = view.findViewById(R.id.phase_tilt_input)
        val removeButton: Button = view.findViewById(R.id.phase_remove_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder{
        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.phase_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int){
        val item = phaseList[position]

        if(item.duration == 0){
            holder.durationInput.setText("0")
        }
        else{
            holder.durationInput.setText("${round(secondsToMinutes(item.duration), DURATION_ROUND_MULTIPLIER)}")
        }

        holder.speedInput.setText("${item.speed}")
        holder.tiltInput.setText("${item.tilt}")

        holder.speedInput.filters = arrayOf(InputFilterMinMax(DEFAULT_MIN_SPEED, DEFAULT_MAX_SPEED))
        holder.tiltInput.filters = arrayOf(InputFilterMinMax(DEFAULT_MIN_TILT, DEFAULT_MAX_TILT))

        holder.removeButton.setOnClickListener {
            if(position<phaseList.size){
                phaseList.removeAt(position)
                removedPhaseIDs.add(item.ID)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, phaseList.size)
                setDuration(holder.durationInput.context)
                setDistance(holder.speedInput.context)
            }
        }

        holder.durationInput.addTextChangedListener {
            if(it.toString()!=""){
                try{
                    item.duration = minutesToSeconds(it.toString().toDouble())
                }
                catch(exception: NumberFormatException){
                    println(exception.message)
                }
            }
            else{
                item.duration = minutesToSeconds(DEFAULT_PHASE_DURATION)
            }
            setDuration(holder.durationInput.context)
            setDistance(holder.speedInput.context)
        }
        holder.speedInput.addTextChangedListener {
            if(it.toString()!=""){
                try{
                    item.speed = it.toString().toDouble()
                }
                catch(exception: NumberFormatException){
                    println(exception.message)
                }
            }
            else{
                item.speed = DEFAULT_PHASE_SPEED
            }
            setDistance(holder.speedInput.context)
        }
        holder.tiltInput.addTextChangedListener {
            if(it.toString()!="" && it.toString()!="-"){
                try{
                    item.tilt = it.toString().toDouble()
                }
                catch(exception: NumberFormatException){
                    println(exception.message)
                }
            }
            else{
                item.tilt = DEFAULT_PHASE_TILT
            }
        }
    }

    override fun getItemCount() = phaseList.size


    private fun setDistance(context: Context){
        var distance = 0.0
        phaseList.forEach {
            distance += (it.duration.toDouble() / SECONDS_IN_HOUR.toDouble())*it.speed
        }
        distance = round(distance, DISTANCE_ROUND_MULTIPLIER)
        totalDistance.text = String.format(context.getString(R.string.total_distance), distance)
    }

    private fun setDuration(context: Context){
        var duration = 0
        phaseList.forEach {
            duration += it.duration
        }
        totalDuration.text = String.format(context.getString(R.string.total_duration_minutes), round(secondsToMinutes(duration), DURATION_ROUND_MULTIPLIER))
    }
}