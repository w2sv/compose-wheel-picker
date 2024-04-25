package com.sd.demo.wheel_picker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.sd.demo.wheel_picker.ui.theme.AppTheme
import com.sd.lib.compose.wheel_picker.VerticalWheelPicker
import com.sd.lib.compose.wheel_picker.WheelPickerDefaults
import com.sd.lib.compose.wheel_picker.rememberWheelPickerState
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
            unfocusedItemCountToEitherSide = 3
        )
            .apply {
                LaunchedEffect(snappedIndex) {
                    i { "Snapped index: $snappedIndex" }
                }
            }

        VerticalWheelPicker(
            state = state,
            itemSize = DpSize(56.dp, 56.dp),
            snapFlingBehaviorAnimationSpecs = WheelPickerDefaults.snapFlingBehaviorAnimationSpecs(
                snap = remember { spring(Spring.DampingRatioHighBouncy, Spring.StiffnessVeryLow) }
            )
        ) { index ->
            Text(index.toString(), fontSize = 18.sp)
        }
    }
}