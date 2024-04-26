package com.sd.lib.compose.wheel_picker

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
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.sd.lib.compose.wheel_picker.internal.rememberSnapFlingBehavior
import com.sd.lib.compose.wheel_picker.internal.rememberWheelPickerSize
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
    snapFlingBehaviorAnimationSpecs: WheelPickerSnapFlingBehaviorAnimationSpecs = WheelPickerDefaults.rememberSnapFlingBehaviorAnimationSpecs(),
    focusBoxOverlay: @Composable (Modifier) -> Unit = {},
    itemBoxGraphicsLayerScope: GraphicsLayerScope.(Float) -> Unit = remember {
        {
            val transitionCoefficient = (1 - abs(it)).coerceAtLeast(0.6f)
            alpha = transitionCoefficient
            scaleX = transitionCoefficient
            scaleY = transitionCoefficient
        }
    },
    item: @Composable (index: Int) -> Unit,
) {
    val size = rememberWheelPickerSize(
        itemSize = itemSize,
        visibleItemCount = state.visibleItemCount,
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
                    val normalizedRelativePosition = state.normalizedRelativePosition(scrollIndex)
                    Box(
                        modifier = itemBoxModifier.graphicsLayer {
                            normalizedRelativePosition?.let {
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