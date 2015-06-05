package com.sellcom.tracker;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
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
import database.models.Exhibition;
import database.models.Product;
import database.models.Promotion;
import database.models.ResourceType;
import database.models.Session;
import database.models.User;
import util.*;

/**
 * Created by jonathan.vazquez on 26/02/15.
 */
public class FragmentOffersPromotionsWholesale extends Fragment implements UIResponseListenerInterface,View.OnClickListener,FragmentPartialPayDialog.setSetImgPhoto, ConfirmationDialogListener {
    final static public String TAG = "offers_promotions_wholesale";

    private Map<String,String> data;

    private ImageView imgCamera,imgPhoto;
    private static final int THUMBNAIL_ID = 1;
    private Bitmap image;
    private String imageEncode = "";
    Thread encodeImage;

    EditText edt_price_offers, edt_commodity_promotion, edt_box_promotion;
    DatePicker dp_op_start_date, dp_op_end_date;
    CheckBox cbx1, cbx2, cbx3, cbx4, cbx5, cbx6;
    RadioButton radio_activation_yes, radio_activation_no, radio_exhibit_yes, radio_exhibit_no;
    Spinner spn_type_promotion, spn_type_exhibit, spn_resourse_ragasa, spn_type_product;
    Button btn_capture_evidence;
    RadioGroup radio_group_exhibit, radio_group_activation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_offers_promotions, container, false);


        if (view != null) {

            imgCamera                   = (ImageView)view.findViewById(R.id.imgCamera);
            imgCamera.setOnClickListener(this);

            imgPhoto                    = (ImageView)view.findViewById(R.id.imgPhoto);
            imgPhoto.setOnClickListener(this);

            spn_type_product            = (Spinner)  view.findViewById(R.id.spn_type_product);
            edt_price_offers            = (EditText) view.findViewById(R.id.edt_price_offers);
            dp_op_start_date            = (DatePicker) view.findViewById(R.id.dp_op_start_date);
            dp_op_end_date              = (DatePicker) view.findViewById(R.id.dp_op_end_date);
            cbx1                        = (CheckBox) view.findViewById(R.id.cbx1);
            cbx2                        = (CheckBox) view.findViewById(R.id.cbx2);
            cbx3                        = (CheckBox) view.findViewById(R.id.cbx3);
            cbx4                        = (CheckBox) view.findViewById(R.id.cbx4);
            cbx5                        = (CheckBox) view.findViewById(R.id.cbx5);
            cbx6                        = (CheckBox) view.findViewById(R.id.cbx6);
            radio_activation_yes        = (RadioButton) view.findViewById(R.id.radio_activation_yes);
            radio_activation_no         = (RadioButton) view.findViewById(R.id.radio_activation_no);
            spn_type_promotion          = (Spinner) view.findViewById(R.id.spn_type_promotion);
            edt_commodity_promotion     = (EditText) view.findViewById(R.id.edt_commodity_promotion);
            radio_exhibit_yes           = (RadioButton) view.findViewById(R.id.radio_exhibit_yes);
            radio_exhibit_no            = (RadioButton) view.findViewById(R.id.radio_exhibit_no);
            spn_type_exhibit            = (Spinner) view.findViewById(R.id.spn_type_exhibit);
            edt_box_promotion           = (EditText) view.findViewById(R.id.edt_box_promotion);
            spn_resourse_ragasa         = (Spinner) view.findViewById(R.id.spn_resourse_ragasa);
            btn_capture_evidence        = (Button) view.findViewById(R.id.btn_capture_evidence);
            radio_group_exhibit         = (RadioGroup) view.findViewById(R.id.radio_group_exhibit);
            radio_group_activation      = (RadioGroup) view.findViewById(R.id.radio_group_activation);
        }

        //Spinner para product type - - - - - - - - - - - - - - - - - - - -
        util.SpinnerAdapter spinnerAdapterProblemProduct = new util.SpinnerAdapter(getActivity(),
                Product.getAllInMaps(getActivity()),
                util.SpinnerAdapter.SPINNER_TYPE.PRODUCTS);
        spn_type_product.setAdapter(spinnerAdapterProblemProduct);

        util.SpinnerAdapter spinnerAdapterProblemType = new util.SpinnerAdapter(getActivity(),
                Promotion.getAllInMaps(getActivity()),
                util.SpinnerAdapter.SPINNER_TYPE.PROBLEM_TYPE);
        spn_type_promotion.setAdapter(spinnerAdapterProblemType);

        util.SpinnerAdapter spinnerAdapterExhibit = new util.SpinnerAdapter(getActivity(),
                Exhibition.getAllInMaps(getActivity()),
                util.SpinnerAdapter.SPINNER_TYPE.EXHIB_TYPE);
        spn_type_exhibit.setAdapter(spinnerAdapterExhibit);

        util.SpinnerAdapter spinnerAdapterRagasa = new util.SpinnerAdapter(getActivity(),
                ResourceType.getAllInMaps(getActivity()),
                util.SpinnerAdapter.SPINNER_TYPE.EXHIB_TYPE);
        spn_resourse_ragasa.setAdapter(spinnerAdapterRagasa);


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
        }
        return super.onOptionsItemSelected(item);
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

    @TargetApi(Build.VERSION_CODES.FROYO)
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

    public void saveMeasure() {

        String      edt_price_offers, edt_box_promotion,edt_commodity_promotion;
        String      cbx1, cbx2, cbx3, cbx4, cbx5, cbx6;
        String promotion_start,promotion_end;

        data = new HashMap<String, String>();

        //PARA LOS DATEPICKER
        //---------------------------------------------------------------------------------------------------
        promotion_start = dp_op_start_date.getYear() + "-" + (dp_op_start_date.getMonth() + 1) + "-" + dp_op_start_date.getDayOfMonth() + " 00:00";
        promotion_end = dp_op_end_date.getYear() + "-" + (dp_op_end_date.getMonth() + 1) + "-" + dp_op_end_date.getDayOfMonth() + " 00:00";


        data.put("promotion_start",promotion_start);
        data.put("promotion_end",promotion_end);
        //---------------------------------------------------------------------------------------------------

        //PARA LOS EDITTEXT
        //---------------------------------------------------------------------------------------------------
        edt_price_offers            = this.edt_price_offers.getText().toString();
        edt_box_promotion           = this.edt_box_promotion.getText().toString();
        edt_commodity_promotion     = this.edt_commodity_promotion.getText().toString();

        data.put("promotion_price",edt_price_offers);
        data.put("?",edt_box_promotion);
        data.put("?",edt_commodity_promotion);
        //---------------------------------------------------------------------------------------------------

        //PARA LOS CHECKBOX
        //---------------------------------------------------------------------------------------------------
        String grafic_material="";
        if(this.cbx1.isChecked()){
            grafic_material = "cenefa";
        }else if(this.cbx2.isChecked()){
            if(grafic_material.equals("")){
                grafic_material += "copete";
            }else{
                grafic_material += ";copete";
            }
        }else if(this.cbx6.isChecked()){
            if(grafic_material.equals("")){
                grafic_material += "collarin";
            }else{
                grafic_material += ";collarin";
            }
        }else if(this.cbx3.isChecked()){
            if(grafic_material.equals("")){
                grafic_material += "folleto";
            }else{
                grafic_material += ";folleto";
            }
        }else if(this.cbx4.isChecked()){
            if(grafic_material.equals("")){
                grafic_material += "periodico";
            }else{
                grafic_material += ";periodico";
            }
        }else if(this.cbx5.isChecked()){
            if(grafic_material.equals("")){
                grafic_material += "anuncio";
            }else{
                grafic_material += ";anuncio";
            }
        }

        data.put("grafic_material",grafic_material);
        //---------------------------------------------------------------------------------------------------

        //PARA LOS RADIOBBUTONS
        //---------------------------------------------------------------------------------------------------
        int checkedRadioButtonActivation = radio_group_activation.getCheckedRadioButtonId();
        switch (checkedRadioButtonActivation){
            case R.id.radio_activation_yes:
                data.put("activation","si");
                break;
            case R.id.radio_activation_no:
                data.put("activation","no");
                break;
        }

        int checkedRadioButtonExhibit = radio_group_exhibit.getCheckedRadioButtonId();
        switch (checkedRadioButtonActivation){
            case R.id.radio_exhibit_yes:
                data.put("exhibit","si");
                break;
            case R.id.radio_exhibit_no:
                data.put("exhibit","no");
                break;
        }

        //---------------------------------------------------------------------------------------------------

        prepareRequest(METHOD.PROMOTION,data);

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


/* Elementos del layout
    edt_price_offers
    np_year_start
    np_month_start
    np_day_start
    np_year_end
    np_month_end
    np_day_end
    cbx1
    cbx2
    cbx3
    cbx4
    cbx5
    cbx6
    radio_activation_yes
    radio_activation_no
    spn_type_promotion
    edt_commodity_promotion
    radio_exhibit_yes
    radio_exhibit_no
    spn_type_exhibit
    edt_box_promotion
    spn_resourse_ragasa
    btn_capture_evidence
    radio_group_exhibit
    radio_group_activation
    */