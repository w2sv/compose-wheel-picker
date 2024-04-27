package com.w2sv.wheelpicker

import androidx.annotation.IntRange
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.lazy.LazyListLayoutInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
    rememberSaveable(itemCount, unfocusedItemCountToEitherSide, saver = WheelPickerState.Saver) {
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
    init {
        require(itemCount >= 0) { "itemCount >= 0 required" }
        require(unfocusedItemCountToEitherSide >= 0) { "unfocusedItemCountToEitherSide >= 0 required" }
        require(itemCount >= 0) { "startIndex >= 0 required" }
    }

    /**
     * The number of items that are fully visible at a time. This is a static, unobservable property.
     */
    val fullyVisibleItemCount = unfocusedItemCountToEitherSide * 2 + 1

    internal val lazyListState = LazyListState()

    /**
     * @see [LazyListState.layoutInfo]
     */
    val layoutInfo: LazyListLayoutInfo get() = lazyListState.layoutInfo

    private val firstVisibleScrollItemIndex: Int get() = lazyListState.firstVisibleItemIndex

    /**
     * @see [LazyListState.firstVisibleItemScrollOffset]
     */
    val firstVisibleItemScrollOffset: Int get() = lazyListState.firstVisibleItemScrollOffset

    /**
     * @see [LazyListState.isScrollInProgress]
     */
    val isScrollInProgress: Boolean get() = lazyListState.isScrollInProgress

    /**
     * @see [LazyListState.firstVisibleItemIndex]
     */
    val firstVisibleItemIndex by derivedStateOf { firstVisibleScrollItemIndex % itemCount }

    /**
     * The converted index the picker last snapped to.
     *
     * Note that this property is observable and if you use it in the composable function it will be recomposed on every change causing potential performance issues.
     * If you want to run some side effects like sending an analytics event or updating a state based on this value consider using "snapshotFlow":
     */
    var snappedIndex by mutableIntStateOf(startIndex)
        private set

    internal fun setSnappedIndex() {
        snappedIndex = (firstVisibleScrollItemIndex + unfocusedItemCountToEitherSide) % itemCount
    }

    internal suspend fun scrollToStartIndex() {
        val offsetZeroIndex = MAX_INT_VALUE_HALVE - MAX_INT_VALUE_HALVE % itemCount
        scrollToItem(offsetZeroIndex + startIndex - unfocusedItemCountToEitherSide)
    }

    /**
     * @see [LazyListState.scrollToItem]
     */
    suspend fun scrollToItem(@IntRange(from = 0) index: Int) {
        lazyListState.scrollToItem(index)
    }

    /**
     * @see [LazyListState.animateScrollToItem]
     */
    suspend fun animateScrollToItem(@IntRange(from = 0) index: Int) {
        lazyListState.animateScrollToItem(index)
    }

    /**
     * @see [LazyListState.animateScrollBy]
     */
    suspend fun animateScrollBy(value: Float, animationSpec: AnimationSpec<Float> = spring()) {
        lazyListState.animateScrollBy(value, animationSpec)
    }

    /**
     * @return null for scroll indices whose items are not currently visible (items beyond firstVisibleScrollItemIndex - 1 to firstVisibleScrollItemIndex + visibleItemCount + 1). Otherwise a value from -1 to 1 with 0 corresponding to the viewport center.
     */
    internal fun normalizedRelativePosition(scrollIndex: Int): Float? {
        val relativePosition =
            scrollIndex - firstVisibleScrollItemIndex - firstVisibleItemScrollOffsetPercentage  // Value from -1 to nVisibleItems for visible items
        return if (relativePosition in visibleItemPositionRange) {
            ((relativePosition + 1f) / (fullyVisibleItemCount + 1f) * 2f - 1f)  // Value from -1 to 1
//                .also { i { "${scrollIndex / itemCount}: $it" } }
        } else {
            null
        }
    }

    private val firstVisibleItemScrollOffsetPercentage by derivedStateOf {
        itemBoxMainAxisPx?.let { firstVisibleItemScrollOffset.toFloat() / it } ?: 0.0f
    }

    internal var itemBoxMainAxisPx by mutableStateOf<Float?>(null)

    private val visibleItemPositionRange = (-1f..fullyVisibleItemCount.toFloat())

    companion object {
        val Saver: Saver<WheelPickerState, Any> = listSaver(
            save = {
                listOf(
                    it.itemCount,
                    it.unfocusedItemCountToEitherSide,
                    it.firstVisibleItemIndex
                )
            },
            restore = {
                WheelPickerState(
                    itemCount = it[0],
                    unfocusedItemCountToEitherSide = it[1],
                    startIndex = it[2] + it[1]
                )
            }
        )
    }
}