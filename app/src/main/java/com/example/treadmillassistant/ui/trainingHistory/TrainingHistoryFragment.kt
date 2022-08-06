package com.example.treadmillassistant.ui.trainingHistory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.treadmillassistant.backend.user
import com.example.treadmillassistant.backend.training.TrainingStatus
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

        //item adapter with training list sorted from newest to oldest as dataset
        val itemAdapter = TrainingHistoryItemAdapter(user.trainingSchedule.trainingLists.filter {
            it.trainingStatus==TrainingStatus.Finished }.sortedByDescending { it.trainingTime })

        val linearLayoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)

        binding.trainingHistoryTrainingList.adapter = itemAdapter
        binding.trainingHistoryTrainingList.layoutManager = linearLayoutManager
        binding.trainingHistoryTrainingList.setHasFixedSize(true)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}