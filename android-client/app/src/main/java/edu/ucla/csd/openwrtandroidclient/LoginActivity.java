package edu.ucla.csd.openwrtandroidclient;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

//  @Override
//  public boolean onCreateOptionsMenu(Menu menu) {
//    // Inflate the menu; this adds items to the action bar if it is present.
//    getMenuInflater().inflate(R.menu.menu_main, menu);
//    return true;
//  }

  public void loginButtonClick(View view) {
    EditText ipAddressText = (EditText) findViewById(R.id.ipAddressText);
    EditText portNumberText = (EditText) findViewById(R.id.portNumberText);
    String ipAddress = ipAddressText.getText().toString();
    String portNumber = portNumberText.getText().toString();
    Constants.SERVER_IP = ipAddress;

    try {
      Intent intent = new Intent(LoginActivity.this, StatusOverviewActivity.class);

      intent.putExtra(Constants.SERVER_URL, NetworkRequest.protocol + ipAddress + ":" + portNumber);
      startActivity(intent);

    } catch (Exception e) {
      Log.e(Constants.DEBUG_TAG, e.getMessage());
    }
  }
}
