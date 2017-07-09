package com.github.op.xchange.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import com.github.op.xchange.R
import com.github.op.xchange.injection.ViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.ArrayAdapter
import com.github.op.xchange.entity.Currency


class MainActivity : BaseActivity() {

    private lateinit var viewModel: MainViewModel

    private lateinit var firstAdapter: ArrayAdapter<Currency>

    private lateinit var secondAdapter: ArrayAdapter<Currency>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProviders.of(this, ViewModelFactory(application))[MainViewModel::class.java]

        setupView()

        setupObservers()
    }

    private fun setupView() {
        firstAdapter = ArrayAdapter<Currency>(this, android.R.layout.simple_spinner_item).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        firstCurrencySpinner.adapter = firstAdapter

        secondAdapter = ArrayAdapter<Currency>(this, android.R.layout.simple_spinner_item).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        secondCurrencySpinner.adapter = secondAdapter

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
    }

    private fun setupObservers() {
        viewModel.rateHistory.observe(this, Observer { list ->
            textView.text = ""
            list?.forEach { textView.append("$it\n") }
        })

        viewModel.firstCurrencyState.observe(this, Observer { state ->
            firstAdapter.clear()
            firstAdapter.addAll(state!!.list)
            val pos = firstAdapter.getPosition(state.selectedCurrency)
            firstCurrencySpinner.setSelection(pos)
        })

        viewModel.secondCurrencyState.observe(this, Observer { state ->
            secondAdapter.clear()
            secondAdapter.addAll(state!!.list)
            val pos = secondAdapter.getPosition(state.selectedCurrency)
            secondCurrencySpinner.setSelection(pos)
        })

    }
}
