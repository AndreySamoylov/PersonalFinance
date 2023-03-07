package com.example.finance.firebase

import com.example.finance.items.*

data class MyFirebaseUserData(var listCost : List<MyCost>,
                              var listIncome : List<MyIncome>,
                              var listCategory : List<MyCategoryFirebase>,
                              var listAccount : List<MyAccount>
){
    constructor() : this(listOf(), listOf(),listOf(),listOf())
}
