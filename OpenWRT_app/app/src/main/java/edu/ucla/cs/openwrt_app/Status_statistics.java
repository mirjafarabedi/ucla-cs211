package edu.ucla.cs.openwrt_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//Tabs to status, firewall, routes, logs, processes, and network visualizations
public class Status_statistics extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_statistics);
    }
}
