package com.sellcom.tracker;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import async_request.ConfirmationDialogListener;
import async_request.METHOD;
import async_request.RequestManager;
import async_request.UIResponseListenerInterface;
import database.models.Fueling;
import database.models.PartialPay;
import database.models.Session;
import database.models.User;
import util.DatesHelper;
import util.Utilities;


/**
 * Created by hugo.figueroa 22/01/15.
 */
public class FragmentFueling extends DialogFragment implements   TextView.OnEditorActionListener ,
                                                                    View.OnClickListener,
                                                                    FragmentPartialPayDialog.setSetImgPhoto,
                                                                    UIResponseListenerInterface,
                                                                    ConfirmationDialogListener{

    final static public String TAG = "fueling";

    private Spinner     spn_fuel_type;
    private EditText    edt_fuel_place,edt_fuel_km_actual,edt_fuel_liters,
                        edt_fuel_amount,edt_fuel_ticket_bill,edt_fuel_observations;
    private String      fuel_type_id;

    private ImageView imgCamera,imgPhoto;
    private Context context;
    private static final int THUMBNAIL_ID = 1;
    private Bitmap image;
    private String imageEncode = "";
    Thread encodeImage;
    private String fueling_id;

    public FragmentFueling() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) getActivity()).onSectionAttached(NavigationDrawerFragment.FUELING);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view       = inflater.inflate(R.layout.fragment_fueling, container, false);

        spn_fuel_type           = (Spinner)view.findViewById(R.id.spn_fuel_type);
        edt_fuel_place          = (EditText)view.findViewById(R.id.edt_fuel_place);
        edt_fuel_km_actual      = (EditText)view.findViewById(R.id.edt_fuel_km_actual);
        edt_fuel_liters         = (EditText)view.findViewById(R.id.edt_fuel_liters);
        edt_fuel_amount         = (EditText)view.findViewById(R.id.edt_fuel_amount);
        edt_fuel_ticket_bill    = (EditText)view.findViewById(R.id.edt_fuel_ticket_bill);
        edt_fuel_observations   = (EditText)view.findViewById(R.id.edt_fuel_observations);

        spn_fuel_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position == 0)
                    fuel_type_id    = "1";
                else
                    fuel_type_id    = "2";
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        context         = getActivity();

        imgCamera       = (ImageView)view.findViewById(R.id.imgCamera);
        imgCamera.setOnClickListener(this);

        imgPhoto        = (ImageView)view.findViewById(R.id.imgPhoto);
        imgPhoto.setOnClickListener(this);

        imageEncode     = null;

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        if (!((MainActivity) getActivity()).isDrawerOpen) {
            menu.clear();
            inflater.inflate(R.menu.partial_pay, menu);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem saveButton = menu.findItem(R.id.send_partial_payment);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.send_partial_payment:
                recoverDataFromUI();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {

        if (actionId == EditorInfo.IME_ACTION_DONE) {
            Utilities.hideKeyboard(context, edt_fuel_place);
            return true;
        }
        return false;
    }

    public void recoverDataFromUI(){
        Log.d(TAG,"Image encoded: "+image);
        if (validateInfo()){


            Fueling.insert(getActivity(),fueling_id,fuel_type_id,edt_fuel_place.getText().toString(),edt_fuel_km_actual.getText().toString(),
                    edt_fuel_liters.getText().toString(),edt_fuel_amount.getText().toString(),edt_fuel_ticket_bill.getText().toString(),
                    imageEncode,edt_fuel_observations.getText().toString());

            Map<String,String> params = new HashMap<String, String>();
            params.put("fuel_type",fuel_type_id);
            params.put("place",edt_fuel_place.getText().toString());
            params.put("km",edt_fuel_km_actual.getText().toString());
            params.put("liters",edt_fuel_liters.getText().toString());
            params.put("total",edt_fuel_amount.getText().toString());
            params.put("folio",edt_fuel_ticket_bill.getText().toString());
            params.put("comments",edt_fuel_observations.getText().toString());

            Log.d(TAG,"Data :"+params.toString());

            params.put("evidence",imageEncode);

            prepareRequest(METHOD.SEND_FUELING,params);
        }
    }

    public boolean validateInfo(){
        if(edt_fuel_place.getText().toString().isEmpty()){
            edt_fuel_place.setError(getString(R.string.error_empty_field));
            edt_fuel_place.requestFocus();
            return false;
        }
        if(edt_fuel_km_actual.getText().toString().isEmpty()){
            edt_fuel_km_actual.setError(getString(R.string.error_empty_field));
            edt_fuel_km_actual.requestFocus();
            return false;
        }
        if(edt_fuel_liters.getText().toString().isEmpty()){
            edt_fuel_liters.setError(getString(R.string.error_empty_field));
            edt_fuel_liters.requestFocus();
            return false;
        }
        if(edt_fuel_amount.getText().toString().isEmpty()){
            edt_fuel_amount.setError(getString(R.string.error_empty_field));
            edt_fuel_amount.requestFocus();
            return false;
        }
        if(edt_fuel_ticket_bill.getText().toString().isEmpty()){
            edt_fuel_ticket_bill.setError(getString(R.string.error_empty_field));
            edt_fuel_ticket_bill.requestFocus();
            return false;
        }
        /*if (imageEncode == null) {
            RequestManager.sharedInstance().showErrorDialog(getString(R.string.req_man_no_evidence_partial_settlement), getActivity());
            return false;
        }
        */
        return true;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.imgCamera:
                takePhoto();
                break;
            case R.id.imgPhoto:
                if (imgPhoto.getDrawable() != null){
                    openDialog(image);
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == getActivity().RESULT_OK && requestCode == THUMBNAIL_ID) {
            //image = getStorageImage();
            image = data.getParcelableExtra("data");
            openDialog(image);
        }
    }

    public void takePhoto(){

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, THUMBNAIL_ID);
    }

    public void openDialog(Bitmap bitmap){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentPartialPayDialog dialogo = new FragmentPartialPayDialog();
        dialogo.setSetSetImgPhotoListener(this);
        dialogo.photoResult(bitmap);
        dialogo.show(fragmentManager, FragmentPartialPayDialog.TAG);
    }

    @Override
    public void setImgPhoto(Bitmap bitmap) {
        image = bitmap;
        encodeImage = new Thread(new Runnable() {
            @Override
            public void run() {
                imageEncode = encodeTobase64(image);
            }
        });
        encodeImage.run();
        Log.d(TAG,"Encoded: "+imageEncode);
        imgPhoto.setImageBitmap(image);
    }

    @Override
    public void retry() {
        takePhoto();
    }

    public static String encodeTobase64(Bitmap image)
    {
        Bitmap immagex=image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        return imageEncoded;
    }

    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
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

            if (resp.getString("method").equalsIgnoreCase(METHOD.SEND_FUELING.toString())) {
                if (resp.getString("error").isEmpty())
                    Fueling.updateFuelingToStatusSent(getActivity(),fueling_id);

                RequestManager.sharedInstance().showConfirmationDialogWithListener(getString(R.string.req_man_fueling_sent), getActivity(), this);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void okFromConfirmationDialog(String message) {
        ((MainActivity)getActivity()).returnToHome();
    }
}