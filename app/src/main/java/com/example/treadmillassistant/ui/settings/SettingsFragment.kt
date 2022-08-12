package com.example.treadmillassistant.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.treadmillassistant.backend.SETTINGS_TAB_NAV_VIEW_POSITION
import com.example.treadmillassistant.backend.lastNavViewPosition
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

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}