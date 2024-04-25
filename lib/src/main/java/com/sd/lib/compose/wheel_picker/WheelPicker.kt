package com.sd.lib.compose.wheel_picker

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.SnapFlingBehavior
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import kotlin.math.abs

@Composable
fun VerticalWheelPicker(
    state: WheelPickerState,
    modifier: Modifier = Modifier,
    itemSize: DpSize = WheelPickerDefaults.itemSize,
    userScrollEnabled: Boolean = true,
    reverseLayout: Boolean = false,
    snapFlingBehaviorAnimationSpecs: WheelPickerSnapFlingBehaviorAnimationSpecs = WheelPickerDefaults.snapFlingBehaviorAnimationSpecs(),
    focusBoxOverlay: @Composable (Modifier) -> Unit = { WheelPickerFocusVertical(modifier = it) },
    content: @Composable (index: Int) -> Unit,
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
        content = content,
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
    focusBoxOverlay: @Composable (Modifier) -> Unit = { WheelPickerFocusHorizontal(modifier = it) },
    content: @Composable (index: Int) -> Unit,
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
        content = content,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun WheelPicker(
    state: WheelPickerState,
    modifier: Modifier = Modifier,
    isVertical: Boolean,
    size: WheelPickerSize,
    userScrollEnabled: Boolean,
    reverseLayout: Boolean,
    snapFlingBehaviorAnimationSpecs: WheelPickerSnapFlingBehaviorAnimationSpecs,
    focusBoxOverlay: @Composable (Modifier) -> Unit,
    content: @Composable (index: Int) -> Unit,
) {
    val itemBoxMainAxisPx = with(LocalDensity.current) { size.itemBoxMainAxis.toPx() }
    LaunchedEffect(state, itemBoxMainAxisPx) {
        state.itemBoxMainAxisPx = itemBoxMainAxisPx
    }

    Box(
        modifier = modifier
            .run {
                if (size.mainAxis > 0.dp) {
                    if (isVertical) {
                        height(size.mainAxis).width(size.crossAxis)
                    } else {
                        width(size.mainAxis).height(size.crossAxis)
                    }
                } else {
                    this
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        val itemBoxModifier = Modifier.run {
            if (isVertical) {
                height(size.itemBoxMainAxis)
            } else {
                width(size.itemBoxMainAxis)
            }
        }

        val lazyListContent: LazyListScope.() -> Unit = {
            items(count = Int.MAX_VALUE) { scrollIndex ->
                val itemIndex = scrollIndex % state.itemCount
                ItemBox(
                    index = scrollIndex,
                    makeCoeff = {
//                            val positionPercentage = (scrollIndex - firstVisibleItemIndex - firstVisibleItemScrollOffsetPercentage) / totalItems
                        val centerBorderingCoeff =
                            scrollIndex - state.firstVisibleItemIndex - state.unfocusedItemCountToEitherSide + 1 - state.firstVisibleItemScrollOffsetPercentage
                        if (centerBorderingCoeff in range) {
                            1 - abs(1 - centerBorderingCoeff)// 0 -> 0, 1 -> 1, 2 -> 0
                        } else {
                            0.0f
                        }
                    },
                    modifier = itemBoxModifier
                ) {
                    content(itemIndex)
                }
            }
        }

        val snapFlingBehavior = rememberSnapFlingBehavior(
            lazyListState = state.lazyListState,
            animationSpecs = snapFlingBehaviorAnimationSpecs
        )

        if (isVertical) {
            LazyColumn(
                state = state.lazyListState,
                horizontalAlignment = Alignment.CenterHorizontally,
                reverseLayout = reverseLayout,
                userScrollEnabled = userScrollEnabled,
                modifier = Modifier.matchParentSize(),
                content = lazyListContent,
                flingBehavior = snapFlingBehavior
            )
        } else {
            LazyRow(
                state = state.lazyListState,
                verticalAlignment = Alignment.CenterVertically,
                reverseLayout = reverseLayout,
                userScrollEnabled = userScrollEnabled,
                modifier = Modifier.matchParentSize(),
                content = lazyListContent,
                flingBehavior = snapFlingBehavior
            )
        }
        focusBoxOverlay(itemBoxModifier)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun rememberSnapFlingBehavior(
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

private val range = (0.0f..2f)

@Composable
private fun ItemBox(
    index: Int,
    makeCoeff: () -> Float,
    modifier: Modifier = Modifier,
    content: @Composable (index: Int) -> Unit
) {
    Box(
        modifier = modifier.graphicsLayer {
            val coeff = makeCoeff().coerceAtLeast(0.6f)
            this.alpha = coeff
            this.scaleX = coeff
            this.scaleY = coeff
        },
        contentAlignment = Alignment.Center
    ) {
        content(index)
    }
}