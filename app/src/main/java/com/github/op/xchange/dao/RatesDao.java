package com.github.op.xchange.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.github.op.xchange.entity.RateEntry;

import java.util.List;

@Dao
public interface RatesDao {
    @Query("SELECT * FROM " + RateEntry.TABLE_NAME + " WHERE code1 = :code1 AND code2 = :code2")
    LiveData<List<RateEntry>> getRates(String code1, String code2);

    @Insert
    void addRate(RateEntry rate);
}
