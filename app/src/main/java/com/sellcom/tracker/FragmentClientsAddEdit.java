package com.sellcom.tracker;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import async_request.ConfirmationDialogListener;
import async_request.METHOD;
import async_request.RequestManager;
import async_request.UIResponseListenerInterface;
import database.models.Customer;
import database.models.People;
import database.models.Session;
import database.models.User;
import location.GPSTracker;
import util.SpinnerAdapter;
import util.Utilities;

public class FragmentClientsAddEdit extends Fragment implements UIResponseListenerInterface, AdapterView.OnItemSelectedListener, ConfirmationDialogListener {

    final static public String TAG = "clients_add_edit";

    EditText    edt_client_name,
                edt_client_last_name_1,
                edt_client_last_name_2,
                edt_client_rfc,
                edt_client_curp,
                edt_client_phone,
                edt_client_email,
                edt_client_latitude,
                edt_client_longitude,
                edt_client_state,
                edt_client_locality,
                edt_client_district,
                edt_client_street,
                edt_client_ext_num,
                edt_client_int_num,
                edt_client_zip_code;
    ;

    Spinner     spn_client_state,spn_client_city,spn_client_type;
    Customer    clientToEdit;
    boolean     isEditing = false;
    boolean     isGeneralCostumer;
    int         costumer_type   = 1;

    double      latitude,longitude;

    List<Map<String,String>>    state_array;
    List<Map<String,String>>    cities_array;

    String                      state_id,city_id;

    public static FragmentClientsAddEdit newInstance(int clientId) {

        Bundle bundle = new Bundle();
        bundle.putInt("id", clientId);
        FragmentClientsAddEdit clientsAddEdit = new FragmentClientsAddEdit();
        clientsAddEdit.setArguments(bundle);
        return clientsAddEdit;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if(bundle != null) {
            int id = bundle.getInt("id", 0);
            clientToEdit = Customer.getCustomer(getActivity(), id);
            isEditing = true;
        }

        GPSTracker tracker = new GPSTracker(getActivity());
        latitude           = tracker.getLatitude();
        longitude          = tracker.getLongitude();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clients_add_edit, container, false);

        if (view != null) {

            isGeneralCostumer       = true;

            InputFilter allCapsFilter[] = {new InputFilter.AllCaps()};

            edt_client_name         =(EditText) view.findViewById(R.id.edt_client_name);
            edt_client_name.setFilters(allCapsFilter);

            spn_client_type         = (Spinner) view.findViewById(R.id.spn_client_type);
            spn_client_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    costumer_type       = position;
                    isGeneralCostumer   = (position == 0);
                    updateBillableInfoVisibility();

                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    Log.d("nothing" , "selected");
                }

            });

            edt_client_last_name_1  =(EditText) view.findViewById(R.id.edt_client_last_name_1);
            edt_client_last_name_1.setFilters(allCapsFilter);

            edt_client_last_name_2  =(EditText) view.findViewById(R.id.edt_client_last_name_2);
            edt_client_last_name_2.setFilters(allCapsFilter);

            edt_client_rfc          =(EditText) view.findViewById(R.id.edt_client_rfc);
            edt_client_rfc.setFilters(allCapsFilter);

            edt_client_curp         =(EditText) view.findViewById(R.id.edt_client_curp);
            edt_client_curp.setFilters(allCapsFilter);

            edt_client_phone        =(EditText) view.findViewById(R.id.edt_client_phone);
            edt_client_email        =(EditText) view.findViewById(R.id.edt_client_email);

            edt_client_latitude     =(EditText) view.findViewById(R.id.edt_client_latitude);
            edt_client_latitude.setText(""+latitude);

            edt_client_longitude    =(EditText) view.findViewById(R.id.edt_client_longitude);
            edt_client_longitude.setText(""+longitude);

            state_array             = new ArrayList<Map<String, String>>();

            spn_client_state        =(Spinner) view.findViewById(R.id.spn_client_state);
            /*spn_client_state.setOnTouchListener(new View.OnTouchListener(){

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN){
                        if (state_array.isEmpty()){
                            Log.d(TAG,"get all the states");

                            prepareRequest(METHOD.GET_STATES,new HashMap<String, String>());
                        }
                    }
                    return false;
                }

            });
            */
            spn_client_city         =(Spinner) view.findViewById(R.id.spn_client_city);
            spn_client_city.setEnabled(false);

            edt_client_locality     =(EditText) view.findViewById(R.id.edt_client_locality);
            edt_client_district      =(EditText) view.findViewById(R.id.edt_client_district);
            edt_client_street       =(EditText) view.findViewById(R.id.edt_client_street);
            edt_client_ext_num      =(EditText) view.findViewById(R.id.edt_client_ext_num);
            edt_client_int_num      =(EditText) view.findViewById(R.id.edt_client_int_num);

            edt_client_zip_code     =(EditText) view.findViewById(R.id.edt_client_zip_code);

            state_id    =   city_id = "";

            if (isEditing)
                setUpForEdit(clientToEdit);

            prepareRequest(METHOD.GET_STATES,new HashMap<String, String>());

        }
        return view;
    }

    public void updateBillableInfoVisibility(){
        if (isGeneralCostumer){
            edt_client_rfc.setVisibility(View.GONE);
            edt_client_curp.setVisibility(View.GONE);
        }
        else{
            edt_client_rfc.setVisibility(View.VISIBLE);
            edt_client_curp.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void setUpForEdit(Customer customer) {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (!((MainActivity) getActivity()).isDrawerOpen) {
            menu.clear();
            inflater.inflate(R.menu.add_client, menu);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(NavigationDrawerFragment.CLIENTS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.save:
                    //saveClient();
                sendClientInformation();
                break;
            case R.id.discard:
                getActivity().onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void sendClientInformation(){

        Utilities.hideKeyboard(getActivity(), edt_client_name);

        if (!hasValidInputs())
            return;
        else{
            Map<String,String> params = new HashMap<String, String>();
            params.put("costumer_type",""+costumer_type);
            params.put("name",edt_client_name.getText().toString());
            params.put("lastname",edt_client_last_name_1.getText().toString());
            params.put("lastname2",edt_client_last_name_2.getText().toString());
            params.put("rfc",edt_client_rfc.getText().toString());
            params.put("curp",edt_client_curp.getText().toString());
            params.put("phone",edt_client_phone.getText().toString());
            params.put("email",edt_client_email.getText().toString());
            params.put("latitude",edt_client_latitude.getText().toString());
            params.put("longitude",edt_client_longitude.getText().toString());
            params.put("city",city_id);
            params.put("state",state_id);
            params.put("locality",edt_client_locality.getText().toString());
            params.put("district",edt_client_district.getText().toString());
            params.put("street",edt_client_street.getText().toString());
            params.put("ext_num",edt_client_ext_num.getText().toString());
            params.put("int_num",edt_client_int_num.getText().toString());
            params.put("zip_code",edt_client_zip_code.getText().toString());

            prepareRequest(METHOD.SET_PROSPECT,params);
        }
    }

    public void saveClient() {

/*        if (hasValidInputs()) {

            String name, tin, phone, email, address1, address2, city, state, country, zipCode, comments;

            if (isEditing)
                People.update(getActivity(), clientToEdit.getInfo().getId(), name, "null", email, tin, phone, address1, address2, city, state, country, comments, zipCode, "null");

            else {
                int peopleId = (int) People.insert(getActivity(), name, "null", email, tin, phone, address1, address2, city, state, country, comments, zipCode, "null");
                Customer.insert(getActivity(), 0, 0, peopleId);
            }

            clearInputs();
            isEditing = false;
            Toast.makeText(getActivity(), getString(R.string.client_saved), Toast.LENGTH_SHORT).show();
        }
*/
    }

    public void clearInputs() {

        edt_client_name.setText("");
        edt_client_last_name_1.setText("");
        edt_client_last_name_2.setText("");
        edt_client_rfc.setText("");
        edt_client_curp.setText("");
        edt_client_phone.setText("");
        edt_client_email.setText("");
        edt_client_latitude.setText("");
        edt_client_longitude.setText("");
        edt_client_state.setText("");
        edt_client_locality.setText("");
        edt_client_district.setText("");
        edt_client_street.setText("");
        edt_client_ext_num.setText("");
        edt_client_int_num.setText("");
        edt_client_zip_code.setText("");

    }

    private boolean hasValidInputs() {

        if (edt_client_name.getText().toString().isEmpty()) {
            edt_client_name.setError(getString(R.string.error_empty_field));
            edt_client_name.requestFocus();
            return false;
        }
        if (!Utilities.RFCValidator(edt_client_rfc.getText().toString())) {
            if (!isGeneralCostumer) {
                edt_client_rfc.setError(getString(R.string.error_empty_field));
                edt_client_rfc.requestFocus();
                return false;
            }
        }
        if (!Utilities.CURPValidator(edt_client_curp.getText().toString())) {
            if (!isGeneralCostumer) {
                edt_client_curp.setError(getString(R.string.error_invalid_curp));
                edt_client_curp.requestFocus();
                return false;
            }
        }
        if (!edt_client_email.getText().toString().isEmpty()){
            if (Utilities.emailValidator(edt_client_email.getText().toString())) {
                edt_client_email.setError(getString(R.string.error_invalid_email_address));
                edt_client_email.requestFocus();
                return false;
            }
        }
        if (state_id.isEmpty()){
            Toast.makeText(getActivity(),getString(R.string.error_no_state),Toast.LENGTH_SHORT).show();
            return false;
        }
        if(city_id.isEmpty()){
            Toast.makeText(getActivity(),getString(R.string.error_no_city),Toast.LENGTH_SHORT).show();
            return false;
        }
        if (edt_client_district.getText().toString().isEmpty()){
            edt_client_district.setError(getString(R.string.error_empty_field));
            edt_client_district.requestFocus();
            return false;
        }
        if (edt_client_street.getText().toString().isEmpty()){
            edt_client_street.setError(getString(R.string.error_empty_field));
            edt_client_street.requestFocus();
            return false;
        }
        if (edt_client_zip_code.getText().toString().isEmpty()){
            edt_client_zip_code.setError(getString(R.string.error_empty_field));
            edt_client_zip_code.requestFocus();
            return false;
        }
        return true;
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
        Log.d(TAG, "Response in Fragment add "+stringResponse);

        JSONObject resp;

        try {
            resp = new JSONObject(stringResponse);

            if (resp.getString("method").equalsIgnoreCase(METHOD.GET_STATES.toString())) {
                JSONArray state_array_json = resp.getJSONArray("states");
                Log.d(TAG,"States array lenght: "+state_array_json.length());
                Map<String,String> clearState   = new HashMap<String, String>();
                clearState.put("st_state","Estado");
                state_array.add(clearState);
                for (int i=0; i<state_array_json.length(); i++){
                    state_array.add(RequestManager.sharedInstance().jsonToMap(state_array_json.getJSONObject(i)));
                }
                spn_client_state.setAdapter(new SpinnerAdapter(getActivity(), state_array, SpinnerAdapter.SPINNER_TYPE.STATES));
                spn_client_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        if (position == 0) {
                            spn_client_city.setAdapter(null);
                            spn_client_city.setEnabled(false);
                            return;
                        }
                        Map<String,String> state_selected = state_array.get(position);

                        state_id                  = state_selected.get("st_code");

                        Map<String,String> params = new HashMap<String, String>();
                        params.put("code",state_selected.get("st_code"));
                        prepareRequest(METHOD.GET_CITIES,params);
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        Log.d("nothing" , "selected");
                    }

                });
            }
            else if (resp.getString("method").equalsIgnoreCase(METHOD.GET_CITIES.toString())) {
                spn_client_city.setEnabled(true);
                cities_array    = new ArrayList<Map<String, String>>();
                JSONArray state_array_json = resp.getJSONArray("cities");
                for (int i=0; i<state_array_json.length(); i++){
                    cities_array.add(RequestManager.sharedInstance().jsonToMap(state_array_json.getJSONObject(i)));
                }
                spn_client_city.setAdapter(new SpinnerAdapter(getActivity(), cities_array, SpinnerAdapter.SPINNER_TYPE.CITIES));
                spn_client_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Map<String,String> state_selected = cities_array.get(position);

                        city_id                  = state_selected.get("ct_code");
                        Log.d(TAG , "selected: "+city_id);
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        Log.d("nothing" , "selected");
                    }

                });
            }
            else
                clientSendSuccessfully();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void clientSendSuccessfully(){
        RequestManager.sharedInstance().showConfirmationDialogWithListener(getString(R.string.req_man_new_client_sent_successfully), getActivity(),this);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void okFromConfirmationDialog(String message) {
        ((MainActivity)getActivity()).returnToHome();
    }
}
