package com.example.treadmillassistant.backend

import javax.net.ssl.SSLPeerUnverifiedException

class Treadmill(private var speed: Double = 0.0,
                private var tilt: Double = 0.0,
                private var mode: String = "",
                var name: String = "empty name",
                val id: Int = 0,
                val maxSpeed: Double = 20.0,
                val minSpeed: Double = 0.1,
                val maxTilt: Double = 15.0,
                val minTilt: Double = -5.0) {

    fun increaseSpeed(){
        if(speed<maxSpeed) {
            speed += 0.1
        }
    }

    fun decreaseSpeed(){
        if(speed>minSpeed){
            speed-=0.1
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
            tilt+=0.1
        }
    }

    fun decreaseTilt(){
        if(tilt>minTilt){
            tilt-=0.1
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