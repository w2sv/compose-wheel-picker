package com.sd.demo.wheel_picker

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.sd.demo.wheel_picker.ui.theme.AppTheme
import com.sd.lib.compose.wheel_picker.VerticalWheelPicker
import com.sd.lib.compose.wheel_picker.WheelPickerFocusVertical
import com.sd.lib.compose.wheel_picker.rememberWheelPickerState
import kotlinx.coroutines.delay

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
        SampleDefault()
    }
}

@Composable
private fun SampleDefault() {
    VerticalWheelPicker(
        modifier = Modifier.width(60.dp),
        // Specified item count.
        count = 50,
    ) { index ->
        Text(index.toString())
    }
}

@Composable
fun SampleCustomItemSize() {
    VerticalWheelPicker(
        modifier = Modifier.width(60.dp),
        count = 50,
        // Specified item height.
        itemSize = DpSize(60.dp, 60.dp),
    ) {
        Text(it.toString())
    }
}

@Composable
fun SampleCustomUnfocusedCount() {
    VerticalWheelPicker(
        modifier = Modifier.width(60.dp),
        count = 50,
        // Specified unfocused count.
        unfocusedCount = 2,
    ) {
        Text(it.toString())
    }
}

@Composable
private fun SampleCustomDivider() {
    VerticalWheelPicker(
        modifier = Modifier.width(60.dp),
        count = 50,
        focus = {
            // Custom divider.
            WheelPickerFocusVertical(dividerColor = Color.Red, dividerThickness = 2.dp)
        },
    ) {
        Text(it.toString())
    }
}

@Composable
private fun SampleCustomFocus() {
    VerticalWheelPicker(
        modifier = Modifier.width(60.dp),
        count = 50,
        focus = {
            // Custom focus.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(width = 1.dp, color = Color.Gray)
            )
        },
    ) {
        Text(it.toString())
    }
}

@Composable
private fun SampleScrollToIndex() {
    // Specified initial index.
    val state = rememberWheelPickerState(10)
    LaunchedEffect(state) {
        delay(2000)
        // Scroll to index.
        state.animateScrollToIndex(20)
    }

    VerticalWheelPicker(
        modifier = Modifier.width(60.dp),
        count = 50,
        // state
        state = state,
    ) {
        Text(it.toString())
    }
}

@Composable
private fun SampleObserveIndex() {
    val state = rememberWheelPickerState()
    VerticalWheelPicker(
        modifier = Modifier.width(60.dp),
        count = 50,
        state = state,
    ) {
        Text(it.toString())
    }

    // Observe currentIndex.
    LaunchedEffect(state) {
        snapshotFlow { state.currentIndex }
            .collect {
                logMsg { "currentIndex ${state.currentIndex}" }
            }
    }

    // Observe currentIndexSnapshot.
    LaunchedEffect(state) {
        snapshotFlow { state.currentIndexSnapshot }
            .collect {
                logMsg { "currentIndexSnapshot ${state.currentIndexSnapshot}" }
            }
    }
}

@Composable
private fun SampleCustomDisplay() {
    VerticalWheelPicker(
        modifier = Modifier.width(60.dp),
        count = 50,
        display = { index ->
            if (state.currentIndexSnapshot == index) {
                Content(index)
            } else {
                // Modify content if it is not in focus.
                Box(
                    modifier = Modifier
                        .rotate(90f)
                        .alpha(0.5f)
                ) {
                    Content(index)
                }
            }
        }
    ) {
        Text(it.toString())
    }
}

@Composable
private fun SampleReverseLayout() {
    VerticalWheelPicker(
        modifier = Modifier.width(60.dp),
        count = 50,
        // Reverse layout.
        reverseLayout = true,
    ) {
        Text(it.toString())
    }
}

@Preview
@Composable
fun PreviewMainView() {
    MainView()
}

fun logMsg(block: () -> String) {
    Log.i("FWheelPicker-demo", block())
}