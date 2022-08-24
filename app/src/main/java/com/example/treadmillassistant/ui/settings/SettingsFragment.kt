package com.example.treadmillassistant.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.fragment.app.Fragment
import com.example.treadmillassistant.backend.localDatabase.TrainingDatabaseService
import com.example.treadmillassistant.backend.user
import com.example.treadmillassistant.databinding.ClearTrainingHistoryConfirmationPopupBinding
import com.example.treadmillassistant.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.changePassword.setOnClickListener {
            val intent = Intent(it.context, ChangePassword::class.java)
            startActivity(intent)
        }

        binding.changeUserData.setOnClickListener {
            val intent = Intent(it.context, ChangeUserData::class.java)
            startActivity(intent)
        }

        binding.changeHealthData.setOnClickListener {
            val intent = Intent(it.context, ChangeHealthData::class.java)
            startActivity(intent)
        }

        binding.clearUserDataButton.setOnClickListener {
            val popupBinding = ClearTrainingHistoryConfirmationPopupBinding.inflate(layoutInflater)

            val width = LinearLayout.LayoutParams.WRAP_CONTENT
            val height = LinearLayout.LayoutParams.WRAP_CONTENT
            val focusable = true
            val popupWindow = PopupWindow(popupBinding.root, width, height, focusable)
            popupWindow.contentView = popupBinding.root
            popupWindow.showAtLocation(binding.clearUserDataButton, Gravity.CENTER, 0, 0)

            popupBinding.cancelDataDeletionButton.setOnClickListener {
                popupWindow.dismiss()
            }
            popupBinding.confirmDataDeletionButton.setOnClickListener {
                TrainingDatabaseService(it.context).clearTrainingHistory()
                user.trainingSchedule.trainingList.clear()
                popupWindow.dismiss()
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}