package com.sd.lib.compose.wheel_picker

import androidx.annotation.IntRange
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

private const val MAX_INT_VALUE_HALVE = 1073741824

@Composable
fun rememberWheelPickerState(
    @IntRange(from = 0) itemCount: Int,
    @IntRange(from = 0) startIndex: Int = 0,
    @IntRange(from = 0) unfocusedItemCountToEitherSide: Int = 2
): WheelPickerState =
    rememberSaveable(saver = WheelPickerState.Saver) {
        WheelPickerState(
            itemCount = itemCount,
            unfocusedItemCountToEitherSide = unfocusedItemCountToEitherSide,
            startIndex = startIndex
        )
    }
        .also {
            // Scroll to start index
            LaunchedEffect(it.startIndex) {
                it.scrollToStartIndex()
            }

            // Set snappedIndex on snap
            LaunchedEffect(it.isScrollInProgress) {
                if (!it.isScrollInProgress) {
                    it.setSnappedIndex()
                }
            }
        }

@Stable
data class WheelPickerState(
    @IntRange(from = 0) val itemCount: Int,
    @IntRange(from = 0) val unfocusedItemCountToEitherSide: Int,
    @IntRange(from = 0) internal val startIndex: Int
) {
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
        private set

    internal fun setSnappedIndex() {
        snappedIndex = (firstVisibleItemIndex + unfocusedItemCountToEitherSide) % itemCount
    }

    internal suspend fun scrollToStartIndex() {
        val offsetZeroIndex = MAX_INT_VALUE_HALVE - MAX_INT_VALUE_HALVE % itemCount
        scrollToItem(offsetZeroIndex + startIndex - unfocusedItemCountToEitherSide)
    }

    suspend fun scrollToItem(@IntRange(from = 0) index: Int) {
        lazyListState.scrollToItem(index)
    }

    suspend fun animateScrollToItem(@IntRange(from = 0) index: Int) {
        lazyListState.animateScrollToItem(index)
    }

    companion object {
        val Saver: Saver<WheelPickerState, Any> = listSaver(
            save = {
                listOf(it.itemCount, it.unfocusedItemCountToEitherSide, it.firstVisibleItemIndex)
            },
            restore = {
                val itemCount = it[0]
                val unfocusedItemCountToEitherSide = it[1]
                WheelPickerState(
                    itemCount = itemCount,
                    unfocusedItemCountToEitherSide = unfocusedItemCountToEitherSide,
                    startIndex = it[2] % itemCount + unfocusedItemCountToEitherSide
                )
            }
        )
    }
}