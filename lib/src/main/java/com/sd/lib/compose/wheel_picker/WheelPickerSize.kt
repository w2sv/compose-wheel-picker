package com.sd.lib.compose.wheel_picker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize

@Stable
internal data class WheelPickerSize(
    val itemBoxMainAxis: Dp,
    val crossAxis: Dp,
    val mainAxis: Dp
) {
    constructor(
        itemBoxMainAxis: Dp,
        itemBoxCrossAxis: Dp,
        visibleItemCount: Int
    ) : this(
        itemBoxMainAxis = itemBoxMainAxis,
        crossAxis = itemBoxCrossAxis,
        mainAxis = itemBoxMainAxis * visibleItemCount
    )
}

@Composable
internal fun rememberWheelPickerSize(
    itemSize: DpSize,
    visibleItemCount: Int,
    verticalLayout: Boolean
): WheelPickerSize {
    return remember(itemSize, visibleItemCount, verticalLayout) {
        WheelPickerSize(
            itemBoxMainAxis = itemSize.mainAxis(verticalLayout),
            itemBoxCrossAxis = itemSize.crossAxis(verticalLayout),
            visibleItemCount = visibleItemCount
        )
    }
}

private fun DpSize.mainAxis(verticalLayout: Boolean): Dp =
    if (verticalLayout) height else width

private fun DpSize.crossAxis(verticalLayout: Boolean): Dp =
    if (verticalLayout) width else height