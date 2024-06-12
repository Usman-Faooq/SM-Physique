package com.buzzwaretech.gymphysique.stripe

data class AccountPaymentReturnData(
    val return_data: ReturnData
)

data class ReturnData(
    val error: Int,
    val key: String
)
