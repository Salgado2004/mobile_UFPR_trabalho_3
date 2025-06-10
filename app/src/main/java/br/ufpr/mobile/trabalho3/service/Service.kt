package br.ufpr.mobile.trabalho3.service

import br.ufpr.mobile.trabalho3.model.AvailableCurrencies
import br.ufpr.mobile.trabalho3.model.CurrencyAPI
import br.ufpr.mobile.trabalho3.model.Envelope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Service {
    private lateinit var envelope: Envelope

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://economia.awesomeapi.com.br/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val currencyAPI: CurrencyAPI = retrofit.create(CurrencyAPI::class.java)

    suspend fun getConversao(moedas: AvailableCurrencies, valor: Double): Double{
        try{

            envelope = withContext(Dispatchers.IO){
                currencyAPI.getQuotation(moedas.sigla)
            }

            when (moedas) {
                AvailableCurrencies.USDBRL -> return (valor * (envelope.usdbrl.high + envelope.usdbrl.low)/2)
                AvailableCurrencies.BRLUSD -> return (valor * (envelope.brlusd.high + envelope.brlusd.low)/2)
                AvailableCurrencies.BTCUSD -> return (valor * (envelope.btcusd.high + envelope.btcusd.low)/2)
                AvailableCurrencies.BTCBRL -> return (valor * (envelope.btcbrl.high + envelope.btcbrl.low)/2)
                AvailableCurrencies.USDBTC -> return (valor / (envelope.btcusd.high + envelope.btcusd.low)/2)
                AvailableCurrencies.BRLBTC -> return (valor / (envelope.btcbrl.high + envelope.btcbrl.low)/2)
                else -> return valor
            }

        } catch (e: Exception) {
            println("Erro: ${e.message}")
            return 0.0
        }
    }
}