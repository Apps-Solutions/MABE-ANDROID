package util;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by juanc.jimenez on 16/05/14.
 */
public class CustomMapFragment extends SupportMapFragment {

    private LatLng location;

    public CustomMapFragment() {
        super();
    }

    public static CustomMapFragment newInstance(LatLng position){
        CustomMapFragment fragment = new CustomMapFragment();

        fragment.location = position;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater arg0, ViewGroup arg1, Bundle arg2) {
        View v = super.onCreateView(arg0, arg1, arg2);
        initMap();
        return v;
    }

    private void initMap(){
        GoogleMap map = getMap();
        if (map != null) {
            UiSettings settings = map.getUiSettings();
            settings.setAllGesturesEnabled(false);
            settings.setZoomGesturesEnabled(true);
            settings.setScrollGesturesEnabled(true);
            settings.setMyLocationButtonEnabled(false);

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16));
            map.setMyLocationEnabled(true);
            map.setTrafficEnabled(true);
        }
    }
}
