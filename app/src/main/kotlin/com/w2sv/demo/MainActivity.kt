package com.w2sv.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.w2sv.demo.ui.theme.AppTheme
import com.w2sv.wheelpicker.WheelPicker
import com.w2sv.wheelpicker.rememberWheelPickerState
import slimber.log.i

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        DurationPickerRow()
                    }
                }
            }
        }
    }
}

@Composable
private fun DurationPickerRow() {
    val hourPickerState = rememberWheelPickerState(
        itemCount = 24,
        startIndex = 12,
        unfocusedItemCountToEitherSide = 2,
    )
    val minutePickerState = rememberWheelPickerState(
        itemCount = 60,
        startIndex = 30,
        unfocusedItemCountToEitherSide = 2,
    )

    LaunchedEffect(hourPickerState.snappedIndex, minutePickerState.snappedIndex) {
        i { "Snapped indices: ${hourPickerState.snappedIndex} ${minutePickerState.snappedIndex}" }
    }

    Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
        WheelPicker(
            state = hourPickerState,
            itemSize = DpSize(56.dp, 56.dp),
            focusBoxOverlay = {
                FocusBoxOverlay(it)
            },
        ) { index ->
            Text(index.toString(), fontSize = 18.sp)
        }
        Spacer(modifier = Modifier.width(16.dp))
        WheelPicker(
            state = minutePickerState,
            itemSize = DpSize(56.dp, 56.dp),
            focusBoxOverlay = {
                FocusBoxOverlay(it)
            },
        ) { index ->
            Text(index.toString(), fontSize = 18.sp)
        }
    }
}

@Composable
private fun SingularWheelPicker() {
    val state = rememberWheelPickerState(
        itemCount = 60,
        startIndex = 15,
        unfocusedItemCountToEitherSide = 2,
    )

    LaunchedEffect(state.snappedIndex) {
        i { "Snapped index: ${state.snappedIndex}" }
    }

    WheelPicker(
        state = state,
        itemSize = DpSize(56.dp, 56.dp),
        focusBoxOverlay = {
            FocusBoxOverlay(it)
        },
    ) { index ->
        Text(index.toString(), fontSize = 18.sp)
    }
}

@Composable
private fun FocusBoxOverlay(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(12.dp)
            )
    )
}