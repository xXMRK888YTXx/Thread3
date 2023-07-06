package com.xxmrk888ytxx.thread3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xxmrk888ytxx.thread3.ui.theme.Thread3Theme

class MainActivity : ComponentActivity() {

    private val activityViewModel by viewModels<ActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val screenState by activityViewModel.screenState.collectAsState()

            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(text = "Car")
                CustomProgressBar(screenState.carProgress)

                Text(text = "Bike")
                CustomProgressBar(screenState.bikeProgress)

                Text(text = "Truck")
                CustomProgressBar(screenState.truckProgress)

                if(!screenState.isRaceInProcess&&screenState.winnerList.isNotEmpty()) {
                    screenState.winnerList.forEachIndexed { index, transportType ->
                        Text(text = "${index + 1} ${transportType.name}")
                    }
                }


                if(!screenState.isRaceInProcess) {
                    Button(onClick = { activityViewModel.start() }) {
                        Text(text = "Start")
                    }
                }
            }

        }
    }
}

@Composable
fun CustomProgressBar(
    progress:Int = 10
) {

    val animateProcess = animateFloatAsState(
        targetValue = progress.toFloat(),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioHighBouncy
        )
    )

    Box(
        modifier = Modifier
            .padding(10.dp)
            .size(300.dp)
            .drawBehind {
                this.drawArc(
                    useCenter = false,
                    color = Color.Red.copy(0.4f),
                    startAngle = 360f,
                    sweepAngle = 360f,
                    style = Stroke(
                        cap = StrokeCap.Round,
                        width = 40f
                    )
                )

                this.drawArc(
                    useCenter = false,
                    color = Color.Red,
                    startAngle = 270f,
                    sweepAngle = 360f * (animateProcess.value / 100f),
                    style = Stroke(
                        cap = StrokeCap.Round,
                        width = 40f
                    )
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(text = progress.toString(), fontSize = 30.sp)
    }
}