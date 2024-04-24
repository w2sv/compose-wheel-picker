package com.sd.lib.compose.wheel_picker

import androidx.annotation.IntRange
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

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

    val lazyListState = rememberLazyListState()
    val snapFlingBehavior = rememberSnapFlingBehavior(lazyListState = lazyListState)

    val firstVisibleItemIndex by remember { derivedStateOf { lazyListState.firstVisibleItemIndex } }
    val isScrollInProgress = lazyListState.isScrollInProgress

    val focusedIndex = remember(firstVisibleItemIndex) {
        firstVisibleItemIndex + unfocusedItemCount
    }

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

        val lazyListContent: LazyListScope.() -> Unit = {
            items(count = Int.MAX_VALUE) { scrollIndex ->
                ItemSizedBox(
                    modifier = itemBoxModifier
                ) {
                    ItemDisplay(index = scrollIndex, isFocused = scrollIndex == focusedIndex) {
                        content(scrollIndex % itemCount)
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
    isFocused: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable (index: Int) -> Unit
) {
    val scale by animateFloatAsState(
        remember(isFocused) {
            if (isFocused) 1.0f else 0.8f
        },
        label = ""
    )
    Box(
        modifier = modifier.graphicsLayer {
            this.alpha = if (isFocused) 1.0f else 0.3f
            this.scaleX = scale
            this.scaleY = scale
        }
    ) {
        content(index)
    }
}