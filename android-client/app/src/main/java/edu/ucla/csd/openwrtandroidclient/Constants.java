package edu.ucla.csd.openwrtandroidclient;

/**
 * Created by zhehaowang on 2/25/16.
 */
public class Constants {
    public final static String DEBUG_TAG = "debug";

    public static String STATUS;
    public static String SYSTEM;
    public static String NETWORK;
    public static String LOGOUT;

    public static String FIREWALL;
    public static String ROUTES;

    public static void loadMenuStrings() {
        STATUS = LoginActivity.get().getResources().getString(R.string.menu_status);
        SYSTEM = LoginActivity.get().getResources().getString(R.string.menu_system);
        NETWORK = LoginActivity.get().getResources().getString(R.string.menu_network);
        LOGOUT = LoginActivity.get().getResources().getString(R.string.menu_logout);

        FIREWALL = LoginActivity.get().getResources().getString(R.string.menu_status_firewall);
        ROUTES = LoginActivity.get().getResources().getString(R.string.menu_status_routes);
    }
}
