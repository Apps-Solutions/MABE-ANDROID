package com.sellcom.tracker;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.ByteArrayOutputStream;


/**
 * Created by hugo.figueroa 23/01/15.
 */
public class FragmentPartialPayDialog extends DialogFragment implements View.OnClickListener{

    final static public String TAG = "partial_pay_dialog";
    private ImageView imgEvidenceImage;
    private Button btnOk, btnCancel, btnRetry;
    private Bitmap image;
    private Context context;
    setSetImgPhoto setSetImgPhoto;

    public FragmentPartialPayDialog() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();

// safety check
        if (getDialog() == null) {
            return;
        }
        setCancelable(false);
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

// ... other stuff you want to do in your onStart() method
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = getActivity();
        View view = inflater.inflate(R.layout.fragment_partial_pay_dialog, container, false);
        imgEvidenceImage = (ImageView)view.findViewById(R.id.imgEvidenceImage);
        imgEvidenceImage.setImageBitmap(image);
        btnOk = (Button)view.findViewById(R.id.btnOk);
        btnCancel = (Button)view.findViewById(R.id.btnCancel);
        btnRetry = (Button)view.findViewById(R.id.btnRetry);
        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnRetry.setOnClickListener(this);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return view;
    }

    public void photoResult(Bitmap data) {
       image = data;
    }


    @Override
    public void onClick(View view) {

        FragmentPartialPay partialPay = new FragmentPartialPay();
        switch (view.getId()){
            case R.id.btnOk:
                setSetImgPhoto.setImgPhoto(image);
                dismiss();
                break;
            case R.id.btnCancel:
                dismiss();
                break;
            case R.id.btnRetry:
                setSetImgPhoto.retry();
                dismiss();
                break;
        }
    }


    /*
    *
    * Interfaz creada para comunicacion con FragmentPadre
     */
    public interface setSetImgPhoto{
        public void setImgPhoto(Bitmap bitmap);
        public void retry();
    }

    public void setSetSetImgPhotoListener(setSetImgPhoto listener) {
        setSetImgPhoto = listener;
    }

}
