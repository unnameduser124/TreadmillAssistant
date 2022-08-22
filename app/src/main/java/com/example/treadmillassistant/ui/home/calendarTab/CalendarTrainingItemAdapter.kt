package com.example.treadmillassistant.ui.home.calendarTab

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.example.treadmillassistant.R
import com.example.treadmillassistant.backend.*
import com.example.treadmillassistant.backend.training.Training
import com.example.treadmillassistant.backend.training.TrainingStatus
import com.example.treadmillassistant.ui.trainingDetails.TrainingDetailsPage
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.*

class CalendarTrainingItemAdapter(private val dataset: List<Training>): RecyclerView.Adapter<CalendarTrainingItemAdapter.ItemViewHolder>(){

    class ItemViewHolder(view: View): RecyclerView.ViewHolder(view){
        val timeView: TextView = view.findViewById(R.id.training_time)
        val durationView: TextView = view.findViewById(R.id.training_duration)
        val startButton: MaterialButton = view.findViewById(R.id.start_training_button)
        val parent = view
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder{
        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.calendar_tab_training_list_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int){
        val item = dataset[position]

        //set training time and duration
        val dateFormat = SimpleDateFormat("HH:mm", Locale.ROOT)
        val trainingTime = dateFormat.format(item.trainingTime.time)

        holder.timeView.text = trainingTime

        setUpDurationView(holder.durationView, item.getTotalDuration())

        //hide start button if training is finished, abandoned or planned one day or more in the past
        val date = Calendar.getInstance()
        date.set(Calendar.HOUR_OF_DAY, 0)
        date.set(Calendar.MINUTE, 0)
        if(item.trainingTime.before(date) || item.trainingStatus==TrainingStatus.Finished || item.trainingStatus==TrainingStatus.Abandoned){
            holder.startButton.isGone = true
        }

        holder.parent.setOnClickListener {
            val intent = Intent(holder.parent.context, TrainingDetailsPage::class.java)
            intent.putExtra("id", item.ID)
            intent.putExtra("fromCalendarPage", true)
            startActivity(holder.parent.context, intent, null)
        }

        holder.startButton.setOnClickListener {
            if(tabLayout!=null){
                tabLayout?.selectTab(tabLayout?.getTabAt(1))
                startClicked?.onStartClicked(item.ID)
            }
        }

    }

    override fun getItemCount() = dataset.size

}