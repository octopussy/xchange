package com.github.op.xchange.ui.main

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.github.op.xchange.R
import com.github.op.xchange.asCurrencyValueString
import com.github.op.xchange.entity.Currency
import com.github.op.xchange.formatDateTime
import com.github.op.xchange.injection.ViewModelFactory
import com.github.op.xchange.ui.BaseActivity
import com.github.op.xchange.ui.settings.SettingsActivity
import com.github.op.xchange.ui.visible
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity() {

    private lateinit var viewModel: MainViewModel

    private lateinit var baseCurrencyAdapter: ArrayAdapter<Currency>

    private lateinit var relCurrencyAdapter: ArrayAdapter<Currency>

    private lateinit var rvAdapter: RateHistoryListAdapter

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
        rvRateHistory.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvRateHistory.setHasFixedSize(true)
        rvAdapter = RateHistoryListAdapter(this)
        rvRateHistory.adapter = rvAdapter

        baseCurrencyAdapter = ArrayAdapter<Currency>(this, android.R.layout.simple_spinner_item).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        baseCurrencySpinner.adapter = baseCurrencyAdapter

        relCurrencyAdapter = ArrayAdapter<Currency>(this, android.R.layout.simple_spinner_item).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        relCurrencySpinner.adapter = relCurrencyAdapter

        baseCurrencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val item = parent.adapter.getItem(position)
                viewModel.selectBaseCurrency(item as Currency)
            }
        }

        relCurrencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val item = parent.adapter.getItem(position)
                viewModel.selectRelatedCurrency(item as Currency)
            }
        }

        swipeRefreshLayout.setOnRefreshListener { viewModel.refreshHistory() }
    }

    private fun setupObservers() {
        viewModel.rateHistoryLiveData.observe(this, Observer {

            noDataText.visible =false
            mainContentPanel.visible = false
            progressBar.visible = false

            when (it) {
                is RatesLiveData.State.Loading -> {
                    progressBar.visible = !swipeRefreshLayout.isRefreshing
                }

                is RatesLiveData.State.SuccessEmpty -> {
                    swipeRefreshLayout.isRefreshing = false
                    mainContentPanel.visible = true
                    noDataText.visible = true
                }

                is RatesLiveData.State.Error -> {
                    swipeRefreshLayout.isRefreshing = false
                    mainContentPanel.visible = true
                    showErrorToast(it.throwable)
                }

                is RatesLiveData.State.Success -> {
                    swipeRefreshLayout.isRefreshing = false
                    mainContentPanel.visible = true
                    rvAdapter.items = it.list

                    lastRateValueTextView.text = it.latestRate.rate.asCurrencyValueString()
                    lastRateUpdateDateTextView.text = resources.getString(R.string.label_last_update,
                            it.latestRate.date.formatDateTime())
                }
            }
        })

        viewModel.selectedCurrenciesLiveData.observe(this, Observer {
            it?.let {
                baseCurrencyAdapter.clear()
                baseCurrencyAdapter.addAll(it.baseList)
                baseCurrencySpinner.setSelection(baseCurrencyAdapter.getPosition(it.selection.first))

                relCurrencyAdapter.clear()
                relCurrencyAdapter.addAll(it.relList)
                relCurrencySpinner.setSelection(relCurrencyAdapter.getPosition(it.selection.second))
            }
        })
    }

    private fun showErrorToast(th: Throwable) {
        Toast.makeText(this, th.localizedMessage, Toast.LENGTH_LONG).show()
    }
}
