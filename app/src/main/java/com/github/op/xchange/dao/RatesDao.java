package com.github.op.xchange.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.github.op.xchange.entity.RateEntry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.threeten.bp.LocalDate;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface RatesDao {

    @Query(DaoHelper.QUERY_RATE_LIST)
    Flowable<List<RateEntry>> getRates(String baseCode, String relatedCode);

    @Query(DaoHelper.QUERY_RATE_LIST + " LIMIT 1")
    RateEntry getLatestRateSync(String baseCode, String relatedCode);

    @Query(DaoHelper.QUERY_RATE_LIST + " LIMIT 1")
    LiveData<RateEntry> getLatestRateLiveData(String baseCode, String relatedCode);

    @Query(DaoHelper.QUERY_RATE_LIST + " LIMIT 1")
    Flowable<RateEntry> getLatestRate(String baseCode, String relatedCode);

    @Insert
    void addRate(@NotNull RateEntry rate);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateRate(@NotNull RateEntry rate);

    @Query("DELETE FROM " + RateEntry.TABLE_NAME)
    void deleteAllRates();
}
