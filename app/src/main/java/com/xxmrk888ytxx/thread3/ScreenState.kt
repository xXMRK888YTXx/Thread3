package com.xxmrk888ytxx.thread3

data class ScreenState(
    val isRaceInProcess:Boolean = false,
    val carProgress:Int = 0,
    val bikeProgress:Int = 0,
    val truckProgress:Int = 0,
    val winnerList:Set<TransportType> = setOf()
)
