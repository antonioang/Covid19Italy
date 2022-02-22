package it.univaq.disim.mwt.covid19italy.Utils;

import androidx.room.TypeConverter;

import java.util.Date;

public class Converters {
    @TypeConverter
    public static Date fromLong(Long value){
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long toLong(Date d){
        return d == null ? null : d.getTime();
    }
}
