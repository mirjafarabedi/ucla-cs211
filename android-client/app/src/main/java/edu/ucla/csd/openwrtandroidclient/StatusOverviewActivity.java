package edu.ucla.csd.openwrtandroidclient;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StatusOverviewActivity extends AppCompatActivity {
    private String urlToken = "";
    private String htmlString = "";
    private String remoteAddress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_overview);

        Intent intent = getIntent();
        this.htmlString = intent.getStringExtra(LoginActivity.HTML_PARAM);
        this.urlToken = intent.getStringExtra(LoginActivity.TOKEN_PARAM);
        this.remoteAddress = intent.getStringExtra(LoginActivity.REMOTE_PARAM);

        sendHttpGetRequest(this.remoteAddress + "/cgi-bin/luci/;stok=" + this.urlToken + "/admin/status/iptables");
    }

    /**
     * List of get requests on the menu:
     * Status sub category:
     * /cgi-bin/luci/;stok=c58e72d3f7659b2230feb90170d1e053/admin/status
     * /cgi-bin/luci/;stok=c58e72d3f7659b2230feb90170d1e053/admin/status/overview
     * /cgi-bin/luci/;stok=c58e72d3f7659b2230feb90170d1e053/admin/status/iptables
     * ../routes
     * ../syslog
     * ../dmesg
     * ../processes
     * ../realtime
     *
     * System sub category:
     * /cgi-bin/luci/;stok=c58e72d3f7659b2230feb90170d1e053/admin/system
     * /cgi-bin/luci/;stok=c58e72d3f7659b2230feb90170d1e053/admin/system/system
     * /cgi-bin/luci/;stok=c58e72d3f7659b2230feb90170d1e053/admin/system/admin
     * ../packages
     * ../startup
     * ../crontab
     * ../leds
     * ../flashops
     * ../reboot
     *
     * Network sub category:
     * /cgi-bin/luci/;stok=c58e72d3f7659b2230feb90170d1e053/admin/network
     * /cgi-bin/luci/;stok=c58e72d3f7659b2230feb90170d1e053/admin/network/network
     * /cgi-bin/luci/;stok=c58e72d3f7659b2230feb90170d1e053/admin/network/dhcp
     * ../hosts
     * ../routes
     * ../diagnostics
     * ../firewall
     *
     * Logout:
     * /cgi-bin/luci/;stok=c58e72d3f7659b2230feb90170d1e053/admin/logout
     */

    private void sendHttpGetRequest(String url) {
        if (urlToken == "") {
            Log.d(LoginActivity.DEBUG_TAG, "Url token is emtpy!");
        }
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        // for passing into the new Response.Listener
        Log.e(LoginActivity.DEBUG_TAG, url);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(LoginActivity.DEBUG_TAG, "Got response!");
                        try {
                            Log.d(LoginActivity.DEBUG_TAG, response);
                            WebView webview = (WebView) findViewById(R.id.webView2);
                            webview.getSettings().setJavaScriptEnabled(true);
                            webview.loadData(response, "text/html", "UTF-8");
                        } catch (Exception e) {
                            Log.d(LoginActivity.DEBUG_TAG, e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null) {
                            String toastString = "Error code: " + error.networkResponse.statusCode;
                            Toast.makeText(getApplicationContext(), toastString, Toast.LENGTH_LONG).show();
                            Log.e(LoginActivity.DEBUG_TAG, toastString);
                        }
                    }
                });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}
