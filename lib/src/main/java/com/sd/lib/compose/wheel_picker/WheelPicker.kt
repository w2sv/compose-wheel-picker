package com.sd.lib.compose.wheel_picker

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import com.sd.lib.compose.wheel_picker.internal.WheelPicker
import com.sd.lib.compose.wheel_picker.internal.rememberWheelPickerSize

@Composable
fun VerticalWheelPicker(
    state: WheelPickerState,
    modifier: Modifier = Modifier,
    itemSize: DpSize = WheelPickerDefaults.itemSize,
    userScrollEnabled: Boolean = true,
    reverseLayout: Boolean = false,
    snapFlingBehaviorAnimationSpecs: WheelPickerSnapFlingBehaviorAnimationSpecs = WheelPickerDefaults.snapFlingBehaviorAnimationSpecs(),
    focusBoxOverlay: @Composable (Modifier) -> Unit = {},
    item: @Composable (index: Int) -> Unit,
) {
    WheelPicker(
        state = state,
        modifier = modifier,
        isVertical = true,
        size = rememberWheelPickerSize(
            itemSize = itemSize,
            visibleItemCount = state.visibleItemCount,
            verticalLayout = true
        ),
        userScrollEnabled = userScrollEnabled,
        reverseLayout = reverseLayout,
        snapFlingBehaviorAnimationSpecs = snapFlingBehaviorAnimationSpecs,
        focusBoxOverlay = focusBoxOverlay,
        item = item,
    )
}

@Composable
fun HorizontalWheelPicker(
    state: WheelPickerState,
    modifier: Modifier = Modifier,
    itemSize: DpSize = WheelPickerDefaults.itemSize,
    userScrollEnabled: Boolean = true,
    reverseLayout: Boolean = false,
    snapFlingBehaviorAnimationSpecs: WheelPickerSnapFlingBehaviorAnimationSpecs = WheelPickerDefaults.snapFlingBehaviorAnimationSpecs(),
    focusBoxOverlay: @Composable (Modifier) -> Unit = {},
    item: @Composable (index: Int) -> Unit,
) {
    WheelPicker(
        state = state,
        modifier = modifier,
        isVertical = false,
        size = rememberWheelPickerSize(
            itemSize = itemSize,
            visibleItemCount = state.visibleItemCount,
            verticalLayout = false
        ),
        userScrollEnabled = userScrollEnabled,
        reverseLayout = reverseLayout,
        snapFlingBehaviorAnimationSpecs = snapFlingBehaviorAnimationSpecs,
        focusBoxOverlay = focusBoxOverlay,
        item = item,
    )
}