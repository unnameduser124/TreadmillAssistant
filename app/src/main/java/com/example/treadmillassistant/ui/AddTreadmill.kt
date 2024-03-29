package com.example.treadmillassistant.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import com.example.treadmillassistant.MainActivity
import com.example.treadmillassistant.backend.*
import com.example.treadmillassistant.backend.localDatabase.TreadmillService
import com.example.treadmillassistant.databinding.AddTreadmillLayoutBinding
import com.example.treadmillassistant.ui.addTraining.AddTraining
import com.example.treadmillassistant.ui.editTraining.EditTraining
import java.util.*

class AddTreadmill: AppCompatActivity() {

    private lateinit var binding: AddTreadmillLayoutBinding
    private var fromEditTraining: Boolean = false
    var date: Date = Date()
    private var fromAddTraining = true
    private var trainingID: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = AddTreadmillLayoutBinding.inflate(layoutInflater)
        binding.treadmillRemoveButton.isGone = true

        setContentView(binding.root)

        fromAddTraining = intent.getBooleanExtra("fromTraining", false)
        fromEditTraining = intent.getBooleanExtra("fromEditTraining", false)
        if(fromAddTraining){
            date.time = intent.getLongExtra("date", Calendar.getInstance().timeInMillis)
        }
        trainingID = intent.getLongExtra("id", -1)

        //filter preventing user from putting in invalid values
        binding.treadmillMaxSpeedInput.filters = arrayOf(InputFilterMinMax(DEFAULT_MIN_SPEED, DEFAULT_MAX_SPEED))
        binding.treadmillMinSpeedInput.filters = arrayOf(InputFilterMinMax(DEFAULT_MIN_SPEED, DEFAULT_MAX_SPEED))
        binding.treadmillMaxTiltInput.filters = arrayOf(InputFilterMinMax(DEFAULT_MIN_TILT, DEFAULT_MAX_TILT))
        binding.treadmillMinTiltInput.filters = arrayOf(InputFilterMinMax(DEFAULT_MIN_TILT, DEFAULT_MAX_TILT))

        binding.treadmillSaveButton.setOnClickListener{
            val name = binding.treadmillNameInput.text.toString()
            val maxSpeed = if(binding.treadmillMaxSpeedInput.text.toString()=="") 0.0 else  binding.treadmillMaxSpeedInput.text.toString().toDouble()
            val minSpeed = if(binding.treadmillMinSpeedInput.text.toString()=="") 0.0 else  binding.treadmillMinSpeedInput.text.toString().toDouble()
            val maxTilt = if(binding.treadmillMaxTiltInput.text.toString()=="") 0.0 else  binding.treadmillMaxTiltInput.text.toString().toDouble()
            val minTilt = if(binding.treadmillMinTiltInput.text.toString()=="") 0.0 else  binding.treadmillMinTiltInput.text.toString().toDouble()

            if(name!="" && name!=" " && maxSpeed>minSpeed && maxTilt>minTilt){
                val intent: Intent

                val treadmill = Treadmill(
                    name=name,
                    maxSpeed = maxSpeed,
                    minSpeed = minSpeed,
                    maxTilt = maxTilt,
                    minTilt = minTilt
                )
                treadmill.ID = TreadmillService(this).insertNewTreadmill(treadmill)
                user.treadmillList.add(treadmill)
                if(fromAddTraining){
                    intent = Intent(this, AddTraining::class.java)
                    intent.putExtra("date", date.time)
                    finish()
                    startActivity(intent)
                }
                else if(fromEditTraining){
                    intent = Intent(this, EditTraining::class.java)
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
            if(fromAddTraining){
                intent = Intent(this, AddTraining::class.java)
                intent.putExtra("date", date.time)
                finish()
                startActivity(intent)
            }
            else if(fromEditTraining){
                intent = Intent(this, EditTraining::class.java)
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
        if(fromAddTraining){
            intent = Intent(this, AddTraining::class.java)
            intent.putExtra("date", date.time)
            finish()
            startActivity(intent)
        }
        else if(fromEditTraining){
            intent = Intent(this, EditTraining::class.java)
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