package com.w2sv.wheelpicker

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

object WheelPickerDefaults {
    val itemSize: DpSize = DpSize(42.dp, 42.dp)

    val decayAnimationSpec: DecayAnimationSpec<Float>
        @Composable
        get() = rememberSplineBasedDecay()

    val snapAnimationSpec: AnimationSpec<Float>
        @Composable
        get() = remember { spring(stiffness = Spring.StiffnessMediumLow) }
}