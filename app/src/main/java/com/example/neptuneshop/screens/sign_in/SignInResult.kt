package com.example.neptuneshop.screens.sign_in

data class SignInResult(
    val data: GoogleUserData?,
    val errorMessage: String?
)
data class GoogleUserData(
    val userId:String,
    val username:String?,
    val email: String?,
    val phoneNumber: String?,
    val profilePic: String?
)