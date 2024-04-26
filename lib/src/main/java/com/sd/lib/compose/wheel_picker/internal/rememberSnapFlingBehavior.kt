package com.sd.lib.compose.wheel_picker.internal

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.SnapFlingBehavior
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import com.sd.lib.compose.wheel_picker.WheelPickerSnapFlingBehaviorAnimationSpecs

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun rememberSnapFlingBehavior(
    lazyListState: LazyListState,
    animationSpecs: WheelPickerSnapFlingBehaviorAnimationSpecs,
): SnapFlingBehavior {
    val snapLayoutInfoProvider = remember(lazyListState) { SnapLayoutInfoProvider(lazyListState) }
    val density = LocalDensity.current

    return remember(
        snapLayoutInfoProvider,
        animationSpecs,
        density
    ) {
        SnapFlingBehavior(
            snapLayoutInfoProvider = snapLayoutInfoProvider,
            lowVelocityAnimationSpec = animationSpecs.lowVelocityApproach,
            highVelocityAnimationSpec = animationSpecs.highVelocityApproach,
            snapAnimationSpec = animationSpecs.snap
        )
    }
}