package br.ufpr.mobile.trabalho3

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RadioGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.ufpr.mobile.trabalho3.model.AvailableCurrencies
import br.ufpr.mobile.trabalho3.model.CurrencyAPI
import br.ufpr.mobile.trabalho3.model.Saldo

class ConvertActivity : AppCompatActivity() {

    private lateinit var CurrencyAPI: CurrencyAPI
    private lateinit var radioGroup: RadioGroup
    private lateinit var radioGroup2: RadioGroup
    private lateinit var valueConvert: EditText
    private lateinit var buttonConvert: Button
    private lateinit var buttonVoltar: Button
    private lateinit var responseText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var saldos: Saldo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_convert)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        radioGroup = findViewById(R.id.radioGroup)
        radioGroup2 = findViewById(R.id.radioGroup2)
        valueConvert = findViewById(R.id.inputValue)
        buttonConvert = findViewById(R.id.buttonConvert)
        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.GONE
        buttonVoltar = findViewById(R.id.buttonVoltar)
        responseText = findViewById(R.id.responseText)

        val bundle = intent.extras
        if (bundle != null) {
            val user = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bundle.getParcelable("saldos", Saldo::class.java)
            } else {
                bundle.getParcelable("saldos")
            }
            if (user == null) {
                responseText.text = "Algo deu errado. Tente novamente mais tarde."
            }
        }

        buttonVoltar.setOnClickListener {
            val intent = Intent(this, ConvertActivity::class.java).apply {
                putExtra("saldos", saldos)
            }
            setResult(RESULT_OK, intent)
            finish()
        }

        buttonConvert.setOnClickListener {
            showLoading()
            val originCurrency = getCurrency(radioGroup)
            val destinationCurrency = getCurrency(radioGroup2)
            val value = valueConvert.text.toString()
            var availableCurrencie: AvailableCurrencies? = null
            when(originCurrency) {
                "BRL" -> when(destinationCurrency) {
                    "USD" -> availableCurrencie = AvailableCurrencies.BRLUSD
                    "BTC" -> availableCurrencie = AvailableCurrencies.BRLBTC
                }
                "USD" -> when(destinationCurrency) {
                    "BRL" -> availableCurrencie = AvailableCurrencies.USDBRL
                    "BTC" -> availableCurrencie = AvailableCurrencies.USDBTC
                }
                "BTC" -> when(destinationCurrency) {
                    "BRL" -> availableCurrencie = AvailableCurrencies.BTCBRL
                    "USD" -> availableCurrencie = AvailableCurrencies.BTCUSD
                }
            }

            if(availableCurrencie == null) {
                responseText.text = "Selecione uma moeda origem e uma moeda destino válida."
                hideLoading()
                return@setOnClickListener
            }


            hideLoading()
        }
    }

    private fun getCurrency(rGroup: RadioGroup): String {
        return when (radioGroup.checkedRadioButtonId) {
            R.id.radioButton -> "BRL"
            R.id.radioButton2 -> "USD"
            R.id.radioButton3 -> "BTC"
            else -> ""
        }
    }


    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        responseText.visibility = View.GONE
        buttonVoltar.visibility = View.GONE
    }

    private fun hideLoading() {
        progressBar.visibility = View.GONE
        responseText.visibility = View.VISIBLE
        buttonVoltar.visibility = View.VISIBLE
    }



}