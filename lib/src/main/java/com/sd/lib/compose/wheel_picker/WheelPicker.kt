package com.sd.lib.compose.wheel_picker

import androidx.annotation.IntRange
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
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
import slimber.log.i
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
    focus: @Composable () -> Unit,
    content: @Composable (index: Int) -> Unit,
) {
    LaunchedEffect(unfocusedItemCount) {
        require(unfocusedItemCount >= 0) { "require unfocusedCount >= 0" }
    }

    val totalItems = remember(unfocusedItemCount) {
        unfocusedItemCount + 2 + 1
    }

    val lazyListState = rememberLazyListState()
    val snapFlingBehavior = rememberSnapFlingBehavior(lazyListState = lazyListState)

    val firstVisibleItemIndex by remember { derivedStateOf { lazyListState.firstVisibleItemIndex } }
    val isScrollInProgress = lazyListState.isScrollInProgress

    val itemBoxMainAxisPx =
        with(LocalDensity.current) { size.itemBoxMainAxis.toPx().toInt() }.toFloat()

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

        LaunchedEffect(firstVisibleItemIndex) {
            i { "First visible item index: $firstVisibleItemIndex" }
        }

        val firstVisibleItemScrollOffsetPercentage by remember {
            derivedStateOf { (firstVisibleItemScrollOffset.toFloat() / itemBoxMainAxisPx).also { i { "Offset percentage: $it" } } }
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
                            if (itemIndex == 26) {
                                i { centerBorderingCoeff.toString() }
                            }
                            if (centerBorderingCoeff in range) {
                                1 - abs(1 - centerBorderingCoeff)
                            } else {
                                0.5f
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

private val range = (0.5f..1.5f)

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