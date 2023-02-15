package com.example.finance.items

import java.io.Serializable

data class MyAccount(val _id: Long, val name: String) : Serializable{
    constructor() : this(0, "")

    override fun toString(): String {
        return name
    }
}
