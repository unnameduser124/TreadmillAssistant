package com.example.treadmillassistant.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.treadmillassistant.databinding.ChangeHealthDataLayoutBinding

class ChangeHealthData: AppCompatActivity() {

    private lateinit var binding: ChangeHealthDataLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ChangeHealthDataLayoutBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }
}