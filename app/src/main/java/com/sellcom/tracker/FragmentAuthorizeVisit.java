package com.sellcom.tracker;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import async_request.ConfirmationDialogListener;
import async_request.METHOD;
import async_request.RequestManager;
import async_request.UIResponseListenerInterface;
import database.DataBaseManager;
import database.models.Session;
import database.models.Signature;
import database.models.User;
import util.TrackerManager;


/**
 * Created by hugo.figueroa on 14/04/15.
 */
public class FragmentAuthorizeVisit extends Fragment implements View.OnClickListener,FragmentSignature.setSignatureImg, UIResponseListenerInterface, ConfirmationDialogListener {

    final static public String      TAG                     = "authorizevisit";
    private Context                 context;
    FragmentManager                 fragmentManager;
    FragmentTransaction             fragmentTransaction;
    private ImageView               img_signature;
    private TextView                txv_signature_hint;
    private EditText                edt_coment_authorize_visit;
    private Bitmap                  bitmapSignature = null;
    private String                  stringSignature = null,
                                    id_visit,
                                    coment;
    FragmentSignature               fragmentSignature;

    String                          str_id;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_authorize_visit, container, false);
        fragmentManager = getActivity().getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        img_signature               = (ImageView) view.findViewById(R.id.img_signature);
        img_signature.setOnClickListener(this);
        txv_signature_hint          = (TextView) view.findViewById(R.id.txv_signature_hint);
        edt_coment_authorize_visit  = (EditText) view.findViewById(R.id.edt_coment_authorize_visit);
        id_visit    = TrackerManager.sharedInstance().getCurrent_pdv().get("visit_id");


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
                coment = edt_coment_authorize_visit.getText().toString();
                if(coment.isEmpty()){
                    edt_coment_authorize_visit.setError(getResources().getString(R.string.error_empty_field));
                    edt_coment_authorize_visit.requestFocus();
                    return false;
                }

                if(img_signature.getDrawable() == null){
                    Toast.makeText(context,getResources().getString(R.string.missing_signature),Toast.LENGTH_SHORT).show();
                    return false;
                }

                long id = Signature.insert(context,id_visit,coment,stringSignature);
                str_id  = ""+id;

                Map<String,String> params = Signature.getSignatureInVisit(getActivity(),id_visit);

                //Log.d(TAG,"Params: "+params);

                prepareRequest(METHOD.SET_SIGNATURE,params);

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (!((MainActivity) getActivity()).isDrawerOpen) {
            menu.clear();
            inflater.inflate(R.menu.add_client, menu);
        }
    }

    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_signature:
                //Toast.makeText(context,"Fragment Firma",Toast.LENGTH_SHORT).show();
                txv_signature_hint.setText(""+ getResources().getString(R.string.signature_hint));
                fragmentSignature = new FragmentSignature();
                fragmentSignature.setSetSignatureImg(this);
                fragmentTransaction.replace(R.id.container, fragmentSignature, FragmentSignature.TAG);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                ((MainActivity) getActivity()).depthCounter = 5;
                break;
        }
    }

    @Override
    public void getSignatureImgBase64(String imgSignature) {
        stringSignature = imgSignature;
        bitmapSignature = decodeBase64(stringSignature);
        txv_signature_hint.setText("");
        img_signature.setImageBitmap(bitmapSignature);
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

        JSONObject resp;

        try {
            resp        = new JSONObject(stringResponse);

            if (resp.getString("method").equalsIgnoreCase(METHOD.SET_SIGNATURE.toString())) {

                if (resp.getString("error").isEmpty())
                    Signature.updateSignatureToStatusSent(getActivity(),str_id);
                RequestManager.sharedInstance().showConfirmationDialogWithListener(getActivity().getString(R.string.req_man_signature_sent), getActivity(), this);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void okFromConfirmationDialog(String message) {
        getActivity().onBackPressed();
    }
}
