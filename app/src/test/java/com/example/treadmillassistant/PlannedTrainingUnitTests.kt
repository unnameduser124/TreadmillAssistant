package com.example.treadmillassistant

import com.example.treadmillassistant.backend.*
import com.example.treadmillassistant.backend.training.*
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.*

class PlannedTrainingUnitTests {

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
        val training = PlannedTraining()

        var phaseOne = TrainingPhase(10, 10.0)
        var phaseTwo = TrainingPhase(10, 20.0)

        training.trainingPlan.addNewPhase(phaseOne)
        training.trainingPlan.addNewPhase(phaseTwo)
        assertTrue(round(training.getAverageSpeed(), SPEED_ROUND_MULTIPLIER)==15.0)
    }
    @Test fun getAverageTiltTest() {
        val training = PlannedTraining()

        var phaseOne = TrainingPhase(10, tilt = 5.0)
        var phaseTwo = TrainingPhase(10, tilt = 7.0)

        training.trainingPlan.addNewPhase(phaseOne)
        training.trainingPlan.addNewPhase(phaseTwo)
        assertTrue(round(training.getAverageTilt(), TILT_ROUND_MULTIPLIER)==6.0)
    }

    @Test fun calculateTotalCaloriesTest() {
        var user = User(TrainingCalendar(),"defaultUser@email.com", hashMessage("easyPassword"), "Jan", "Kowalski", "Janek", 20, 80.0)

        val training = PlannedTraining()
        var phaseOne = TrainingPhase(120, 11.0)
        training.trainingPlan.addNewPhase(phaseOne)

        val calories = training.calculateCalories()
        assertTrue(calories==30)
    }
    @Test fun getTotalDuration() {

        val training = PlannedTraining()

        var phaseOne = TrainingPhase(10)
        var phaseTwo = TrainingPhase(15)

        training.trainingPlan.addNewPhase(phaseOne)
        training.trainingPlan.addNewPhase(phaseTwo)
        assertTrue(training.getTotalDuration() == 25)
    }
    @Test fun getTotalDistance() {
        val training = PlannedTraining()

        var phaseOne = TrainingPhase(60, speed = 60.0)

        training.trainingPlan.addNewPhase(phaseOne)
        assertTrue(training.getTotalDistance() == 1.0)
    }
    @Test fun getCurrentPhase() {
        val training = PlannedTraining()

        var phaseOne = TrainingPhase(10, orderNumber = 1)
        var phaseTwo = TrainingPhase(15, orderNumber = 2)
        var phaseThree = TrainingPhase(15, orderNumber = 3)

        training.trainingPlan.addNewPhase(phaseOne)
        training.trainingPlan.addNewPhase(phaseTwo)
        training.trainingPlan.addNewPhase(phaseThree)
        training.lastPhaseStart = Calendar.getInstance().timeInMillis - 15000L

        assertTrue(training.getCurrentPhase() == phaseTwo)
    }
    @Test fun startWorkout() {
        val training = PlannedTraining()

        training.startTraining()

        assertTrue(training.trainingStatus == TrainingStatus.InProgress
                && training.treadmill.getSpeed() == DEFAULT_WORKOUT_START_SPEED
                && training.treadmill.getTilt() == DEFAULT_WORKOUT_START_TILT)
    }
    @Test fun pauseWorkout() {
        val training = GenericTraining()

        training.startTraining()
        training.pauseTraining()
        assertTrue(training.trainingStatus == TrainingStatus.Paused
                && training.trainingPlan.trainingPhaseList.size>0)
    }
    @Test fun resumeWorkout() {
        val training = GenericTraining()

        training.startTraining()
        training.speedUp()
        training.tiltDown()
        training.pauseTraining()
        val workoutPhaseListSize = training.trainingPlan.trainingPhaseList.size
        training.resumeTraining()
        assertTrue(training.trainingStatus == TrainingStatus.InProgress
                && training.trainingPlan.trainingPhaseList.size == workoutPhaseListSize
                && training.treadmill.getSpeed() == DEFAULT_WORKOUT_START_SPEED
                && training.treadmill.getTilt() == DEFAULT_WORKOUT_START_TILT)
    }
    @Test fun finishWorkout() {
        val training = PlannedTraining()
        training.trainingPlan.addNewPhase(TrainingPhase())

        training.startTraining()
        training.pauseTraining()
        training.resumeTraining()
        training.pauseTraining()
        training.finishTraining()
        assertTrue(training.trainingStatus == TrainingStatus.Finished)
    }
}