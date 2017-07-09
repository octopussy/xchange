package com.github.op.xchange.ui.main

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.github.op.xchange.R
import com.github.op.xchange.entity.Currency
import com.github.op.xchange.injection.ViewModelFactory
import com.github.op.xchange.ui.BaseActivity
import com.github.op.xchange.ui.settings.SettingsActivity
import com.github.op.xchange.ui.visible
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity() {

    private lateinit var viewModel: MainViewModel

    private lateinit var baseCurrencyAdapter: ArrayAdapter<Currency>

    private lateinit var relCurrencyAdapter: ArrayAdapter<Currency>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProviders.of(this, ViewModelFactory(application))[MainViewModel::class.java]

        setupView()

        setupObservers()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menuItemSettings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupView() {
        baseCurrencyAdapter = ArrayAdapter<Currency>(this, android.R.layout.simple_spinner_item).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        firstCurrencySpinner.adapter = baseCurrencyAdapter

        relCurrencyAdapter = ArrayAdapter<Currency>(this, android.R.layout.simple_spinner_item).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        secondCurrencySpinner.adapter = relCurrencyAdapter

        firstCurrencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val item = parent.adapter.getItem(position)
                viewModel.selectBaseCurrency(item as Currency)
            }
        }

        secondCurrencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val item = parent.adapter.getItem(position)
                viewModel.selectRelatedCurrency(item as Currency)
            }
        }

        btnRetry.setOnClickListener { viewModel.retryLoading() }
    }

    private fun setupObservers() {
        viewModel.lastRateValue.observe(this, Observer { value ->
            lastRateValueTextView.text = value
        })

        viewModel.rateHistory.observe(this, Observer { list ->
            textView.text = ""
            list?.forEach { textView.append("$it\n") }
        })

        viewModel.selectedCurrenciesLiveData.observe(this, Observer {
            hideAll()
            when (it) {
                is SelectedCurrenciesLiveData.State.Error -> {
                    errorLayout.visible = true
                    Toast.makeText(this, it.throwable.localizedMessage, Toast.LENGTH_LONG).show()
                }

                is SelectedCurrenciesLiveData.State.Loading -> progressBar.visible = true

                is SelectedCurrenciesLiveData.State.Success -> {
                    content.visible = true

                    baseCurrencyAdapter.clear()
                    baseCurrencyAdapter.addAll(it.baseList)
                    val pos1 = baseCurrencyAdapter.getPosition(it.selection.first)
                    firstCurrencySpinner.setSelection(pos1)

                    relCurrencyAdapter.clear()
                    relCurrencyAdapter.addAll(it.relList)
                    val pos2 = relCurrencyAdapter.getPosition(it.selection.second)
                    secondCurrencySpinner.setSelection(pos2)
                }
            }
        })
    }

    private fun hideAll() {
        errorLayout.visible = false
        progressBar.visible = false
        content.visible = false
    }
}
