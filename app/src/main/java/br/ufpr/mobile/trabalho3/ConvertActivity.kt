package br.ufpr.mobile.trabalho3

import android.os.Bundle
import android.widget.ProgressBar
import android.widget.RadioGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.ufpr.mobile.trabalho3.model.CurrencyAPI
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ConvertActivity : AppCompatActivity() {

    private lateinit var CurrencyAPI: CurrencyAPI
    private lateinit var radioGroup: RadioGroup
    private lateinit var radioGroup2: RadioGroup
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_convert)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

            radioGroup = findViewById(R.id.radioGroup)
            radioGroup2 = findViewById(R.id.radioGroup2)
            progressBar = findViewById(R.id.progressBar)

            val retrofit = Retrofit.Builder()
                .baseUrl("https://economia.awesomeapi.com.br")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            CurrencyAPI = retrofit.create(CurrencyAPI::class.java)
        }
    }
}