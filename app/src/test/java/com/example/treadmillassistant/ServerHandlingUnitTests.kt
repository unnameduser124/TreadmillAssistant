package com.example.treadmillassistant

import com.example.treadmillassistant.backend.User
import com.example.treadmillassistant.backend.serverDatabase.databaseClasses.ServerTraining
import com.example.treadmillassistant.backend.serverDatabase.databaseClasses.ServerTrainingPhase
import com.example.treadmillassistant.backend.serverDatabase.databaseClasses.ServerUser
import com.example.treadmillassistant.backend.training.PlannedTraining
import com.example.treadmillassistant.backend.training.TrainingPhase
import org.junit.Assert
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

class ServerHandlingUnitTests {

    @Test fun convertServerUserToUser(){
        val serverUser = ServerUser("Jan", "Kowalski", "Janek", 25, 90.0, "janek@email.com", "")
        val user = User(serverUser, -1L)

        Assert.assertEquals(serverUser.FirstName, user.firstName)
        Assert.assertEquals(serverUser.LastName, user.lastName)
        Assert.assertEquals(serverUser.Nick, user.username)
        Assert.assertEquals(serverUser.Age, user.age)
        Assert.assertEquals(serverUser.Weight, user.weight, 0.1)
        Assert.assertEquals(serverUser.Email, user.email)
    }

    @Test fun convertServerTrainingToPlannedTraining(){
        val serverTraining = ServerTraining("14-09-2022", " 08:00", "link", "Upcoming")

        val training = PlannedTraining(serverTraining)

        Assert.assertEquals(training.ID, serverTraining.ID)
        Assert.assertEquals(training.trainingStatus.toString(), serverTraining.TrainingStatus)
        Assert.assertEquals(training.mediaLink, serverTraining.Link)
        Assert.assertEquals(training.trainingPlan.ID, serverTraining.TrainingPlanID)
        Assert.assertEquals(training.treadmill.ID, serverTraining.Treadmill)
        val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.ROOT)
        val date = simpleDateFormat.parse("${serverTraining.Date} ${serverTraining.Time}")
        Assert.assertEquals(training.trainingTime.time, date)
    }

    @Test fun convertPlannedTrainingToServerTraining(){
        val training = PlannedTraining()

        val serverTraining = ServerTraining(training)

        Assert.assertEquals(training.ID, serverTraining.ID)
        Assert.assertEquals(training.trainingStatus.toString(), serverTraining.TrainingStatus)
        Assert.assertEquals(training.mediaLink, serverTraining.Link)
        Assert.assertEquals(training.trainingPlan.ID, serverTraining.TrainingPlanID)
        Assert.assertEquals(training.treadmill.ID, serverTraining.Treadmill)
        val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.ROOT)
        val date = simpleDateFormat.parse("${serverTraining.Date} ${serverTraining.Time}")
        Assert.assertEquals(simpleDateFormat.format(training.trainingTime.time), simpleDateFormat.format(date!!))
    }

    @Test fun convertServerTrainingPhaseToTrainingPhase(){
        val serverTrainingPhase = ServerTrainingPhase(10.0, 10.0, 10, 10, 10)

        val trainingPhase = TrainingPhase(serverTrainingPhase)

        Assert.assertEquals(serverTrainingPhase.Speed, trainingPhase.speed, 0.1)
        Assert.assertEquals(serverTrainingPhase.Tilt, trainingPhase.tilt, 0.1)
        Assert.assertEquals(serverTrainingPhase.Duration, trainingPhase.duration)
        Assert.assertEquals(serverTrainingPhase.TrainingPlanID, trainingPhase.trainingPlanID)
        Assert.assertEquals(serverTrainingPhase.OrderNumber, trainingPhase.orderNumber)
        Assert.assertEquals(serverTrainingPhase.ID, trainingPhase.ID)
    }

    @Test fun convertTrainingPhaseToServerTrainingPhase(){
        val trainingPhase = TrainingPhase()

        val serverTrainingPhase = ServerTrainingPhase(trainingPhase)

        Assert.assertEquals(serverTrainingPhase.Speed, trainingPhase.speed, 0.1)
        Assert.assertEquals(serverTrainingPhase.Tilt, trainingPhase.tilt, 0.1)
        Assert.assertEquals(serverTrainingPhase.Duration, trainingPhase.duration)
        Assert.assertEquals(serverTrainingPhase.TrainingPlanID, trainingPhase.trainingPlanID)
        Assert.assertEquals(serverTrainingPhase.OrderNumber, trainingPhase.orderNumber)
        Assert.assertEquals(serverTrainingPhase.ID, trainingPhase.ID)
    }

    @Test fun serializeWithExceptions(){
        val trainingPhase = TrainingPhase()

        val exceptions = listOf("ID", "speed")

        val json = com.example.treadmillassistant.backend.serializeWithExceptions(trainingPhase, exceptions)

        val expectedJson = "{\"duration\":0,\"tilt\":0.0,\"trainingPlanID\":-1,\"orderNumber\":-1,\"isFinished\":false}"

        Assert.assertEquals(expectedJson, json)
    }
}