package edu.ucla.csd.openwrtandroidclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.Iterator;

public class TrafficAnalysisActivity extends AppCompatActivity {
  private WebView webView;
  private Menu menu;
  private RequestQueue requestQueue;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_status_overview);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    requestQueue = Volley.newRequestQueue(this);

    webView = (WebView) findViewById(R.id.webView2);
    webView.setWebViewClient(new WebViewClient() {
      @Override
      public boolean shouldOverrideUrlLoading(WebView view, String url) {
        return false;
      }
    });
    webView.getSettings().setJavaScriptEnabled(true);

    if(!Constants.LAST_REQUEST_URL.contains(";stok")) {
      webView.loadData("<h1>Please log in first</h1>", "text/html", "UTF-8");
    }
    else {
      webView.loadUrl("http://" + Constants.SERVER_IP + ":8000/output.txt");
      StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://" + Constants.SERVER_IP + ":8000/output.txt",
        new Response.Listener<String>() {
          @Override
          public void onResponse(String response) {
            try {
              StringBuilder display = new StringBuilder();
              JSONObject responseJson = new JSONObject(response);
              // the first level keywords
              JSONObject outgoingJson = responseJson.getJSONObject("outgoing");
              JSONObject incomingJson = responseJson.getJSONObject("incoming");
              display.append("<h2>Outgoing Traffic</h2>");
              // second level keywords are service names (e.g. Google, Facebook)
              Iterator<String> outgoingIterator = outgoingJson.keys();
              while(outgoingIterator.hasNext()) {
                String key = outgoingIterator.next();
                display.append("<h3>").append(key).append("</h3>");
                display.append("<table style=\"width:100%\">");
                display.append("<tr><td>src IP address</td><td>Traffic (Bytes)</td></tr>");
                JSONObject ipAndBytes = outgoingJson.getJSONObject(key);
                Iterator<String> ipAndBytesIterator = ipAndBytes.keys();
                while(ipAndBytesIterator.hasNext()) {
                  String ip = ipAndBytesIterator.next();
                  int count = ipAndBytes.getInt(ip);
                  display.append("<tr><td>").append(ip).append("</td><td>").append(count).append("</td></tr>");
                }
                display.append("</table>");
              }

              display.append("<h2>Incoming Traffic</h2>");
              Iterator<String> incomingIterator = incomingJson.keys();
              while(incomingIterator.hasNext()) {
                String key = incomingIterator.next();
                display.append("<h3>").append(key).append("</h3>");
                display.append("<table style=\"width:100%\">");
                display.append("<tr><td>src IP address</td><td>Traffic (Bytes)</td></tr>");
                JSONObject ipAndBytes = outgoingJson.getJSONObject(key);
                Iterator<String> ipAndBytesIterator = ipAndBytes.keys();
                while(ipAndBytesIterator.hasNext()) {
                  String ip = ipAndBytesIterator.next();
                  int count = ipAndBytes.getInt(ip);
                  display.append("<tr><td>").append(ip).append("</td><td>").append(count).append("</td></tr>");
                }
                display.append("</table>");
              }
              // third level keywords are

              webView.loadData(display.toString(), "text/html", "UTF-8");
            } catch (Exception e) {
              Log.e(Constants.DEBUG_TAG, e.getMessage());
            }
          }
        },
        new Response.ErrorListener() {
          @Override
          public void onErrorResponse(VolleyError error) {
            if (error.networkResponse != null) {
              String toastString = "Error code: " + error.networkResponse.statusCode;
              Log.d(Constants.DEBUG_TAG, toastString);
            }
          }
        });

      // Add the request to the RequestQueue.
      requestQueue.add(stringRequest);
    }
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

    if(title.equals(Constants.TRAFFIC)) {

    } else if(title.equals(Constants.STATUS_AND_CONFIGURATION)) {
      try {
        Intent intent = new Intent(TrafficAnalysisActivity.this, StatusOverviewActivity.class);

        intent.putExtra(Constants.SERVER_URL, Constants.LAST_REQUEST_URL);
        startActivity(intent);

      } catch (Exception e) {
        Log.e(Constants.DEBUG_TAG, e.getMessage());
      }
    }
  }
}
