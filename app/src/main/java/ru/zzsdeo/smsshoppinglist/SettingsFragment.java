package ru.zzsdeo.smsshoppinglist;

import android.app.DialogFragment;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.DialogPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.provider.SyncStateContract;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Patterns;

import java.util.regex.Pattern;

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
                new AllProductsInListAsyncTask(getActivity()).execute();
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
        Preference backgroundTransparency = findPreference("background_transparency");
        backgroundTransparency.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                DialogFragment df = new SeekBarTransparencyDialog();
                df.show(getFragmentManager(), "seekBarTransparencyDialog");
                return true;
            }
        });
        Preference fontSize = findPreference("font_size");
        fontSize.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                DialogFragment df = new SeekBarDialog();
                df.show(getFragmentManager(), "seekBarDialog");
                return true;
            }
        });
        EditTextPreference emailPreference = (EditTextPreference) findPreference("email");
        emailPreference.setSummary(emailPreference.getText() + "\n" + getString(R.string.email_setting_summary));
        emailPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                String email = o.toString();
                boolean matches = Patterns.EMAIL_ADDRESS.matcher(email).matches();
                if (!matches & !email.equals("")) {
                    Spannable summary = new SpannableString("'" + o.toString() + "'" + "\n" + getString(R.string.email_setting_error));
                    summary.setSpan(new ForegroundColorSpan(Color.RED), 0, summary.length(), 0);
                    preference.setSummary(summary);
                    return false;
                } else {
                    preference.setSummary(o.toString() + "\n" + getString(R.string.email_setting_summary));
                    return true;
                }
            }
        });
    }
}
