package it.univaq.disim.mwt.covid19italy.Views;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

import it.univaq.disim.mwt.covid19italy.Data.DatabaseService;
import it.univaq.disim.mwt.covid19italy.Data.Provincia;
import it.univaq.disim.mwt.covid19italy.Dialogs.ExitDialog;
import it.univaq.disim.mwt.covid19italy.R;
import it.univaq.disim.mwt.covid19italy.ViewModels.ProvinceViewModel;


public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_LOCATION = 17;

    FusedLocationProviderClient locationClient;
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (action.equals(DatabaseService.FILTER_GET)) {
                ProvinceViewModel provider = ViewModelProviders.of(MainActivity.this).get(ProvinceViewModel.class);
                ArrayList<Provincia> province = intent.getParcelableArrayListExtra("province");
                Log.i("BROADCAST", ""+province.size());
                provider.setProvince(province);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        locationClient = LocationServices.getFusedLocationProviderClient(this);
        ProvinceViewModel provider = ViewModelProviders.of(this).get(ProvinceViewModel.class);

        setContentView(R.layout.activity_main);

        //Chiamo il Database Service per aggiornare la lista delle province nel provider
        Intent intent = new Intent(MainActivity.this, DatabaseService.class);
        intent.putExtra(DatabaseService.EXTRA_ACTION, DatabaseService.ACTION_GET);
        startService(intent);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.frameLayout, new FragmentProvinceList(), "province")
                .commit();

    }

    public void setMyFragment(Fragment fragment) {

        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment).commit();
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
                .registerReceiver(receiver, new IntentFilter(DatabaseService.FILTER_GET));
        //Chiamo il Database Service per aggiornare la lista delle piattaforme nel provider
        Intent intent = new Intent(MainActivity.this, DatabaseService.class);
        intent.putExtra(DatabaseService.EXTRA_ACTION, DatabaseService.ACTION_GET);
        startService(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("MainActivity in pausa");
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(receiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){

            case R.id.exit :
                new ExitDialog().show(getSupportFragmentManager(),"exit");
                break;
            case R.id.listView :
                //Setto il fragmentList
                System.out.println("Lista");
                setMyFragment(new FragmentProvinceList());
                break;
            case R.id.mapView :
                //Setto il fragmentMaps
                System.out.println("Mappa");
                setMyFragment(new FragmentMaps());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION: {
                //controllo la risposta alla richiesta del permesso
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permesso garantito
                    System.out.println("Permesso Garantito");
                    //Ottengo il dialogo dal tag e lo chiudo
                    Fragment fragment = getSupportFragmentManager().findFragmentByTag("permission");
                    if(fragment != null){
                        DialogFragment dialog = (DialogFragment) fragment;
                        dialog.dismiss();
                    }

                } else {
                    // Permesso rifiutato
                    System.out.println("Permesso Rifiutato");
                }
                return;
            }
        }
    }

}
