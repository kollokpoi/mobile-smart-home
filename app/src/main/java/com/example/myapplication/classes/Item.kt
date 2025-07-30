package com.example.myapplication.classes

import java.nio.Buffer

class Item(
    var name : String,
    var ipaddr : String,
    var id : Int?=null,
    var image : String? = null,
    var commands:List<Command> = emptyList()
)