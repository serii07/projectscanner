package com.example.myapplication

data class ArchivedProduct(
    var data: String = "",
    var productName: String = "",
    var productImage: String? = null // Nullable for easier future integration
)
