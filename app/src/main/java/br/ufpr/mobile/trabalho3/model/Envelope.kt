package br.ufpr.mobile.trabalho3.model

import com.google.gson.annotations.SerializedName

data class Envelope (
    @SerializedName("USDBRL")
    val usdbrl: Currency,
    @SerializedName("BRLUSD")
    val brlusd: Currency,
    @SerializedName("BTCUSD")
    val btcusd: Currency,
    @SerializedName("BTCBRL")
    val btcbrl: Currency,
)