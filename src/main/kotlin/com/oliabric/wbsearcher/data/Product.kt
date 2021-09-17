package com.oliabric.wbsearcher.data

class Product(
    val id: Int,
    val root: Int,
    val kindId: Int,
    val subjectId: Int,
    val name: String,
    val brand: String,
    val brandId: Int,
    val priceU: Int,
    val salePriceU: Int,
    val pics: Int,
    val colors: List<Color>,
    val sizes: List<Size>
) {
}