package com.github.op.xchange.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.github.op.xchange.entity.QuoteEntry;

import org.jetbrains.annotations.NotNull;
import org.threeten.bp.LocalDateTime;

import java.util.List;

@Dao
public interface QuotesDao {

    @Query("SELECT * FROM " + QuoteEntry.TABLE_NAME
            + " WHERE "     + QuoteEntry.FIELD_PAIR + " = :pair "
            + " ORDER BY "  + QuoteEntry.FIELD_DATE_TIME +" DESC" )
    LiveData<List<QuoteEntry>> getQuoteHistory(String pair);

    @Query("SELECT * FROM " + QuoteEntry.TABLE_NAME
            + " WHERE "     + QuoteEntry.FIELD_PAIR + " = :pair "
            + " AND "       + QuoteEntry.FIELD_DATE_TIME + " = :dt "
            + " ORDER BY "  + QuoteEntry.FIELD_DATE_TIME + " DESC" )
    List<QuoteEntry> getQuotesByTimeSync(String pair, LocalDateTime dt);

    @Insert
    void addQuoteEntry(@NotNull QuoteEntry rate);

    @Query("DELETE FROM " + QuoteEntry.TABLE_NAME)
    void deleteAllRates();
}
