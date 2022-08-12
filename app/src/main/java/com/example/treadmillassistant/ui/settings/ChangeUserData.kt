package com.example.treadmillassistant.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.treadmillassistant.databinding.ChangeUserDataLayoutBinding

class ChangeUserData: AppCompatActivity() {

    private lateinit var binding: ChangeUserDataLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ChangeUserDataLayoutBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }
}