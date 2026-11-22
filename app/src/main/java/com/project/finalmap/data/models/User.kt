package com.project.finalmap.data.models

data class User(
    val uid:String = "",
    val username:String = "",
    val nama:String = "",
    val phone:String = "",
    val photo:String? = null,
    val createdAt:Long = System.currentTimeMillis()
)
