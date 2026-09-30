package com.jafritech.gymshark.core.network

import kotlinx.serialization.json.Json

/**
 * Single Json config shared by Retrofit and tests.
 * - ignoreUnknownKeys: API sends many fields we don't model.
 * - coerceInputValues: a null/invalid value for a non-null field falls back to its default.
 * - explicitNulls = false: missing keys and nulls are treated the same.
 *
 * Retrofit: Retrofit.Builder().addConverterFactory(
 *     ApiJson.asConverterFactory("application/json".toMediaType())
 * )
 */
val ApiJson: Json = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
    explicitNulls = false
    isLenient = true
}
