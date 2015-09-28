package com.cypressworks.mensaplan;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.webkit.WebView;

/**
 * @author Kirill Rakhman
 */
public class WebCamActivity extends ActionBarActivity {
    private static final String webcamURL = "http://www.studentenwerk-karlsruhe.de/de/essen/livecams/popup/?page=1";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webcam_activity);
        final WebView w = (WebView) findViewById(R.id.webkitWebView1);
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
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return false;
    }
}
