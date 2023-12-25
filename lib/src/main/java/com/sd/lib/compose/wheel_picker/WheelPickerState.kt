package com.sd.lib.compose.wheel_picker

import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.math.absoluteValue

@Composable
fun rememberFWheelPickerState(
    initialIndex: Int = 0
): FWheelPickerState {
    return rememberSaveable(saver = FWheelPickerState.Saver) {
        FWheelPickerState(
            initialIndex = initialIndex,
        )
    }
}

class FWheelPickerState(
    initialIndex: Int = 0,
) {
    internal var debug = false
    internal val lazyListState = LazyListState()

    private var _count = 0
    private var _currentIndex by mutableIntStateOf(-1)
    private var _currentIndexSnapshot by mutableIntStateOf(-1)

    private var _pendingIndex: Int? = initialIndex.coerceAtLeast(0)
        set(value) {
            field = value
            if (value == null) resumeAwaitScroll()
        }
    private var _pendingIndexContinuation: Continuation<Unit>? = null

    /**
     * Index of picker when it is idle, -1 means that there is no data.
     *
     * Note that this property is observable and if you use it in the composable function
     * it will be recomposed on every change.
     */
    val currentIndex: Int get() = _currentIndex

    /**
     * Index of picker when it is idle or drag but not fling, -1 means that there is no data.
     *
     * Note that this property is observable and if you use it in the composable function
     * it will be recomposed on every change.
     */
    val currentIndexSnapshot: Int get() = _currentIndexSnapshot

    /**
     * [LazyListState.interactionSource]
     */
    val interactionSource: InteractionSource get() = lazyListState.interactionSource

    /**
     * [LazyListState.isScrollInProgress]
     */
    val isScrollInProgress: Boolean get() = lazyListState.isScrollInProgress

    suspend fun animateScrollToIndex(index: Int) {
        logMsg(debug) { "animateScrollToIndex index:$index" }
        @Suppress("NAME_SHADOWING")
        val index = index.coerceAtLeast(0)

        lazyListState.animateScrollToItem(index)
        synchronizeCurrentIndex()
    }

    suspend fun scrollToIndex(index: Int, pending: Boolean = true) {
        logMsg(debug) { "scrollToIndex index:$index pending:$pending" }
        @Suppress("NAME_SHADOWING")
        val index = index.coerceAtLeast(0)

        lazyListState.scrollToItem(index)
        synchronizeCurrentIndex()

        if (pending) {
            awaitScroll(index)
        }
    }

    private suspend fun awaitScroll(index: Int) {
        if (_currentIndex == index) return
        logMsg(debug) { "awaitScroll index $index start" }

        // Resume last continuation before suspend.
        resumeAwaitScroll()

        _pendingIndex = index
        suspendCancellableCoroutine {
            _pendingIndexContinuation = it
            it.invokeOnCancellation {
                logMsg(debug) { "awaitScroll index $index canceled" }
                _pendingIndexContinuation = null
                _pendingIndex = null

            }
        }

        logMsg(debug) { "awaitScroll index $index finish" }
    }

    private fun resumeAwaitScroll() {
        _pendingIndexContinuation?.let {
            logMsg(debug) { "resumeAwaitScroll pendingIndex:$_pendingIndex" }
            it.resume(Unit)
            _pendingIndexContinuation = null
        }
    }

    internal suspend fun updateCount(count: Int) {
        logMsg(debug) { "updateCount count:$count currentIndex:$_currentIndex pendingIndex:$_pendingIndex" }

        _count = count
        val maxIndex = count - 1

        if (_currentIndex > maxIndex) {
            setCurrentIndex(maxIndex)
        }

        _pendingIndex?.let { pendingIndex ->
            if (count > pendingIndex) {
                scrollToIndex(pendingIndex, pending = false)
            }
        }

        if (_currentIndex < 0 && count > 0) {
            synchronizeCurrentIndex()
        }
    }

    private fun synchronizeCurrentIndex() {
        logMsg(debug) { "synchronizeCurrentIndex" }
        val index = synchronizeCurrentIndexSnapshot()
        setCurrentIndex(index)
    }

    private fun setCurrentIndex(index: Int) {
        @Suppress("NAME_SHADOWING")
        val index = index.coerceAtLeast(-1)
        if (_currentIndex != index) {
            logMsg(debug) { "currentIndex:$index pendingIndex:$_pendingIndex" }
            _currentIndex = index
            _currentIndexSnapshot = index
            if (_pendingIndex == index) {
                _pendingIndex = null
            }
        }
    }

    internal fun synchronizeCurrentIndexSnapshot(): Int {
        return (mostStartItemInfo()?.index ?: -1).also {
            _currentIndexSnapshot = it
        }
    }

    /**
     * The item closest to the viewport start.
     */
    private fun mostStartItemInfo(): LazyListItemInfo? {
        if (_count <= 0) return null

        val layoutInfo = lazyListState.layoutInfo
        val listInfo = layoutInfo.visibleItemsInfo

        if (listInfo.isEmpty()) return null
        if (listInfo.size == 1) return listInfo.first()

        val firstItem = listInfo.first()
        val firstOffsetDelta = (firstItem.offset - layoutInfo.viewportStartOffset).absoluteValue
        return if (firstOffsetDelta < firstItem.size / 2) {
            firstItem
        } else {
            listInfo[1]
        }
    }

    companion object {
        val Saver: Saver<FWheelPickerState, *> = listSaver(
            save = {
                listOf<Any>(
                    it.currentIndex,
                )
            },
            restore = {
                FWheelPickerState(
                    initialIndex = it[0] as Int,
                )
            }
        )
    }
}