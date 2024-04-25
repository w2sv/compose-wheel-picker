package com.sd.lib.compose.wheel_picker

import androidx.annotation.IntRange
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.SnapFlingBehavior
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
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
    @IntRange(from = 0) itemCount: Int,
    modifier: Modifier = Modifier,
    @IntRange(from = 0) startIndex: Int = 0,
    @IntRange(from = 0) unfocusedItemCount: Int = 1,
    onIndexSnap: (Int) -> Unit = {},
    itemSize: DpSize = WheelPickerDefaults.itemSize,
    userScrollEnabled: Boolean = true,
    reverseLayout: Boolean = false,
    lowVelocityApproachAnimationSpec: AnimationSpec<Float> = remember { tween(easing = LinearEasing) },
    highVelocityApproachAnimationSpec: DecayAnimationSpec<Float> = rememberSplineBasedDecay(),
    snapAnimationSpec: AnimationSpec<Float> = remember { spring(stiffness = Spring.StiffnessMediumLow) },
    focus: @Composable () -> Unit = { WheelPickerFocusVertical(modifier = Modifier.width(itemSize.width)) },
    content: @Composable (index: Int) -> Unit,
) {
    WheelPicker(
        itemCount = itemCount,
        modifier = modifier,
        startIndex = startIndex,
        unfocusedItemCount = unfocusedItemCount,
        onIndexSnap = onIndexSnap,
        isVertical = true,
        size = rememberWheelPickerSize(
            itemSize = itemSize,
            unfocusedItemCount = unfocusedItemCount,
            verticalLayout = true
        ),
        userScrollEnabled = userScrollEnabled,
        reverseLayout = reverseLayout,
        lowVelocityApproachAnimationSpec = lowVelocityApproachAnimationSpec,
        highVelocityApproachAnimationSpec = highVelocityApproachAnimationSpec,
        snapAnimationSpec = snapAnimationSpec,
        focus = focus,
        content = content,
    )
}

@Composable
fun HorizontalWheelPicker(
    @IntRange(from = 0) itemCount: Int,
    modifier: Modifier = Modifier,
    @IntRange(from = 0) startIndex: Int = 0,
    @IntRange(from = 0) unfocusedItemCount: Int = 1,
    onIndexSnap: (Int) -> Unit = {},
    itemSize: DpSize = WheelPickerDefaults.itemSize,
    userScrollEnabled: Boolean = true,
    reverseLayout: Boolean = false,
    lowVelocityApproachAnimationSpec: AnimationSpec<Float> = remember { tween(easing = LinearEasing) },
    highVelocityApproachAnimationSpec: DecayAnimationSpec<Float> = rememberSplineBasedDecay(),
    snapAnimationSpec: AnimationSpec<Float> = remember { spring(stiffness = Spring.StiffnessMediumLow) },
    focus: @Composable () -> Unit = { WheelPickerFocusHorizontal(modifier = Modifier.height(itemSize.height)) },
    content: @Composable (index: Int) -> Unit,
) {
    WheelPicker(
        itemCount = itemCount,
        modifier = modifier,
        unfocusedItemCount = unfocusedItemCount,
        startIndex = startIndex,
        onIndexSnap = onIndexSnap,
        isVertical = false,
        size = rememberWheelPickerSize(
            itemSize = itemSize,
            unfocusedItemCount = unfocusedItemCount,
            verticalLayout = false
        ),
        userScrollEnabled = userScrollEnabled,
        reverseLayout = reverseLayout,
        lowVelocityApproachAnimationSpec = lowVelocityApproachAnimationSpec,
        highVelocityApproachAnimationSpec = highVelocityApproachAnimationSpec,
        snapAnimationSpec = snapAnimationSpec,
        focus = focus,
        content = content,
    )
}

private const val MAX_INT_VALUE_HALVE = 1073741824

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun WheelPicker(
    @IntRange(from = 0) itemCount: Int,
    onIndexSnap: (Int) -> Unit,
    modifier: Modifier = Modifier,
    @IntRange(from = 0) startIndex: Int = 0,
    @IntRange(from = 0) unfocusedItemCount: Int = 1,
    isVertical: Boolean,
    size: WheelPickerSize,
    userScrollEnabled: Boolean,
    reverseLayout: Boolean,
    lowVelocityApproachAnimationSpec: AnimationSpec<Float>,
    highVelocityApproachAnimationSpec: DecayAnimationSpec<Float>,
    snapAnimationSpec: AnimationSpec<Float>,
    focus: @Composable () -> Unit,
    content: @Composable (index: Int) -> Unit,
) {
    LaunchedEffect(unfocusedItemCount, itemCount) {
        require(itemCount >= 0) { "itemCount >= 0 required" }
        require(unfocusedItemCount >= 0) { "unfocusedCount >= 0 required" }
    }

    val totalItems = remember(unfocusedItemCount) {
        unfocusedItemCount + 2 + 1
    }

    val lazyListState = rememberLazyListState()
    val snapFlingBehavior = rememberSnapFlingBehavior(
        lazyListState = lazyListState,
        lowVelocityApproachAnimationSpec = lowVelocityApproachAnimationSpec,
        highVelocityApproachAnimationSpec = highVelocityApproachAnimationSpec,
        snapAnimationSpec = snapAnimationSpec
    )

    val firstVisibleItemIndex by remember { derivedStateOf { lazyListState.firstVisibleItemIndex } }
    val isScrollInProgress = lazyListState.isScrollInProgress

    val itemBoxMainAxisPx by remember { derivedStateOf { lazyListState.layoutInfo.mainAxisItemSpacing } }

    val firstVisibleItemScrollOffset by remember { derivedStateOf { lazyListState.firstVisibleItemScrollOffset } }

    // Scroll to start index
    LaunchedEffect(startIndex) {
        val offsetZeroIndex = MAX_INT_VALUE_HALVE - MAX_INT_VALUE_HALVE % itemCount
        lazyListState.scrollToItem(offsetZeroIndex + startIndex - unfocusedItemCount)
    }

    // Invoke onIndexSnap on snap
    LaunchedEffect(isScrollInProgress) {
        if (!isScrollInProgress) {
            onIndexSnap(firstVisibleItemIndex % itemCount + unfocusedItemCount)
        }
    }

    Box(
        modifier = modifier
            .run {
                if (size.mainAxis > 0.dp) {
                    if (isVertical) {
                        height(size.mainAxis).widthIn(size.crossAxis)
                    } else {
                        width(size.mainAxis).heightIn(size.crossAxis)
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

        val firstVisibleItemScrollOffsetPercentage by remember {
            derivedStateOf { (firstVisibleItemScrollOffset.toFloat() / itemBoxMainAxisPx) }
        }

        val lazyListContent: LazyListScope.() -> Unit = {
            items(count = Int.MAX_VALUE) { scrollIndex ->
                ItemSizedBox(
                    modifier = itemBoxModifier
                ) {
                    val itemIndex = scrollIndex % itemCount
                    ItemDisplay(
                        index = scrollIndex,
                        makeCoeff = {
//                            val positionPercentage = (scrollIndex - firstVisibleItemIndex - firstVisibleItemScrollOffsetPercentage) / totalItems
                            val centerBorderingCoeff =
                                scrollIndex - firstVisibleItemIndex - unfocusedItemCount + 1 - firstVisibleItemScrollOffsetPercentage
                            if (centerBorderingCoeff in range) {
                                val centerMaxEdgesMinMapping =
                                    1 - abs(1 - centerBorderingCoeff)// 0 = 0, 1 = 1, 2 = 0
                                centerMaxEdgesMinMapping * 0.4f + 0.6f
                            } else {
                                0.6f
                            }
                        }
                    ) {
                        content(itemIndex)
                    }
                }
            }
        }

        if (isVertical) {
            LazyColumn(
                state = lazyListState,
                horizontalAlignment = Alignment.CenterHorizontally,
                reverseLayout = reverseLayout,
                userScrollEnabled = userScrollEnabled,
                modifier = Modifier.matchParentSize(),
                content = lazyListContent,
                flingBehavior = snapFlingBehavior
            )
        } else {
            LazyRow(
                state = lazyListState,
                verticalAlignment = Alignment.CenterVertically,
                reverseLayout = reverseLayout,
                userScrollEnabled = userScrollEnabled,
                modifier = Modifier.matchParentSize(),
                content = lazyListContent,
                flingBehavior = snapFlingBehavior
            )
        }

        ItemSizedBox(
            modifier = itemBoxModifier
        ) {
            focus()
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun rememberSnapFlingBehavior(
    lazyListState: LazyListState,
    lowVelocityApproachAnimationSpec: AnimationSpec<Float>,
    highVelocityApproachAnimationSpec: DecayAnimationSpec<Float>,
    snapAnimationSpec: AnimationSpec<Float>
): SnapFlingBehavior {
    val snapLayoutInfoProvider = remember(lazyListState) { SnapLayoutInfoProvider(lazyListState) }
    val density = LocalDensity.current

    return remember(
        snapLayoutInfoProvider,
        lowVelocityApproachAnimationSpec,
        highVelocityApproachAnimationSpec,
        snapAnimationSpec,
        density
    ) {
        SnapFlingBehavior(
            snapLayoutInfoProvider = snapLayoutInfoProvider,
            lowVelocityAnimationSpec = lowVelocityApproachAnimationSpec,
            highVelocityAnimationSpec = highVelocityApproachAnimationSpec,
            snapAnimationSpec = snapAnimationSpec
        )
    }
}

private val range = (0.0f..2f)

@Composable
private fun ItemSizedBox(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {},
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

/**
 * Default display.
 */
@Composable
private fun ItemDisplay(
    index: Int,
    makeCoeff: () -> Float,
    modifier: Modifier = Modifier,
    content: @Composable (index: Int) -> Unit
) {
    Box(
        modifier = modifier.graphicsLayer {
            val coeff = makeCoeff()
            this.alpha = coeff
            this.scaleX = coeff
            this.scaleY = coeff
        }
    ) {
        content(index)
    }
}