package com.example.treadmillassistant

import com.example.treadmillassistant.backend.*
import com.example.treadmillassistant.backend.workout.Workout
import com.example.treadmillassistant.backend.workout.WorkoutCalendar
import com.example.treadmillassistant.backend.workout.WorkoutPhase
import org.junit.Assert.assertTrue
import org.junit.Test

class Training {

    @Test fun speedRoundTest(){
        val roundedValue = 2.03
        assertTrue(round(roundedValue, SPEED_ROUND_MULTIPLIER)==2.0)
    }
    @Test fun distanceRoundTest(){
        val roundedValue = 2.0379
        assertTrue(round(roundedValue, DISTANCE_ROUND_MULTIPLIER)==2.04)
    }

    @Test fun speedUpTest() {
        val treadmill = Treadmill(speed = 5.0)
        val speedBefore = treadmill.getSpeed()

        treadmill.increaseSpeed()

        assertTrue(speedBefore+0.1==treadmill.getSpeed())
    }
    @Test fun speedDownTest() {
        val treadmill = Treadmill(speed = 5.0)
        val speedBefore = treadmill.getSpeed()

        treadmill.decreaseSpeed()

        assertTrue(speedBefore-0.1==treadmill.getSpeed())
    }
    @Test fun tiltUpTest() {
        val treadmill = Treadmill(tilt = 5.0)
        val tiltBefore = treadmill.getTilt()

        treadmill.increaseTilt()

        assertTrue(tiltBefore+0.1==treadmill.getTilt())
    }
    @Test fun tiltDownTest() {
        val treadmill = Treadmill(tilt = 5.0)
        val tiltBefore = treadmill.getTilt()

        treadmill.decreaseTilt()

        assertTrue(tiltBefore-0.1==treadmill.getTilt())
    }
    @Test fun getAverageSpeedTest() {
        val workout = Workout()

        var phaseOne = WorkoutPhase(10, 10.0)
        var phaseTwo = WorkoutPhase(10, 20.0)

        workout.workoutPlan.addNewPhase(phaseOne)
        workout.workoutPlan.addNewPhase(phaseTwo)
        assertTrue(round(workout.getAverageSpeed(), SPEED_ROUND_MULTIPLIER)==15.0)
    }
    @Test fun getAverageTiltTest() {
        val workout = Workout()

        var phaseOne = WorkoutPhase(10, tilt = 5.0)
        var phaseTwo = WorkoutPhase(10, tilt = 7.0)

        workout.workoutPlan.addNewPhase(phaseOne)
        workout.workoutPlan.addNewPhase(phaseTwo)
        assertTrue(round(workout.getAverageTilt(), TILT_ROUND_MULTIPLIER)==6.0)
    }

    @Test fun calculateTotalCaloriesTest() {
        var user = User(WorkoutCalendar(),"defaultUser@email.com", hashMessage("easyPassword"), "Jan", "Kowalski", "Janek", 20, 80.0)

        val workout = Workout()
        var phaseOne = WorkoutPhase(120, 11.0)
        workout.workoutPlan.addNewPhase(phaseOne)

        val calories = workout.calculateCalories()
        assertTrue(calories==30)
    }
    @Test fun getTotalDuration() {

        val workout = Workout()

        var phaseOne = WorkoutPhase(10)
        var phaseTwo = WorkoutPhase(15)

        workout.workoutPlan.addNewPhase(phaseOne)
        workout.workoutPlan.addNewPhase(phaseTwo)
        assertTrue(workout.getTotalDuration() == 25)
    }
    @Test fun getTotalDistance() {
        val workout = Workout()

        var phaseOne = WorkoutPhase(60, speed = 60.0)

        workout.workoutPlan.addNewPhase(phaseOne)
        assertTrue(workout.getTotalDistance() == 1.0)
    }
    @Test fun getCurrentPhase() {
        TODO("Not implemented yet")
    }
    @Test fun startWorkout() {
        TODO("Not implemented yet")
    }
    @Test fun pauseWorkout() {
        TODO("Not implemented yet")
    }
    @Test fun resumeWorkout() {
        TODO("Not implemented yet")
    }
    @Test fun finishWorkout() {
        TODO("Not implemented yet")
    }
}