package br.ufpr.mobile.trabalho3

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
        progressBar.visibility = View.GONE
        buttonVoltar = findViewById(R.id.buttonVoltar)
        responseText = findViewById(R.id.responseText)

        val bundle = intent.extras
        if (bundle != null) {
            val user = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
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
            var value = valueConvert.text.toString().toDouble()
            var convertedValue: Double = 0.0
            var availableCurrencie: AvailableCurrencies? = null

            when (originCurrency) {
                "BRL" -> {
                    if (value > saldos.reais) {
                        responseText.text = "Saldo insuficiente."
                        hideLoading()
                        return@setOnClickListener
                    }
                    when (destinationCurrency) {
                        "USD" -> availableCurrencie = AvailableCurrencies.BRLUSD
                        "BTC" -> availableCurrencie = AvailableCurrencies.BRLBTC
                    }
                }

                "USD" -> {
                    if (value > saldos.dolar) {
                        responseText.text = "Saldo insuficiente."
                        hideLoading()
                        return@setOnClickListener
                    }
                    when (destinationCurrency) {
                        "BRL" -> availableCurrencie = AvailableCurrencies.USDBRL
                        "BTC" -> availableCurrencie = AvailableCurrencies.USDBTC
                    }
                }

                "BTC" -> {
                    if (value > saldos.bitcoin) {
                        responseText.text = "Saldo insuficiente."
                        hideLoading()
                        return@setOnClickListener
                    }
                    when (destinationCurrency) {
                        "BRL" -> availableCurrencie = AvailableCurrencies.BTCBRL
                        "USD" -> availableCurrencie = AvailableCurrencies.BTCUSD
                    }
                }
            }

            if ((originCurrency == "BRL" && value > saldos.reais) || (originCurrency == "USD" && value > saldos.dolar) || (originCurrency == "BTC" && value > saldos.bitcoin)) {
                responseText.text = "Saldo insuficiente."
                hideLoading()
                return@setOnClickListener
            }

            if (availableCurrencie == null) {
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
                    service.getConversao(availableCurrencie, value)
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

            }
            hideLoading()
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
        responseText.visibility = View.GONE
        buttonVoltar.visibility = View.GONE
        buttonConvert.isEnabled = false
    }

    private fun hideLoading() {
        progressBar.visibility = View.GONE
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