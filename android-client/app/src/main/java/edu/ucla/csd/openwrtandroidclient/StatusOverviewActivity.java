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

        WebView webView = (WebView) findViewById(R.id.webView2);

        webView.setWebViewClient(new WebViewClient());

        if (htmlString != "") {
            webView.getSettings().setJavaScriptEnabled(true);
            webView.loadData(removeMenu(htmlString), "text/html", "UTF-8");
        }

        //sendHttpGetRequest(this.remoteAddress + "/cgi-bin/luci/;stok=" + this.urlToken + "/admin/status/iptables");
    }

    public String removeMenu(String html) {
        Document doc = Jsoup.parse(html);
        Elements menu = doc.select("ul");
        for (Element element : menu) {
            element.remove();
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
        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    WebView webview = (WebView) findViewById(R.id.webView2);
                    webview.getSettings().setJavaScriptEnabled(true);
                    webview.loadData(removeMenu(response), "text/html", "UTF-8");
                } catch (Exception e) {
                    Log.d(Constants.DEBUG_TAG, e.getMessage());
                }
            }
        };

        if (title == Constants.STATUS) {
            NetworkRequest.sendHttpGetRequest(this.remoteAddress + NetworkRequest.scriptPath + "/;stok=" + this.urlToken + "/admin/status/overview", listener);
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
