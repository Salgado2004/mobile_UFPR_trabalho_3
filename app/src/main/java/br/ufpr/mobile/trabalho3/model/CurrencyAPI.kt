package br.ufpr.mobile.trabalho3.model

import retrofit2.http.GET

interface CurrencyAPI {
    @GET
    suspend fun getQuotation(): Envelope
}