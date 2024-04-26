package com.w2sv.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.w2sv.demo.ui.theme.AppTheme
import com.w2sv.wheelpicker.WheelPicker
import com.w2sv.wheelpicker.WheelPickerDefaults
import com.w2sv.wheelpicker.rememberWheelPickerState
import slimber.log.i

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainView()
                }
            }
        }
    }
}

@Composable
private fun MainView() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val state = rememberWheelPickerState(
            itemCount = 50,
            startIndex = 25,
            unfocusedItemCountToEitherSide = 2,
        )
            .apply {
                LaunchedEffect(snappedIndex) {
                    i { "Snapped index: $snappedIndex" }
                }
            }

        WheelPicker(
            state = state,
            itemSize = DpSize(56.dp, 56.dp),
            snapFlingBehaviorAnimationSpecs = WheelPickerDefaults.rememberSnapFlingBehaviorAnimationSpecs(
                snap = remember { spring(Spring.DampingRatioHighBouncy, Spring.StiffnessVeryLow) }
            ),
            focusBoxOverlay = {
                Box(
                    modifier = it
                        .fillMaxSize()
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(12.dp)
                        )
                )
            },
        ) { index ->
            Text(index.toString(), fontSize = 18.sp)
        }
    }
}