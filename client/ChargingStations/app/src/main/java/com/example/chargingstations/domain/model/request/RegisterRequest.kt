package com.example.chargingstations.domain.model.request

data class RegisterRequest (
    val name: String,
    val email: String,
    val password: String
)