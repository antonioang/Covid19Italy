package it.univaq.disim.mwt.covid19italy.Data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import it.univaq.disim.mwt.covid19italy.Data.HistoricData;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface HistoricData_DAO {

    @Query("SELECT * FROM historic_data WHERE sigla = :sigla ORDER BY data DESC")
    List<HistoricData> getAllBySigla(String sigla);

    @Insert(onConflict = REPLACE)
    List<Long> save(List<HistoricData> dataList);
}
