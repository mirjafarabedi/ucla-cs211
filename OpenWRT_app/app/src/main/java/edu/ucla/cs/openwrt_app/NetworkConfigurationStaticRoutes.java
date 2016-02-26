package edu.ucla.cs.openwrt_app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

//holds static routes, directs to new entry form
/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NetworkConfigurationStaticRoutes.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NetworkConfigurationStaticRoutes#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NetworkConfigurationStaticRoutes extends Fragment {

    public NetworkConfigurationStaticRoutes() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_network_configuration_static_routes, container, false);
    }
}
