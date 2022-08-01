package com.example.treadmillassistant

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.core.content.ContextCompat.startActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.treadmillassistant.backend.*
import com.example.treadmillassistant.backend.database.TrainingDatabaseService
import com.example.treadmillassistant.backend.database.UserService
import com.example.treadmillassistant.databinding.ActivityMainBinding
import com.example.treadmillassistant.ui.addworkoutplan.AddWorkoutPlan
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(!loggedIn){
            generateMockData(this)
            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val userService = UserService(this)

        val navViewPosition = intent.getIntExtra("navViewPosition", 0)

        setSupportActionBar(binding.appBarMain.toolbar)

        val header = binding.navView.getHeaderView(0)
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_training_history, R.id.nav_settings
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        header.findViewById<TextView>(R.id.email_header).text = "${user.email}"

        navView.menu.getItem(navViewPosition).isChecked = true
        navController.navigate(navView.menu.getItem(navViewPosition).itemId)

        header.setOnClickListener {
            val intent = Intent(this, ProfilePage::class.java)
            startActivity(intent)
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


    // create an action bar button
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.appbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // handle button activities
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
        if (id == R.id.add_new_workout_menu_item) {
            val intent = Intent(this, AddWorkout::class.java)
            startActivity(intent)
        }
        else if(id == R.id.add_new_workout_plan_menu_item){
            val intent = Intent(this, AddWorkoutPlan::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}