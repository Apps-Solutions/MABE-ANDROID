package com.sellcom.tracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.common.base.Splitter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import async_request.METHOD;
import async_request.RequestManager;
import async_request.UIResponseListenerInterface;
import database.DataBaseHelper;
import database.models.Order;
import database.models.OrderDetail;
import database.models.Receipt;
import database.models.Receiving;
import database.models.ReceivingItem;
import database.models.Sale;
import database.models.SaleItem;
import database.models.Session;
import database.models.User;
import util.CaptureSignature;
import util.DatesHelper;
import util.DigitFilter;
import util.ReceiptAdapter;
import util.TrackerManager;
import util.Utilities;


public class FragmentAssortmentAndQuality extends DialogFragment implements View.OnClickListener{

    public Map<String,String> product;

    EditText            edt_current_existence,edt_box_shelf,edt_box_exhibit,
                        edt_box_total,edt_price_to_public;

    Button              btn_add_product,btn_cancel;
    int                 exh_boxes,shelf_boxes;
    boolean             isUpdate;

    NotifyAddProduct   listener;

    public final static String TAG = "DIALOG_ASSORTMENT_QUALITY";


    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.AnimatedDialog;
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_assortment_and_quality, container, false);

        if (view != null) {

            isUpdate                = false;

            TextView    header      =   (TextView)view.findViewById(R.id.txt_dialog_header);
            header.setText(product.get("name"));

            shelf_boxes = exh_boxes = 0;

            btn_add_product         =   (Button)view.findViewById(R.id.btn_add_product);
            btn_cancel              =   (Button)view.findViewById(R.id.btn_cancel);

            edt_current_existence   =   (EditText)view.findViewById(R.id.edt_current_existence);

            if (!edt_current_existence.getText().toString().isEmpty()){ // Previous info, is update
                btn_add_product.setText("ACTUALIZAR");
                isUpdate    = true;
            }

            edt_current_existence.setText(product.get("edt_current_existence"));

            edt_box_shelf           =   (EditText)view.findViewById(R.id.edt_box_shelf);
            edt_box_shelf.setText(product.get("edt_box_shelf"));

            edt_box_exhibit         =   (EditText)view.findViewById(R.id.edt_box_exhibit);
            edt_box_exhibit.setText(product.get("edt_box_exhibit"));

            edt_box_total           =   (EditText)view.findViewById(R.id.edt_box_total);
            edt_box_total.setText(product.get("edt_box_total"));

            edt_price_to_public     =   (EditText)view.findViewById(R.id.edt_price_to_public);
            edt_price_to_public.setText(product.get("edt_price_to_public"));

            btn_add_product.setOnClickListener(this);
            btn_cancel.setOnClickListener(this);

            edt_box_shelf.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    Log.d(TAG,"OnTextChange: "+charSequence.toString());
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    Log.d(TAG,"AfterChange: "+editable.toString());
                    String str = editable.toString();

                    if (str.isEmpty())
                        str = "0";

                    shelf_boxes = Integer.parseInt(str);
                    int total = exh_boxes + shelf_boxes;
                    edt_box_total.setText(""+total);

                }
            });

            edt_box_exhibit.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    Log.d(TAG,"OnTextChange: "+charSequence.toString());
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    Log.d(TAG,"AfterChange: "+editable.toString());
                    String str = editable.toString();

                    if (str.isEmpty())
                        str = "0";

                    exh_boxes = Integer.parseInt(str);
                    int total = exh_boxes + shelf_boxes;
                    edt_box_total.setText(""+total);
                }
            });
        }

        return view;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_add_product:
                Log.d(TAG,"Agregar producto");
                if (IsFormComplete()){
                    product.put("edt_current_existence",edt_current_existence.getText().toString());
                    product.put("edt_box_shelf",edt_box_shelf.getText().toString());
                    product.put("edt_box_exhibit",edt_box_exhibit.getText().toString());
                    product.put("edt_box_total",edt_box_total.getText().toString());
                    product.put("edt_price_to_public",edt_price_to_public.getText().toString());
                    product.put("expiration_date","20151018");
                    listener.updateInfoProductAdd(isUpdate);
                    dismiss();
                }
                else
                    Log.d(TAG,"Error en traer información");

                break;
            case R.id.btn_cancel:
                Log.d(TAG,"Salir");
                dismiss();
                break;
        }
    }

    public boolean IsFormComplete() {

        if (edt_current_existence.getText().toString().isEmpty()) {
            edt_current_existence.setError(getString(R.string.error_empty_field));
            return false;
        }
        if (edt_box_shelf.getText().toString().isEmpty()) {
            edt_box_shelf.setError(getString(R.string.error_empty_field));
            return false;
        }
        if (edt_box_exhibit.getText().toString().isEmpty()) {
            edt_box_exhibit.setError(getString(R.string.error_empty_field));
            return false;
        }
        if (edt_box_total.getText().toString().isEmpty()) {
            edt_box_total.setError(getString(R.string.error_empty_field));
            return false;
        }
        if (edt_price_to_public.getText().toString().isEmpty()) {
            edt_price_to_public.setError(getString(R.string.error_empty_field));
            return false;
        }
        else
            return true;
    }

    public interface NotifyAddProduct{
        public void updateInfoProductAdd(boolean isUpdate);
    }
}
