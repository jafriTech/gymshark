package com.jafritech.gymshark.core.util

sealed interface DataResult<out T> {
    data class Success<T>(val data: T) : DataResult<T>
    data class Error(val error: DataError) : DataResult<Nothing>
}

enum class DataError {
    Network,
    Server,
    Parsing,
    NotFound,
    Unknown,
}
