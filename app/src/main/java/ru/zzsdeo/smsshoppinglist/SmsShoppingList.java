package ru.zzsdeo.smsshoppinglist;

import android.app.Application;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

@ReportsCrashes (
        formKey = "", // This is required for backward compatibility but not used
        formUri = "http://acra.mamarada.su/smsShoppingList/acra.php",
        formUriBasicAuthLogin = "admin",
        formUriBasicAuthPassword = "yyuzypuv"
)

public class SmsShoppingList extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // The following line triggers the initialization of ACRA
        ACRA.init(this);
    }
}
