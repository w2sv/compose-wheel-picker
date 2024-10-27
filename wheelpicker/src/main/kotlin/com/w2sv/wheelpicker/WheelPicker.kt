package com.w2sv.wheelpicker

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.TargetedFlingBehavior
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.snapFlingBehavior
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
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import kotlin.math.abs

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WheelPicker(
    state: WheelPickerState,
    modifier: Modifier = Modifier,
    isVertical: Boolean = true,
    itemSize: DpSize = WheelPickerDefaults.itemSize,
    userScrollEnabled: Boolean = true,
    reverseLayout: Boolean = false,
    decayAnimationSpec: DecayAnimationSpec<Float> = WheelPickerDefaults.decayAnimationSpec,
    snapAnimationSpec: AnimationSpec<Float> = WheelPickerDefaults.snapAnimationSpec,
    focusBoxOverlay: @Composable (Modifier) -> Unit = {},
    itemBoxGraphicsLayerScope: GraphicsLayerScope.(Float) -> Unit = remember {
        { normalizedRelativePosition ->
            val unsignedCenterDistance = 1 - abs(normalizedRelativePosition)
            val alphaAndScale = unsignedCenterDistance * 0.5f + 0.5f  // Map to range [0.5, 1]
            alpha = alphaAndScale
            scaleX = alphaAndScale
            scaleY = alphaAndScale
            rotationX = normalizedRelativePosition * 60
        }
    },
    item: @Composable (index: Int) -> Unit,
) {
    val size = rememberWheelPickerSize(
        itemSize = itemSize,
        visibleItemCount = state.fullyVisibleItemCount,
        verticalLayout = true
    )
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

        val lazyListContent: LazyListScope.() -> Unit = remember(state) {
            {
                items(count = Int.MAX_VALUE) { scrollIndex ->
                    Box(
                        modifier = itemBoxModifier.graphicsLayer {
                            state.normalizedRelativePosition(scrollIndex)?.let {
                                itemBoxGraphicsLayerScope(it)
                            }
                        },
                        contentAlignment = Alignment.Center
                    ) {
                        item(scrollIndex % state.itemCount)
                    }
                }
            }
        }

        val snapFlingBehavior = rememberSnapFlingBehavior(
            lazyListState = state.lazyListState,
            decayAnimationSpec = decayAnimationSpec,
            snapAnimationSpec = snapAnimationSpec
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
    decayAnimationSpec: DecayAnimationSpec<Float>,
    snapAnimationSpec: AnimationSpec<Float>
): TargetedFlingBehavior {
    val snapLayoutInfoProvider = remember(lazyListState) { SnapLayoutInfoProvider(lazyListState) }
    val density = LocalDensity.current

    return remember(
        snapLayoutInfoProvider,
        decayAnimationSpec,
        snapAnimationSpec,
        density
    ) {
        snapFlingBehavior(
            snapLayoutInfoProvider = snapLayoutInfoProvider,
            decayAnimationSpec = decayAnimationSpec,
            snapAnimationSpec = snapAnimationSpec
        )
    }
}