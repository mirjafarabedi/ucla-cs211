package edu.ucla.csd.openwrtandroidclient;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public final static String TOKEN_PARAM = "urlToken";
    public final static String HTML_PARAM = "htmlString";
    public final static String REMOTE_PARAM = "remoteParam";

    public CookieManager cookieManager;

    private static LoginActivity _instance;
    private RequestQueue requestQueue;

    public static LoginActivity get() {
        return _instance;
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (CookieHandler.getDefault() == null) {
            cookieManager = new CookieManager();
            CookieHandler.setDefault(cookieManager);
            Log.d(Constants.DEBUG_TAG, "Creating new cookie manager and setting as default");
        } else {
            Log.d(Constants.DEBUG_TAG, "Cookie handler has a default cookie manager");
        }
        requestQueue = Volley.newRequestQueue(this);
        _instance = this;

        Constants.loadMenuStrings();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void authenticateHttpRequest(String url, final String username, final String password, final String remoteAddress) {
        Log.d(Constants.DEBUG_TAG, url);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(Constants.DEBUG_TAG, "Got response!");
                        try {
                            //WebView webview = (WebView) findViewById(R.id.webView);
                            //webview.getSettings().setJavaScriptEnabled(true);
                            //webview.loadData(response, "text/html", "UTF-8");

                            // Extra token from the authentication response html string
                            Document doc = Jsoup.parse(response);
                            // check exception for index
                            Element link = doc.select("a").get(1);
                            // Second "a" element should look like
                            // /cgi-bin/luci/;stok=c58e72d3f7659b2230feb90170d1e053/admin/status
                            if (link != null) {
                                String firstLinkUrl = link.attr("href");

                                Pattern urlPattern = Pattern.compile("[^=]*=([^/]*).*");
                                Matcher matcher = urlPattern.matcher(firstLinkUrl);
                                if (matcher.find()) {
                                    // We start the status overview activity after successful login
                                    Intent intent = new Intent(LoginActivity.this, StatusOverviewActivity.class);

                                    intent.putExtra(TOKEN_PARAM, matcher.group(1));
                                    intent.putExtra(HTML_PARAM, response);
                                    intent.putExtra(REMOTE_PARAM, remoteAddress);
                                    startActivity(intent);
                                } else {
                                    // this should at least be updated to handle authentication failure!
                                    Log.d(Constants.DEBUG_TAG, "Unexpected html response");
                                }
                            }
                        } catch (Exception e) {
                            Log.d(Constants.DEBUG_TAG, e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null) {
                            String toastString = "Error code: " + error.networkResponse.statusCode;
                            Toast.makeText(getApplicationContext(), toastString, Toast.LENGTH_LONG).show();
                            Log.e(Constants.DEBUG_TAG, toastString);
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
        requestQueue.add(stringRequest);
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
            authenticateHttpRequest(NetworkRequest.protocol + ipAddress + ":" + portNumber + NetworkRequest.scriptPath, username, password, NetworkRequest.protocol + ipAddress + ":" + portNumber);
        } catch (Exception e) {
            Log.e(Constants.DEBUG_TAG, e.getMessage());
        }
    }
}
