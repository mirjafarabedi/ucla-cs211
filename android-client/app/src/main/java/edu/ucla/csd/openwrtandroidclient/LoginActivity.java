package edu.ucla.csd.openwrtandroidclient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.Toast;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class LoginActivity extends AppCompatActivity {
    public final static String DEBUG_TAG = "debug";
    public final static String scriptPath = "/cgi-bin/luci";
    public final static String protocol = "http://";

    public final static String TOKEN_PARAM = "urlToken";
    public final static String HTML_PARAM = "htmlString";
    public final static String REMOTE_PARAM = "remoteParam";

    private static final String SET_COOKIE_KEY = "Set-Cookie";
    private static final String COOKIE_KEY = "Cookie";
    private static final String SESSION_COOKIE = "sessionid";

    public CookieManager cookieManager;

    private static LoginActivity _instance;

    public static LoginActivity get() {
        return _instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (CookieHandler.getDefault() == null) {
            cookieManager = new CookieManager();
            CookieHandler.setDefault(cookieManager);
            Log.d(DEBUG_TAG, "Creating new cookie manager and setting as default");
        } else {
            Log.d(DEBUG_TAG, "Cookie handler has a default cookie manager");
        }
        _instance = this;
    }

    private void authenticateHttpRequest(String url, final String username, final String password, final String remoteAddress) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        // for passing into the new Response.Listener
        Log.e(DEBUG_TAG, url);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(DEBUG_TAG, "Got response!");
                        try {
                            Log.d(DEBUG_TAG, response);
                            WebView webview = (WebView) findViewById(R.id.webView);
                            webview.getSettings().setJavaScriptEnabled(true);
                            webview.loadData(response, "text/html", "UTF-8");

                            // Extra token from the authentication response html string
                            Document doc = Jsoup.parse(response);
                            // check exception for index
                            Element link = doc.select("a").get(1);
                            // First "a" element should look like
                            // /cgi-bin/luci/;stok=c58e72d3f7659b2230feb90170d1e053/admin/status
                            if (link != null) {
                                String firstLinkUrl = link.attr("href");

                                Pattern urlPattern = Pattern.compile("[^=]*=([^/]*).*");
                                Matcher matcher = urlPattern.matcher(firstLinkUrl);
                                if (matcher.find()) {
                                    // We start the status overview activity after successful login
                                    Intent intent = new Intent(LoginActivity.this, StatusOverviewActivity.class);
                                    Log.d("matcher", matcher.group(1));

                                    intent.putExtra(TOKEN_PARAM, matcher.group(1));
                                    intent.putExtra(HTML_PARAM, response);
                                    intent.putExtra(REMOTE_PARAM, remoteAddress);
                                    startActivity(intent);
                                } else {
                                    // this should at least be updated to handle authentication failure!
                                    Log.d(DEBUG_TAG, "Unexpected html response");
                                }
                            }
                        } catch (Exception e) {
                            Log.d(DEBUG_TAG, e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null) {
                            String toastString = "Error code: " + error.networkResponse.statusCode;
                            Toast.makeText(getApplicationContext(), toastString, Toast.LENGTH_LONG).show();
                            Log.e(DEBUG_TAG, toastString);
                        }
                    }
                }) {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("password", password);

                return params;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void loginButtonClick(View view) {
        EditText ipAddressText = (EditText) findViewById(R.id.ipAddressText);
        EditText portNumberText = (EditText) findViewById(R.id.portNumberText);
        EditText usernameText = (EditText) findViewById(R.id.usernameText);
        EditText passwordText = (EditText) findViewById(R.id.passwordText);

        String username = usernameText.getText().toString();
        String password = passwordText.getText().toString();
        String ipAddress = ipAddressText.getText().toString();
        String portNumber = portNumberText.getText().toString();

        try {
            authenticateHttpRequest(protocol + ipAddress + ":" + portNumber + scriptPath, username, password, protocol + ipAddress + ":" + portNumber);
        } catch (Exception e) {
            Log.e(DEBUG_TAG, e.getMessage());
        }
    }
}
