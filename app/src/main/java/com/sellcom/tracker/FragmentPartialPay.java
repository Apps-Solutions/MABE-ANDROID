package com.sellcom.tracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import async_request.ConfirmationDialogListener;
import async_request.METHOD;
import async_request.RequestManager;
import async_request.UIResponseListenerInterface;
import database.DataBaseManager;
import database.models.PartialPay;
import database.models.Session;
import database.models.User;
import util.DatesHelper;
import util.ReceiptAdapter;
import util.Utilities;


/**
 * Created by hugo.figueroa 22/01/15.
 */
public class FragmentPartialPay extends DialogFragment implements   TextView.OnEditorActionListener ,
                                                                    View.OnClickListener,
                                                                    FragmentPartialPayDialog.setSetImgPhoto,
                                                                    UIResponseListenerInterface,
                                                                    ConfirmationDialogListener{

    final static public String TAG = "partial_pay";
    private static final    int THUMBNAIL_ID = 1;

    private EditText    edtMoventNumber, edtQuantity;
    private ImageView   imgCamera,imgPhoto;
    private Context     context;
    private Bitmap      image;
    private String      imageEncode = "";
    private Uri         mImageUri;
    private Thread      encodeImage;

    private String partial_pay_id;

    public FragmentPartialPay() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) getActivity()).onSectionAttached(NavigationDrawerFragment.LIQUIDATION);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view       = inflater.inflate(R.layout.fragment_partial_pay, container, false);

        context         = getActivity();
        edtMoventNumber = (EditText)view.findViewById(R.id.edtMovementNumber);
        edtQuantity     = (EditText)view.findViewById(R.id.edtQuantity);
        edtQuantity.setOnEditorActionListener(this);

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
            Utilities.hideKeyboard(context, edtQuantity);
            return true;
        }
        return false;
    }

    public void recoverDataFromUI(){
        Log.d(TAG,"Image encoded: "+image);
        if (validateInfo()){

            partial_pay_id          = ""+Calendar.getInstance().get(Calendar.MILLISECOND);
            String date             = DatesHelper.sharedInstance().getStringDate(new Date());

            PartialPay.insert(getActivity(),partial_pay_id,edtMoventNumber.getText().toString(),edtQuantity.getText().toString(),
                                date,imageEncode);

            Map<String,String> params = new HashMap<String, String>();
            params.put("number",    edtMoventNumber.getText().toString());
            params.put("total",     edtQuantity.getText().toString());
            params.put("date",      date);
            params.put("evidence",  imageEncode);

            prepareRequest(METHOD.SEND_PARTIAL_SETTLEMENT,params);
        }
    }

    public boolean validateInfo(){
        if(edtMoventNumber.getText().toString().isEmpty()){
            edtMoventNumber.setError(getString(R.string.error_empty_field));
            edtMoventNumber.requestFocus();
            return false;
        }
        if(edtQuantity.getText().toString().isEmpty()){
            edtQuantity.setError(getString(R.string.error_empty_field));
            edtQuantity.requestFocus();
            return false;
        }
        if (imageEncode == null) {
            RequestManager.sharedInstance().showErrorDialog(getString(R.string.req_man_no_evidence_partial_settlement), getActivity());
            return false;
        }
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

    private File createTemporaryFile(String part, String ext) throws Exception
    {
        File tempDir= Environment.getExternalStorageDirectory();
        tempDir=new File(tempDir.getAbsolutePath()+"/.temp/");
        if(!tempDir.exists())
        {
            tempDir.mkdir();
        }
        return File.createTempFile(part, ext, tempDir);
    }

    public Bitmap getStorageImage()
    {
        getActivity().getContentResolver().notifyChange(mImageUri, null);
        ContentResolver cr = getActivity().getContentResolver();
        Bitmap bitmap = null;
        try
        {
            bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, mImageUri);
        }
        catch (Exception e)
        {
            Toast.makeText(getActivity(), "Failed to load", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Failed to load", e);
        }
        return bitmap;
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
        /**** Request manager stub
         * 0. Recover data from UI
         * 1. Add credentials information
         * 2. Set the RequestManager listener to 'this'
         * 3. Send the request (Via RequestManager)
         * 4. Wait for it
         */

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
        try {
            JSONObject resp          = new JSONObject(stringResponse);

            if (resp.getString("method").equalsIgnoreCase(METHOD.SEND_PARTIAL_SETTLEMENT.toString())) {
                if (resp.getString("error").isEmpty())
                    PartialPay.updatePartialPayToStatusSent(getActivity(), partial_pay_id);
                RequestManager.sharedInstance().showConfirmationDialogWithListener(getString(R.string.req_man_settlement_sent), getActivity(), this);
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
