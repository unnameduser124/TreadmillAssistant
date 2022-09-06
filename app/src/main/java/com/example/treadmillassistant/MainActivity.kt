package com.example.treadmillassistant

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.treadmillassistant.backend.*
import com.example.treadmillassistant.backend.localDatabase.UserService
import com.example.treadmillassistant.backend.serverDatabase.databaseClasses.ServerTrainingPlan
import com.example.treadmillassistant.backend.serverDatabase.serverDatabaseService.ServerConstants.BASE_URL
import com.example.treadmillassistant.backend.serverDatabase.serverDatabaseService.ServerTrainingPlanService
import com.example.treadmillassistant.backend.serverDatabase.serverDatabaseService.StatusCode
import com.example.treadmillassistant.backend.serverDatabase.serverDatabaseService.getResponseCode
import com.example.treadmillassistant.databinding.ActivityMainBinding
import com.example.treadmillassistant.ui.addTraining.AddTraining
import com.example.treadmillassistant.ui.addTrainingPlan.AddTrainingPlan
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {

    private var doubleBackToExitPressedOnce: Boolean = false
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(appStart){
            val tempUser =  UserService(this).loadUser()

            if(tempUser!=null){
                user = tempUser
            }
            else{
                val intent = Intent(this, LoginPage::class.java)
                appStart = false
                startActivity(intent)
                return
            }
            loadUser(this)
            /*thread{
                val allPlansPair = ServerTrainingPlanService().getAllTrainingPlans(0, 10)
                //println(allPlansPair.first)
                if(allPlansPair.first == StatusCode.OK){
                    allPlansPair.second.forEach {
                        val getPlanPair = ServerTrainingPlanService().getTrainingPlan(it.ID)
                        println(getPlanPair.second.name)
                    }

                }
            }*/
            appStart = false
        }
        setSupportActionBar(binding.appBarMain.toolbar)
        //navigation drawer setup
        val header = binding.navView.getHeaderView(0)
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView

        navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_training_history, R.id.nav_settings
            ), drawerLayout
        )
        navView.menu.getItem(lastNavViewPosition).isChecked = true
        navController.navigate(navView.menu.getItem(lastNavViewPosition).itemId)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        //profile header email setup
        header.findViewById<TextView>(R.id.email_header).text = user.email
        navView.setNavigationItemSelectedListener {
            if(it ==  navView.menu.getItem(HOME_TAB_NAV_VIEW_POSITION)){
                lastNavViewPosition = HOME_TAB_NAV_VIEW_POSITION
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({drawerLayout.closeDrawers()}, 50)
                navController.navigate(navView.menu.getItem(lastNavViewPosition).itemId)
                binding.appBarMain.toolbar.menu.setGroupVisible(0, true)

            }
            else if(it ==  navView.menu.getItem(TRAINING_HISTORY_NAV_VIEW_POSITION)){
                lastNavViewPosition = TRAINING_HISTORY_NAV_VIEW_POSITION
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({drawerLayout.closeDrawers()}, 50)
                navController.navigate(navView.menu.getItem(lastNavViewPosition).itemId)
                binding.appBarMain.toolbar.menu.setGroupVisible(0, false)
            }
            else{

                lastNavViewPosition = SETTINGS_TAB_NAV_VIEW_POSITION
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({drawerLayout.closeDrawers()}, 50)
                navController.navigate(navView.menu.getItem(lastNavViewPosition).itemId)
                binding.appBarMain.toolbar.menu.setGroupVisible(0, false)
            }
            it.isChecked = true
            true
        }

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
        if (id == R.id.add_new_training_menu_item) {
            val intent = Intent(this, AddTraining::class.java)
            startActivity(intent)
        }
        else if(id == R.id.add_new_training_plan_menu_item){
            val intent = Intent(this, AddTrainingPlan::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {

        if(lastNavViewPosition!= HOME_TAB_NAV_VIEW_POSITION){
            lastNavViewPosition = HOME_TAB_NAV_VIEW_POSITION
            binding.navView.menu.getItem(lastNavViewPosition).isChecked = true
            binding.drawerLayout.closeDrawers()
            navController.navigate(binding.navView.menu.getItem(lastNavViewPosition).itemId)
        }
        else{
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed()
                finishAffinity()
                return
            }
            doubleBackToExitPressedOnce = true
            Toast.makeText(this, "click again to exit", Toast.LENGTH_SHORT).show()
            tabLayout?.selectTab(tabLayout?.getTabAt(0))
            Handler(Looper.getMainLooper()).postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
        }

    }
}