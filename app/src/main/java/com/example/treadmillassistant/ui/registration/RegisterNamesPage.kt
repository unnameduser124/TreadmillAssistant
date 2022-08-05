package com.example.treadmillassistant.ui.registration

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.treadmillassistant.databinding.RegisterPageNamesLayoutBinding

class RegisterNamesPage: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = RegisterPageNamesLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerNamesNextButton.setOnClickListener{
            val firstName = binding.firstNameInput.text.toString()
            val lastName = binding.lastNameInput.text.toString()
            val username = binding.usernameInput.text.toString()

            if(firstName!="" && lastName!="" && username!="") {
                val intent = Intent(this, RegisterCredentialsPage::class.java)
                intent.putExtra("firstName", firstName)
                intent.putExtra("lastName", lastName)
                intent.putExtra("username", username)
                startActivity(intent)
            }
            else{
                Toast.makeText(this, "Fill in the fields!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}