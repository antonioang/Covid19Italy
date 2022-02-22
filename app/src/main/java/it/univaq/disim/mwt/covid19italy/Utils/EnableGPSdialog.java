package it.univaq.disim.mwt.covid19italy.Utils;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class EnableGPSdialog extends DialogFragment {

    final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Attiva il gps per visualizzare le province");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface d, int id) {
                d.dismiss();
                getActivity().startActivity(new Intent(action));
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface d, int id) {
                getActivity().finish();
            }
        });
        return builder.create();
    }
}
