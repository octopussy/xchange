package com.github.op.xchange.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.github.op.xchange.R
import com.github.op.xchange.entity.Currency

class CurrenciesSpinnerAdapter(val context: Context, val items: List<Currency>) : BaseAdapter(){

    override fun getItem(position: Int): Currency = items[position]

    override fun getCount(): Int = items.size

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var v: TextView? = convertView as TextView?

        if (v == null) {
            v = LayoutInflater.from(context).inflate(R.layout.li_currency, parent, false) as TextView?
        }

        v?.text = getItem(position).format(context)
        return v!!
    }

}