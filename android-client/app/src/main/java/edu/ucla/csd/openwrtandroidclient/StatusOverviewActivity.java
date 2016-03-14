package edu.ucla.csd.openwrtandroidclient;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class StatusOverviewActivity extends AppCompatActivity {
  private String server = "";

  private WebView webView;

  private Menu menu;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_status_overview);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    Intent intent = getIntent();
    server = intent.getStringExtra(Constants.SERVER_URL);

    webView = (WebView) findViewById(R.id.webView2);
    webView.setWebViewClient(new WebViewClient() {
      @Override
      public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Log.d(Constants.DEBUG_TAG, url);
        Constants.LAST_REQUEST_URL = url;
        return false;
      }
    });
    webView.getSettings().setJavaScriptEnabled(true);
    webView.loadUrl(server);
//
//        if (htmlString != "") {
//            htmlString = removeMenu(htmlString);
//            // Disabled temporarily since it seems that loadData can happen out of order; even when response of GET triggers the POST request
//            //webView.loadData(htmlString, "text/html", "UTF-8");
//
//            HashMap<String, String> params = new HashMap<>();
//            params.put("status", "1");
//            webView.loadUrl(remoteAddress + NetworkRequest.scriptPath + "/;stok=" + urlToken);
////            NetworkRequest.sendHttpPostRequest(remoteAddress + NetworkRequest.scriptPath + "/;stok=" + urlToken, overviewUpdateListener, params);
//        } else {
//            Log.e(Constants.DEBUG_TAG, "Unexpected html string when opening Overview activity");
//        }
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    Log.d("debug", "prepare option menu");
    return super.onPrepareOptionsMenu(menu);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_default, menu);
    this.menu = menu;
    Log.d("debug", "create option menu");
    return true;
  }

  public void menuSwitch(MenuItem item) {
    String title = item.getTitle().toString();
    if (title.equals(Constants.TRAFFIC)) {
      try {
        Intent intent = new Intent(StatusOverviewActivity.this, TrafficAnalysisActivity.class);
        startActivity(intent);
      } catch (Exception e) {
        Log.e(Constants.DEBUG_TAG, e.getMessage());
      }
    } else if (title.equals(Constants.STATUS_AND_CONFIGURATION)) {

    }
  }
}
