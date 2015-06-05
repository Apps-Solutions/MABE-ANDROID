package com.sellcom.tracker;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import async_request.METHOD;
import async_request.RequestManager;
import async_request.UIResponseListenerInterface;
import database.DataBaseManager;
import database.models.CustomerWorkPlan;
import database.models.Session;
import database.models.User;
import location.GPSTracker;
import util.DatesHelper;
import util.TrackerManager;

public class FragmentCustomerWorkPlan extends Fragment implements UIResponseListenerInterface{

    final static public String              TAG = "customer_work_plan";
    private             Fragment            fragment;
    private             FragmentManager     fragmentManager;

                        SupportMapFragment  mMapFragment;

    public              Button              workPlan_Iniciar,
                                            getWorkPlan_Reasignar;

    private             TextView            txt_pdv_name,
                                            txt_pdv_jde,
                                            txt_pdv_address,
                                            txt_pdv_phone_number;
    private             float               latitude, longitude;
    private             String              pdv_id;
    private             String              visit_id;
    private             String              visit_type_id;
    private             String              visit_status_code;
    private             String              real_end;

    static              Context             context;
    View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_customer_work_plan, container, false);

        txt_pdv_name            = (TextView)view.findViewById(R.id.txt_pdv_name);
        txt_pdv_address         = (TextView)view.findViewById(R.id.txt_pdv_address);
        txt_pdv_phone_number    = (TextView)view.findViewById(R.id.txt_pdv_phone_number);
        txt_pdv_jde             = (TextView)view.findViewById(R.id.txt_pdv_jde);

        visit_id   = getArguments().getString("visit_id");

        Map<String,String> current_pdv  = CustomerWorkPlan.getCustomerWorkPlanByVisit(getActivity(),visit_id);
        TrackerManager.sharedInstance().setCurrent_pdv(current_pdv);
        Log.d(TAG,""+current_pdv);

        txt_pdv_name.setText(current_pdv.get("pdv"));
        txt_pdv_address.setText(current_pdv.get("address"));
        txt_pdv_jde.setText(current_pdv.get("jde"));

        if (current_pdv.get("pdv_phone").trim().contains("null"))
            txt_pdv_phone_number.setText("");
        else
            txt_pdv_phone_number.setText(current_pdv.get("pdv_phone"));

        pdv_id                        = current_pdv.get("visit_id");
        if (current_pdv.get("latitude").equalsIgnoreCase("null"))
            latitude    = 0;
        else
            latitude    = Float.parseFloat(current_pdv.get("latitude"));

        if (current_pdv.get("longitude").equalsIgnoreCase("null"))
            longitude   = 0;
        else
            longitude   = Float.parseFloat(current_pdv.get("longitude"));

        visit_type_id       = current_pdv.get("visit_type_id");

        visit_status_code   = current_pdv.get("visit_status_id");
        real_end            = current_pdv.get("real_end");

        Log.d(TAG,"Latitude: "+latitude+"Latitude: "+visit_status_code);

        GPSTracker  tracker = new GPSTracker(getActivity());
        final double user_latitude      = tracker.getLatitude();
        final double user_longitude     = tracker.getLongitude();

        workPlan_Iniciar      = (Button) view.findViewById(R.id.workP_buttonIniciar);
        getWorkPlan_Reasignar = (Button) view.findViewById(R.id.workP_buttonReagendar);

        updateInitButton();

        fragmentManager = getActivity().getSupportFragmentManager();

        mMapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.mapview);

        if (mMapFragment == null) {
            mMapFragment = SupportMapFragment.newInstance();
            fragmentManager.beginTransaction().replace(R.id.mapview, mMapFragment).commit();

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mMapFragment != null) {
                        GoogleMap map = mMapFragment.getMap();
                        if (map != null) {
                            map.setMyLocationEnabled(true);
                            if(latitude != 0 && longitude != 0){
                                LatLng pdv_location = new LatLng(latitude, longitude);
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(pdv_location, 17));
                                map.addMarker(new MarkerOptions()
                                        .position(pdv_location));
                            }
                            else {
                                LatLng user_location = new LatLng(user_latitude, user_longitude);
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(user_location, 17));
                            }
                        }
                    }
                    else{
                        Toast.makeText(getActivity(),"Error al obtener localización",Toast.LENGTH_SHORT).show();
                    }
                }
            }, 2000);
        }

        workPlan_Iniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (visit_status_code.equalsIgnoreCase("1")){
                    GPSTracker tracker          = new GPSTracker(getActivity());
                    float latitude              = (float)tracker.getLatitude();
                    float longitude             = (float)tracker.getLongitude();

                    String dateTime             = DatesHelper.sharedInstance().getStringDate(new Date());

                    Map<String, String> params  = new HashMap<String, String>();
                    params.put("latitude",String.valueOf(latitude));
                    params.put("longitude",String.valueOf(longitude));
                    params.put("date_time", dateTime);
                    params.put("visit_id",visit_id);
                    params.put("pdv_id",pdv_id);

                    DataBaseManager.sharedInstance().registerCheckIn(params);
                    prepareRequest(METHOD.SEND_START_VISIT, params);
                }
                else if (visit_status_code.equalsIgnoreCase("2")){
                    Bundle bundle = new Bundle();
                    bundle.putString("visit_type_id",visit_type_id);

                    fragment        = new FragmentStepVisit();

                    fragment.setArguments(bundle);

                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.shrink_out);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.replace(R.id.container, fragment, FragmentStepVisit.TAG);
                    fragmentTransaction.commit();
                    ((MainActivity) getActivity()).depthCounter = 3;
                }
                else if (visit_status_code.equalsIgnoreCase("3")){
                    RequestManager.sharedInstance().showErrorDialog("La visita ya ha sido finalizada",getActivity());
                }
            }
        });

        getWorkPlan_Reasignar.setVisibility(View.GONE);
        getWorkPlan_Reasignar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "REASIGNAR", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    public void updateInitButton(){
        if (visit_status_code.equalsIgnoreCase("1"))
            workPlan_Iniciar.setText(getActivity().getString(R.string.wp_start));
        else if (visit_status_code.equalsIgnoreCase("2"))
            workPlan_Iniciar.setText(getActivity().getString(R.string.wp_continue));
        else if (visit_status_code.equalsIgnoreCase("3"))
            workPlan_Iniciar.setText(getActivity().getString(R.string.wp_closed));
    }
    @Override
    public void onDestroyView (){
        super.onDestroyView();
        Log.e("DEBUG", "onStop of Fragment");
        fragmentManager.beginTransaction().remove(mMapFragment).commit();
    }

    public void onStart(){
        super.onStart();
        Log.e("DEBUG", "onStart of Fragment");
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(NavigationDrawerFragment.WORK_PLAN);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void prepareRequest(METHOD method, Map<String, String> params) {

        String token      = Session.getSessionActive(getActivity()).getToken();
        String username   = User.getUser(getActivity(), Session.getSessionActive(getActivity()).getUser_id()).getEmail();
        params.put("request", method.toString());
        params.put("user", username);
        params.put("token", token);

        RequestManager.sharedInstance().setListener(this);
        RequestManager.sharedInstance().makeRequestWithDataAndMethod(params, method);
    }

    @Override
    public void decodeResponse(String stringResponse) {

        JSONObject  resp;

        try {
            resp        = new JSONObject(stringResponse);

            if (resp.getString("method").equalsIgnoreCase(METHOD.SEND_START_VISIT.toString())) {

                if (resp.getString("error").isEmpty())
                    DataBaseManager.sharedInstance().updateCheckInStateToSentInVisit(visit_id);

                fragment = new FragmentStepVisit();

                Bundle bundle = new Bundle();
                bundle.putString("visit_type_id", visit_type_id);
                fragment.setArguments(bundle);

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.shrink_out);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.container, fragment, FragmentStepVisit.TAG);
                fragmentTransaction.commit();
                ((MainActivity) getActivity()).depthCounter = 3;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}