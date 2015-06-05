package com.sellcom.tracker;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import async_request.METHOD;
import async_request.RequestManager;
import async_request.UIResponseListenerInterface;
import database.models.Session;
import database.models.User;


/**
 * Created by jonathan.vazquez on 25/02/15.
 */
public class FragmentMeasurementFurniture extends Fragment implements UIResponseListenerInterface {


    final static public String TAG = "MEASUREMENT_FORNITURE";

    TextView    tv_mf_client_name, tv_mf_client_promoter_name;
    EditText    et_mf_front, et_mf_left, et_mf_right, et_mf_sections;
    EditText    et_mf_levels_section, et_mf_width_section, et_mf_height_section, et_mf_depth;
    EditText    et_mf_what_section;
    EditText    et_mf_what_level, et_mf_fronts, et_mf_depths;
    Spinner     sp_mf_product;
    ImageButton bt_mf_add_product;

    Map<String,String> data;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_measurement_furniture, container, false);

        if (view != null) {

            tv_mf_client_name           = (TextView) view.findViewById(R.id.tv_mf_client_name);
            //tv_mf_client_promoter_name  = (TextView) view.findViewById(R.id.tv_mf_client_promoter_name);
            et_mf_front                 = (EditText) view.findViewById(R.id.et_mf_front);
            et_mf_left                  = (EditText) view.findViewById(R.id.et_mf_left);
            et_mf_right                 = (EditText) view.findViewById(R.id.et_mf_right);
            et_mf_sections              = (EditText) view.findViewById(R.id.et_mf_sections);
            et_mf_levels_section        = (EditText) view.findViewById(R.id.et_mf_levels_section);
            et_mf_width_section         = (EditText) view.findViewById(R.id.et_mf_width_section);
            et_mf_height_section        = (EditText) view.findViewById(R.id.et_mf_height_section);
            et_mf_depth                 = (EditText) view.findViewById(R.id.et_mf_depth);
            et_mf_what_section          = (EditText) view.findViewById(R.id.et_mf_what_section);
            et_mf_what_level            = (EditText) view.findViewById(R.id.et_mf_what_level);
            et_mf_fronts                = (EditText) view.findViewById(R.id.et_mf_fronts);
            et_mf_depths                = (EditText) view.findViewById(R.id.et_mf_depths);
            sp_mf_product               = (Spinner) view.findViewById(R.id.sp_mf_product);
            bt_mf_add_product           = (ImageButton) view.findViewById(R.id.bt_mf_add_product);
        }
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (!((MainActivity) getActivity()).isDrawerOpen) {
            menu.clear();
            inflater.inflate(R.menu.add_client, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                saveMeasure();
                break;
            case R.id.discard:
                getActivity().onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void saveMeasure() {

        String    front, left, right, sections;
        String    levels_section, width_section, height_section, depth;
        String    what_section;
        String    what_level, fronts, depths;
        String    product;

        data = new HashMap<String, String>();


        /*front
        left
        right
        sections
        levels_section
        width_section
        height_section
        depth
        what_section
        what_level
        fronts
        depths
        product
        */


        front           = et_mf_front.getText().toString();
        left            = et_mf_left.getText().toString();
        right           = et_mf_right.getText().toString();
        sections        = et_mf_sections.getText().toString();

        levels_section  = et_mf_levels_section.getText().toString();
        width_section   = et_mf_width_section.getText().toString();
        height_section  = et_mf_height_section.getText().toString();
        depth           = et_mf_depth.getText().toString();

        what_section    = et_mf_what_section.getText().toString();

        what_level      = et_mf_what_level.getText().toString();
        fronts          = et_mf_fronts.getText().toString();
        depths          = et_mf_depths.getText().toString();
        product         = sp_mf_product.getSelectedItem().toString();

        data.put("front",front);
        data.put("left",left);
        data.put("right",right);
        data.put("sections",sections);
        data.put("levels_section",levels_section);
        data.put("width_section",width_section);
        data.put("height_section",height_section);
        data.put("depth",depth);
        data.put("what_section",what_section);
        data.put("what_level",what_level);
        data.put("fronts",fronts);
        data.put("depths",depths);
        data.put("product",product);

        Toast.makeText(getActivity(), getString(R.string.client_saved), Toast.LENGTH_SHORT).show();

        prepareRequest(METHOD.QUALITY_INCIDENCE,data);


    }

    @Override
    public void prepareRequest(METHOD method, Map<String, String> params) {
        // 1
        String token      = Session.getSessionActive(getActivity()).getToken();
        String username   = User.getUser(getActivity(), Session.getSessionActive(getActivity()).getUser_id()).getEmail();
        params.put("request", method.toString());
        params.put("user", username);
        params.put("token", token);

        //2
        RequestManager.sharedInstance().setListener(this);

        //3
        RequestManager.sharedInstance().makeRequestWithDataAndMethod(params, method);
    }

    @Override
    public void decodeResponse(String stringResponse) {

        JSONObject resp;



    }

}
