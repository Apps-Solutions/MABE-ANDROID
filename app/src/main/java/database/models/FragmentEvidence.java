package com.sellcom.tracker;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;

import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import android.widget.Button;

import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import database.models.Evidence;
import database.models.EvidenceBrandProduct;
import database.models.EvidenceProduct;
import util.EvidenceAdapter;
import util.EvidenceBrandProductAdapter;
import util.EvidenceProductAdapter;

/**
 * Created by juanc.jimenez on 08/09/14.
 */
public class FragmentEvidence extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener{

    //UI Elements
    TextView mTitle;
    TextView mContinueButton;
    Spinner evidenceSpinner;
    Button mCamera;

    //Adapter
    EvidenceAdapter mEvidenceAdapter;
    EvidenceBrandProductAdapter mBrandAdapter;
    EvidenceProductAdapter mProductAdapter;

    //Data
    List<Evidence> evidences;
    List<EvidenceBrandProduct> evidenceBrandProduct;
    List<EvidenceProduct> evidenceProduct;

    //Number of clicks
    int clicks = 0;


    private BroadcastReceiver mUpdateUIReceiver = new BroadcastReceiver() {

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            Bitmap mImage = (Bitmap)extras.get("data");
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.fragment_evidence, null);
            BitmapDrawable bpd = new BitmapDrawable(mImage);

            mTitle.setVisibility(View.GONE);
            evidenceSpinner.setVisibility(View.GONE);
            mCamera.setVisibility(View.GONE);
            mContinueButton.setVisibility(View.GONE);

            view.setBackground(bpd);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                mUpdateUIReceiver,
                new IntentFilter("picture-taken"));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_evidence, container, false);

        evidences = Evidence.getAll(getActivity());

        mEvidenceAdapter = new EvidenceAdapter(getActivity(), evidences);

        mTitle = (TextView)view.findViewById(R.id.title_evidence);
        evidenceSpinner = (Spinner)view.findViewById(R.id.spinner_evidence);
        mCamera = (Button)view.findViewById(R.id.evidence_camera);
        mContinueButton = (TextView)view.findViewById(R.id.continue_button);

        mCamera.setOnClickListener(this);
        mCamera.setVisibility(View.GONE);


        evidenceSpinner.setAdapter(mEvidenceAdapter);

        evidenceSpinner.setOnItemClickListener(this);

        return view;
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroyView();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mUpdateUIReceiver);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        clicks++;
        if(clicks == 1){
            evidenceBrandProduct = EvidenceBrandProduct.getByEvidenceId(getActivity(), evidences.get(position).getId());
            mBrandAdapter = new EvidenceBrandProductAdapter(getActivity(), evidenceBrandProduct);
            evidenceSpinner.setAdapter(mBrandAdapter);
            evidenceSpinner.notifyAll();

        }else if(clicks == 2){
            evidenceProduct = EvidenceProduct.getByProductBrandId(getActivity(), evidenceBrandProduct.get(position).getId());
            mProductAdapter = new EvidenceProductAdapter(getActivity(), evidenceProduct);
            evidenceSpinner.setAdapter(mProductAdapter);
            evidenceSpinner.notifyAll();
        }else if(clicks == 3){
            clicks = 0;
            mCamera.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        dispatchTakePictureIntent();
    }
}
