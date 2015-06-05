package com.sellcom.tracker;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;


/**
 * Created by hugo.figueroa on 04/05/15.
 */
public class FragmentDialogEvidenceExtraTask extends DialogFragment implements View.OnClickListener{

    final static public String TAG = "dialog_evidence_extra_task";

    private ImageView imgEvidenceImage;
    private Button btnOk, btnCancel, btnRetry;
    private Bitmap image;
    private Context context;
    setSetImgPhoto setSetImgPhoto;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();

        setCancelable(false);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Dialog_MinWidth);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dialog_evidence_extra_task, container, false);


        imgEvidenceImage        = (ImageView)view.findViewById(R.id.imgEvidenceImage);
        imgEvidenceImage.setImageBitmap(image);

        btnOk                   = (Button)view.findViewById(R.id.btnOk);
        btnCancel               = (Button)view.findViewById(R.id.btnCancel);
        btnRetry                = (Button)view.findViewById(R.id.btnRetry);

        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnRetry.setOnClickListener(this);

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
