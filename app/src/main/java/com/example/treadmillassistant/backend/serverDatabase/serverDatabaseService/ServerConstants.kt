package com.example.treadmillassistant.backend.serverDatabase.serverDatabaseService

object ServerConstants {
    const val BASE_URL: String = "http://vps-a015c8d3.vps.ovh.net"
}

enum class StatusCode(val code: Int) {
    OK(200),
    Created(201),

    BadRequest(400),
    Unauthorized(401),
    NotFound(404),
    Conflict(409),
    UnprocessableEntity(422),

    InternalServerError(500),
    ServiceUnavailable(503),

    Unknown(0)
}

fun getResponseCode(responseCode: Int): StatusCode{
    return if(StatusCode.values().find { it.code == responseCode }!=null){
        StatusCode.values().first { it.code == responseCode }
    }else{
        StatusCode.Unknown
    }
}