package com.sellcom.tracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import async_request.ConfirmationDialogListener;
import async_request.METHOD;
import async_request.RequestManager;
import async_request.UIResponseListenerInterface;
import database.DataBaseManager;
import database.models.NoSaleReason;
import database.models.NoSaleReasonRecord;
import database.models.Product;
import database.models.Receipt;
import database.models.Session;
import database.models.User;
import util.CaptureSignature;
import util.SpinnerAdapter;
import util.TrackerManager;
import util.Utilities;

/**
 * Created by Raiseralex21 on 02/03/15.
 */
public class FragmentNoReasonForSale extends Fragment implements UIResponseListenerInterface,View.OnClickListener, ConfirmationDialogListener {

    public final static String TAG= "fragment_no_sale";
    public final static int ISSUING_SIGNATURE = 1;

    Bitmap      signature;

    EditText    commentsET;

    Spinner     sp_no_sale_reason;

    ImageView   signatureCustomer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_no_reason_for_sale, container, false);

        sp_no_sale_reason   = (Spinner) view.findViewById(R.id.sp_no_sale_reason);
        sp_no_sale_reason.setAdapter(new util.SpinnerAdapter(getActivity(), NoSaleReason.getAllInMaps(getActivity()), SpinnerAdapter.SPINNER_TYPE.NO_SALES_REASON));
        commentsET = (EditText) view.findViewById(R.id.edt_no_sale_comments);

        signatureCustomer = (ImageView) view.findViewById(R.id.imv_no_sale_comments_customer_signature);
        signatureCustomer.setOnClickListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                sendReason();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

         case R.id.imv_no_sale_comments_customer_signature:
             int requestCode;
             Intent intent = new Intent(getActivity(), CaptureSignature.class);

             signatureCustomer.setBackgroundColor(Color.WHITE);
                 requestCode = ISSUING_SIGNATURE;

             getActivity().startActivityForResult(intent, requestCode);

             break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*String encodedImage;
        signature = null;
        Bundle bundle = data.getExtras();
        if (bundle != null) {
            encodedImage = bundle.getString("data");
            signature = Utilities.stringToBitmap(encodedImage);

            if (resultCode == getActivity().RESULT_OK) {
            //image = getStorageImage();
            signature = data.getParcelableExtra("data");
            signatureCustomer.setImageBitmap(signature);
        }

        }*/

        //signatureCustomer.setImageBitmap(signature);

        Log.d(TAG,"PROBLEMAS CON IMAGEN...." + signature);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (!((MainActivity) getActivity()).isDrawerOpen) {
            menu.clear();
            inflater.inflate(R.menu.add_client, menu);
        }
    }

    public void sendReason() {

        String comments     = commentsET.getText().toString();

        Map<String,String> params = new HashMap<String, String>();
        params.put("id_visit",TrackerManager.sharedInstance().getCurrent_pdv().get("visit_id"));
        params.put("id_reason",""+((Map<String,String>)sp_no_sale_reason.getSelectedItem()).get("id"));
        if (comments.isEmpty())
            comments = " ";
        params.put("comments",comments);
        params.put("signature","0000000000000000000");

        NoSaleReasonRecord.insert(getActivity(),TrackerManager.sharedInstance().getCurrent_pdv().get("visit_id"),""+((Map<String,String>)sp_no_sale_reason.getSelectedItem()).get("id"),
                comments,"0000000000000000000");

        prepareRequest(METHOD.SET_NO_SALE_REASON, params);
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

        try {
            JSONObject resp          = new JSONObject(stringResponse);

            if (resp.getString("method").equalsIgnoreCase(METHOD.SET_NO_SALE_REASON.toString())) {
                if (resp.getString("error").isEmpty()){
                    NoSaleReasonRecord.updateNoSaleReasonToSentInVisit(getActivity(),TrackerManager.sharedInstance().getCurrent_pdv().get("visit_id"));
                }
                RequestManager.sharedInstance().showConfirmationDialogWithListener(getString(R.string.req_man_no_sale_reason), getActivity(), this);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void okFromConfirmationDialog(String message) {
        (getActivity()).onBackPressed();
    }
}
