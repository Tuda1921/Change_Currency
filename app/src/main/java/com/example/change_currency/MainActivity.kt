package com.example.change_currency

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {

    private lateinit var sourceAmount: EditText
    private lateinit var targetAmount: EditText
    private lateinit var sourceCurrency: Spinner
    private lateinit var targetCurrency: Spinner

    private val exchangeRates = mapOf(
        "USD" to 1.0,
        "EUR" to 0.85,
        "JPY" to 110.0,
        "VND" to 23000.0
    )

    private var selectedSourceCurrency: String by Delegates.observable("USD") { _, _, _ -> convertCurrency() }
    private var selectedTargetCurrency: String by Delegates.observable("EUR") { _, _, _ -> convertCurrency() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sourceAmount = findViewById(R.id.sourceAmount)
        targetAmount = findViewById(R.id.targetAmount)
        sourceCurrency = findViewById(R.id.sourceCurrency)
        targetCurrency = findViewById(R.id.targetCurrency)

        setupCurrencySpinners()

        sourceAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = convertCurrency()
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        sourceAmount.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) setAsSourceCurrency()
        }

        targetAmount.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) setAsTargetCurrency()
        }
    }

    private fun setupCurrencySpinners() {
        val currencyOptions = exchangeRates.keys.toList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencyOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        sourceCurrency.adapter = adapter
        targetCurrency.adapter = adapter

        sourceCurrency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                selectedSourceCurrency = currencyOptions[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        targetCurrency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                selectedTargetCurrency = currencyOptions[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setAsSourceCurrency() {
        sourceAmount.isEnabled = true
        targetAmount.isEnabled = false
    }

    private fun setAsTargetCurrency() {
        sourceAmount.isEnabled = false
        targetAmount.isEnabled = true
    }

    private fun convertCurrency() {
        val amount = sourceAmount.text.toString().toDoubleOrNull() ?: return
        val sourceRate = exchangeRates[selectedSourceCurrency] ?: return
        val targetRate = exchangeRates[selectedTargetCurrency] ?: return

        val convertedAmount = amount * (targetRate / sourceRate)
        targetAmount.setText("%.2f".format(convertedAmount))
    }
}
