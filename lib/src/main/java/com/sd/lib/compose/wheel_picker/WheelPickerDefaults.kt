package com.sd.lib.compose.wheel_picker

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

object WheelPickerDefaults {
    val itemSize: DpSize = DpSize(42.dp, 42.dp)

    @Composable
    fun snapFlingBehaviorAnimationSpecs(
        lowVelocityApproach: AnimationSpec<Float> = remember { tween(easing = LinearEasing) },
        highVelocityApproach: DecayAnimationSpec<Float> = rememberSplineBasedDecay(),
        snap: AnimationSpec<Float> = remember { spring(stiffness = Spring.StiffnessMediumLow) }
    ): WheelPickerSnapFlingBehaviorAnimationSpecs {
        return remember(lowVelocityApproach, highVelocityApproach, snap) {
            WheelPickerSnapFlingBehaviorAnimationSpecs(
                lowVelocityApproach = lowVelocityApproach,
                highVelocityApproach = highVelocityApproach,
                snap = snap
            )
        }
    }
}