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
import android.widget.DatePicker;
import android.widget.RadioGroup;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.EditText;
import android.widget.ImageView;

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
import util.SpinnerAdapter;

/**
 * Created by hugo.figueroa 26/02/15.
 */

public class FragmentCompetitionPromotionSelfservice extends Fragment implements UIResponseListenerInterface,View.OnClickListener,FragmentPartialPayDialog.setSetImgPhoto, ConfirmationDialogListener {

    final static public String TAG = "FRAGMENT_QCOMPETITION_PROMOTION_SELFSERVICE";

    private Map<String,String> data;

    private ImageView imgCamera,imgPhoto;
    private static final int THUMBNAIL_ID = 1;
    private Bitmap image;
    private String imageEncode = "";
    Thread encodeImage;

    EditText        edt_price_public, edt_price_offers, edt_promotional_merchandise, edt_box_promotion;
    DatePicker      dp_cp_start_date, dp_cp_end_date;
    CheckBox        cbx1, cbx2, cbx3;
    //RadioButton     radio_activation_yes, radio_activation_no, radio_exhibit_yes, radio_exhibit_no, radio_sku_yes, radio_sku_no;
    Spinner         spn_product,spn_type_promotion, spn_type_exhibit, spn_resourse_ragasa;
    RadioGroup      radio_group_exhibit, radio_group_activation, radio_group_sku;


    public FragmentCompetitionPromotionSelfservice() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_competition_promotion, container, false);

        imgCamera                   = (ImageView)view.findViewById(R.id.imgCamera);
        imgCamera.setOnClickListener(this);

        imgPhoto                    = (ImageView)view.findViewById(R.id.imgPhoto);
        imgPhoto.setOnClickListener(this);

        spn_product                 = (Spinner) view.findViewById(R.id.spn_product);
        edt_price_public            = (EditText) view.findViewById(R.id.edt_price_public);
        edt_price_offers            = (EditText) view.findViewById(R.id.edt_price_offers);
        dp_cp_start_date            = (DatePicker) view.findViewById(R.id.dp_cp_start_date);
        dp_cp_end_date              = (DatePicker) view.findViewById(R.id.dp_cp_end_date);
        cbx1                        = (CheckBox) view.findViewById(R.id.cbx1);
        cbx2                        = (CheckBox) view.findViewById(R.id.cbx2);
        cbx3                        = (CheckBox) view.findViewById(R.id.cbx3);
        //radio_activation_yes        = (RadioButton) view.findViewById(R.id.radio_activation_yes);
        //radio_activation_no         = (RadioButton) view.findViewById(R.id.radio_activation_no);
        spn_type_promotion          = (Spinner) view.findViewById(R.id.spn_type_promotion);
        //radio_sku_yes               = (RadioButton) view.findViewById(R.id.radio_sku_yes);
        //radio_sku_no                = (RadioButton) view.findViewById(R.id.radio_sku_no);
        edt_promotional_merchandise = (EditText) view.findViewById(R.id.edt_promotional_merchandise);
        //radio_exhibit_yes           = (RadioButton) view.findViewById(R.id.radio_exhibit_yes);
        //radio_exhibit_no            = (RadioButton) view.findViewById(R.id.radio_exhibit_no);
        spn_type_exhibit            = (Spinner) view.findViewById(R.id.spn_type_exhibit);
        edt_box_promotion           = (EditText) view.findViewById(R.id.edt_box_promotion);
        spn_resourse_ragasa         = (Spinner) view.findViewById(R.id.spn_resourse_ragasa);
        radio_group_exhibit         = (RadioGroup) view.findViewById(R.id.radio_group_exhibit);
        radio_group_activation      = (RadioGroup) view.findViewById(R.id.radio_group_activation);
        radio_group_sku             = (RadioGroup) view.findViewById(R.id.radio_group_activation);

        //Spinner para product type - - - - - - - - - - - - - - - - - - - -
        util.SpinnerAdapter spinnerAdapterProblemProduct = new SpinnerAdapter(getActivity(),
                Product.getAllInMaps(getActivity()),
                SpinnerAdapter.SPINNER_TYPE.PRODUCTS);
        spn_product.setAdapter(spinnerAdapterProblemProduct);

        util.SpinnerAdapter spinnerAdapterProblemType = new SpinnerAdapter(getActivity(),
                Promotion.getAllInMaps(getActivity()),
                SpinnerAdapter.SPINNER_TYPE.PROBLEM_TYPE);
        spn_type_promotion.setAdapter(spinnerAdapterProblemType);

        SpinnerAdapter spinnerAdapterExhibit = new SpinnerAdapter(getActivity(),
                Exhibition.getAllInMaps(getActivity()),
                SpinnerAdapter.SPINNER_TYPE.EXHIB_TYPE);
        spn_type_exhibit.setAdapter(spinnerAdapterExhibit);

        SpinnerAdapter spinnerAdapterRagasa = new SpinnerAdapter(getActivity(),
                ResourceType.getAllInMaps(getActivity()),
                SpinnerAdapter.SPINNER_TYPE.EXHIB_TYPE);
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

        String      edt_price_public, edt_price_offers, edt_promotional_merchandise, edt_box_promotion;
        String      cbx1, cbx2, cbx3;
        String promotion_start,promotion_end;

        data = new HashMap<String, String>();

        //PARA LOS EDITTEXT
        //---------------------------------------------------------------------------------------------------
        edt_price_public            = this.edt_price_public.getText().toString();
        edt_price_offers            = this.edt_price_offers.getText().toString();
        edt_promotional_merchandise = this.edt_promotional_merchandise.getText().toString();
        edt_box_promotion           = this.edt_box_promotion.getText().toString();

        data.put("public_price",edt_price_public);
        data.put("promotion_price",edt_price_offers);
        data.put("?",edt_promotional_merchandise);
        data.put("?",edt_box_promotion);
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
        }else if(this.cbx3.isChecked()){
            if(grafic_material.equals("")){
                grafic_material += "folleto";
            }else{
                grafic_material += ";folleto";
            }
        }

        data.put("grafic_material",grafic_material);
        //---------------------------------------------------------------------------------------------------

        //PARA LOS RADIOBUTTON
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

        int checkedRadioButtonSKU = radio_group_sku.getCheckedRadioButtonId();
        switch (checkedRadioButtonActivation){
            case R.id.radio_sku_yes:
                data.put("sku","si");
                break;
            case R.id.radio_sku_no:
                data.put("sku","no");
                break;
        }
        //---------------------------------------------------------------------------------------------------

        //PARA LOS DATEPICKER
        //---------------------------------------------------------------------------------------------------
        promotion_start = dp_cp_start_date.getYear() + "-" + (dp_cp_start_date.getMonth() + 1) + "-" + dp_cp_start_date.getDayOfMonth() + " 00:00";
        promotion_end = dp_cp_end_date.getYear() + "-" + (dp_cp_end_date.getMonth() + 1) + "-" + dp_cp_end_date.getDayOfMonth() + " 00:00";


        data.put("promotion_start",promotion_start);
        data.put("promotion_end",promotion_end);
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
