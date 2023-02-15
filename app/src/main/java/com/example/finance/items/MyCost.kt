package com.example.finance.items

data class MyCost(
    var _id: Long,
    var _sum: Double,
    var _date_cost: String,
    var _comment: String,
    var _account: Long,
    var _category: Long
) : java.io.Serializable{
    constructor() : this(0, 0.0, "", "", 0, 0)
}