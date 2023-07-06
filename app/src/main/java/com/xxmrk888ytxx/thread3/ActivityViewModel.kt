package com.xxmrk888ytxx.thread3

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.UUID
import kotlin.random.Random

class ActivityViewModel : ViewModel() {

    private val mutex = Mutex()

    val screenState = MutableStateFlow(ScreenState())


    private suspend fun transportFinished(transportType: TransportType) {
        updateState { it.copy(
            winnerList = it.winnerList + transportType
        ) }

        if(screenState.value.winnerList.size == 3) {
            screenState.update { it.copy(isRaceInProcess = false) }

            viewModelScope.coroutineContext.cancelChildren()
        }
    }

    private suspend fun updateState(onUpdate:(ScreenState) -> ScreenState) {
        mutex.withLock {
            screenState.update { onUpdate(it) }
        }
    }

    private suspend fun move(additionalProgress:Int,transportType: TransportType) {
        when(transportType) {
            TransportType.TRUCK -> {
                updateState {
                    it.copy(truckProgress = minOf(100,it.truckProgress + additionalProgress))
                }

                if(screenState.value.truckProgress == 100) transportFinished(transportType)
            }
            TransportType.CAR -> {
                updateState {
                    it.copy(carProgress = minOf(100,it.carProgress + additionalProgress))
                }

                if(screenState.value.carProgress == 100) transportFinished(transportType)
            }
            TransportType.BIKE -> {
                updateState {
                    it.copy(bikeProgress = minOf(100,it.bikeProgress + additionalProgress))
                }

                if(screenState.value.bikeProgress == 100) transportFinished(transportType)
            }
        }
    }

    private fun writeToLog(string: String) {
        Log.d("def",string)
    }

    private fun reset() {
        screenState.update { ScreenState() }
    }

    fun start() {


        reset()
        screenState.update { ScreenState(isRaceInProcess = true) }

        //Car
        viewModelScope.launch(Dispatchers.IO) {
            while (screenState.value.carProgress != 100 && isActive) {
                val speed = Random(UUID.randomUUID().toString().hashCode()).nextInt(0,20)
                val chanceWheelPuncture = Random(UUID.randomUUID().toString().hashCode()).nextInt(0,20)
                val peopleCount = Random(UUID.randomUUID().toString().hashCode()).nextInt(1,5)

                writeToLog("Speed: $speed chanceWheelPuncture: $chanceWheelPuncture peopleCount $peopleCount")

                runTransport(speed,chanceWheelPuncture,TransportType.CAR)
            }

        }

        //Bike
        viewModelScope.launch(Dispatchers.IO) {

            while (screenState.value.bikeProgress != 100 && isActive) {
                val speed = Random(UUID.randomUUID().toString().hashCode()).nextInt(0,20)
                val chanceWheelPuncture = Random(UUID.randomUUID().toString().hashCode()).nextInt(0,20)
                val isHaveWheelChair = Random(UUID.randomUUID().toString().hashCode()).nextBoolean()

                writeToLog("Speed: $speed chanceWheelPuncture: $chanceWheelPuncture isHaveWheelChair $isHaveWheelChair")

                runTransport(speed,chanceWheelPuncture,TransportType.BIKE)
            }
        }

        //Truck
        viewModelScope.launch(Dispatchers.IO) {
            while (screenState.value.truckProgress != 100 && isActive) {
                val speed = Random(UUID.randomUUID().toString().hashCode()).nextInt(0,20)
                val chanceWheelPuncture = Random(UUID.randomUUID().toString().hashCode()).nextInt(0,20)
                val truckCapacity = Random(UUID.randomUUID().toString().hashCode()).nextInt(0,100)

                writeToLog("Speed: $speed chanceWheelPuncture: $chanceWheelPuncture truckCapacity $truckCapacity")

                runTransport(speed,chanceWheelPuncture,TransportType.TRUCK)
            }
        }
    }

    private suspend fun runTransport(
        speed:Int,
        chanceWheelPuncture:Int,
        transportType: TransportType,
    ) {
        val isWheelPunctured = Random(UUID.randomUUID().toString().hashCode()).nextInt(0,100) <= chanceWheelPuncture

        if(!isWheelPunctured) {
            move(speed,transportType)
        }
        
        if(isWheelPunctured) {
            writeToLog("Wheel is Punctured ${transportType.name}")
        }

        delay(if(isWheelPunctured) 2000 else 1000)
    }

}