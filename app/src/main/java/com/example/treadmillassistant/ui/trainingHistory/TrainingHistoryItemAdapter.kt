package com.example.treadmillassistant.ui.trainingHistory

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.treadmillassistant.R
import com.example.treadmillassistant.backend.*
import com.example.treadmillassistant.backend.training.Training
import com.example.treadmillassistant.ui.trainingDetails.TrainingDetailsPage
import java.text.SimpleDateFormat
import java.util.*

class TrainingHistoryItemAdapter(private val dataset: List<Training>): RecyclerView.Adapter<TrainingHistoryItemAdapter.ItemViewHolder>(){

    class ItemViewHolder(view: View): RecyclerView.ViewHolder(view){
        val duration: TextView = view.findViewById(R.id.history_item_duration_text)
        val avgSpeed: TextView = view.findViewById(R.id.history_item_avg_speed_text)
        val distance: TextView = view.findViewById(R.id.history_item_calories_text)
        val date: TextView = view.findViewById(R.id.history_item_date_text)
        val item = view
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder{
        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.history_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int){
        val training = dataset[position]

        val dateFormat = SimpleDateFormat("dd.MM.${training.trainingTime.get(Calendar.YEAR)}")
        val trainingTime = dateFormat.format(training.trainingTime.time)

        holder.duration.text = String.format(holder.duration.context.getString(R.string.duration_minutes),
            round(secondsToMinutes(training.getTotalDuration()), DURATION_ROUND_MULTIPLIER))
        holder.avgSpeed.text = String.format(holder.avgSpeed.context.getString(R.string.speed), round(training.getAverageSpeed(), SPEED_ROUND_MULTIPLIER))
        holder.distance.text = String.format(holder.distance.context.getString(R.string.distance), round(training.getTotalDistance(), DISTANCE_ROUND_MULTIPLIER))
        holder.date.text = trainingTime

        holder.item.setOnClickListener {
            val intent = Intent(holder.date.context, TrainingDetailsPage::class.java)
            intent.putExtra("fromCalendarPage", false)
            intent.putExtra("id", training.ID)
            startActivity(holder.date.context, intent, null)
        }
    }

    override fun getItemCount() = dataset.size

}