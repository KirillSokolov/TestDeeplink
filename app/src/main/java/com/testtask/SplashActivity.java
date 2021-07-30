package com.testtask;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.firestore.FirebaseFirestore;
import com.testtask.model.BrowserData;
import com.viksaa.sssplash.lib.activity.AwesomeSplash;
import com.viksaa.sssplash.lib.model.ConfigSplash;

import java.util.List;

import io.realm.Realm;

public class SplashActivity extends AwesomeSplash {
    Realm realm = Realm.getDefaultInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BrowserData browserData = realm.where(BrowserData.class).findFirst();

        if(browserData != null) {
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    @Override
    public void initSplash(ConfigSplash configSplash) {
        configSplash.initDefaults();
    }

    @Override
    public void animationsFinished() {
        BrowserData browserData = realm.where(BrowserData.class).findFirst();

        if(browserData == null) {

            final BrowserData browserData_default = new BrowserData();
            //Base url for test cookie
            browserData_default.setUrl("https://www.facebook.com/");

            FirebaseFirestore remoteDB = FirebaseFirestore.getInstance();
            remoteDB.collection("Test").get().addOnSuccessListener(queryDocumentSnapshots -> {
                List<RemoteTask> remoteTaskList = queryDocumentSnapshots.toObjects(RemoteTask.class);

                browserData_default.setDomain(remoteTaskList.get(0).domain);
                browserData_default.setLand(remoteTaskList.get(0).land);
                browserData_default.setEnable(remoteTaskList.get(0).enable);
                browserData_default.setShowOboarding(remoteTaskList.get(0).showOnboarding);
                realm.beginTransaction();
                realm.copyToRealmOrUpdate(browserData_default);
                realm.commitTransaction();
                startActivity(new Intent(this, MainActivity.class));
            }).addOnFailureListener(e -> {
                browserData_default.setDomain("");
                browserData_default.setLand("");
                browserData_default.setEnable(false);
                browserData_default.setShowOboarding(false);
                browserData_default.setEditable(false);
                realm.beginTransaction();
                realm.copyToRealmOrUpdate(browserData_default);
                realm.commitTransaction();
                startActivity(new Intent(this, MainActivity.class));
            });
        }else {
            startActivity(new Intent(this, MainActivity.class));
        }

    }
}