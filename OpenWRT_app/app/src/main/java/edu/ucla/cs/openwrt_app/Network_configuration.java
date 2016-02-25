package edu.ucla.cs.openwrt_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//activity tabs to DHCP/DNS, Firewall, Interfaces, Static Routes
public class Network_configuration extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_configuration);
    }
}
