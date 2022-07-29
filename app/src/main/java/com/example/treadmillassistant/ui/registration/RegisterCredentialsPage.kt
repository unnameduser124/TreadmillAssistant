package com.example.treadmillassistant.ui.registration

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.treadmillassistant.databinding.RegisterPageCredentialsLayoutBinding
import com.example.treadmillassistant.hashMessage

class RegisterCredentialsPage: AppCompatActivity() {

    companion object {
        const val PASSWORD_LENGTH_MIN = 8
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = RegisterPageCredentialsLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val firstName = intent.getStringExtra("firstName")
        val lastName = intent.getStringExtra("lastName")
        val username = intent.getStringExtra("username")

        binding.registerCredentialsNextButton.setOnClickListener {
            val email = binding.emailInput.text.toString()
            val password = hashMessage(binding.passwordInput.text.toString())
            if(firstName!="" && lastName!="" && username!="" && email!="" && password!=""
                && binding.passwordInput.text.toString().length>= PASSWORD_LENGTH_MIN
                && binding.confirmPasswordInput.text.toString()==binding.passwordInput.text.toString()) {
                val intent = Intent(this, RegisterHealthPage::class.java)
                intent.putExtra("firstName", firstName)
                intent.putExtra("lastName", lastName)
                intent.putExtra("username", username)
                intent.putExtra("email", email)
                intent.putExtra("password", password)
                startActivity(intent)
            }
            else if(binding.confirmPasswordInput.text.toString()!=binding.passwordInput.text.toString()){
                Toast.makeText(this, "Passwords don't match!", Toast.LENGTH_SHORT).show()
            }
            else if(binding.passwordInput.text.toString().length < PASSWORD_LENGTH_MIN){
                Toast.makeText(this, "Password has to be between $PASSWORD_LENGTH_MIN to 20 characters long", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this, "Fill in the fields!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}