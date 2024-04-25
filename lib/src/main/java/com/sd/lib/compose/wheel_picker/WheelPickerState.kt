package com.sd.lib.compose.wheel_picker

import androidx.annotation.IntRange
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

private const val MAX_INT_VALUE_HALVE = 1073741824

@Stable
class WheelPickerState(val itemCount: Int, val unfocusedItemCountToEitherSide: Int) {

    internal var itemBoxMainAxisPx by mutableStateOf<Float?>(null)

    init {
        require(itemCount >= 0) { "itemCount >= 0 required" }
        require(unfocusedItemCountToEitherSide >= 0) { "unfocusedItemCountToEitherSide >= 0 required" }
    }

    val visibleItemCount = unfocusedItemCountToEitherSide * 2 + 1

    internal val lazyListState = LazyListState()
    val layoutInfo by derivedStateOf { lazyListState.layoutInfo }

    val firstVisibleItemIndex by derivedStateOf { lazyListState.firstVisibleItemIndex }
    val firstVisibleItemScrollOffset by derivedStateOf { lazyListState.firstVisibleItemScrollOffset }
    val firstVisibleItemScrollOffsetPercentage by derivedStateOf {
        itemBoxMainAxisPx?.let { firstVisibleItemScrollOffset.toFloat() / it } ?: 0.0f
    }
    val isScrollInProgress by derivedStateOf { lazyListState.isScrollInProgress }

    var snappedIndex by mutableStateOf<Int?>(null)
        internal set

    internal suspend fun scrollToStartIndex(@IntRange(from = 0) startIndex: Int) {
        val offsetZeroIndex = MAX_INT_VALUE_HALVE - MAX_INT_VALUE_HALVE % itemCount
        scrollToItem(offsetZeroIndex + startIndex - unfocusedItemCountToEitherSide)
    }

    suspend fun scrollToItem(@IntRange(from = 0) index: Int) {
        lazyListState.scrollToItem(index)
    }

    suspend fun animateScrollToItem(@IntRange(from = 0) index: Int) {
        lazyListState.animateScrollToItem(index)
    }
}

@Composable
fun rememberWheelPickerState(
    itemCount: Int,
    startIndex: Int = 0,
    unfocusedItemCountToEitherSide: Int = 2
): WheelPickerState =
    remember {
        WheelPickerState(
            itemCount = itemCount,
            unfocusedItemCountToEitherSide = unfocusedItemCountToEitherSide
        )
    }
        .apply {
            // Scroll to start index
            LaunchedEffect(startIndex) {
                scrollToStartIndex(startIndex)
            }

            // Set snappedIndex on snap
            LaunchedEffect(isScrollInProgress) {
                if (!isScrollInProgress) {
                    snappedIndex = (firstVisibleItemIndex + unfocusedItemCountToEitherSide) % itemCount
                }
            }
        }