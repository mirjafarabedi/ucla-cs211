package edu.ucla.csd.openwrtandroidclient;

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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class LoginActivity extends AppCompatActivity {
    public String debugLogTag = "debug";
    public final String scriptPath = "/cgi-bin/luci";
    public final String protocol = "http://";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    private void sendHttpRequest(String url, final String username, final String password) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        // for passing into the new Response.Listener
        Log.e(debugLogTag, url);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(debugLogTag, "Got response!");
                        try {
                            Log.d(debugLogTag, response);
                            WebView webview = (WebView) findViewById(R.id.webView);
                            webview.getSettings().setJavaScriptEnabled(true);
                            webview.loadData(response, "text/html", "UTF-8");

                        } catch (Exception e) {
                            Log.d(debugLogTag, e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null) {
                            String toastString = "Error code: " + error.networkResponse.statusCode;
                            Toast.makeText(getApplicationContext(), toastString, Toast.LENGTH_LONG).show();
                            Log.e(debugLogTag, toastString);
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
            sendHttpRequest(protocol + ipAddress + ":" + portNumber + scriptPath, username, password);
        } catch (Exception e) {
            Log.e(debugLogTag, e.getMessage());
        }
    }
}
