package com.example.myapplication

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Product(
    @Json(name = "barcode_number") val BarcodeNumber: String,
    @Json(name = "title") val title: String,

)