package br.ufpr.mobile.trabalho3

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
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
import androidx.lifecycle.lifecycleScope
import br.ufpr.mobile.trabalho3.model.AvailableCurrencies
import br.ufpr.mobile.trabalho3.model.Saldo
import br.ufpr.mobile.trabalho3.service.Service
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConvertActivity : AppCompatActivity() {

    private lateinit var radioGroup: RadioGroup
    private lateinit var radioGroup2: RadioGroup
    private lateinit var valueConvert: EditText
    private lateinit var buttonConvert: Button
    private lateinit var buttonVoltar: Button
    private lateinit var responseText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var saldos: Saldo
    private var service: Service = Service()

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
        buttonVoltar = findViewById(R.id.buttonVoltar)
        responseText = findViewById(R.id.responseText)
        progressBar.visibility = View.GONE

        val bundle = intent.extras
        if (bundle != null) {
            val data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bundle.getParcelable("saldos", Saldo::class.java)
            } else {
                bundle.getParcelable("saldos")
            }
            if (data == null) {
                responseText.text = "Algo deu errado. Tente novamente mais tarde."
            } else {
                saldos = data
            }
        }

        buttonVoltar.setOnClickListener {
            val intent = Intent(this, ConvertActivity::class.java).apply {
                putExtra("saldos", saldos)
            }
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        buttonConvert.setOnClickListener {
            showLoading()

            var convertedValue: Double
            val originCurrency = getCurrency(radioGroup2)
            val destinationCurrency = getCurrency(radioGroup)
            val value = valueConvert.text.toString().toDouble()
            var availableCurrencies: AvailableCurrencies? = null

            when (originCurrency) {
                "BRL" -> {
                    if (value > saldos.reais) {
                        responseText.text = "Saldo insuficiente."
                        hideLoading()
                        return@setOnClickListener
                    }
                    when (destinationCurrency) {
                        "USD" -> availableCurrencies = AvailableCurrencies.BRLUSD
                        "BTC" -> availableCurrencies = AvailableCurrencies.BRLBTC
                    }
                }

                "USD" -> {
                    if (value > saldos.dolar) {
                        responseText.text = "Saldo insuficiente."
                        hideLoading()
                        return@setOnClickListener
                    }
                    when (destinationCurrency) {
                        "BRL" -> availableCurrencies = AvailableCurrencies.USDBRL
                        "BTC" -> availableCurrencies = AvailableCurrencies.USDBTC
                    }
                }

                "BTC" -> {
                    if (value > saldos.bitcoin) {
                        responseText.text = "Saldo insuficiente."
                        hideLoading()
                        return@setOnClickListener
                    }
                    when (destinationCurrency) {
                        "BRL" -> availableCurrencies = AvailableCurrencies.BTCBRL
                        "USD" -> availableCurrencies = AvailableCurrencies.BTCUSD
                    }
                }
            }

            if ((originCurrency == "BRL" && value > saldos.reais) || (originCurrency == "USD" && value > saldos.dolar) || (originCurrency == "BTC" && value > saldos.bitcoin)) {
                responseText.text = "Saldo insuficiente."
                hideLoading()
                return@setOnClickListener
            }

            if (availableCurrencies == null) {
                responseText.text = "Selecione uma moeda origem e uma moeda destino válida."
                hideLoading()
                return@setOnClickListener
            }

            if (value <= 0) {
                responseText.text = "O valor deve ser maior que zero."
                hideLoading()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                convertedValue = withContext(Dispatchers.Main) {
                    service.getConversao(availableCurrencies, value)
                }

                when (originCurrency) {
                    "BRL" -> {
                        saldos.reais -= value
                        when (destinationCurrency) {
                            "USD" -> saldos.dolar += convertedValue
                            "BTC" -> saldos.bitcoin += convertedValue
                        }
                    }
                    "USD" -> {
                        saldos.dolar -= value
                        when (destinationCurrency) {
                            "BRL" -> saldos.reais += convertedValue
                            "BTC" -> saldos.bitcoin += convertedValue
                        }
                    }
                    "BTC" -> {
                        saldos.bitcoin -= value
                        when (destinationCurrency) {
                            "BRL" -> saldos.reais += convertedValue
                            "USD" -> saldos.dolar += convertedValue
                        }
                    }
                }
                responseText.text = "Conversão bem sucedida!"
                hideLoading()
            }
        }
    }

    private fun getCurrency(rGroup: RadioGroup): String {
        return when (rGroup.checkedRadioButtonId) {
            R.id.radioButton, R.id.radioButton5 -> "BRL"
            R.id.radioButton2, R.id.radioButton6 -> "USD"
            R.id.radioButton3, R.id.radioButton7 -> "BTC"
            else -> ""
        }
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        responseText.visibility = View.INVISIBLE
        buttonVoltar.visibility = View.INVISIBLE
        buttonConvert.isEnabled = false
    }

    private fun hideLoading() {
        progressBar.visibility = View.INVISIBLE
        responseText.visibility = View.VISIBLE
        buttonVoltar.visibility = View.VISIBLE
        buttonConvert.isEnabled = true
    }

    override fun onDestroy() {
        val intent = Intent(this, ConvertActivity::class.java).apply {
            putExtra("saldos", saldos)
        }
        setResult(RESULT_OK, intent)
        super.onDestroy()

    }


}