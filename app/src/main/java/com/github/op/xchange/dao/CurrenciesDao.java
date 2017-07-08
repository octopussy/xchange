package com.github.op.xchange.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.github.op.xchange.entity.Currency;
import com.github.op.xchange.entity.RateEntry;

import java.util.List;

@Dao
public interface CurrenciesDao {
    @Query("SELECT * FROM " + Currency.TABLE_NAME)
    LiveData<List<Currency>> getCurrencies();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addCurrency(RateEntry rate);
}
