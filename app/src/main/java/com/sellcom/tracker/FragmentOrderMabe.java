package com.sellcom.tracker;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.AdapterView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import async_request.METHOD;
import async_request.RequestManager;
import async_request.UIResponseListenerInterface;
import database.DataBaseManager;
import database.models.CheckOut;
import database.models.Session;
import database.models.User;
import location.GPSTracker;
import util.Constants;
import util.DatesHelper;
import util.OrderMabeAdapter;
import util.TrackerManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentOrderMabe extends Fragment implements AdapterView.OnItemClickListener, UIResponseListenerInterface{


    final static public String TAG = "order_mabe";


    Fragment fragment;
    String locked_type;
    public FragmentOrderMabe() {
        // Required empty public constructor

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(NavigationDrawerFragment.WORK_PLAN);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order_mabe, container, false);

        locked_type = getArguments().getString("lock");

        OrderMabeAdapter adapter = new OrderMabeAdapter(getActivity());

        GridView gridView = (GridView) view.findViewById(R.id.gridViewElements);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(this);

        return view;
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        switch(position){
            case 0:
                fragment = new FragmentCapturaServicios();
                fragmentTransaction.replace(R.id.container, fragment, FragmentCapturaServicios.TAG);
                ((MainActivity) getActivity()).depthCounter = 5;
                break;
            case 1:
                fragment = new FragmentLocalizacion();
                fragmentTransaction.replace(R.id.container, fragment, FragmentLocalizacion.TAG);
                ((MainActivity) getActivity()).depthCounter = 5;
                break;
            case 2:
                fragment = new FragmentCapturaFallas();
                Bundle bundle1   = new Bundle();
                bundle1.putString("type","1");
                fragment.setArguments(bundle1);
                fragmentTransaction.replace(R.id.container, fragment, FragmentCapturaFallas.TAG);
                ((MainActivity) getActivity()).depthCounter = 5;
                break;
            case 3:
                    if (DataBaseManager.sharedInstance().getNotPaidInvoicesFromPDV(TrackerManager.sharedInstance().getCurrent_pdv().get("pdv_id")) != null){
                        Toast.makeText(getActivity(),getActivity().getString(R.string.req_man_error_order_pending_invoices),Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (locked_type.equals("LOCKED_BY_ORDER_SENT")){
                        Toast.makeText(getActivity(),getActivity().getString(R.string.req_man_error_order_already_sent),Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else if (locked_type.equals("LOCKED_BY_PRE_ORDER")){
                        Toast.makeText(getActivity(),getActivity().getString(R.string.req_man_error_preorder_available),Toast.LENGTH_SHORT).show();
                        return;
                    }

                    fragment = new FragmentOrders();

                    Bundle bundle2   = new Bundle();
                    bundle2.putString("options", Constants.FRAGMENTS_OPTIONS.INSIDE_PDV.toString());
                    fragment.setArguments(bundle2);
                    fragmentTransaction.replace(R.id.container, fragment, FragmentOrders.TAG);
                    ((MainActivity) getActivity()).depthCounter = 5;
                break;
            case 4:
                fragment = new FragmentCapturaFallas();
                Bundle bundle3   = new Bundle();
                bundle3.putString("type","2");
                fragment.setArguments(bundle3);
                fragmentTransaction.replace(R.id.container, fragment, FragmentCapturaFallas.TAG);
                ((MainActivity) getActivity()).depthCounter = 5;
                break;
            case 5:
                fragment = new FragmentCapturaFallas();
                Bundle bundle4   = new Bundle();
                bundle4.putString("type","3");
                fragment.setArguments(bundle4);
                fragmentTransaction.replace(R.id.container, fragment, FragmentCapturaFallas.TAG);
                ((MainActivity) getActivity()).depthCounter = 5;
                break;
            case 6:
                prepareEndVisit();
                break;
        }


        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();


    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).onSectionAttached(NavigationDrawerFragment.WORK_PLAN);
    }
    public void prepareEndVisit() {
        Map<String, String> params;
        params = new HashMap<String, String>();
        GPSTracker tracker = new GPSTracker(getActivity());
        float latitude = (float) tracker.getLatitude();
        float longitude = (float) tracker.getLongitude();

        String dateTime = DatesHelper.sharedInstance().getStringDate(new Date());
        String pdv_id = TrackerManager.sharedInstance().getCurrent_pdv().get("pdv_id");
        String visit_id = TrackerManager.sharedInstance().getCurrent_pdv().get("visit_id");

        params.put("latitude", String.valueOf(latitude));
        params.put("longitude", String.valueOf(longitude));
        params.put("date_time", dateTime);

        params.put("visit_id", visit_id);
        params.put("pdv_id", pdv_id);

        DataBaseManager.sharedInstance().registerCheckOut(params);
        prepareRequest(METHOD.SEND_END_VISIT,params);
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

        String  visit_id             = TrackerManager.sharedInstance().getCurrent_pdv().get("visit_id");

        JSONObject resp;

        try {
            resp        = new JSONObject(stringResponse);

        if (resp.getString("method").equalsIgnoreCase(METHOD.SEND_END_VISIT.toString())) {
            if (resp.getString("error").isEmpty()) {
                CheckOut.updateCheckOutStatusToSent(getActivity(), visit_id);
            } else
                Toast.makeText(getActivity(), "Sin acceso a red, módulo offline", Toast.LENGTH_SHORT).show();

            closeVisit();
        }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void closeVisit(){

        Toast.makeText(getActivity(), getActivity().getString(R.string.req_man_visit_ended_successfully), Toast.LENGTH_SHORT).show();

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragment = new FragmentWorkPlan();
        fragmentTransaction.replace(R.id.container, fragment, FragmentWorkPlan.TAG);
        ((MainActivity) getActivity()).depthCounter = 2;

        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
