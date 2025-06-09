package br.ufpr.mobile.trabalho3.model

import retrofit2.http.GET
import retrofit2.http.Path

interface CurrencyAPI {
    @GET("json/last/{moeda}")
    suspend fun getQuotation(@Path("moeda") moeda: String): Envelope
}