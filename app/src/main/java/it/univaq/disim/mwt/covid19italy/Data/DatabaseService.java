package it.univaq.disim.mwt.covid19italy.Data;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import it.univaq.disim.mwt.covid19italy.Data.DataBase;
import it.univaq.disim.mwt.covid19italy.Data.HistoricData;
import it.univaq.disim.mwt.covid19italy.Data.Provincia;
import it.univaq.disim.mwt.covid19italy.Utils.MyVolley;

public class DatabaseService extends IntentService {
    public static final String FILTER_GET = "it.univaq.disim.mwt.covid19italy.GET";
    public static final String FILTER_HISTORY = "it.univaq.disim.mwt.covid19italy.HISTORY";
    public static final String EXTRA_ACTION = "extra_action";
    public static final int ACTION_SAVE     = 0;
    public static final int ACTION_GET    = 1;
    public static final int ACTION_GET_HISTORY = 2;

    public DatabaseService() {
        super("DatabaseService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        int action = intent.getIntExtra(EXTRA_ACTION, -1);
        String sigla = intent.getStringExtra("sigla");
        switch (action){
            case ACTION_SAVE:
                this.save(intent);
                break;
            case ACTION_GET:
                this.get();
                break;
            case ACTION_GET_HISTORY:
                this.getHistory(sigla);
                break;
        }
    }

    private void save(Intent intent){
        ArrayList<Provincia> province = intent.getParcelableArrayListExtra("province");
        System.out.println("Numero di pronvince dall'intent: "+ province.size());
        DataBase.getInstance(getApplicationContext()).provincia_dao().save(province);
    }

    private void get() {
        //Ottengo le Preferenze "preferenze"
        SharedPreferences pref = getSharedPreferences("preferenze", Context.MODE_PRIVATE);
        Boolean firstTime = pref.getBoolean("firstTime", true);
        Calendar defaultCalendar = Calendar.getInstance();
        defaultCalendar.setTimeZone(TimeZone.getTimeZone("GMT+1"));
        Date lastUpdate = new Date(pref.getLong("lastUpdate",Long.MIN_VALUE));
        makeHttpRequest(firstTime, lastUpdate);
    }

    private void getHistory(final String sigla){
        final LocalBroadcastManager broadcast = LocalBroadcastManager.getInstance(this);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                List<HistoricData> history = DataBase.getInstance(getApplicationContext()).historicData_dao().getAllBySigla(sigla);
                Intent intent = new Intent(FILTER_HISTORY);
                intent.putParcelableArrayListExtra("history", new ArrayList<HistoricData>(history));
                broadcast.sendBroadcast(intent);
            }
        });
        t.start();
    }

    private void makeHttpRequest(final boolean firstTime, final Date lastUpdate){
        Log.i("RICHIESTA HTTP", "EFFETTUO RICHIESTA HTTP");
        StringRequest richiesta = new StringRequest("https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-json/dpc-covid19-ita-province-latest.json",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ArrayList<Provincia> province = new ArrayList<>();
                        List<HistoricData> dataList = new ArrayList<HistoricData>();
                        try {
                            JSONArray array = new JSONArray(response);
                            Date httpDate = new SimpleDateFormat("yyyy-MM-dd").parse(array.optJSONObject(0).getString("data"));
                            Calendar httpCalendarDate = Calendar.getInstance();
                            httpCalendarDate.setTime(httpDate);
                            Calendar lastUpdateCalendar = Calendar.getInstance();
                            lastUpdateCalendar.setTime(lastUpdate);

                            System.out.println("HTTP DATE: "+httpDate);
                            System.out.println("LASTUPDATE DATE: "+lastUpdate);
                            if(firstTime || httpDate.after(lastUpdate)){
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject json = array.optJSONObject(i);
                                    if (json == null || json.optString("lat") == "null") continue;

                                    Provincia p = new Provincia();

                                    //Parsing del JSON
                                    p.setNome(json.optString("denominazione_provincia"));
                                    p.setRegione(json.optString("denominazione_regione"));
                                    p.setStato(json.optString("stato"));
                                    p.setSigla(json.optString("sigla_provincia"));
                                    p.setCodiceNuts1(json.optString("codice_nuts_1"));
                                    p.setCodiceNuts2(json.optString("codice_nuts_2"));
                                    p.setCodiceNuts3(json.optString("codice_nuts_3"));
                                    p.setCodiceProvincia(json.optInt("codice_provincia"));
                                    p.setCodiceRegione(json.optInt("codice_regione"));
                                    p.setTotaleCasi(json.optInt("totale_casi"));
                                    p.setLatitudine(json.getDouble("lat"));
                                    p.setLongitudine(json.getDouble("long"));
                                    p.setLastUpdateDateTime(new SimpleDateFormat("yyyy-MM-dd").parse(json.optString("data")));
                                    province.add(p);

                                    HistoricData h = new HistoricData();
                                    h.setSiglaProvincia(p.getSigla());
                                    h.setNCasi(p.getTotaleCasi());
                                    h.setData(new SimpleDateFormat("yyyy-MM-dd").parse(json.optString("data")));
                                    dataList.add(h);
                                }
                                saveProvinceToDB(province);
                                saveHistoricDataToDB(dataList);

                                //Aggiorno le preferenze
                                SharedPreferences pref = getSharedPreferences("preferenze", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();
                                if(firstTime){
                                    editor.putBoolean("firstTime", false);
                                }
                                editor.putLong("lastUpdate", httpDate.getTime());
                                editor.apply();
                                getProvinceFromDB();
                            }
                            else{
                                getProvinceFromDB();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Richiesta Http fallita");
                System.out.println(error.toString());
            }
        });
        //Aggiungo la richiesta alla coda di Volley
        MyVolley.getInstance(this).getQueue().add(richiesta);
    }

    public void saveHistoricDataToDB(final List<HistoricData> dataList){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                List<Long> insert = DataBase.getInstance(getApplicationContext()).historicData_dao().save(dataList);
                System.out.println("Inseriti nel Database: " + insert.size() + " dati");
            }
        });
        t.start();
    }

    public void saveProvinceToDB(final List<Provincia> province){
        //Inserire nel DB
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                List<Long> insert = DataBase.getInstance(getApplicationContext()).provincia_dao().save(province);
                System.out.println("Inserite nel Database: " + insert.size() + " province");
            }
        });
        t.start();
    }

    private void getProvinceFromDB(){
        final LocalBroadcastManager broadcast = LocalBroadcastManager.getInstance(this);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                List<Provincia> province = DataBase.getInstance(getApplicationContext()).provincia_dao().getAll();
                Intent intent = new Intent(FILTER_GET);
                intent.putParcelableArrayListExtra("province", new ArrayList<Provincia>(province));
                broadcast.sendBroadcast(intent);
            }
        });
        t.start();
    }

}
