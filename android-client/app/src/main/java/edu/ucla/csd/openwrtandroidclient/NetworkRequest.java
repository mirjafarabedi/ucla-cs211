package edu.ucla.csd.openwrtandroidclient;

import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

/**
 * Created by zhehaowang on 2/25/16.
 */
public class NetworkRequest {
    public final static String scriptPath = "/cgi-bin/luci";
    public final static String protocol = "http://";

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

    public static void sendHttpGetRequest(String url, Response.Listener<String> responseListener) {
        RequestQueue queue = LoginActivity.get().getRequestQueue();
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                responseListener,
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
        queue.add(stringRequest);
    }
}
