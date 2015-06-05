package com.sellcom.tracker;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import async_request.METHOD;
import async_request.RequestManager;

import async_request.UIResponseListenerInterface;
import database.models.Invoice;
import database.models.Session;
import database.models.User;
import util.Constants;
import util.DatesHelper;
import util.InvoiceAdapter;
import util.ReceiptAdapter;
import util.TrackerManager;

public class FragmentCashing extends Fragment implements UIResponseListenerInterface, FragmentReceiptPreview.NotifyReloadInvoicesArrayListener{

    final static public String TAG = "cashing";

    static  Context                     context;
    public  Fragment                    fragment;
    public  FragmentManager             fragmentManager;
    public  FragmentTransaction         fragmentTransaction;
            InvoiceAdapter              invoice_list_adapter;

    private ListView                    listViewCashing;

    private List<Map<String,String>>    invoice_array;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cashing, container, false);
        listViewCashing = (ListView) view.findViewById(R.id.lv_cashing);

        invoice_array   = Invoice.getAllInvoicesNotPaidInMapsForPDV(getActivity(), TrackerManager.sharedInstance().getCurrent_pdv().get("pdv_id"));

        invoice_list_adapter = new InvoiceAdapter(context,invoice_array);
        listViewCashing.setAdapter(invoice_list_adapter);

        listViewCashing.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showReceiptFragment(position);
            }
        });
        return  view;
    }

    public void populateInvoicesArray(String strArray){
        try {
            JSONArray jsonArray = new JSONArray(strArray);

            if(jsonArray.length() == 0)
                RequestManager.sharedInstance().showConfirmationDialog(getString(R.string.req_man_no_invoices),getActivity());

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object          = jsonArray.getJSONObject(i);
                invoice_array.add(RequestManager.sharedInstance().jsonToMap(object));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void showReceiptFragment(int itemPosition){
        Bundle bundle = new Bundle();
        bundle.putString("invoice_id",invoice_array.get(itemPosition).get("id"));
        bundle.putString("folio",invoice_array.get(itemPosition).get("folio"));
        bundle.putString("order_id", "CLEAR");
        bundle.putString("pdv_id", invoice_array.get(itemPosition).get("pdv_id"));
        bundle.putString("pdv_name",invoice_array.get(itemPosition).get("pdv_name"));
        bundle.putString("date", DatesHelper.getStringDate(new Date()));
        bundle.putDouble("total", Double.parseDouble(invoice_array.get(itemPosition).get("total")));

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        Fragment fragment = getFragmentManager().findFragmentByTag(FragmentReceiptPreview.TAG);

        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }
        fragmentTransaction.addToBackStack(null);
        ((MainActivity) getActivity()).depthCounter = 5;

        FragmentReceiptPreview newFragment  = FragmentReceiptPreview.newInstance(0, ReceiptAdapter.ORDER);
        newFragment.listener                = this;
        newFragment.setArguments(bundle);
        newFragment.show(fragmentTransaction, FragmentReceiptPreview.TAG);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(NavigationDrawerFragment.CASHING);
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

            if (resp.getString("method").equalsIgnoreCase(METHOD.GET_INVOICES.toString())) {
                JSONArray inv_array = resp.getJSONArray("invoices");
                invoice_array.clear();
                populateInvoicesArray(inv_array.toString());
                invoice_list_adapter.notifyDataSetChanged();
            }
        }
        catch (JSONException e) {   e.printStackTrace();    }
    }
    @Override
    public void notifyPaymentSucceed() {
        getActivity().onBackPressed();
    }
}