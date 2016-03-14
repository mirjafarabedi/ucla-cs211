package edu.ucla.csd.openwrtandroidclient;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class NetworkRequest {
    public final static String scriptPath = "/cgi-bin/luci";
    public final static String protocol = "http://";

    /**
     * List of get requests on the menu (GET):
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

    /**
     * JSON API:
     *
     * GET: /cgi-bin/luci/;stok=9a023cdafe2ddef8e74ccd1ec8cfd5bf/admin/network/iface_status/lan,wan,wan6
     * POST: /cgi-bin/luci/;stok=c58e72d3f7659b2230feb90170d1e053/ {status=1} (Overview status)
     * GET /cgi-bin/luci/;stok=9a023cdafe2ddef8e74ccd1ec8cfd5bf/admin/system/clock_status
     *
     */

    public static void sendHttpGetRequest(String url, Response.Listener<String> responseListener) {
        RequestQueue queue = LoginActivity.get().getRequestQueue();
        Log.d(Constants.DEBUG_TAG, "GET: " + url);
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

    public static void sendHttpPostRequest(String url, Response.Listener<String> responseListener, final HashMap<String, String> params) {
        RequestQueue queue = LoginActivity.get().getRequestQueue();
        Log.d(Constants.DEBUG_TAG, "POST: " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                responseListener,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null) {
                            String toastString = "Error code: " + error.networkResponse.statusCode;
                            Log.d(Constants.DEBUG_TAG, toastString);
                        }
                    }
                }) {
            @Override
            protected Map<String,String> getParams(){
                return params;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}
