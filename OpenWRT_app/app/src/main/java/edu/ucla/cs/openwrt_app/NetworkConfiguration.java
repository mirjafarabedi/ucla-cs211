package edu.ucla.cs.openwrt_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.design.widget.TabLayout;
//activity tabs to DHCP/DNS, Firewall, Interfaces, Static Routes
public class NetworkConfiguration extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_configuration);
        // Setup Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        // Assign created adapter to viewPager
        viewPager.setAdapter(new TabsExamplePagerAdapter(getSupportFragmentManager()));

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        // This method setup all required method for TabLayout with Viewpager
        tabLayout.setupWithViewPager(viewPager);

    }

    public static class TabsExamplePagerAdapter extends FragmentPagerAdapter {
        // As we are implementing two tabs
        private static final int NUM_ITEMS = 4;

        public TabsExamplePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        // For each tab different fragment is returned
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new NetworkConfigurationInterfaces();
                case 1:
                    return new NetworkConfigurationFirewall();
                case 2:
                    return new NetworkConfigurationStaticRoutes();
                case 3:
                    return new NetworkConfigurationDHCPDNS();
                default:
                    return new NetworkConfigurationInterfaces();
            }
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;

        }

        @Override
        public CharSequence getPageTitle(int position) {
            // For simplicity of this tutorial this string is hardcoded
            // Otherwise it should be access using string resource
            return "Tab " + (position + 1);
        }
    }
}
