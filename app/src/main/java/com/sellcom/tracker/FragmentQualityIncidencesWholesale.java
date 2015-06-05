package com.sellcom.tracker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.SpinnerAdapter;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import async_request.ConfirmationDialogListener;
import async_request.METHOD;
import async_request.RequestManager;
import async_request.UIResponseListenerInterface;
import database.models.Product;
import database.models.QualityProblemType;
import database.models.Session;
import database.models.User;
import util.*;

/**
 * Created by jonathan.vazquez on 26/02/15.
 */
public class FragmentQualityIncidencesWholesale extends Fragment implements UIResponseListenerInterface,View.OnClickListener,FragmentPartialPayDialog.setSetImgPhoto, ConfirmationDialogListener {

    final static public String TAG = "quality_incidences_wholesale";

    Map<String,String> data;
    private Button          btn_capture_evidence;
    private EditText        edt_description_problem,edt_bottles_problem;
    private ImageView imgCamera,imgPhoto;
    private static final int THUMBNAIL_ID = 1;
    private Bitmap image;
    private String imageEncode = "";
    private Spinner         spn_problem_type,spn_line,spn_product_type;
    private DatePicker      datePicker;
    private String          dateQualityIndicidences;
    //np_year_start,np_month_start,np_day_start;
    Thread encodeImage;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quality_incidences, container, false);

        //btn_capture_evidence        = (Button)view.findViewById(R.id.btn_capture_evidence);
        edt_description_problem     = (EditText)view.findViewById(R.id.edt_description_problem);
        edt_bottles_problem         = (EditText)view.findViewById(R.id.edt_bottles_problem);
        spn_problem_type            = (Spinner)view.findViewById(R.id.spn_problem_type);
        spn_line                    = (Spinner)view.findViewById(R.id.spn_line);
        datePicker                  = (DatePicker)view.findViewById(R.id.datePicker_quality_incidences);
        imgPhoto                    = (ImageView)view.findViewById(R.id.imgPhoto_quality_incidences);
        imgCamera                   = (ImageView)view.findViewById(R.id.imgCamera_quality_incidences);


        //Spinner para product type - - - - - - - - - - - - - - - - - - - -
        util.SpinnerAdapter spinnerAdapterProblemProduct = new util.SpinnerAdapter(getActivity(),
                Product.getAllInMaps(getActivity()),
                util.SpinnerAdapter.SPINNER_TYPE.PRODUCTS);
        spn_product_type.setAdapter(spinnerAdapterProblemProduct);

        //Spinner para problem type - - - - - - - - - - - - - - - - - - - -
        util.SpinnerAdapter spinnerAdapterProblemType = new util.SpinnerAdapter(getActivity(),
                QualityProblemType.getAllInMaps(getActivity()),
                util.SpinnerAdapter.SPINNER_TYPE.Q_INCIDENCES);
        spn_problem_type.setAdapter(spinnerAdapterProblemType);

        imgCamera.setOnClickListener(this);
        imgPhoto.setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.imgCamera_quality_incidences:
                takePhoto();
                break;
            case R.id.imgPhoto_quality_incidences:
                if (imgPhoto.getDrawable() != null){
                    openDialog(image);
                }
                break;
        }
    }

    public void takePhoto(){

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, THUMBNAIL_ID);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == getActivity().RESULT_OK && requestCode == THUMBNAIL_ID) {
            //image = getStorageImage();
            image = data.getParcelableExtra("data");
            openDialog(image);
        }
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
        Log.d(TAG, "Encoded: " + imageEncode);
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



    @Override
    public void okFromConfirmationDialog(String message) {
        ((MainActivity)getActivity()).returnToHome();
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

        data = new HashMap<String, String>();
        String
                id_visit,
                id_product,
                id_problem_type,
                description,
                batch,
                product_line,
                quantity,
                evidence;

        //Para los spinner *******
        //sp_problem_type -> {timestamp=0, id=1, status=1, description=Filtracion por tapon}

        id_product      = ((Map<String,String>) spn_product_type.getSelectedItem()).get("id");
        id_problem_type = ((Map<String,String>) spn_problem_type.getSelectedItem()).get("id");
        product_line    = spn_line.getSelectedItem().toString();

        //EditText ***************
        description = edt_description_problem.getText().toString();
        quantity    = edt_bottles_problem.getText().toString();

        //Para el date picker ****
        batch = datePicker.getYear()+ "" + (datePicker.getMonth() + 1)+ ""+ datePicker.getDayOfMonth() + "";

        //Para la camara *********
        evidence = imageEncode;
        //  FALTAN ESTOS DOS...........
        //data.put("id_user",);
        //data.put("id_product",);

        Log.d(TAG, " DATOS :\n" + " id_product -> " + id_product +" \n sp_problem_type -> " + id_problem_type + "\n spn_line -> " + product_line + "\n batch -> " + batch);

        data.put("id_product"      ,id_product);
        data.put("id_problem_type" ,id_problem_type);
        data.put("description"     ,description);
        data.put("batch"           ,batch);
        data.put("product_line"    ,product_line);
        data.put("quantity"        ,quantity);
        data.put("evidence"        ,evidence);


        //prepareRequest(METHOD.QUALITY_INCIDENCE,data);

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

