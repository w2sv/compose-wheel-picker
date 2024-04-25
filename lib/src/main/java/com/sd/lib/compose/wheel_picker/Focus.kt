package com.sd.lib.compose.wheel_picker

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

/**
 * The default implementation of focus view in vertical.
 */
@Composable
fun WheelPickerFocusVertical(
    modifier: Modifier = Modifier,
    dividerThickness: Dp = DividerDefaults.Thickness,
    dividerColor: Color = DividerDefaults.color,
) {
    Box(modifier = modifier) {
        HorizontalDivider(
            thickness = dividerThickness,
            color = dividerColor,
            modifier = Modifier
                .align(Alignment.TopCenter)
        )
        HorizontalDivider(
            thickness = dividerThickness,
            color = dividerColor,
            modifier = Modifier
                .align(Alignment.BottomCenter)
        )
    }
}

/**
 * The default implementation of focus view in horizontal.
 */
@Composable
fun WheelPickerFocusHorizontal(
    modifier: Modifier = Modifier,
    dividerThickness: Dp = DividerDefaults.Thickness,
    dividerColor: Color = DividerDefaults.color,
) {
    Box(modifier = modifier) {
        VerticalDivider(
            thickness = dividerThickness,
            color = dividerColor,
            modifier = Modifier
                .align(Alignment.TopCenter)
        )
        VerticalDivider(
            thickness = dividerThickness,
            color = dividerColor,
            modifier = Modifier
                .align(Alignment.BottomCenter)
        )
    }
}