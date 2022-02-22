package it.univaq.disim.mwt.covid19italy.Dialogs;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;


public class PermissionDialog extends DialogFragment {
    public static final int REQUEST_LOCATION = 17;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Attenzione!").setMessage("Per poter trovare la tua provincia l'applicazione ha bisogno di ottenere il permesso alla geolocalizzazione");
        builder.setPositiveButton("Ho capito", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            }
        });
        builder.setNegativeButton("Esci", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().finish();
            }
        });
        return builder.create();
    }
}
