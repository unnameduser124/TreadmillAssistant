package com.example.treadmillassistant.backend

import javax.net.ssl.SSLPeerUnverifiedException

class Treadmill(private var speed: Double = 0.0,
                private var tilt: Double = 0.0,
                private var mode: String = "",
                var name: String = "empty name",
                val ID: Long = 0,
                val maxSpeed: Double = DEFAULT_MAX_SPEED,
                val minSpeed: Double = DEFAULT_MIN_SPEED,
                val maxTilt: Double = DEFAULT_MAX_TILT,
                val minTilt: Double = DEFAULT_MIN_TILT,
                val userID: Long = user.ID) {

    fun increaseSpeed(){
        if(speed<maxSpeed) {
            speed += SPEED_TILT_INCREMENT
        }
    }

    fun decreaseSpeed(){
        if(speed>minSpeed){
            speed -= SPEED_TILT_INCREMENT
        }
    }

    fun setSpeed(speed: Double){
        this.speed = speed
    }

    fun getSpeed(): Double{
        return speed
    }

    fun increaseTilt(){
        if(tilt<maxTilt){
            tilt+=SPEED_TILT_INCREMENT
        }
    }

    fun decreaseTilt(){
        if(tilt>minTilt){
            tilt-=SPEED_TILT_INCREMENT
            if(tilt<minTilt){
                tilt = minTilt
            }
        }
    }

    fun setTilt(tilt: Double){
        this.tilt = tilt
    }

    fun getTilt(): Double{
        return tilt
    }

}