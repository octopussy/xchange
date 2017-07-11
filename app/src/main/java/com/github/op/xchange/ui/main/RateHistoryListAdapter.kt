package com.github.op.xchange.ui.main

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.github.op.xchange.R
import com.github.op.xchange.util.asCurrencyValueString
import com.github.op.xchange.util.formatDateTime
import com.github.op.xchange.util.visible

class RateHistoryListAdapter(val context: Context) : RecyclerView.Adapter<RateHistoryListAdapter.VH>() {

    var items = listOf<QuoteVO>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        with(holder) {
            val isDiffPositive = item.diff > 0f
            val isDiffZero = item.diff == 0f

            tvValue.text = item.value.asCurrencyValueString()

            val absDiff = Math.abs(item.diff).asCurrencyValueString()
            tvDiff.text = if (isDiffPositive) "+ $absDiff" else "- $absDiff"

            val id = if (isDiffPositive) R.drawable.ic_arrow_drop_up else R.drawable.ic_arrow_drop_down
            val icon = ContextCompat.getDrawable(context, id)
            icDiff.setImageDrawable(icon)

            tvDiff.visible = !isDiffZero
            icDiff.visible = !isDiffZero

            tvDate.text = item.date.formatDateTime()

            val color = if (position.rem(2) != 1) R.color.historyItemOdd else R.color.historyItemEven
            rootView.setBackgroundColor(ContextCompat.getColor(context, color))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(parent.context).inflate(R.layout.li_rate_history, parent, false) as ViewGroup)
    }

    class VH(val rootView: ViewGroup) : RecyclerView.ViewHolder(rootView) {
        @BindView(R.id.textDate) lateinit var tvDate: TextView
        @BindView(R.id.textValue) lateinit var tvValue: TextView
        @BindView(R.id.textDiff) lateinit var tvDiff: TextView
        @BindView(R.id.icDiff) lateinit var icDiff: ImageView

        init {
            ButterKnife.bind(this, rootView)
        }
    }
}