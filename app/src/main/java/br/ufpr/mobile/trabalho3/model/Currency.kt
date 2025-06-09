package br.ufpr.mobile.trabalho3.model

data class Currency (
    val code: String,
    val codein: String,
    val name: String,
    val high: Double,
    val low: Double,
    val varBid: Double,
    val pctChange: Double,
    val bid: Double,
    val ask: Double,
    val timestamp: Double,
    val create_date: String
)