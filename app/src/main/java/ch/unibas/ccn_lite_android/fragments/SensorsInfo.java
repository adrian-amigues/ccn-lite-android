package ch.unibas.ccn_lite_android.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ch.unibas.ccn_lite_android.R;

/**
 * Created by eirini on 10/9/16.
 */
public class SensorsInfo extends Fragment {

    View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.info_sensors, container, false);
        return rootView;
    }
}
