package com.example.treadmillassistant.ui.trainingHistory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.treadmillassistant.backend.user
import com.example.treadmillassistant.backend.workout.WorkoutStatus
import com.example.treadmillassistant.databinding.FragmentTrainingHistoryBinding

class TrainingHistoryFragment : Fragment() {

    private var _binding: FragmentTrainingHistoryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTrainingHistoryBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val ItemAdapter = TrainingHistoryItemAdapter(user.workoutSchedule.workoutList.filter {
            it.workoutStatus==WorkoutStatus.Finished }.sortedByDescending { it.workoutTime })

        val linearLayoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)

        binding.trainingHistoryTrainingList.adapter = ItemAdapter
        binding.trainingHistoryTrainingList.layoutManager = linearLayoutManager
        binding.trainingHistoryTrainingList.setHasFixedSize(true)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}