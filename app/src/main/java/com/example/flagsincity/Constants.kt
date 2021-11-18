package com.example.flagsincity

object Constants {
    const val TAG = "RealtimeDatabaseTag"
    const val FORTRESSES_REF = "fortresses"

    const val LOC_PRESICION = 0.00016 // in degrees; [m] = 111 139 * LOC_PRESICION
    const val LOC_PRESICION_METRES = LOC_PRESICION * 111139.0
    const val MIN_DISTANCE = 0.0045 // in degrees; [m] = 111 139 * MIN_DISTANCE ~ 500m
    const val MIN_DISTANCE_METRES = MIN_DISTANCE * 111139.0

}