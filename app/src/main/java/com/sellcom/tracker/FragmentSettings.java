package com.sellcom.tracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.zebra.sdk.comm.BluetoothConnectionInsecure;
import com.zebra.sdk.comm.Connection;

import java.util.ArrayList;
import java.util.Locale;

import async_request.RequestManager;

/**
 * Created by juanc.jimenez on 07/05/14.
 */
public class FragmentSettings extends Fragment implements AdapterView.OnItemSelectedListener {

    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    Toast   toast;
    String btMacAddress;

    ArrayList<String> spinnerArray      = new ArrayList<String>();
    ArrayList<String> macAddressArray   = new ArrayList<String>();
    ArrayAdapter<String> spinnerAdapter;

    LinearLayout    changeLanguage;
    Spinner         languageSelector;
    Button          btn_find_printer;
    boolean         selectionFromUser;
    int             position_selected;

    public static final String TAG = "settings";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {



        final View view = inflater.inflate(R.layout.fragment_settings, container, false);

        if (view != null) {

            changeLanguage = (LinearLayout) view.findViewById(R.id.change_language);
            languageSelector = (Spinner) view.findViewById(R.id.language_selector);

            selectionFromUser = false;
            String[] languages = new String[]{getString(R.string.spanish_label), getString(R.string.english_label)};
            ArrayAdapter<String> languageAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_activated_1, languages);
            languageSelector.setAdapter(languageAdapter);

            Locale current = getResources().getConfiguration().locale;

            if(String.valueOf(current).equals("en")) {
                languageSelector.setSelection(1);
            }

            languageSelector.setOnItemSelectedListener(this);

            spinnerAdapter = new ArrayAdapter<String>(
                    getActivity(), android.R.layout.simple_spinner_item, spinnerArray);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            Spinner printerSpinner = (Spinner) view.findViewById(R.id.bt_spinner_preferences);
            printerSpinner.setAdapter(spinnerAdapter);
            String mCurrentPrinter = RequestManager.sharedInstance().getPreferencesValueForKey("BluetoothMacAddress");
            if (!mCurrentPrinter.equalsIgnoreCase("CLEAR")) {
                spinnerArray.add(0, mCurrentPrinter);
            }
            spinnerAdapter.notifyDataSetChanged();

            printerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        position_selected   = position;
                    } catch (Exception e) {
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            btn_find_printer = (Button) view.findViewById(R.id.bt_button_preferences);
            btn_find_printer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mBluetoothAdapter == null) {
                        // Pop up toast saying that there is no bt in this device
                    } else {
                        if (!mBluetoothAdapter.isEnabled()) {
                            Toast.makeText(getActivity(),getActivity().getString(R.string.sts_enabling_bluetooth),Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Enabling bluetooth");
                            mBluetoothAdapter.enable();
                        } else {
                            Toast.makeText(getActivity(),getActivity().getString(R.string.sts_enabled_bluetooth),Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Bluetooth is already enabled");
                        }

                        spinnerArray.clear();
                        macAddressArray.clear();

                        mBluetoothAdapter.startDiscovery();

                        // Register the BroadcastReceiver
                        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                        getActivity().registerReceiver(mReceiver, filter);

                        ProgressBar progressBar = (ProgressBar)view.findViewById(R.id.pgb_searching_printer);
                        progressBar.setVisibility(View.VISIBLE);
                        btn_find_printer.setText(getActivity().getString(R.string.sts_searching_printer));
                        btn_find_printer.setEnabled(false);
                    }
                }
            });

            Button test_button = (Button) view.findViewById(R.id.test);
            test_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (macAddressArray.isEmpty()){
                        toast = Toast.makeText(getActivity(),
                                "No se han encontrado impresoras", Toast.LENGTH_SHORT);
                        toast.show();
                        return;
                    }
                    else {
                        btMacAddress = macAddressArray.get(position_selected);

                        if (btMacAddress != null && !btMacAddress.equals("CLEAR")) {
                            printTestReceipt(btMacAddress);
                        } else {
                            toast = Toast.makeText(getActivity(),
                                    "Seleccione una impresora", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                }
            });

            Button save_printer = (Button) view.findViewById(R.id.btn_save_printer);
            save_printer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (macAddressArray.isEmpty()){
                        toast = Toast.makeText(getActivity(),
                                "No se han encontrado impresoras", Toast.LENGTH_SHORT);
                        toast.show();
                        return;
                    }
                    else {
                        Log.d(TAG, "MAC Address selected: " + macAddressArray.get(position_selected));
                        RequestManager.sharedInstance().saveInPreferencesKeyAndValue("BluetoothMacAddress", macAddressArray.get(position_selected));

                        String message = getActivity().getString(R.string.sts_printer_saved) + macAddressArray.get(position_selected);
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(NavigationDrawerFragment.SETTINGS);
    }

    public void changeLang(String lang) {
        if(lang.equals(""))
            return;

        Locale locale       = new Locale(lang);
        Locale.setDefault(locale);
        Resources res       = getResources();
        DisplayMetrics dm   = res.getDisplayMetrics();
        Configuration conf  = res.getConfiguration();
        conf.locale         = locale;
        res.updateConfiguration(conf, dm);

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        if (selectionFromUser) {
            String currentLanguage = (String) adapterView.getSelectedItem();
            if (currentLanguage.equals(getString(R.string.english_label)))
                changeLang("en");
            else
                changeLang("es");

            getActivity().recreate();
        }
        selectionFromUser = true;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Started BroadcastReceiver");
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                try {
                    spinnerArray.add(device.getName() + "\n" + device.getAddress());
                    macAddressArray.add(device.getAddress());
                    spinnerAdapter.notifyDataSetChanged();

                    Log.d(TAG,"MAC ADDRESS : "+device.getAddress());
                    Log.d(TAG,"MAC ADDRESS ARRAY SIZE: "+macAddressArray.size());
                    Log.d(TAG,"MAC ADDRESS ARRAY ITEM: "+macAddressArray.get(0));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private void printTestReceipt(final String theBtMacAddress) {

        new Thread(new Runnable() {
            public void run() {

                try {

                    Log.d(TAG,"MAC ADDRESS IN TEST : "+theBtMacAddress);

                    Connection thePrinterConn = new BluetoothConnectionInsecure(theBtMacAddress);

                    Looper.prepare();

                    thePrinterConn.open();

                    String cpclData = "! 0 200 200 200 1\r\n"
                            + "ML 30\r\n"
                            + "TEXT 5 0 30 40\r\n"
                            + "Probando Impresora \r\n"
                            + "Probando Impresora \r\n"
                            + "Probando Impresora \r\n"
                            + "ENDML\r\n"
                            + "PRINT\r\n";

                    thePrinterConn.write(cpclData.getBytes());

                    Thread.sleep(2500);

                    thePrinterConn.close();

                    Looper.myLooper().quit();

                } catch (Exception e) {

                    e.printStackTrace();
                    toast = Toast.makeText(getActivity(),
                            "Imposible contactar con la impresora", Toast.LENGTH_SHORT);
                    toast.show();

                }
            }
        }).start();
    }
}
