package br.ufpr.mobile.trabalho3

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.ufpr.mobile.trabalho3.model.Saldo

class MainActivity : AppCompatActivity() {

    var saldos: Saldo = Saldo(100000.00, 50000.00, 0.5)
    lateinit var launcher: ActivityResultLauncher<Intent>
    lateinit var tvValueReal: TextView
    lateinit var tvValueUsDolar: TextView
    lateinit var tvValueBitCoin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvValueReal = findViewById(R.id.valueReal)
        tvValueUsDolar = findViewById(R.id.valueUSDolar)
        tvValueBitCoin = findViewById(R.id.valueBitCoin)

        launcher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                if (data != null) {
                    val tempsaldos = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        data.getParcelableExtra("saldos", Saldo::class.java)
                    } else {
                        data.getParcelableExtra("saldos")
                    }
                    tempsaldos?.let {
                        saldos = it
                        updateSaldos()
                    }
                }
            }
        }

        updateSaldos()
    }

    fun converterValores(view: View) {
        val intent = Intent(this, ConvertActivity::class.java).apply {
            putExtra("saldos", saldos)
        }
        launcher.launch(intent)
    }

    @SuppressLint("DefaultLocale")
    private fun updateSaldos(){
        tvValueReal.text = String.format("R$ %.2f", saldos.reais)
        tvValueUsDolar.text = String.format("US$ %.2f", saldos.dolar)
        tvValueBitCoin.text = String.format("₿ %.4f", saldos.bitcoin)
    }
}