package com.example.flagsincity.utils

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.flagsincity.database.HistoryAction

@BindingAdapter("actionDurationFormatted")
fun TextView.setActionDurationFormatted(item: HistoryAction) {
    text = convertDurationToFormatted(item.startTimeMilli, item.endTimeMilli, context.resources)
}