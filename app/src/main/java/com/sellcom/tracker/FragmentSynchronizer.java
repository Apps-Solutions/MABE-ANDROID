package com.sellcom.tracker;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
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
import android.widget.Button;
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

import OfflineMode.Synchronizer;
import async_request.ConfirmationDialogListener;
import async_request.METHOD;
import async_request.RequestManager;
import async_request.UIResponseListenerInterface;
import database.DataBaseManager;
import database.models.Fueling;
import database.models.PartialPay;
import database.models.Session;
import database.models.User;
import util.DatesHelper;
import util.Utilities;


public class FragmentSynchronizer extends Fragment implements View.OnClickListener, Synchronizer.UploadInformationInterface, DataBaseManager.InitialInformationDownloadInterface {

    final static public String TAG = "SYNCHRONIZER";

    private Button      btn_synchronize;
    private TextView    txv_last_synchronizer;

    public FragmentSynchronizer() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) getActivity()).onSectionAttached(NavigationDrawerFragment.SYNCHRONIZER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view       = inflater.inflate(R.layout.fragment_synchronizer, container, false);

        SharedPreferences sharedPref        = getActivity().getPreferences(Context.MODE_PRIVATE);
        String last_sinchronizer = sharedPref.getString("last_sinchronizer","---- -- -- --:--");

        txv_last_synchronizer = (TextView) view.findViewById(R.id.txv_last_synchronizer);
        txv_last_synchronizer.setText(getActivity().getString(R.string.sync_last_synchronization) + " " + last_sinchronizer);

        btn_synchronize         = (Button)view.findViewById(R.id.btn_synchronize);
        btn_synchronize.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btn_synchronize:
                startSynchronize();
                break;
        }
    }

    public void startSynchronize(){
        Synchronizer sync = new Synchronizer(getActivity());
        sync.setListener(this);
        sync.run();
    }

    @Override
    public void finishedUploadInformation() {
        Toast.makeText(getActivity(),getActivity().getString(R.string.sync_upload_info_success),Toast.LENGTH_SHORT).show();


        SharedPreferences sharedPref        = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor     = sharedPref.edit();
        editor.putString("last_sinchronizer", DatesHelper.getStringDate(new Date()));
        editor.commit();


        sharedPref        = getActivity().getPreferences(Context.MODE_PRIVATE);
        String last_sinchronizer = sharedPref.getString("last_sinchronizer", "---- -- -- --:--");

        txv_last_synchronizer.setText(getActivity().getString(R.string.sync_last_synchronization) + " " + last_sinchronizer);
        // Start downloading information
        DataBaseManager.sharedInstance().startInitialInformationDownload(getActivity(), this, false);
    }

    @Override
    public void initialInformationDownloadFinished() {
        Toast.makeText(getActivity(),getActivity().getString(R.string.sync_down_info_success),Toast.LENGTH_SHORT).show();


    }
}