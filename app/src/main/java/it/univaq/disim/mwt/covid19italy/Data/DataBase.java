package it.univaq.disim.mwt.covid19italy.Data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import it.univaq.disim.mwt.covid19italy.Utils.Converters;


@Database(entities = {Provincia.class, HistoricData.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class DataBase extends RoomDatabase {

    public abstract Provincia_DAO provincia_dao();

    public abstract HistoricData_DAO historicData_dao();

    private static DataBase instance = null;

    public static DataBase getInstance(Context context) {
        if(instance == null) {
            instance = Room.databaseBuilder(
                    context,
                    DataBase.class,
                    "database.db")
                    .build();
        }
        return instance;
    }
}