package com.testtask.presenters;

import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;

import com.testtask.model.BrowserData;

import io.realm.Realm;


public class MainPresenterImpl {

    private Realm realm;

    public MainPresenterImpl() {
        realm = Realm.getDefaultInstance();
    }

    private String getLocal(Context context, Activity activity) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String countryCode = tm.getSimCountryIso();
        return countryCode;
    }

    public BrowserData getUrl() {
        return realm.where(BrowserData.class).findFirst();
    }

    public void save(BrowserData browserData) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(browserData);
        realm.commitTransaction();
    }
}
