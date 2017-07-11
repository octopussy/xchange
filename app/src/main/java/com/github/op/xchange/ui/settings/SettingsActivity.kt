package com.github.op.xchange.ui.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import com.github.op.xchange.R
import com.github.op.xchange.XChangeApp
import com.github.op.xchange.repository.XChangeRepository
import com.github.op.xchange.util.BaseActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_settings.*
import javax.inject.Inject

class SettingsActivity : BaseActivity() {

    @Inject lateinit var repository: XChangeRepository
    @Inject lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (application !is XChangeApp) {
            finish()
            return
        }

        setContentView(R.layout.activity_settings)

        (application as XChangeApp).component.inject(this)

        btnWipeDb.setOnClickListener {
            repository.clearData()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        Toast.makeText(this@SettingsActivity, R.string.data_wiped, Toast.LENGTH_LONG).show()
                    }, {
                        Toast.makeText(this@SettingsActivity, R.string.error, Toast.LENGTH_LONG).show()
                    })
        }
    }
}