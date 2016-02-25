package edu.ucla.csd.openwrtandroidclient;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.Toast;

import java.net.InetAddress;
import java.net.UnknownHostException;
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
import org.jsoup.select.Elements;

public class LoginActivity extends AppCompatActivity {
    public final static String DEBUG_TAG = "debug";
    public final String scriptPath = "/cgi-bin/luci";
    public final String protocol = "http://";

    public final static String TOKEN_PARAM = "urlToken";
    public final static String HTML_PARAM = "htmlString";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    private void authenticateHttpRequest(String url, final String username, final String password) {
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
                            Element link = doc.select("a").first();
                            // First "a" element should look like
                            // /cgi-bin/luci/;stok=c58e72d3f7659b2230feb90170d1e053/admin/status
                            if (link != null) {
                                String firstLinkUrl = link.attr("href");
                                Pattern urlPattern = Pattern.compile("[^=]*([^/]*).*");
                                Matcher matcher = urlPattern.matcher(firstLinkUrl);
                                if (matcher.find()) {
                                    // We start the status overview activity after successful login
                                    Intent intent = new Intent(LoginActivity.this, StatusOverviewActivity.class);
                                    intent.putExtra(TOKEN_PARAM, matcher.group(1));
                                    intent.putExtra(HTML_PARAM, response);
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
            authenticateHttpRequest(protocol + ipAddress + ":" + portNumber + scriptPath, username, password);
        } catch (Exception e) {
            Log.e(DEBUG_TAG, e.getMessage());
        }
    }
}
