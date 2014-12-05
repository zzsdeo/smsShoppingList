package ru.zzsdeo.smsshoppinglist;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class SeekBarTransparencyDialog extends DialogFragment {

    SharedPreferences preferences;
    float progress;
    private final static int K = 100;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        preferences = getActivity().getSharedPreferences("layout_prefs", Context.MODE_PRIVATE);
        LinearLayout view = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.seekbar_transparency_dialog, null);
        SeekBar sb = (SeekBar) view.findViewById(R.id.transparencySeekBar);
        final TextView tv = (TextView) view.findViewById(R.id.transparencyTextView);
        progress = preferences.getFloat("transparency", 0.9f);
        sb.setProgress((int) (progress*K));
        tv.setText(String.valueOf((int) (progress*K)) + "%");
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progress = (float) i / K;
                tv.setText(String.valueOf(i) + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.background_transparency))
                .setView(view)
                .setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        preferences.edit().putFloat("transparency", progress).apply();
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        return adb.create();
    }
}
