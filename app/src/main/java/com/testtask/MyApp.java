package com.testtask;

import android.app.Application;

import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.appsflyer.AppsFlyerLib;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.onesignal.OneSignal;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MyApp extends Application {
    private static GoogleAnalytics sAnalytics;
    private static Tracker sTracker;
    InstallReferrerClient referrerClient;

    @Override
    public void onCreate() {
        super.onCreate();
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        sAnalytics = GoogleAnalytics.getInstance(this);

        AppsFlyerLib.getInstance().start(this.getApplicationContext());


        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().name("typeCode.realm").build();
        Realm.setDefaultConfiguration(config);

        referrerClient = InstallReferrerClient.newBuilder(this).build();

        referrerClient.startConnection(new InstallReferrerStateListener() {
            @Override
            public void onInstallReferrerSetupFinished(int responseCode) {
                switch (responseCode) {
                    case InstallReferrerClient.InstallReferrerResponse.OK:
                        // Connection established.
                        break;
                    case InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED:
                        // API not available on the current Play Store app.
                        break;
                    case InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE:
                        // Connection couldn't be established.
                        break;
                }
            }

            @Override
            public void onInstallReferrerServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        });


    }

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     *
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
        if (sTracker == null) {
            // sTracker = sAnalytics.newTracker(R.xml.global_tracker);
        }

        return sTracker;
    }

}
