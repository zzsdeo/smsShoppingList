package ru.zzsdeo.smsshoppinglist;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

public class ProductsActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    private ProductsCursorAdapter adapter;
    Cursor c;
    SharedPreferences preferences;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (c != null) {
            c.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.products_menu, menu);
        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView search = (SearchView) menu.findItem(R.id.search).getActionView();
        search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                adapter.getFilter().filter(query.toLowerCase());
                return true;
            }

        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.sort_item:
                SharedPreferences.Editor e = preferences.edit();
                if (preferences.getString("products_sort", ProductsTable.COLUMN_ID).equals(ProductsTable.COLUMN_ID)) {
                    e.putString("products_sort", ProductsTable.COLUMN_ITEM);
                } else {
                    e.putString("products_sort", ProductsTable.COLUMN_ID);
                }
                e.apply();
                getLoaderManager().restartLoader(0, null, this);
                return true;
            case R.id.add_item:
                DialogFragment df = new AddUpdateProductsDialog();
                Bundle args = new Bundle();
                args.putString("title", getString(R.string.add_item_hint));
                df.setArguments(args);
                df.show(getFragmentManager(), "addDialog");
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_products);

        preferences = getSharedPreferences("sort_prefs", MODE_PRIVATE);

        getLoaderManager().initLoader(0, null, this);

        ActionBar bar = getActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        final ListView productsList = (ListView) findViewById(R.id.productsList);

        adapter = new ProductsCursorAdapter(this, null, 0);
        adapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence charSequence) {
                c = getContentResolver().query(ShoppingListContentProvider.CONTENT_URI_PRODUCTS, null, ProductsTable.COLUMN_ITEM + " like " + '"' + "%" + charSequence.toString().toLowerCase() + "%" + '"', null, null);
                return c;
            }
        });
        productsList.setAdapter(adapter);
        productsList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        productsList.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {
                actionMode.setTitle(getString(R.string.selected) + " " + productsList.getCheckedItemCount());
            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.context_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.itemDelete:
                        long[] id = productsList.getCheckedItemIds();
                        if (id.length != 0) {
                            for (long l : id) {
                                getContentResolver().delete(ShoppingListContentProvider.CONTENT_URI_PRODUCTS, ProductsTable.COLUMN_ID + "=" + l, null);
                            }
                        }
                        actionMode.finish();
                        return true;
                    case R.id.selectAll:
                        for (int i = adapter.getCount() - 1; i >= 0; i--) {
                            productsList.setItemChecked(i, true);
                        }
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {

            }
        });
        productsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                DialogFragment df = new AddUpdateProductsDialog();
                Bundle args = new Bundle();
                TextView tv = (TextView) view.findViewById(R.id.productsItem);
                args.putString("title", getString(R.string.update));
                args.putString("item", tv.getText().toString().toLowerCase());
                args.putLong("id", l);
                df.setArguments(args);
                df.show(getFragmentManager(), "updateDialog");
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this, ShoppingListContentProvider.CONTENT_URI_PRODUCTS, null, null, null, preferences.getString("products_sort", ProductsTable.COLUMN_ID));
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        adapter.swapCursor(null);
    }
}
