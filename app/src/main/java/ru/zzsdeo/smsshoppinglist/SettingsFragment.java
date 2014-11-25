package ru.zzsdeo.smsshoppinglist;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        Preference manageProducts = findPreference("manage_products");
        manageProducts.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent i = new Intent(getActivity(), ProductsActivity.class);
                startActivity(i);
                return true;
            }
        });

        Preference allProductsInList = findPreference("all_products_in_list");
        allProductsInList.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                getActivity().getContentResolver().delete(ShoppingListContentProvider.CONTENT_URI_LIST, null, null);
                Cursor c = getActivity().getContentResolver().query(ShoppingListContentProvider.CONTENT_URI_PRODUCTS, null, null, null, null);
                if (c.moveToFirst()) {
                    ContentValues values = new ContentValues();
                    do {
                        values.put(ListTable.COLUMN_ITEM, c.getString(c.getColumnIndex(ProductsTable.COLUMN_ITEM)));
                        values.put(ListTable.COLUMN_CHECKED, 0);
                        getActivity().getContentResolver().insert(ShoppingListContentProvider.CONTENT_URI_LIST, values);
                        values.clear();
                    } while (c.moveToNext());
                }
                getActivity().finish();
                return true;
            }
        });

        CheckBoxPreference smsParser = (CheckBoxPreference) findPreference("sms_parser");
        smsParser.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                PackageManager pm = getActivity().getPackageManager();
                ComponentName component = new ComponentName(getActivity(), SmsReceiver.class);
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());

                if (!settings.getBoolean("sms_parser", true)) {
                    pm.setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                }
                if ((settings.getBoolean("sms_parser", true)) & (pm.getComponentEnabledSetting(component) != PackageManager.COMPONENT_ENABLED_STATE_DEFAULT)) {
                    pm.setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT, PackageManager.DONT_KILL_APP);
                }
                return true;
            }
        });
    }
}
