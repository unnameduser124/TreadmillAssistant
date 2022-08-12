package com.example.treadmillassistant.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.treadmillassistant.databinding.ChangePasswordLayoutBinding

class ChangePassword: AppCompatActivity() {

    private lateinit var binding: ChangePasswordLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ChangePasswordLayoutBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }
}