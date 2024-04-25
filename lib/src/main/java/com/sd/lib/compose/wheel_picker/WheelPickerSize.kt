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
        unfocusedItemCount: Int
    ) : this(
        itemBoxMainAxis = itemBoxMainAxis,
        crossAxis = itemBoxCrossAxis,
        mainAxis = itemBoxMainAxis * (unfocusedItemCount * 2 + 1)
    )
}

@Composable
internal fun rememberWheelPickerSize(
    itemSize: DpSize,
    unfocusedItemCount: Int,
    verticalLayout: Boolean
): WheelPickerSize {
    return remember(itemSize, unfocusedItemCount, verticalLayout) {
        WheelPickerSize(
            itemBoxMainAxis = itemSize.mainAxis(verticalLayout),
            itemBoxCrossAxis = itemSize.crossAxis(verticalLayout),
            unfocusedItemCount = unfocusedItemCount
        )
    }
}

private fun DpSize.mainAxis(verticalLayout: Boolean): Dp =
    if (verticalLayout) height else width

private fun DpSize.crossAxis(verticalLayout: Boolean): Dp =
    if (verticalLayout) width else height