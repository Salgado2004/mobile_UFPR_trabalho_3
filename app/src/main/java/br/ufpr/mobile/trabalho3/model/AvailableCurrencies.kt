package br.ufpr.mobile.trabalho3.model

enum class AvailableCurrencies(sigla: String, nome: String) {
    USDBRL("USD-BRL", "Dólar Americano/Real Brasileiro"),
    BRLUSD("BRL-USD", "Real Brasileiro/Dólar Americano"),
    BTCUSD("BTC-USD", "Bitcoin/Dólar Americano"),
    BTCBRL("BTC-BRL", "Bitcoin/Real Brasileiro"),
    USDBTC("BTC-USD", "Dólar Americano/Bitcoin"),
    BRLBTC("BTC-BRL", "Real Brasileiro/Bitcoin")
}