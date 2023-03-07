package com.example.finance.items

// Класс категория для добавления в firebase realtime database
data class MyCategoryFirebase(
    var _id: Long,
    var _name: String,
    var _color: String,
    var _image: Int,
    var _type : Int,
    var _undeletable : Int = 0
) : java.io.Serializable{

    constructor() : this(0, "", "", 0, 0, 0)

    override fun toString(): String {
        return _name
    }
}