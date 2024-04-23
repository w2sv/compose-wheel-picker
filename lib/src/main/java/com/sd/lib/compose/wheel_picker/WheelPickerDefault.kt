package com.sd.lib.compose.wheel_picker

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp

/**
 * The default implementation of focus view in vertical.
 */
@Composable
fun FWheelPickerFocusVertical(
    modifier: Modifier = Modifier,
    dividerThickness: Dp = DividerDefaults.Thickness,
    dividerColor: Color = DividerDefaults.color,
) {
    Box(modifier = modifier) {
        VerticalDivider(
            thickness = dividerThickness,
            color = dividerColor,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        )
        VerticalDivider(
            thickness = dividerThickness,
            color = dividerColor,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        )
    }
}

/**
 * The default implementation of focus view in horizontal.
 */
@Composable
fun FWheelPickerFocusHorizontal(
    modifier: Modifier = Modifier,
    dividerThickness: Dp = DividerDefaults.Thickness,
    dividerColor: Color = DividerDefaults.color,
) {
    Box(modifier = modifier) {
        HorizontalDivider(
            thickness = dividerThickness,
            color = dividerColor,
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.TopCenter)
        )
        HorizontalDivider(
            thickness = dividerThickness,
            color = dividerColor,
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.BottomCenter)
        )
    }
}

/**
 * Default display.
 */
@Composable
fun FWheelPickerDisplayScope.DefaultWheelPickerDisplay(
    index: Int,
    modifier: Modifier = Modifier
) {
    val focused = index == state.currentIndexSnapshot
    val animateScale by animateFloatAsState(
        remember(focused) {
            if (focused) 1.0f else 0.8f
        },
        label = ""
    )
    Box(
        modifier = modifier.graphicsLayer {
            this.alpha = if (focused) 1.0f else 0.3f
            this.scaleX = animateScale
            this.scaleY = animateScale
        }
    ) {
        Content(index)
    }
}