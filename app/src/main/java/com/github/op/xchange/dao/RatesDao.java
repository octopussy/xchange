package com.github.op.xchange.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.github.op.xchange.entity.RateEntry;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface RatesDao {

    @Query("SELECT * FROM " + RateEntry.TABLE_NAME + " WHERE "
            + RateEntry.FIELD_BASE_CODE + " = :baseCode AND "
            + RateEntry.FIELD_RELATED_CODE + " = :relatedCode")
    LiveData<List<RateEntry>> getRates(String baseCode, String relatedCode);

    @Insert
    void addRate(RateEntry rate);

    @Query("DELETE FROM " + RateEntry.TABLE_NAME)
    void deleteAllRates();
}
