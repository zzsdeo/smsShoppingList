package ru.zzsdeo.smsshoppinglist;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * Created by Andrew on 16.11.2014.
 */
public class AddUpdateProductsDialog extends DialogFragment {

    String title;
    String item;
    long id;
    LinearLayout view;
    EditText et;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title = getArguments().getString("title");
        item = getArguments().getString("item");
        id = getArguments().getLong("id");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (item != null) {
            et.setText(item);
        }
        et.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        view = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.add_update_dialog, null);
        et = (EditText) view.findViewById(R.id.addUpdateProduct);
        et.setHint(title);
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setView(view)
                .setPositiveButton(title, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (et.getText().toString().trim().length() != 0) {
                            ContentValues values = new ContentValues();
                            values.put(ProductsTable.COLUMN_ITEM, et.getText().toString().toLowerCase().trim());
                            if (item != null) {
                                getActivity().getContentResolver().update(ShoppingListContentProvider.CONTENT_URI_PRODUCTS, values, ProductsTable.COLUMN_ID + "=" + id, null);
                            } else {
                                getActivity().getContentResolver().insert(ShoppingListContentProvider.CONTENT_URI_PRODUCTS, values);
                            }
                            dialogInterface.dismiss();
                        }
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
