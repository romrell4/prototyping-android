package com.romrell4.prototyping

data class Event(
    val type: String?,
    val sender: String?,
    val message: String?
) {
    constructor(map: Map<String, Any>): this(
        map["type"] as? String,
        map["sender"] as? String,
        map["message"] as? String
    )
}