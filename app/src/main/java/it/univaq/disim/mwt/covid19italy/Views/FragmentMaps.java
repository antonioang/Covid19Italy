package it.univaq.disim.mwt.covid19italy.Views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

import it.univaq.disim.mwt.covid19italy.Data.Provincia;
import it.univaq.disim.mwt.covid19italy.R;
import it.univaq.disim.mwt.covid19italy.ViewModels.ProvinceViewModel;


public class FragmentMaps extends Fragment implements OnMapReadyCallback {

    private ProvinceViewModel provider;
    private MainActivity current;
 private LatLngBounds.Builder builder = new LatLngBounds.Builder();

    public static FragmentMaps getInstance(ArrayList<Provincia> province) {
        FragmentMaps f = new FragmentMaps();

        Bundle b = new Bundle();
        b.putParcelableArrayList("province", province);

        f.setArguments(b);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        provider = ViewModelProviders.of(getActivity()).get(ProvinceViewModel.class);

        SupportMapFragment map = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        map.getMapAsync(this);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            current = (MainActivity) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        current = null;
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        //Piazzo il marcatore dello smartphone
        FusedLocationProviderClient locationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        locationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                //Imposto la posizione della mappa sulla posizione dello smartphone
                builder.include(new LatLng(location.getLatitude(),location.getLongitude()));

                //Piazzo il marcatore dello smartphone
                MarkerOptions options = new MarkerOptions();
                options.position(new LatLng(location.getLatitude(), location.getLongitude()));
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                googleMap.addMarker(options);
            }
        });

        if(getArguments() != null) {

            //Piazzo i marcatori delle piattaforme
            ArrayList<Provincia> province = getArguments().getParcelableArrayList("province");
            for(Provincia p: province){
                Marker marker = googleMap.addMarker( new MarkerOptions()
                        .title(p.getNome())
                        .position(new LatLng(p.getLatitudine(), p.getLongitudine()))
                        .snippet("Totale Casi: "+p.getTotaleCasi()));

                marker.setTag(p);
                builder.include(new LatLng(p.getLatitudine(),p.getLongitudine()));
            }

           LatLngBounds bounds = builder.build();
            googleMap.moveCamera(
                    CameraUpdateFactory.newLatLngBounds(bounds,100));

        }
        else  {
            ArrayList<Provincia> province = provider.getProvince().getValue();
            for(Provincia plt: province){
                Marker marker = googleMap.addMarker( new MarkerOptions()
                .title(plt.getNome())
                .snippet("Totale Casi: "+plt.getTotaleCasi())
                .position(new LatLng(plt.getLatitudine(), plt.getLongitudine())));
                marker.setTag(plt);
                builder.include(new LatLng(plt.getLatitudine(),plt.getLongitudine()));
            }

            LatLngBounds bounds = builder.build();
            googleMap.moveCamera(
                    CameraUpdateFactory.newLatLngBounds(bounds,100));

        }

        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                LinearLayout info = new LinearLayout(getActivity());
                info.setOrientation(LinearLayout.VERTICAL);
                TextView title = new TextView(getActivity());

                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(getActivity());
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener(){
            @Override
            public void onInfoWindowClick(Marker marker) {
                //ottengo la posizione del marker
                Provincia p = (Provincia) marker.getTag();
                //Faccio partire l'activity per i dettagli
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.setAction("DETAILS");
                intent.putExtra("provincia", p);
                startActivity(intent);
            }
        });
    }

}
