package it.univaq.disim.mwt.covid19italy.Data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "province")
public class Provincia implements Parcelable {

    public static Parcelable.Creator<Provincia> CREATOR = new Creator<Provincia>() {
        @Override
        public Provincia createFromParcel(Parcel source) {
            return new Provincia(source);
        }

        @Override
        public Provincia[] newArray(int size) {
            return new Provincia[size];
        }
    };

    @PrimaryKey()
    @NonNull
    private String sigla;

    private String nome, regione, stato, codiceNuts1, codiceNuts2, codiceNuts3;
    private int codiceRegione;
    private int codiceProvincia;
    private int totaleCasi;
    private Double latitudine, longitudine;
    private Date lastUpdateDateTime;

    public Provincia() {
    }

    public Provincia(Parcel in) {
        this.nome = in.readString();
        this.regione = in.readString();
        this.stato = in.readString();
        this.sigla = in.readString();
        this.codiceNuts1 = in.readString();
        this.codiceNuts2 = in.readString();
        this.codiceNuts3 = in.readString();
        this.codiceProvincia = in.readInt();
        this.codiceRegione = in.readInt();
        this.totaleCasi = in.readInt();
        this.latitudine = in.readDouble();
        this.longitudine = in.readDouble();
        this.lastUpdateDateTime = (Date) in.readSerializable();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nome);
        dest.writeString(regione);
        dest.writeString(stato);
        dest.writeString(sigla);
        dest.writeString(codiceNuts1);
        dest.writeString(codiceNuts2);
        dest.writeString(codiceNuts3);
        dest.writeInt(codiceProvincia);
        dest.writeInt(codiceRegione);
        dest.writeInt(totaleCasi);
        dest.writeDouble(latitudine);
        dest.writeDouble(longitudine);
        dest.writeSerializable(lastUpdateDateTime);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getRegione() {
        return regione;
    }

    public void setRegione(String regione) {
        this.regione = regione;
    }

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public String getCodiceNuts1() {
        return codiceNuts1;
    }

    public void setCodiceNuts1(String codiceNuts1) {
        this.codiceNuts1 = codiceNuts1;
    }

    public String getCodiceNuts2() {
        return codiceNuts2;
    }

    public void setCodiceNuts2(String codiceNuts2) {
        this.codiceNuts2 = codiceNuts2;
    }

    public String getCodiceNuts3() {
        return codiceNuts3;
    }

    public void setCodiceNuts3(String codiceNuts3) {
        this.codiceNuts3 = codiceNuts3;
    }

    public int getCodiceRegione() {
        return codiceRegione;
    }

    public void setCodiceRegione(int codiceRegione) {
        this.codiceRegione = codiceRegione;
    }

    public int getCodiceProvincia() {
        return codiceProvincia;
    }

    public void setCodiceProvincia(int codiceProvincia) {
        this.codiceProvincia = codiceProvincia;
    }

    public int getTotaleCasi() {
        return totaleCasi;
    }

    public void setTotaleCasi(int totaleCasi) {
        this.totaleCasi = totaleCasi;
    }

    public Double getLatitudine() {
        return latitudine;
    }

    public void setLatitudine(Double latitudine) {
        this.latitudine = latitudine;
    }

    public Double getLongitudine() {
        return longitudine;
    }

    public void setLongitudine(Double longitudine) {
        this.longitudine = longitudine;
    }

    public Date getLastUpdateDateTime() {
        return lastUpdateDateTime;
    }

    public void setLastUpdateDateTime(Date lastUpdateDateTime) {
        this.lastUpdateDateTime = lastUpdateDateTime;
    }

}

