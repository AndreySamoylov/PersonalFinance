package com.example.finance.items

data class MyCategory(
     var _id: Long,
     var _name: String,
     var _color: String,
     var _image: Int,
     var _type : Byte,
     var _undeletable : Byte = 0
) : java.io.Serializable{
     constructor() : this(0, "", "", 0, 0, 0)
     override fun toString(): String {
          return _name
     }
}