package com.aslansari.hypocoin.ui

import androidx.core.view.children
import com.google.android.material.chip.ChipGroup

object ChipUtil {

    /**
     * Changes the enabled status of each chip in a chip group
     * @param enabled enabled status of chips in the group
     */
    fun ChipGroup.setChildrenEnabled(enabled: Boolean) {
        children.forEach { it.isEnabled = enabled }
    }
}