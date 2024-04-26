package com.sd.lib.compose.wheel_picker.internal

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.sd.lib.compose.wheel_picker.WheelPickerSnapFlingBehaviorAnimationSpecs
import com.sd.lib.compose.wheel_picker.WheelPickerState
import slimber.log.i
import kotlin.math.abs

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun WheelPicker(
    state: WheelPickerState,
    modifier: Modifier = Modifier,
    isVertical: Boolean,
    size: WheelPickerSize,
    userScrollEnabled: Boolean,
    reverseLayout: Boolean,
    snapFlingBehaviorAnimationSpecs: WheelPickerSnapFlingBehaviorAnimationSpecs,
    focusBoxOverlay: @Composable (Modifier) -> Unit,
    item: @Composable (index: Int) -> Unit,
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

        val visibleItemRelativePositionRange = remember(state.visibleItemCount) {
            (-1f..state.visibleItemCount.toFloat())
        }

        val lazyListContent: LazyListScope.() -> Unit = remember(state) {
            {
                items(count = Int.MAX_VALUE) { scrollIndex ->
                    val itemIndex = scrollIndex % state.itemCount
                    ItemBox(
                        index = scrollIndex,
                        makeCoeff = {
                            val relativePosition =
                                scrollIndex - state.firstVisibleItemIndex - state.firstVisibleItemScrollOffsetPercentage  // Value from -1 to nVisibleItems for visible items
                            if (relativePosition in visibleItemRelativePositionRange) {
                                ((relativePosition + 1f) / (state.visibleItemCount + 1f) * 2f - 1f).also { i { "$itemIndex: $it" } }  // Value from -1 to 1
                            } else {
                                null
                            }
                        },
                        modifier = itemBoxModifier
                    ) {
                        item(itemIndex)
                    }
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

@Composable
private fun ItemBox(
    index: Int,
    makeCoeff: () -> Float?,
    modifier: Modifier = Modifier,
    content: @Composable (index: Int) -> Unit
) {
    Box(
        modifier = modifier.graphicsLayer {
            makeCoeff()?.let {
                val coeff = (1 - abs(it)).coerceAtLeast(0.6f)
                this.alpha = coeff
                this.scaleX = coeff
                this.scaleY = coeff
            }
        },
        contentAlignment = Alignment.Center
    ) {
        content(index)
    }
}