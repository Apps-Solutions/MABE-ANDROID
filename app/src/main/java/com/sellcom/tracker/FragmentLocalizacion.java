package com.sellcom.tracker;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Toast;

import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.LatLng;

import location.GPSTracker;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentLocalizacion extends Fragment implements OnMapReadyCallback {

    SupportMapFragment mMapFragment;
    FragmentManager fragmentManager;
    GoogleMap map;
    final static public String TAG = "localizacion";

    public FragmentLocalizacion() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager = getActivity().getSupportFragmentManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_localizacion, container, false);


        mMapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.mapview);


        if (mMapFragment == null) {
            mMapFragment = SupportMapFragment.newInstance();
            fragmentManager.beginTransaction().replace(R.id.mapview, mMapFragment).commitAllowingStateLoss();
            mMapFragment.getMapAsync(this);
        }

        return view;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        GPSTracker tracker = new GPSTracker(getActivity());
        final double user_latitude      = tracker.getLatitude();
        final double user_longitude     = tracker.getLongitude();

        if (mMapFragment != null) {
            map = googleMap;
            if (map != null) {
                map.setMyLocationEnabled(true);

                    UiSettings settings = map.getUiSettings();
                    settings.setAllGesturesEnabled(true);
                    settings.setZoomGesturesEnabled(true);
                    settings.setZoomControlsEnabled(false);
                    settings.setScrollGesturesEnabled(true);
                    settings.setMyLocationButtonEnabled(false);

                    LatLngBounds.Builder builder = new LatLngBounds.Builder();

                    LatLng pdv_location1 = new LatLng(19.441184, -99.121350);
                    MarkerOptions markerOptions1 = new MarkerOptions();
                    markerOptions1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                    markerOptions1.title("1");
                    markerOptions1.position(pdv_location1);
                    builder.include(pdv_location1);
                    map.addMarker(markerOptions1);

                    LatLng pdv_location2 = new LatLng(19.440860, -99.139890);
                    MarkerOptions markerOptions2 = new MarkerOptions();
                    markerOptions2.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                    markerOptions2.position(pdv_location2);
                    markerOptions2.title("2");
                    builder.include(pdv_location2);
                    map.addMarker(markerOptions2);

                    LatLng pdv_location3 = new LatLng(19.426776, -99.141263);
                    MarkerOptions markerOptions3 = new MarkerOptions();
                    markerOptions3.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                    markerOptions3.position(pdv_location3);
                    markerOptions3.title("3");
                    builder.include(pdv_location3);
                    map.addMarker(markerOptions3);

                    LatLng pdv_location4 = new LatLng(19.427019, -99.109849);
                    MarkerOptions markerOptions4 = new MarkerOptions();
                    markerOptions4.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                    markerOptions4.position(pdv_location4);
                    markerOptions4.title("4");
                    builder.include(pdv_location4);
                    map.addMarker(markerOptions4);

                    LatLng pdv_location5 = new LatLng(19.425481, -99.155940);
                    MarkerOptions markerOptions5 = new MarkerOptions();
                    markerOptions5.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                    markerOptions5.position(pdv_location5);
                    markerOptions5.title("5");
                    builder.include(pdv_location5);
                    map.addMarker(markerOptions5);

                    LatLng pdv_location6 = new LatLng(19.438108, -99.131307);
                    MarkerOptions markerOptions6 = new MarkerOptions();
                    markerOptions6.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                    markerOptions6.position(pdv_location6);
                    markerOptions6.title("6");
                    builder.include(pdv_location6);
                    map.addMarker(markerOptions6);

                    LatLng pdv_location7 = new LatLng(19.458179, -99.144525);
                    MarkerOptions markerOptions7 = new MarkerOptions();
                    markerOptions7.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                    markerOptions7.position(pdv_location7);
                    markerOptions7.title("7");
                    builder.include(pdv_location7);
                    map.addMarker(markerOptions7);

                    LatLng pdv_location8 = new LatLng(19.437703, -99.095773);
                    MarkerOptions markerOptions8 = new MarkerOptions();
                    markerOptions8.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                    markerOptions8.position(pdv_location8);
                    markerOptions8.title("8");
                    builder.include(pdv_location8);
                    map.addMarker(markerOptions8);

                    LatLng pdv_location9 = new LatLng(19.399739, -99.136971);
                    MarkerOptions markerOptions9 = new MarkerOptions();
                    markerOptions9.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                    markerOptions9.position(pdv_location9);
                    markerOptions9.title("9");
                    builder.include(pdv_location9);
                    map.addMarker(markerOptions9);

                    final LatLngBounds bounds = builder.build();

                    final int padding = 50; // offset from edges of the map in pixels
                    //CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

                    map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

                        @Override
                        public void onCameraChange(CameraPosition arg0) {
                            // Move camera.
                            map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
                            // Remove listener to prevent position reset on camera move.
                            map.setOnCameraChangeListener(null);
                        }
                    });

            }
        }
        else{
            Toast.makeText(getActivity(), "Error al obtener localización", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onDestroyView (){
        super.onDestroyView();
        fragmentManager.beginTransaction().remove(mMapFragment).commitAllowingStateLoss();
    }
}
