package br.ufpr.mobile.trabalho3.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Saldo (
    val reais: Double,
    val dolar: Double,
    val bitcoin: Double
) : Parcelable