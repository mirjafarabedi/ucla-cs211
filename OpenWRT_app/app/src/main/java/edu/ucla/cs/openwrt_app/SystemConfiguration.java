package edu.ucla.cs.openwrt_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

//switches to system properties, user administration, software
public class SystemConfiguration extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_configuration);
    }
}
