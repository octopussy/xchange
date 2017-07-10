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
import com.github.op.xchange.R
import com.github.op.xchange.asCurrencyValueString
import com.github.op.xchange.entity.Currency
import com.github.op.xchange.formatDateTime
import com.github.op.xchange.ui.ViewModelFactory
import com.github.op.xchange.ui.BaseActivity
import com.github.op.xchange.ui.settings.SettingsActivity
import com.github.op.xchange.ui.visible
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity() {

    private lateinit var viewModel: MainViewModel

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

        baseCurrencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val item = parent.adapter.getItem(position)
                viewModel.selectBaseCurrency(item as Currency)
            }
        }

        relCurrencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val item = parent.adapter.getItem(position)
                viewModel.selectRelatedCurrency(item as Currency)
            }
        }

        swipeRefreshLayout.setOnRefreshListener { viewModel.refreshHistory() }

        btnSwap.setOnClickListener { viewModel.swapCurrencies() }
    }

    private fun setupObservers() {
        val ac = this
        with(viewModel) {
            isLoading.observe(ac, Observer { swipeRefreshLayout.isRefreshing = it ?: false })

            isNoDataTextVisible.observe(ac, Observer { noDataText.visible = it ?: false })

            quotesStream.observe(ac, Observer {
                val list = it?.first
                val latest = it?.second
                rvAdapter.items = list ?: listOf()
                if (latest != null) {
                    lastRateValueTextView.text = latest.value.asCurrencyValueString()
                    lastRateUpdateDateTextView.text = resources.getString(R.string.label_last_update,
                            latest.date.formatDateTime())
                } else {
                    lastRateValueTextView.text = ""
                    lastRateUpdateDateTextView.text = ""
                }
            })

            selectedCurrenciesStream.observe(ac, Observer {
                it?.let {
                    baseCurrencySpinner.adapter = CurrenciesSpinnerAdapter(this@MainActivity, it.baseList)
                    baseCurrencySpinner.setSelection(it.baseList.indexOf(it.selection.base))

                    relCurrencySpinner.adapter = CurrenciesSpinnerAdapter(this@MainActivity, it.relList)
                    relCurrencySpinner.setSelection(it.relList.indexOf(it.selection.related))
                }
            })
        }
    }
}
