package it.univaq.disim.mwt.covid19italy.Data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

import java.util.Date;

@Entity(tableName = "historic_data", primaryKeys= { "sigla", "data" })
public class HistoricData implements Parcelable {

    public static Parcelable.Creator<HistoricData> CREATOR = new Creator<HistoricData>() {
        @Override
        public HistoricData createFromParcel(Parcel source) {
            return new HistoricData(source);
        }

        @Override
        public HistoricData[] newArray(int size) {
            return new HistoricData[size];
        }
    };

    @NonNull
    @ColumnInfo(name = "sigla")
    private String siglaProvincia;

    @ColumnInfo(name = "ncasi")
    private int nCasi;

    @NonNull
    @ColumnInfo(name = "data")
    private Date data;

    public HistoricData() {
    }

    public HistoricData(Parcel in) {
        this.siglaProvincia = in.readString();
        this.nCasi = in.readInt();
        this.data = (Date) in.readSerializable();
    }

    public String getSiglaProvincia() {
        return siglaProvincia;
    }

    public void setSiglaProvincia(String siglaProvincia) {
        this.siglaProvincia = siglaProvincia;
    }

    public int getNCasi() {
        return nCasi;
    }

    public void setNCasi(int nCasi) {
        this.nCasi = nCasi;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.siglaProvincia);
        dest.writeInt(this.nCasi);
        dest.writeSerializable(this.data);
    }

}
