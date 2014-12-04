package ru.zzsdeo.smsshoppinglist;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class SeekBarDialog extends DialogFragment {

    SharedPreferences preferences;
    int progress;
    private final static int MAX_SIZE = 50;
    private final static int MIN_SIZE = 10;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        preferences = getActivity().getSharedPreferences("font_prefs", Context.MODE_PRIVATE);
        LinearLayout view = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.seekbar_dialog, null);
        SeekBar sb = (SeekBar) view.findViewById(R.id.seekBar);
        final TextView tv = (TextView) view.findViewById(R.id.fontSizeTextView);
        sb.setMax(MAX_SIZE - MIN_SIZE);
        progress = preferences.getInt("font_size", 18);
        sb.setProgress(progress - MIN_SIZE);
        tv.setText(String.valueOf(progress));
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progress = i + MIN_SIZE;
                tv.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.font_size))
                .setView(view)
                .setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        preferences.edit().putInt("font_size", progress).apply();
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
