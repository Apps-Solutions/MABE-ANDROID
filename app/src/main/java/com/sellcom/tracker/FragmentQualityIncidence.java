package com.sellcom.tracker;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import async_request.ConfirmationDialogListener;
import async_request.METHOD;
import async_request.RequestManager;
import async_request.UIResponseListenerInterface;
import database.models.Product;
import database.models.QualityIncidence;
import database.models.QualityProblemType;
import database.models.Session;
import database.models.User;
import util.DataCollectedReceiver;
import util.SpinnerAdapter;
import util.TrackerManager;


public class FragmentQualityIncidence extends Fragment implements View.OnClickListener,FragmentPartialPayDialog.setSetImgPhoto {

    final static public String TAG = "FRAGMENT_QUALITY_INCIDENCE";

    private static final int THUMBNAIL_ID = 1;

    public  List<Map<String,String>>problem_type_array;

            String                  product_id, product_name;
    private EditText                edt_description_problem,edt_bottles_problem;
    private ImageView               imgCamera,imgPhoto;

    private Bitmap                  image;
    private String                  imageEncode = "";
    private Spinner                 spn_problem_type,spn_line;
    private DatePicker              datePicker;
            Thread                  encodeImage;
    public DataCollectedReceiver    listener;
    public String                   batch_data;

    public FragmentQualityIncidence() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view                   = inflater.inflate(R.layout.fragment_quality_incidences, container, false);

        product_id      = getArguments().getString("product_id");
        product_name    = getArguments().getString("product_name");

        ((TextView)view.findViewById(R.id.lbl_product_name)).setText(product_name);

        spn_problem_type            = (Spinner)view.findViewById(R.id.spn_problem_type);

        edt_description_problem     = (EditText)view.findViewById(R.id.edt_description_problem);
        edt_bottles_problem         = (EditText)view.findViewById(R.id.edt_bottles_problem);


        spn_line                    = (Spinner)view.findViewById(R.id.spn_line);
        datePicker                  = (DatePicker)view.findViewById(R.id.datePicker_quality_incidences);
        imgCamera                   = (ImageView)view.findViewById(R.id.imgCamera_quality_incidences);
        imgPhoto                    = (ImageView)view.findViewById(R.id.imgPhoto_quality_incidences);

        SpinnerAdapter spinnerAdapterProblemType = new SpinnerAdapter(getActivity(),
                                                                      problem_type_array,
                                                                      SpinnerAdapter.SPINNER_TYPE.Q_INCIDENCES);
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
            image = data.getParcelableExtra("data");
            openDialog(image);
        }
    }

    public void openDialog(Bitmap bitmap){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentPartialPayDialog dialog = new FragmentPartialPayDialog();
        dialog.setSetSetImgPhotoListener(this);
        dialog.photoResult(bitmap);
        dialog.show(fragmentManager, FragmentPartialPayDialog.TAG);
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
                saveData();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean validateData(){
        if (edt_description_problem.getText().toString().isEmpty()){
            edt_description_problem.setError(getString(R.string.error_empty_field));
            return false;
        }
        if (edt_bottles_problem.getText().toString().isEmpty()){
            edt_bottles_problem.setError(getString(R.string.error_empty_field));
            return false;
        }
        return true;
    }

    public boolean validateProblemTypeInBatch(String id_visit, String product_id, String batch, String id_problem_type){
        if (QualityIncidence.getQualityProblemTypeIDInProductBatchAndVisit(getActivity(),id_visit,product_id,batch,id_problem_type) != null)
            return false;
        return true;
    }


    public void saveData() {

        String id_problem_type,id_problem_name,
               description,
               batch,
               product_line,
               quantity,
               evidence;

        if (!validateData())
            return;

        id_problem_type = ((Map<String,String>) spn_problem_type.getSelectedItem()).get("id");
        id_problem_name = ((Map<String,String>) spn_problem_type.getSelectedItem()).get("description");
        product_line    = ""+(spn_line.getSelectedItemPosition() + 1);

        description =  edt_description_problem.getText().toString();
        quantity    = edt_bottles_problem.getText().toString();

        batch = datePicker.getYear()+ "" + (datePicker.getMonth() + 1)+ ""+ datePicker.getDayOfMonth() + "";

        evidence = imageEncode;

        String id_visit = TrackerManager.sharedInstance().getCurrent_pdv().get("visit_id");

        if (!validateProblemTypeInBatch(id_visit,product_id,batch,id_problem_type)) {
            Toast.makeText(getActivity(),"Incidencia de calidad ya capturada en el producto y lote",Toast.LENGTH_SHORT).show();
            return;
        }

        QualityIncidence.insert(getActivity(),id_visit,product_id,id_problem_type,id_problem_name,description,batch,product_line,quantity,evidence);
        listener.collectDataFromQualityIncidence(null);

        getActivity().onBackPressed();

    }
}
