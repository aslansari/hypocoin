package com.aslansari.hypocoin.ui

import androidx.core.view.children
import com.aslansari.hypocoin.R
import com.aslansari.hypocoin.account.ui.RoiChip
import com.aslansari.hypocoin.account.ui.RoiType
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

object ChipUtil {

    /**
     * Changes the enabled status of each chip in a chip group
     * @param enabled enabled status of chips in the group
     */
    fun ChipGroup.setChildrenEnabled(enabled: Boolean) {
        children.forEach { it.isEnabled = enabled }
    }

    fun Chip.updateForRoi(roiChip: RoiChip) {
        when (roiChip.roiType) {
            RoiType.GAIN -> {
                val chipBackgroundColor = DisplayColorUtil.getGainColor()
                val chipTextColor = DisplayColorUtil.getGainTextColor(resources)
                setChipBackgroundColorResource(chipBackgroundColor)
                setTextColor(chipTextColor)
                text = context.getString(
                    R.string.positive_rate,
                    DisplayTextUtil.Amount.getRateFormat(roiChip.rate)
                )
            }
            RoiType.LOSS -> {
                val chipBackgroundColor = DisplayColorUtil.getLossColor(context)
                val chipTextColor = DisplayColorUtil.getLossTextColor(context, resources)
                setChipBackgroundColorResource(chipBackgroundColor)
                setTextColor(chipTextColor)
                text = context.getString(
                    R.string.negative_rate,
                    DisplayTextUtil.Amount.getRateFormat(roiChip.rate)
                )
            }
        }
    }
}