package com.sd.lib.compose.wheel_picker

import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp

@Stable
interface WheelPickerContentScope {
    val state: WheelPickerState
}

@Stable
interface WheelPickerDisplayScope : WheelPickerContentScope {
    @Composable
    fun Content(index: Int)
}

@Composable
fun VerticalWheelPicker(
    modifier: Modifier = Modifier,
    count: Int,
    state: WheelPickerState = rememberWheelPickerState(),
    key: ((index: Int) -> Any)? = null,
    itemSize: DpSize = WheelPickerDefaults.itemSize,
    unfocusedCount: Int = 1,
    userScrollEnabled: Boolean = true,
    reverseLayout: Boolean = false,
    focus: @Composable () -> Unit = { WheelPickerFocusVertical(modifier = Modifier.width(itemSize.width)) },
    display: @Composable WheelPickerDisplayScope.(index: Int) -> Unit = {
        DefaultWheelPickerDisplay(
            it
        )
    },
    content: @Composable WheelPickerContentScope.(index: Int) -> Unit,
) {
    WheelPicker(
        modifier = modifier,
        isVertical = true,
        count = count,
        state = state,
        key = key,
        itemSize = itemSize,
        unfocusedCount = unfocusedCount,
        userScrollEnabled = userScrollEnabled,
        reverseLayout = reverseLayout,
        focus = focus,
        display = display,
        content = content,
    )
}

@Composable
fun HorizontalWheelPicker(
    modifier: Modifier = Modifier,
    count: Int,
    state: WheelPickerState = rememberWheelPickerState(),
    key: ((index: Int) -> Any)? = null,
    itemSize: DpSize = WheelPickerDefaults.itemSize,
    unfocusedCount: Int = 1,
    userScrollEnabled: Boolean = true,
    reverseLayout: Boolean = false,
    focus: @Composable () -> Unit = { WheelPickerFocusHorizontal(modifier = Modifier.height(itemSize.height)) },
    display: @Composable WheelPickerDisplayScope.(index: Int) -> Unit = {
        DefaultWheelPickerDisplay(
            it
        )
    },
    content: @Composable WheelPickerContentScope.(index: Int) -> Unit,
) {
    WheelPicker(
        modifier = modifier,
        isVertical = false,
        count = count,
        state = state,
        key = key,
        itemSize = itemSize,
        unfocusedCount = unfocusedCount,
        userScrollEnabled = userScrollEnabled,
        reverseLayout = reverseLayout,
        focus = focus,
        display = display,
        content = content,
    )
}

private fun DpSize.mainAxis(verticalLayout: Boolean): Dp =
    if (verticalLayout) height else width

private fun DpSize.crossAxis(verticalLayout: Boolean): Dp =
    if (verticalLayout) width else height

@Composable
private fun WheelPicker(
    modifier: Modifier,
    isVertical: Boolean,
    count: Int,
    state: WheelPickerState,
    key: ((index: Int) -> Any)?,
    itemSize: DpSize,
    unfocusedCount: Int,
    userScrollEnabled: Boolean,
    reverseLayout: Boolean,
    focus: @Composable () -> Unit,
    display: @Composable WheelPickerDisplayScope.(index: Int) -> Unit,
    content: @Composable WheelPickerContentScope.(index: Int) -> Unit,
) {
    LaunchedEffect(count, unfocusedCount) {
        require(count >= 0) { "require count >= 0" }
        require(unfocusedCount >= 0) { "require unfocusedCount >= 0" }
    }

    LaunchedEffect(state, count) {
        state.updateCount(count)
    }

    val mainAxisSize = remember(itemSize, isVertical) {
        itemSize.mainAxis(isVertical)
    }
    val crossAxisSize = remember(itemSize, isVertical) {
        itemSize.crossAxis(isVertical)
    }

    val nestedScrollConnection = remember(state) {
        WheelPickerNestedScrollConnection(state)
    }.apply {
        this.isVertical = isVertical
        this.itemSizePx = with(LocalDensity.current) { mainAxisSize.roundToPx() }
        this.reverseLayout = reverseLayout
    }

    val totalSize = remember(mainAxisSize, unfocusedCount) {
        mainAxisSize * (unfocusedCount * 2 + 1)
    }

    val displayScope = remember(state) {
        WheelPickerDisplayScopeImpl(state, content)
    }

    Box(
        modifier = modifier
            .nestedScroll(nestedScrollConnection)
            .run {
                if (totalSize > 0.dp) {
                    if (isVertical) {
                        height(totalSize).widthIn(crossAxisSize)
                    } else {
                        width(totalSize).heightIn(crossAxisSize)
                    }
                } else {
                    this
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        val itemBoxModifier = Modifier.run {
            if (isVertical) {
                height(mainAxisSize)
            } else {
                width(mainAxisSize)
            }
        }

        val lazyListContent = rememberLazyListContent(
            unfocusedCount = unfocusedCount,
            count = count,
            itemBoxModifier = itemBoxModifier,
            displayScope = displayScope,
            key = key,
            display = display
        )

        if (isVertical) {
            LazyColumn(
                state = state.lazyListState,
                horizontalAlignment = Alignment.CenterHorizontally,
                reverseLayout = reverseLayout,
                userScrollEnabled = userScrollEnabled,
                modifier = Modifier.matchParentSize(),
                content = lazyListContent,
            )
        } else {
            LazyRow(
                state = state.lazyListState,
                verticalAlignment = Alignment.CenterVertically,
                reverseLayout = reverseLayout,
                userScrollEnabled = userScrollEnabled,
                modifier = Modifier.matchParentSize(),
                content = lazyListContent,
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
private fun rememberLazyListContent(
    unfocusedCount: Int,
    count: Int,
    displayScope: WheelPickerDisplayScope,
    key: ((index: Int) -> Any)?,
    itemBoxModifier: Modifier,
    display: @Composable WheelPickerDisplayScope.(index: Int) -> Unit,
): LazyListScope.() -> Unit {
    return remember(unfocusedCount, count, displayScope, key, itemBoxModifier) {
        {
            items(
                count = count,
                key = key,
            ) { index ->
                ItemSizedBox(
                    modifier = itemBoxModifier
                ) {
                    displayScope.display(index)
                }
            }
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

private class WheelPickerNestedScrollConnection(
    private val state: WheelPickerState,
) : NestedScrollConnection {
    var isVertical: Boolean = true
    var itemSizePx: Int = 0
    var reverseLayout: Boolean = false

    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource
    ): Offset {
        state.synchronizeCurrentIndexSnapshot()
        return super.onPostScroll(consumed, available, source)
    }

    override suspend fun onPreFling(available: Velocity): Velocity {
        val currentIndex = state.synchronizeCurrentIndexSnapshot()
        return if (currentIndex >= 0) {
            available.flingItemCount(
                isVertical = isVertical,
                itemSize = itemSizePx,
                decay = exponentialDecay(2f),
                reverseLayout = reverseLayout,
            ).let { flingItemCount ->
                if (flingItemCount == 0) {
                    state.animateScrollToIndex(currentIndex)
                } else {
                    state.animateScrollToIndex(currentIndex - flingItemCount)
                }
            }
            available
        } else {
            super.onPreFling(available)
        }
    }
}

private fun Velocity.flingItemCount(
    isVertical: Boolean,
    itemSize: Int,
    decay: DecayAnimationSpec<Float>,
    reverseLayout: Boolean,
): Int {
    if (itemSize <= 0) return 0
    val velocity = if (isVertical) y else x
    val targetValue = decay.calculateTargetValue(0f, velocity)
    val flingItemCount = (targetValue / itemSize).toInt()
    return if (reverseLayout) -flingItemCount else flingItemCount
}

@Stable
private class WheelPickerDisplayScopeImpl(
    override val state: WheelPickerState,
    private val content: @Composable WheelPickerContentScope.(index: Int) -> Unit
) : WheelPickerDisplayScope {

    @Composable
    override fun Content(index: Int) {
        content(index)
    }
}