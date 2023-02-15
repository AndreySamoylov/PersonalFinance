package com.example.finance.items

data class MyIncome(
    val _id: Long,
    val _sum: Double,
    val _date_income: String,
    val _comment: String,
    val _account: Long,
    val _category: Long
) : java.io.Serializable{
    constructor() : this(0, 0.0, "", "", 0, 0)
}