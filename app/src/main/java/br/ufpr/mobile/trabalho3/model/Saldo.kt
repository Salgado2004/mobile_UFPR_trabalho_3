package br.ufpr.mobile.trabalho3.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Saldo (
    var reais: Double,
    var dolar: Double,
    var bitcoin: Double
) : Parcelable