package com.example.flagsincity.data


data class Fortress (var userName: String?,
                     var latitude: Double?,
                     var longitude: Double?,
                     var lives: Int?) {
        constructor() : this("Já", 0.0,0.0,
        10)
    // Null default values create a no-argument default constructor which is needed for deserialization.
}