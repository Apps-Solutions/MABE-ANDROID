package com.sellcom.tracker;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;

import location.GPSTracker;
import util.CustomMapFragment;

/**
 * Created by juanc.jimenez on 16/05/14.
 */
public class FragmentTrafficMap extends DialogFragment{

    static FragmentTrafficMap newInstance() {

        return new FragmentTrafficMap();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int style, theme;

        style = DialogFragment.STYLE_NO_TITLE;
        theme = R.style.AnimatedDialog;

        setStyle(style, theme);

        LatLng position;
        Location myLocation = new GPSTracker(getActivity()).getCurrentLocation();
        if (myLocation != null)
            position = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
        else
            position = new LatLng(19.35, -99.35);

        CustomMapFragment mapFragment = (CustomMapFragment) getChildFragmentManager().findFragmentByTag("map");

        if (mapFragment == null)
            mapFragment = CustomMapFragment.newInstance(position);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.dialog_from_bottom, R.anim.dialog_to_bottom);
        transaction.add(R.id.fragment_map_container, mapFragment).commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_traffic_map, container, false);
    }

}
