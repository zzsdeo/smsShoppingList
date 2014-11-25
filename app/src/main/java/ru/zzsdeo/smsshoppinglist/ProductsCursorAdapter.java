package ru.zzsdeo.smsshoppinglist;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class ProductsCursorAdapter extends CursorAdapter {

    private LayoutInflater mInflater;

    public ProductsCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return mInflater.inflate(R.layout.products_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView tv = (TextView) view.findViewById(R.id.productsItem);
        tv.setText(cursor.getString(cursor.getColumnIndex(ProductsTable.COLUMN_ITEM)));
    }
}
