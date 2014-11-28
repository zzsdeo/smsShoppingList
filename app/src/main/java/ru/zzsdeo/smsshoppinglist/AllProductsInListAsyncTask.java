package ru.zzsdeo.smsshoppinglist;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.widget.Toast;

import wei.mark.standout.StandOutWindow;

public class AllProductsInListAsyncTask extends AsyncTask<Void, Integer, Void> {

    private ProgressDialog progressBar;
    private Context context;
    private int i;

    public AllProductsInListAsyncTask (Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        progressBar = new ProgressDialog(context);
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setIndeterminate(true);
        progressBar.show();
    }

    @Override
    protected void onProgressUpdate(Integer... option) {
        progressBar.setMessage(context.getString(R.string.added) + option[0]);
    }

    @Override
    protected void onPostExecute(Void unused) {
        progressBar.dismiss();
        int sum = i - 1;
        Toast.makeText(context, context.getString(R.string.added) + sum, Toast.LENGTH_LONG).show();
        ((Activity)context).finish();
        StandOutWindow.show(context, FloatingWindow.class, StandOutWindow.DEFAULT_ID);
    }

    @Override
    protected Void doInBackground(Void... unused) {
        context.getContentResolver().delete(ShoppingListContentProvider.CONTENT_URI_LIST, null, null);
        Cursor c = context.getContentResolver().query(ShoppingListContentProvider.CONTENT_URI_PRODUCTS, null, null, null, null);
        i = 1;
        if (c.moveToFirst()) {
            ContentValues values = new ContentValues();
            do {
                values.put(ListTable.COLUMN_ITEM, c.getString(c.getColumnIndex(ProductsTable.COLUMN_ITEM)));
                values.put(ListTable.COLUMN_CHECKED, 0);
                context.getContentResolver().insert(ShoppingListContentProvider.CONTENT_URI_LIST, values);
                values.clear();
                publishProgress(i);
                i++;
            } while (c.moveToNext());
        }
        return  null;
    }
}