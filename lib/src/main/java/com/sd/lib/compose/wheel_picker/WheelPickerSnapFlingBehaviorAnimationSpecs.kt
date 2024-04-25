package com.sd.lib.compose.wheel_picker

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.runtime.Stable

@Stable
data class WheelPickerSnapFlingBehaviorAnimationSpecs(
    val lowVelocityApproach: AnimationSpec<Float>,
    val highVelocityApproach: DecayAnimationSpec<Float>,
    val snap: AnimationSpec<Float>,
)