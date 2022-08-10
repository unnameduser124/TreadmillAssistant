package com.example.treadmillassistant.ui

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.treadmillassistant.MainActivity
import com.example.treadmillassistant.backend.Treadmill
import com.example.treadmillassistant.backend.training.TrainingPlan
import com.example.treadmillassistant.backend.user
import com.example.treadmillassistant.databinding.AddTrainingLayoutBinding
import com.example.treadmillassistant.databinding.AddTreadmillLayoutBinding
import com.example.treadmillassistant.ui.addTraining.AddTraining
import com.example.treadmillassistant.ui.editTraining.EditTraining
import java.util.*

class AddTreadmill: AppCompatActivity() {

    private lateinit var binding: AddTreadmillLayoutBinding
    private var fromEditTraining: Boolean = false
    var date: Date = Date()
    private var fromTraining = true
    private var trainingID: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = AddTreadmillLayoutBinding.inflate(layoutInflater)

        setContentView(binding.root)

        fromTraining = intent.getBooleanExtra("fromTraining", false)
        if(fromTraining){
            date.time = intent.getLongExtra("date", Calendar.getInstance().timeInMillis)
        }
        fromEditTraining = intent.getBooleanExtra("fromEditTraining", false)
        trainingID = intent.getLongExtra("id", -1)

        binding.treadmillMaxSpeedInput.filters = arrayOf(InputFilterMinMax(1.0, 30.0))
        binding.treadmillMinSpeedInput.filters = arrayOf(InputFilterMinMax(1.0, 30.0))
        binding.treadmillMaxTiltInput.filters = arrayOf(InputFilterMinMax(-5.0, 15.0))
        binding.treadmillMinTiltInput.filters = arrayOf(InputFilterMinMax(-5.0, 15.0))

        binding.treadmillSaveButton.setOnClickListener{
            val name = binding.treadmillNameInput.text.toString()
            val maxSpeed = if(binding.treadmillMaxSpeedInput.text.toString()=="") 0.0 else  binding.treadmillMaxSpeedInput.text.toString().toDouble()
            val minSpeed = if(binding.treadmillMinSpeedInput.text.toString()=="") 0.0 else  binding.treadmillMinSpeedInput.text.toString().toDouble()
            val maxTilt = if(binding.treadmillMaxTiltInput.text.toString()=="") 0.0 else  binding.treadmillMaxTiltInput.text.toString().toDouble()
            val minTilt = if(binding.treadmillMinTiltInput.text.toString()=="") 0.0 else  binding.treadmillMinTiltInput.text.toString().toDouble()

            if(name!="" && name!=" " && maxSpeed>minSpeed && maxTilt>minTilt){
                val intent: Intent
                user.treadmillList.add(
                    Treadmill(name=name,
                        maxSpeed = maxSpeed,
                        minSpeed = minSpeed,
                        maxTilt = maxTilt,
                        minTilt = minTilt,
                        ID = if(user.treadmillList.isEmpty()) 1 else user.treadmillList.last().ID+1
                    )
                )
                if(fromTraining){
                    intent = Intent(this, AddTraining::class.java)
                    intent.putExtra("date", date.time)
                    finish()
                    startActivity(intent)
                }
                else if(fromEditTraining){
                    intent = Intent(this, EditTraining::class.java)
                    intent.putExtra("date", date.time)
                    intent.putExtra("id", trainingID)
                    finish()
                    startActivity(intent)
                }
                else{
                    intent = Intent(this, MainActivity::class.java)
                    finishAffinity()
                    startActivity(intent)
                }
            }
            else{
                Toast.makeText(this, "Fill in the fields", Toast.LENGTH_SHORT).show()
            }
        }

        binding.cancelButton.setOnClickListener{
            if(fromTraining){
                intent = Intent(this, AddTraining::class.java)
                intent.putExtra("date", date.time)
                finish()
                startActivity(intent)
            }
            else if(fromEditTraining){
                intent = Intent(this, EditTraining::class.java)
                intent.putExtra("date", date.time)
                intent.putExtra("id", trainingID)
                finish()
                startActivity(intent)
            }
            else{
                intent = Intent(this, MainActivity::class.java)
                finishAffinity()
                startActivity(intent)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if(fromTraining){
            intent = Intent(this, AddTraining::class.java)
            intent.putExtra("date", date.time)
            finish()
            startActivity(intent)
        }
        else if(fromEditTraining){
            intent = Intent(this, EditTraining::class.java)
            intent.putExtra("date", date.time)
            intent.putExtra("id", trainingID)
            finish()
            startActivity(intent)
        }
        else{
            intent = Intent(this, MainActivity::class.java)
            finishAffinity()
            startActivity(intent)
        }
    }

}