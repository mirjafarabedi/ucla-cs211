package edu.ucla.cs.openwrt_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//Holds a clickable list directing to network_configuration, system configuration, status/statistics, log out
public class Selection_screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection_screen);
    }
}
