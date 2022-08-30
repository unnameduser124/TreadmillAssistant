package com.example.treadmillassistant.backend.serverDatabase.databaseClasses

class NewID(private val detail: Int = -1) {
    val id: Long get() {
        return detail.toLong()
    }
}