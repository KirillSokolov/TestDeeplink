package com.testtask;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.appsflyer.AppsFlyerProperties;
import com.facebook.applinks.AppLinkData;
import com.framgia.android.emulator.EmulatorDetector;
import com.testtask.model.BrowserData;
import com.testtask.presenters.MainPresenterImpl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import bolts.AppLinks;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.webView)
    WebView webView;
    private Handler handler;
    private URL lastUrl = null;
    private String noSmart = "&no-smart=1";

    private MainPresenterImpl presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cite);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        handler = new Handler();
        ButterKnife.bind(this);
        AppLinkData appLinkData = AppLinkData.createFromActivity(this);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);

        }

        presenter = new MainPresenterImpl();

        BrowserData browserData = presenter.getUrl();

        if (!browserData.getEnable()) {
            webView.setVisibility(View.INVISIBLE);
        } else if (!browserData.getEditable()) {
            showResult(browserData);
        } else {
            getUrl(browserData);
        }
    }

    private void getUrl(BrowserData browserData) {
        Intent appLinkIntent = getIntent();
        final Uri appLinkDat = appLinkIntent.getData();
        final Uri targetUrl = new AppLinks().getTargetUrlFromInboundIntent(this, getIntent());
        if (targetUrl != null || appLinkDat != null) {
            EmulatorDetector.with(this)
                    .setCheckTelephony(true)
                    .addPackageName("com.bluestacks")
                    .setDebug(true)
                    .detect(isEmulator -> {
                        if (!isEmulator) {
                            String sub_id_1 = getKey(appLinkDat, targetUrl, "sub1");
                            String deviceID = getKey(appLinkDat, targetUrl, "deviceID");
                            String adID = getKey(appLinkDat, targetUrl, "adID");
                            AppsFlyerProperties appsFlyerProperties = AppsFlyerProperties.getInstance();
                            String af_status = appsFlyerProperties.getString("type");
                            browserData.setEditable(false);
                            browserData.setUrl("https://" + "key_domen" + "/" + "?deviceID=" + deviceID + "&sub1=" + sub_id_1 + "&ad_id=" + adID + "&ad_id=" + "&type=" + af_status + "&land" + "key_land");
                            presenter.save(browserData);
                            showResult(browserData);
                        } else {
                            webView.setVisibility(View.INVISIBLE);
                        }
                    });
        } else {
            webView.setVisibility(View.INVISIBLE);
        }
    }

    private String getKey(Uri appLinkDat, Uri targetUrl, String key) {
        if (targetUrl != null && targetUrl.getQueryParameter(key) != null)
            return targetUrl.getQueryParameter(key);
        else if (appLinkDat != null && appLinkDat.getQueryParameter(key) == null)
            return appLinkDat.getQueryParameter(key);
        else
            return "";
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void openBrowser(String url) {
        handler.post(() -> {
            webView.setVisibility(View.VISIBLE);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setAppCacheEnabled(true);
            webView.getSettings().setGeolocationEnabled(true);
            webView.getSettings().setDomStorageEnabled(true);
            webView.getSettings().setUserAgentString(webView.getSettings().getUserAgentString() + "MobileAppClient/Android/0.9");
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);

            // указываем страницу загрузки
            webView.loadUrl(url);
            webView.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onReceivedTitle(WebView view, String title) {
                    getWindow().setTitle(title); //Set Activity tile to page title.
                }
            });
            webView.setWebViewClient(new WebViewClient() {

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    String redirect = request.getUrl().toString();
                    if (redirect.startsWith("mailto")) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("plain/text");
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{redirect.replace("mailto:", "")});
                        startActivity(Intent.createChooser(intent, "Mail to Support"));
                    } else if (redirect.startsWith("tel:")) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse(redirect));
                        startActivity(intent);
                    } else if (redirect.startsWith("https://t.me/joinchat")) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(redirect));
                        startActivity(intent);
                    } else {
                        Map headers = new HashMap<>();
                        if (lastUrl != null) {
                        //    headers.put("Referer", lastUrl);
                        }
                        view.loadUrl(request.getUrl().toString() + noSmart , headers);
                        try {
                            lastUrl = new URL(redirect);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }

                    }
                    return true;

                }

                @Override
                public void onPageFinished(WebView view, String url1) {
                    super.onPageFinished(view, url1);
                    CookieManager.getInstance().flush();
                   // syncCookie(url1);
                }
            });
        });
    }

    private void syncCookie(String url) {
        if(url == null)
            return;
        String cookieKey = "remember_me";
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);

        String[] cookies = cookieManager.getCookie(url).split(";");
        String hash = "";

        if (cookieManager.getCookie(url).contains(cookieKey)) {
            for (String cookie : cookies) {
                if (cookie.contains(cookieKey)){
                   //Todo: get hash
                   // hash = cookie;
                }
            }
        }
        try {
            URL uri = new URL(url);
            cookieManager.setCookie(uri.getHost(), "${"+cookieKey+"}=$"+hash);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


    }


    public void showResult(BrowserData browserData) {
        openBrowser(browserData.getUrl().replace("key_domen", browserData.getDomain() ).replace("key_land", browserData.getLand()) + noSmart);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

}