package com.sellcom.tracker;


import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import async_request.ConfirmationDialogListener;
import async_request.METHOD;
import async_request.RequestManager;
import async_request.UIResponseListenerInterface;
import database.models.Exhibition;
import database.models.Offers;
import database.models.Product;
import database.models.Promotion;
import database.models.ResourceType;
import database.models.Session;
import database.models.User;
import util.DatesHelper;
import util.SpinnerAdapter;
import util.TrackerManager;
import util.Utilities;


public class FragmentRivalOffer extends Fragment implements View.OnClickListener,FragmentPartialPayDialog.setSetImgPhoto, ConfirmationDialogListener {

    final static public String TAG = "FRAGMENT_RIVAL_OFFER";

    private ImageView imgCamera,imgPhoto;
    private static final int THUMBNAIL_ID = 1;
    private Bitmap image;
    private String imageEncode = "";
    Thread encodeImage;

    String      product_id;
    boolean     capture_sku = true;
    public List<Map<String,String>> product_array;

    EditText        edt_price_public, edt_price_offers,edt_rp_start_offers,edt_rp_end_offers,edt_promotional_merchandise, edt_box_promotion,edt_sku_offers;
    private DatePickerDialog dlg_riv_offers_start;
    private DatePickerDialog dlg_riv_offers_end;

    CheckBox        cbx1, cbx2, cbx3;
    Spinner         spn_product,spn_type_promotion, spn_type_exhibit, spn_resourse_ragasa;
    RadioGroup      radio_group_exhibit, radio_group_activation, radio_group_sku;


    public FragmentRivalOffer() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_rival_offer, container, false);

        product_id      = getArguments().getString("product_id");

        imgCamera                   = (ImageView)view.findViewById(R.id.imgCamera);
        imgCamera.setOnClickListener(this);

        imgPhoto                    = (ImageView)view.findViewById(R.id.imgPhoto);
        imgPhoto.setOnClickListener(this);

        spn_product                 = (Spinner) view.findViewById(R.id.spn_product);
        edt_price_public            = (EditText) view.findViewById(R.id.edt_price_public);
        edt_price_offers            = (EditText) view.findViewById(R.id.edt_price_offers);
        edt_rp_start_offers         = (EditText) view.findViewById(R.id.edt_rp_start_offers);
        edt_rp_start_offers.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    dlg_riv_offers_start.show();
                    edt_rp_start_offers.setError(null);
                }
            }
        });
        setupStartOfferDateDialog();
        edt_rp_end_offers           = (EditText) view.findViewById(R.id.edt_rp_end_offers);
        edt_rp_end_offers.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    dlg_riv_offers_end.show();
                    edt_rp_end_offers.setError(null);
                }
            }
        });
        setupEndOfferDateDialog();
        cbx1                        = (CheckBox) view.findViewById(R.id.cbx1);
        cbx2                        = (CheckBox) view.findViewById(R.id.cbx2);
        cbx3                        = (CheckBox) view.findViewById(R.id.cbx3);

        spn_type_promotion          = (Spinner) view.findViewById(R.id.spn_type_promotion);

        edt_promotional_merchandise = (EditText) view.findViewById(R.id.edt_promotional_merchandise);

        spn_type_exhibit            = (Spinner) view.findViewById(R.id.spn_type_exhibit);
        edt_box_promotion           = (EditText) view.findViewById(R.id.edt_box_promotion);
        spn_resourse_ragasa         = (Spinner) view.findViewById(R.id.spn_resourse_ragasa);
        radio_group_exhibit         = (RadioGroup) view.findViewById(R.id.radio_group_exhibit);
        radio_group_activation      = (RadioGroup) view.findViewById(R.id.radio_group_activation);
        radio_group_sku             = (RadioGroup) view.findViewById(R.id.radio_group_sku);

        radio_group_sku.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.radio_sku_yes:
                        view.findViewById(R.id.lyt_sku_offers).setVisibility(View.VISIBLE);
                        capture_sku = true;
                        break;
                    default:
                        view.findViewById(R.id.lyt_sku_offers).setVisibility(View.GONE);
                        capture_sku = false;
                }
            }
        });

        //Spinner para product type - - - - - - - - - - - - - - - - - - - -
        util.SpinnerAdapter spinnerAdapterProblemProduct = new SpinnerAdapter(getActivity(),
                product_array,
                SpinnerAdapter.SPINNER_TYPE.PRODUCTS);
        spn_product.setAdapter(spinnerAdapterProblemProduct);

        util.SpinnerAdapter spinnerAdapterProblemType = new SpinnerAdapter(getActivity(),
                Promotion.getAllInMaps(getActivity()),
                SpinnerAdapter.SPINNER_TYPE.PROBLEM_TYPE);
        spn_type_promotion.setAdapter(spinnerAdapterProblemType);

        util.SpinnerAdapter spinnerAdapterExhibit = new SpinnerAdapter(getActivity(),
                Exhibition.getAllInMaps(getActivity()),
                SpinnerAdapter.SPINNER_TYPE.EXHIB_TYPE);
        spn_type_exhibit.setAdapter(spinnerAdapterExhibit);

        edt_sku_offers  = (EditText) view.findViewById(R.id.edt_sku_offers);

        return view;
    }

    public void setupStartOfferDateDialog(){
        edt_rp_start_offers.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        dlg_riv_offers_start = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                edt_rp_start_offers.setText(""+dayOfMonth+"-"+monthOfYear+"-"+year);
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    public void setupEndOfferDateDialog(){
        edt_rp_end_offers.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        dlg_riv_offers_end = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                edt_rp_end_offers.setText(""+dayOfMonth+"-"+monthOfYear+"-"+year);
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
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

    public boolean validateData(){
        if (this.edt_price_public.getText().toString().isEmpty()){
            this.edt_price_public.setError(getString(R.string.error_empty_field));
            return false;
        }
        if (this.edt_price_offers.getText().toString().isEmpty()){
            this.edt_price_offers.setError(getString(R.string.error_empty_field));
            return false;
        }
        if (this.edt_rp_start_offers.getText().toString().isEmpty()){
            this.edt_rp_start_offers.setError(getString(R.string.error_empty_field));
            return false;
        }
        if (this.edt_rp_end_offers.getText().toString().isEmpty()){
            this.edt_rp_end_offers.setError(getString(R.string.error_empty_field));
            return false;

        }
        if (capture_sku){
            if (this.edt_sku_offers.getText().toString().isEmpty()){
                this.edt_sku_offers.setError(getString(R.string.error_empty_field));
                return false;
            }
        }
        if (this.edt_box_promotion.getText().toString().isEmpty()){
            this.edt_box_promotion.setError(getString(R.string.error_empty_field));
            return false;
        }

        return true;
    }

    public void saveMeasure() {

        if (!validateData())
            return;

        Map<String,String> data = new HashMap<String, String>();

        data.put("id_visit", TrackerManager.sharedInstance().getCurrent_pdv().get("visit_id"));
        data.put("id_product",((Map<String,String>) this.spn_product.getSelectedItem()).get("id"));
        data.put("name_product",((Map<String,String>) this.spn_product.getSelectedItem()).get("name"));

        data.put("public_price", Utilities.validateWithZeroDefault(this.edt_price_public.getText().toString()));
        data.put("promotion_price",Utilities.validateWithZeroDefault(this.edt_price_offers.getText().toString()));

        data.put("promotion_start", DatesHelper.dateFormattedWithYearMonthAndDay(edt_rp_start_offers.getText().toString()));
        data.put("promotion_end",DatesHelper.dateFormattedWithYearMonthAndDay(edt_rp_end_offers.getText().toString()));
        data.put("id_graphic_material",recoverDataFromCheckBoxes());

        switch (radio_group_activation.getCheckedRadioButtonId()){
            case R.id.radio_activation_yes:
                data.put("activation","1");
                break;
            case R.id.radio_activation_no:
                data.put("activation","0");
                break;
        }

        data.put("id_promotion_type",((Map<String,String>) this.spn_type_promotion.getSelectedItem()).get("id"));

        switch (radio_group_sku.getCheckedRadioButtonId()){
            case R.id.radio_sku_yes:
                data.put("differed_sku","1");
                break;
            case R.id.radio_sku_no:
                data.put("differed_sku","0");
                break;
        }

        data.put("mechanics",Utilities.validateWithZeroDefault(this.edt_promotional_merchandise.getText().toString()));

        switch (radio_group_exhibit.getCheckedRadioButtonId()){
            case R.id.radio_exhibit_yes:
                data.put("exhibition","1");
                break;
            case R.id.radio_exhibit_no:
                data.put("exhibition","0");
                break;
        }

        if (capture_sku)
            data.put("sku",Utilities.validateWithBlankSpaceDefault(this.edt_sku_offers.getText().toString()));

        data.put("id_exhibition_type",((Map<String,String>) this.spn_type_exhibit.getSelectedItem()).get("id"));
        data.put("existence",Utilities.validateWithZeroDefault(this.edt_box_promotion.getText().toString()));
        data.put("evidence",imageEncode);
        data.put("type","RIVAL");

        data.put("id_resource_type","1");
        data.put("own_id_product",product_id);

        data.put("type","RIVAL");

        Log.d(TAG,"Data to insert: "+data.toString());

        Offers.insertFromMap(getActivity(), data);

        getActivity().onBackPressed();

    }

    public String recoverDataFromCheckBoxes(){
        String graphic_material="";
        if(this.cbx1.isChecked()){
            graphic_material = "cenefa";
        }else if(this.cbx2.isChecked()){
            if(graphic_material.equals("")){
                graphic_material += "copete";
            }else{
                graphic_material += ";copete";
            }
        }else if(this.cbx3.isChecked()){
            if(graphic_material.equals("")){
                graphic_material += "folleto";
            }else{
                graphic_material += ";folleto";
            }
        }
        return graphic_material;
    }
}
