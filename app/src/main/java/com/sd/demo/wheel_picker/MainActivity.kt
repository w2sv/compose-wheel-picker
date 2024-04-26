package com.sd.demo.wheel_picker

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sd.demo.wheel_picker.ui.theme.AppTheme
import com.sd.lib.compose.wheel_picker.VerticalWheelPicker
import com.sd.lib.compose.wheel_picker.WheelPickerDefaults
import com.sd.lib.compose.wheel_picker.rememberWheelPickerState
import kotlinx.coroutines.delay
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

//        var targetIndex by remember {
//            mutableIntStateOf(0)
//        }
//        LaunchedEffect(targetIndex) {
//            state.animateScrollToItem(targetIndex)
//            delay(2_000)
//            targetIndex += 13
//        }

        VerticalWheelPicker(
            state = state,
            itemSize = DpSize(56.dp, 56.dp),
            snapFlingBehaviorAnimationSpecs = WheelPickerDefaults.snapFlingBehaviorAnimationSpecs(
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