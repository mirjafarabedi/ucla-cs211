package edu.ucla.csd.openwrtandroidclient;

/**
 * Created by zhehaowang on 2/25/16.
 */
public class Constants {
    public final static String DEBUG_TAG = "debug";
    public static String SERVER_IP;
    public static String LAST_REQUEST_URL;
    public final static String SERVER_URL = "server_url";

    public static String TRAFFIC;
    public static String STATUS_AND_CONFIGURATION;

    public static void loadMenuStrings() {
        TRAFFIC = "Traffic Analysis";
        STATUS_AND_CONFIGURATION = "Status and Configuration";
    }
}
