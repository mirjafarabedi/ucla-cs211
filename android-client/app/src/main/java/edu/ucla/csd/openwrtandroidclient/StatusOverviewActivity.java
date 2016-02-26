package edu.ucla.csd.openwrtandroidclient;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StatusOverviewActivity extends AppCompatActivity {
    private String urlToken = "";
    private String htmlString = "";
    private String remoteAddress = "";

    private WebView webView;
    Response.Listener<String> updateListener;
    Response.Listener<String> listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_overview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        this.htmlString = intent.getStringExtra(LoginActivity.HTML_PARAM);
        this.urlToken = intent.getStringExtra(LoginActivity.TOKEN_PARAM);
        this.remoteAddress = intent.getStringExtra(LoginActivity.REMOTE_PARAM);

        webView = (WebView) findViewById(R.id.webView2);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(false);

        if (htmlString != "") {
            webView.loadData(removeMenu(htmlString), "text/html", "UTF-8");

            updateListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.e("debug", response);
                        JSONObject responseObject = new JSONObject(response);
                        htmlString = removeMenu(fillInDetails(htmlString, responseObject));
                        webView.loadData(htmlString, "text/html", "UTF-8");
                    } catch (Exception e) {
                        Log.d(Constants.DEBUG_TAG, e.getMessage());
                    }
                }
            };

            listener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        // JS doesn't help us in this case
                        htmlString = removeMenu(response);
                        webView.loadData(htmlString, "text/html", "UTF-8");
                    } catch (Exception e) {
                        Log.d(Constants.DEBUG_TAG, e.getMessage());
                    }
                }
            };

            HashMap<String, String> params = new HashMap<>();
            params.put("status", "1");
            NetworkRequest.sendHttpPostRequest(this.remoteAddress + NetworkRequest.scriptPath + "/;stok=" + this.urlToken, updateListener, params);
        }

        //sendHttpGetRequest(this.remoteAddress + "/cgi-bin/luci/;stok=" + this.urlToken + "/admin/status/iptables");
    }

    public String removeMenu(String html) {
        Document doc = Jsoup.parse(html);
        try {
            doc.select("header").first().remove();
        } catch (NullPointerException exception) {
            Log.e("debug", "null pointer");
        }
        return doc.html();
    }

    public String fillInDetails(String html, JSONObject responseObject) {
        Document doc = Jsoup.parse(html);
        try {
            doc.getElementById("memtotal").html(Integer.toString(responseObject.getInt("memtotal")));
            doc.getElementById("memfree").html(Integer.toString(responseObject.getInt("memfree")));
            doc.getElementById("memcache").html(Integer.toString(responseObject.getInt("memcached")));
            doc.getElementById("membuff").html(Integer.toString(responseObject.getInt("membuffers")));

            doc.getElementById("uptime").html(Integer.toString(responseObject.getInt("uptime")));
            doc.getElementById("localtime").html(responseObject.getString("localtime"));
            doc.getElementById("loadavg").html(responseObject.getJSONArray("loadavg").toString());

            doc.getElementById("wan4_s").html(responseObject.getJSONObject("wan").toString());
        } catch (JSONException exception) {
            Log.e(Constants.DEBUG_TAG, exception.getMessage());
        }
        return doc.html();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_default, menu);
        return true;
    }

    public void menuSwitch(MenuItem item) {
        String title = item.getTitle().toString();

        if (title == Constants.STATUS) {
            NetworkRequest.sendHttpGetRequest(this.remoteAddress + NetworkRequest.scriptPath + "/;stok=" + this.urlToken + "/admin/status/overview", listener);
            HashMap<String, String> params = new HashMap<>();
            params.put("status", "1");
            NetworkRequest.sendHttpPostRequest(this.remoteAddress + NetworkRequest.scriptPath + "/;stok=" + this.urlToken, updateListener, params);
        } else if (title == Constants.NETWORK) {
            NetworkRequest.sendHttpGetRequest(this.remoteAddress + NetworkRequest.scriptPath + "/;stok=" + this.urlToken + "/admin/network/network", listener);
        } else if (title == Constants.SYSTEM) {
            NetworkRequest.sendHttpGetRequest(this.remoteAddress + NetworkRequest.scriptPath + "/;stok=" + this.urlToken + "/admin/system/system", listener);
        } else if (title == Constants.LOGOUT) {
            NetworkRequest.sendHttpGetRequest(this.remoteAddress + NetworkRequest.scriptPath + "/;stok=" + this.urlToken + "/admin/logout", listener);
        }
    }

    /*
    new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            try {
                WebView webview = (WebView) findViewById(R.id.webView2);
                webview.getSettings().setJavaScriptEnabled(true);
                webview.loadData(response, "text/html", "UTF-8");
            } catch (Exception e) {
                Log.d(Constants.DEBUG_TAG, e.getMessage());
            }
        }
    }
    */
}
