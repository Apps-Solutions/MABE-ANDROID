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
import android.text.InputFilter;
import android.text.InputType;
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

import org.json.JSONException;
import org.json.JSONObject;

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
import database.models.CheckIn;
import database.models.Invoice;
import database.models.Order;
import database.models.OrderDetail;
import database.models.OrderSent;
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

public class FragmentReceiptPreview extends DialogFragment implements View.OnClickListener, UIResponseListenerInterface{

    TextView receiptHeader,receiptSubtotal, receiptTaxes, receiptTotal;
    EditText edt_receipt_payable;
    boolean  isFromOrder;
    int index = 1;
    String payment_method = "1";
    String payment_type;

    Button      okButton;
    Spinner     spinner_payMethod;
    ListView    productsList;
    int         activeView;

    boolean isPreOrder  = false;

    NotifyReloadInvoicesArrayListener   listener;

    public final static int RECEIVER_SIGNATURE = 1;
    public final static int ISSUING_SIGNATURE = 2;
    public final static String TAG = "dialog_receipt_preview";
    final static int RECEIPT = 49;
    final static int PEOPLE = 50;

    String order_id,invoice_id,date,pdv_name,pdv_id,folio;
    double total;

    String[]            invoices_payment_method = {"Contado","Ficha de Depósito"};

    public static FragmentReceiptPreview newInstance(int id, int receiptType) {

        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        bundle.putInt("receipt_type", receiptType);
        FragmentReceiptPreview fragmentReceiptPreview = new FragmentReceiptPreview();
        fragmentReceiptPreview.setArguments(bundle);

        return fragmentReceiptPreview;
    }

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null) {
            index = 1;
            order_id        = bundle.getString("order_id");
            invoice_id      = bundle.getString("invoice_id");
            folio           = bundle.getString("folio");
            pdv_id          = bundle.getString("pdv_id");
            pdv_name        = bundle.getString("pdv_name");
            date            = bundle.getString("date");
            total           = bundle.getDouble("total");
        }
        activeView = RECEIPT;
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

        View view = inflater.inflate(R.layout.fragment_receipt, container, false);

        setCancelable(false);

        if (view != null) {

            okButton        = (Button) view.findViewById(R.id.pay_button);

            receiptHeader       = (TextView) view.findViewById(R.id.txt_receipt_header);
            DecimalFormat form = new DecimalFormat("0.00");
            ((TextView) view.findViewById(R.id.txt_receipt_total)).setText("$"+form.format(total));

            edt_receipt_payable = (EditText)view.findViewById(R.id.edt_receipt_payable);
            edt_receipt_payable.setText(""+String.format(Locale.ENGLISH, "%.2f", total));
            edt_receipt_payable.setFilters(new InputFilter[]{new DigitFilter(7,2)});


            ((TextView) view.findViewById(R.id.txt_receipt_client)).setText(pdv_name);
            ((TextView) view.findViewById(R.id.txt_receipt_date)).setText(date);

            receiptSubtotal = (TextView) view.findViewById(R.id.receipt_subtotal);
            receiptTaxes = (TextView) view.findViewById(R.id.receipt_tax);
            receiptTotal = (TextView) view.findViewById(R.id.receipt_total);
            productsList = (ListView) view.findViewById(R.id.products_list);

            okButton.setOnClickListener(this);

            if (invoice_id.equalsIgnoreCase("CLEAR")) {
                isFromOrder = true;
                receiptHeader.setText(getString(R.string.receipt_order));
                ((TextView) view.findViewById(R.id.txt_receipt_folio)).setText(order_id);
                spinner_payMethod = (Spinner) view.findViewById(R.id.sp_receipt_payment_method);
                (view.findViewById(R.id.sp_receipt_payment_method_invoices)).setVisibility(View.GONE);
            }
            else{
                isFromOrder = false;
                spinner_payMethod = (Spinner) view.findViewById(R.id.sp_receipt_payment_method_invoices);
                (view.findViewById(R.id.sp_receipt_payment_method)).setVisibility(View.GONE);
                receiptHeader.setText(getString(R.string.receipt_invoice));
                ((TextView) view.findViewById(R.id.txt_receipt_folio)).setText(folio);
                edt_receipt_payable.setText(""+String.format(Locale.ENGLISH, "%.2f", total));
                edt_receipt_payable.setEnabled(false);
            }


            spinner_payMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    if (isFromOrder) {
                        index = position + 1;
                        payment_method = "" + position;
                        if (index == 1)
                            payment_type    = "CASH";
                        else
                            payment_type    = "CREDIT";
                    }
                    else{
                        if (position == 0)
                            payment_method = "" + position;
                        else{
                            payment_method = "" + 2;
                        }
                    }
                    /*Log.d(TAG,"Index: "+index);

                    if (index == 2) {
                        edt_receipt_payable.setEnabled(false);
                        edt_receipt_payable.setText("");
                    }
                    else
                        edt_receipt_payable.setEnabled(true);
                    */
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                    payment_method = "1";
                }

            });

        }

        return view;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.pay_button:
                Log.d(TAG,"Enviar pago index: "+index);
                if (!edt_receipt_payable.getText().toString().isEmpty() ||
                        (edt_receipt_payable.getText().toString().isEmpty() && index == 2)){
                    Log.d(TAG,"Enviar pago 1");
                    send_payment();
                }
                else
                    RequestManager.sharedInstance().showErrorDialog(getString(R.string.req_man_error_empty_payment),getActivity());
                break;
        }
    }

    public void send_payment(){
        Map<String,String> params = new HashMap<String, String>();

        String payment_date = DatesHelper.getStringDate(new Date());

        params.put("date",payment_date);
        params.put("pdv_id", pdv_id);
        params.put("method_id","" + index);

        String money = edt_receipt_payable.getText().toString().replace(',', '.');

        if (money.isEmpty() || index == 2)
            params.put("total","0.001");
        else if (money.startsWith("$")){
            params.put("total",money.substring(1));
        }
        else{
            params.put("total", edt_receipt_payable.getText().toString());
        }

        payment_method  = ""+index;

        if (isFromOrder) {
            params.put("order_id", order_id);
            OrderSent.updateOrderSentToNoSentPaidAndNewType(getActivity(),order_id,payment_method,payment_date);
        }
        else {
            params.put("invoice_id", invoice_id);
            Invoice.updateInvoiceToStatusPaid(getActivity(),invoice_id,payment_method,payment_date);
        }
        prepareRequest(METHOD.SEND_PAYMENT,params);
    }

    @Override
    public void prepareRequest(METHOD method, Map<String, String> params) {

        String token      = Session.getSessionActive(getActivity()).getToken();
        String username   = User.getUser(getActivity(),Session.getSessionActive(getActivity()).getUser_id()).getEmail();
        params.put("request", method.toString());
        params.put("user", username);
        params.put("token", token);

        RequestManager.sharedInstance().setListener(this);

        RequestManager.sharedInstance().makeRequestWithDataAndMethod(params, method);
    }

    @Override
    public void decodeResponse(String stringResponse) {
        Log.d(TAG,"Pago enviado exitosamente");

        JSONObject  resp            = null;
        String      sent_message    = "";
        try {
            resp = new JSONObject(stringResponse);

            if(resp.getString("error").isEmpty()){
                if (order_id.equalsIgnoreCase("CLEAR"))
                    Invoice.updateInvoiceToStatusSent(getActivity(),invoice_id);
                else
                    OrderSent.updateOrderSentToSent(getActivity(),order_id);
            }

            sent_message    = "Pago enviado exitosamente.";

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Atención");
            builder.setMessage(sent_message);
            builder.setNeutralButton("OK",new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int id) {
                    dismiss();
                    listener.notifyPaymentSucceed();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public interface NotifyReloadInvoicesArrayListener{
        public void notifyPaymentSucceed();
    }
}