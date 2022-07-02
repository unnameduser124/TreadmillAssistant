package com.example.treadmillassistant.backend

class Treadmill(private var speed: Double = 0.0, private var tilt: Double = 0.0, private var mode: String = "", var name: String = "empty name", val id: Int=0) {

    fun increaseSpeed(){
        speed+=0.1
    }

    fun decreaseSpeed(){
        speed-=0.1
    }

    fun setSpeed(speed: Double){
        this.speed = speed
    }

    fun getSpeed(): Double{
        return speed
    }

    fun increaseTilt(){
        tilt+=0.1
    }

    fun decreaseTilt(){
        tilt-=0.1
    }

    fun setTilt(tilt: Double){
        this.tilt = tilt
    }

    fun getTilt(): Double{
        return tilt
    }

}