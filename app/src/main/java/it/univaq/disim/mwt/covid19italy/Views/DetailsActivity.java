package it.univaq.disim.mwt.covid19italy.Views;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.List;

import it.univaq.disim.mwt.covid19italy.Data.DatabaseService;
import it.univaq.disim.mwt.covid19italy.Data.HistoricData;
import it.univaq.disim.mwt.covid19italy.Data.Provincia;
import it.univaq.disim.mwt.covid19italy.R;


public class DetailsActivity extends AppCompatActivity {

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            System.out.println(action);
            if (action.equals(DatabaseService.FILTER_HISTORY)) {
                List<HistoricData> history = intent.getParcelableArrayListExtra("history");
                updateStatistics(history);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView stato = findViewById(R.id.valore_stato);
        TextView regione = findViewById(R.id.valore_regione);
        TextView sigla = findViewById(R.id.valore_sigla);
        TextView codiceNuts1 = findViewById(R.id.valore_codice_nuts_1);
        TextView codiceNuts2 = findViewById(R.id.valore_codice_nuts_2);
        TextView codiceNuts3 = findViewById(R.id.valore_codice_nuts_3);
        TextView codiceProvincia = findViewById(R.id.valore_codice_provincia);
        TextView codiceRegione = findViewById(R.id.valore_codice_regione);
        TextView casi = findViewById(R.id.valore_casi);
        TextView ultimoAggiornamento = findViewById(R.id.valore_ultimo_aggiornamento);



        //Ottengo l'intent
        Intent provinciaIntent = getIntent();
        //Ottengo la provincia dall'intent
        Provincia provincia = provinciaIntent.getParcelableExtra("provincia");

        //chiedo i dati storici
        Intent intent = new Intent(DetailsActivity.this, DatabaseService.class);
        intent.putExtra(DatabaseService.EXTRA_ACTION, DatabaseService.ACTION_GET_HISTORY);
        intent.putExtra("sigla", provincia.getSigla());
        startService(intent);


        if(provincia != null){
            //Imposto il nome della provincia come titolo dell'activity
            toolbar.setTitle(provincia.getNome());
            //Imposto tutti i dettagli della provincia
            stato.setText(provincia.getStato());
            regione.setText(provincia.getRegione());
            sigla.setText(provincia.getSigla());
            codiceNuts1.setText(provincia.getCodiceNuts1());
            codiceNuts2.setText(provincia.getCodiceNuts2());
            codiceNuts3.setText(provincia.getCodiceNuts3());
            codiceProvincia.setText(""+provincia.getCodiceProvincia());
            codiceRegione.setText(""+provincia.getCodiceRegione());
            casi.setText(""+provincia.getTotaleCasi());
            ultimoAggiornamento.setText(provincia.getLastUpdateDateTime().toString());
        }

        setSupportActionBar(toolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("nogps");
        if(fragment != null){
            DialogFragment dialog = (DialogFragment) fragment;
            dialog.dismiss();
        }

        LocalBroadcastManager.getInstance(this)
                .registerReceiver(receiver, new IntentFilter(DatabaseService.FILTER_HISTORY));
    }

    private void updateStatistics(List<HistoricData> history){
        System.out.println(history.size());
        TextView casiOggi = findViewById(R.id.valore_casi_oggi);
        TextView percentuale = findViewById(R.id.valore_percentuale);
        if(history.size() > 1){
            HistoricData today = history.get(0);
            HistoricData yesterday = history.get(0);
            int todayCases = today.getNCasi()-yesterday.getNCasi();
            casiOggi.setText(todayCases);
            if(history.size() > 6){
                HistoricData lastWeek = history.get(6);
                float percentage = ((today.getNCasi()-lastWeek.getNCasi())*100)/today.getNCasi();
                percentuale.setText(percentage+"%");
            }
        }
    }

}
