package com.example.treadmillassistant.backend.serverDatabase.databaseClasses

data class ServerUser(val FirstName: String,
                 val LastName: String,
                 val Nick: String,
                 val Age: Int,
                 val Weight: Double,
                 val Email: String,
                 val Password: String)