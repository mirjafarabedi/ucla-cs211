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

import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
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
    Response.Listener<String> overviewUpdateListener;
    Response.Listener<String> networkUpdateListener;
    Response.Listener<String> overviewListener;
    Response.Listener<String> networkListener;
    Response.Listener<String> listener;

    private Menu menu;
    private String viewCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_overview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        htmlString = intent.getStringExtra(LoginActivity.HTML_PARAM);
        urlToken = intent.getStringExtra(LoginActivity.TOKEN_PARAM);
        remoteAddress = intent.getStringExtra(LoginActivity.REMOTE_PARAM);

        webView = (WebView) findViewById(R.id.webView2);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(false);

        overviewUpdateListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.e("debug", "got response: " + response);
                    htmlString = fillInOverviewDetails(htmlString, response);
                    webView.loadData(htmlString, "text/html", "UTF-8");
                    Log.e("debug", "webview updated!");
                } catch (Exception e) {
                    Log.d(Constants.DEBUG_TAG, e.getMessage());
                }
            }
        };

        networkUpdateListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    htmlString = fillInNetworkDetails(htmlString, response);
                    webView.loadData(htmlString, "text/html", "UTF-8");
                } catch (Exception e) {
                    Log.d(Constants.DEBUG_TAG, e.getMessage());
                }
            }
        };

        networkListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    // JS doesn't help us in this case
                    htmlString = removeMenu(response);
                    webView.loadData(htmlString, "text/html", "UTF-8");

                    NetworkRequest.sendHttpGetRequest(remoteAddress + NetworkRequest.scriptPath + "/;stok=" + urlToken + "/admin/network/iface_status/lan,wan,wan6", networkUpdateListener);
                } catch (Exception e) {
                    Log.d(Constants.DEBUG_TAG, e.getMessage());
                }
            }
        };

        overviewListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    htmlString = removeMenu(response);
                    //webView.loadData(htmlString, "text/html", "UTF-8");
                    getMenuInflater().inflate(R.menu.menu_status, menu);
                    invalidateOptionsMenu();

                    HashMap<String, String> params = new HashMap<>();
                    params.put("status", "1");
                    NetworkRequest.sendHttpPostRequest(remoteAddress + NetworkRequest.scriptPath + "/;stok=" + urlToken, overviewUpdateListener, params);
                } catch (Exception e) {
                    Log.d(Constants.DEBUG_TAG, e.getMessage());
                }
            }
        };

        listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    htmlString = removeMenu(response);
                    webView.loadData(htmlString, "text/html", "UTF-8");
                } catch (Exception e) {
                    Log.d(Constants.DEBUG_TAG, e.getMessage());
                }
            }
        };

        if (htmlString != "") {
            htmlString = removeMenu(htmlString);
            // Disabled temporarily since it seems that loadData can happen out of order; even when response of GET triggers the POST request
            //webView.loadData(htmlString, "text/html", "UTF-8");

            HashMap<String, String> params = new HashMap<>();
            params.put("status", "1");
            NetworkRequest.sendHttpPostRequest(remoteAddress + NetworkRequest.scriptPath + "/;stok=" + urlToken, overviewUpdateListener, params);
        } else {
            Log.e(Constants.DEBUG_TAG, "Unexpected html string when opening Overview activity");
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        /*
        if (viewCategory == Constants.STATUS) {
            getMenuInflater().inflate(R.menu.menu_status, menu);
            this.menu = menu;
        } else {
            getMenuInflater().inflate(R.menu.menu_default, menu);
            this.menu = menu;
        }
        */
        Log.d("debug", "prepare option menu");
        return super.onPrepareOptionsMenu(menu);
    }

    public String removeMenu(String html) {
        Document doc = Jsoup.parse(html);
        try {
            doc.select("header").first().remove();
        } catch (NullPointerException exception) {
            Log.e(Constants.DEBUG_TAG, "Header already does not exist");
        }
        return doc.html();
    }

    public String fillInOverviewDetails(String html, String jsonString) {
        Document doc = Jsoup.parse(html);
        try {
            JSONObject responseObject = new JSONObject(jsonString);
            doc.getElementById("memtotal").html(Integer.toString(responseObject.getInt("memtotal")));
            doc.getElementById("memfree").html(Integer.toString(responseObject.getInt("memfree")));
            doc.getElementById("memcache").html(Integer.toString(responseObject.getInt("memcached")));
            doc.getElementById("membuff").html(Integer.toString(responseObject.getInt("membuffers")));

            doc.getElementById("uptime").html(Integer.toString(responseObject.getInt("uptime")));
            doc.getElementById("localtime").html(responseObject.getString("localtime"));
            doc.getElementById("loadavg").html(responseObject.getJSONArray("loadavg").toString());

            JSONObject object = responseObject.getJSONObject("wan");
            String interfaceHtmlString = "<strong>Type: </strong>" + object.getString("proto") + "<br>" +
                    "<strong>Address: </strong>" + object.getString("ipaddr") + "<br>" +
                    "<strong>Netmask: </strong>" + object.getString("netmask") + "<br>" +
                    "<strong>Gateway: </strong>" + object.getString("gwaddr") + "<br>" +
                    "<strong>Uptime: </strong>" + object.getInt("uptime") + "<br>";
            JSONArray dnsArray = object.getJSONArray("dns");
            for (int j = 0; j < dnsArray.length(); j++) {
                interfaceHtmlString += "<strong>DNS" + (j+1) + ": </strong>" + dnsArray.getString(j) + "<br>";
            }
            doc.getElementById("wan4_s").html(interfaceHtmlString);
        } catch (JSONException exception) {
            Log.e(Constants.DEBUG_TAG, exception.getMessage());
        }
        return doc.html();
    }

    public String fillInNetworkDetails(String html, String jsonString) {
        Document doc = Jsoup.parse(html);
        try {
            JSONArray responseArray = new JSONArray(jsonString);

            for (int i = 0; i < responseArray.length(); i++) {
                JSONObject object = responseArray.getJSONObject(i);
                String interfaceHtmlString = "<strong>Uptime: </strong>" + object.getInt("uptime") + "<br>" +
                        "<strong>Mac-address: </strong>" + object.getString("macaddr") + "<br>" +
                        "<strong>RX: </strong>" + object.getInt("rx_bytes") + "B<br>" +
                        "<strong>TX: </strong>" + object.getInt("tx_bytes") + "B<br>" +
                        "<strong>IPv4: </strong>";
                try {
                    JSONArray ipv4Array = object.getJSONArray("ipaddrs");
                    JSONArray ipv6Array = object.getJSONArray("ip6addrs");
                    for (int j = 0; j < ipv4Array.length(); j++) {
                        interfaceHtmlString += ipv4Array.getJSONObject(j).getString("addr") + "/" + ipv4Array.getJSONObject(j).getInt("prefix") + "<br>";
                    }
                    interfaceHtmlString += "<strong>IPv6: </strong>";
                    for (int j = 0; j < ipv6Array.length(); j++) {
                        interfaceHtmlString += ipv6Array.getJSONObject(j).getString("addr") + "/" + ipv6Array.getJSONObject(j).getInt("prefix") + "<br>";
                    }
                    interfaceHtmlString += "<br>";
                } catch (JSONException exception) {
                    Log.e(Constants.DEBUG_TAG, "IP address parsing exception: " + exception.getMessage());
                }

                doc.getElementById(object.getString("id") + "-ifc-description").html(interfaceHtmlString);
            }
        } catch (JSONException exception) {
            Log.e(Constants.DEBUG_TAG, exception.getMessage());
        }
        return doc.html();
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

        if (title == Constants.STATUS) {
            NetworkRequest.sendHttpGetRequest(this.remoteAddress + NetworkRequest.scriptPath + "/;stok=" + this.urlToken + "/admin/status/overview", overviewListener);
            viewCategory = Constants.STATUS;
        } else if (title == Constants.NETWORK) {
            NetworkRequest.sendHttpGetRequest(this.remoteAddress + NetworkRequest.scriptPath + "/;stok=" + this.urlToken + "/admin/network/network", networkListener);
            viewCategory = Constants.NETWORK;
        } else if (title == Constants.SYSTEM) {
            NetworkRequest.sendHttpGetRequest(this.remoteAddress + NetworkRequest.scriptPath + "/;stok=" + this.urlToken + "/admin/system/system", listener);
            viewCategory = Constants.SYSTEM;
        } else if (title == Constants.LOGOUT) {
            NetworkRequest.sendHttpGetRequest(this.remoteAddress + NetworkRequest.scriptPath + "/;stok=" + this.urlToken + "/admin/logout", listener);
            viewCategory = Constants.LOGOUT;
        } else if (title == Constants.FIREWALL) {
            NetworkRequest.sendHttpGetRequest(this.remoteAddress + NetworkRequest.scriptPath + "/;stok=" + this.urlToken + "/admin/status/iptables", listener);
        } else if (title == Constants.ROUTES) {
            NetworkRequest.sendHttpGetRequest(this.remoteAddress + NetworkRequest.scriptPath + "/;stok=" + this.urlToken + "/admin/status/routes", listener);
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
