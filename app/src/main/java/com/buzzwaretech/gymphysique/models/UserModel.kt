package com.buzzwaretech.gymphysique.models

data class UserModel(
    var userId : String = "",
    var name : String = "",
    var email : String = "",
    var phone : String = "",
    var bio : String = "",
    var password : String = "",
    var imageUrl : String = "",
    var bannerUrl : String = "",
    var token : String = "",
    var userDate : Long = 0,
    var deviceType : String = "",
    var cust_id : String = "",
    var followers: HashMap<String, Any> = hashMapOf(),
    var following: HashMap<String, Any> = hashMapOf(),
    var videoCount : Long = 0,
)
