package com.cypressworks.mensaplan;

import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @author Kirill Rakhman
 */
public class WebCamActivity extends AppCompatActivity {
    private static final String webcamURL = "https://www.sw-ka.de/de/essen/livecams/popup/?page=1";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webcam_activity);
        final WebView w = findViewById(R.id.webkitWebView1);
        w.getSettings().setUserAgentString(
                "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:13.0) Gecko/20100101 Firefox/13.0");
        w.loadUrl(webcamURL);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.drawable.icon_white);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return false;
    }
}
