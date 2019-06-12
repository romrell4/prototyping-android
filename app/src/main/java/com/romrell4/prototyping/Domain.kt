package com.romrell4.prototyping

data class Event(
    val sender: String?,
    val message: String?
) {
    constructor(map: Map<String, Any>): this(
        map["sender"] as? String,
        map["message"] as? String
    )
}