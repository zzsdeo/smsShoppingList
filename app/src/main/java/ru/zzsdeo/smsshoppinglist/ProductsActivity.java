package ru.zzsdeo.smsshoppinglist;

import android.app.ActionBar;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import wei.mark.standout.StandOutWindow;

/**
 * Created by Andrey on 14.11.2014.
 */
public class ProductsActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    private ProductsCursorAdapter adapter;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_products);

        getLoaderManager().initLoader(0, null, this);

        ActionBar bar = getActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        ListView productsList = (ListView) findViewById(R.id.productsList);

        adapter = new ProductsCursorAdapter(this, null, 0);
        productsList.setAdapter(adapter);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this, ShoppingListContentProvider.CONTENT_URI_PRODUCTS, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }
}
