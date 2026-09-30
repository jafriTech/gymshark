package com.jafritech.gymshark.presentation.common

import androidx.annotation.StringRes
import com.jafritech.gymshark.R
import com.jafritech.gymshark.core.util.DataError


@StringRes
fun DataError.messageRes(): Int = when (this) {
    DataError.Network -> R.string.error_network
    DataError.Server -> R.string.error_server
    DataError.Parsing -> R.string.error_parsing
    DataError.NotFound -> R.string.error_not_found
    DataError.Unknown -> R.string.error_unknown
}