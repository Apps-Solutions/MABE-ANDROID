package com.sellcom.tracker;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import async_request.ConfirmationDialogListener;
import async_request.DecisionDialogWithListener;
import async_request.METHOD;
import async_request.RequestManager;
import async_request.UIResponseListenerInterface;
import database.DataBaseManager;
import database.models.CancelledOrder;
import database.models.OrderSent;
import database.models.PendingOrder;
import database.models.PendingOrderProduct;
import database.models.Product;
import database.models.Session;
import database.models.User;
import location.GPSTracker;
import util.DatesHelper;
import util.FilterAdapter_Map;
import util.PendingOrderProductAdapter;
import util.ReceiptAdapter;
import util.TrackerManager;

public class FragmentPreOrders extends Fragment implements UIResponseListenerInterface, DecisionDialogWithListener, FragmentReceiptPreview.NotifyReloadInvoicesArrayListener, ConfirmationDialogListener {

    final static public String TAG = "preorders";

    ListView            lst_product_order;
    Map<String,String>  pre_order;
    String              current_order_id;

    public Button       btn_use_order,
                        btn_cancel_order;

    private TextView    txt_order_id,
                        txt_order_subtotal,
                        txt_order_tax,
                        txt_order_total,
                        txt_order_date;

    static Context context;
    View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_pre_orders, container, false);

        pre_order    =  (PendingOrder.getPendingOrderFromPDV(getActivity(), TrackerManager.sharedInstance().getCurrent_pdv().get("pdv_id"))).get(0);

        List<Map<String,String>>      prod_order   = PendingOrderProduct.getAllFromOrderInMaps(getActivity(),pre_order.get("id"));

        txt_order_id            = (TextView)view.findViewById(R.id.txt_order_id);
        txt_order_id.setText(pre_order.get("id"));

        txt_order_subtotal      = (TextView)view.findViewById(R.id.txt_order_subtotal);
        txt_order_subtotal.setText("$ "+pre_order.get("subtotal"));

        txt_order_tax           = (TextView)view.findViewById(R.id.txt_order_tax);
        txt_order_tax.setText("$ "+pre_order.get("tax"));

        txt_order_total         = (TextView)view.findViewById(R.id.txt_order_total);
        txt_order_total.setText("$ "+pre_order.get("total"));

        txt_order_date          = (TextView)view.findViewById(R.id.txt_order_date);
        txt_order_date.setText(pre_order.get("date"));

        btn_use_order           = (Button) view.findViewById(R.id.btn_use_order);
        btn_cancel_order        = (Button) view.findViewById(R.id.btn_cancel_order);

        lst_product_order       = (ListView) view.findViewById(R.id.lst_product_order);
        lst_product_order.setAdapter(new PendingOrderProductAdapter(getActivity(),prod_order));

        btn_use_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPayDialog();
            }
        });

        btn_cancel_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCancelPreOrderDialog();
            }
        });
        return view;
    }

    public void showPayDialog(){

        if (DataBaseManager.sharedInstance().getNotPaidInvoicesFromPDV(TrackerManager.sharedInstance().getCurrent_pdv().get("pdv_id")) != null){
            Toast.makeText(getActivity(),getActivity().getString(R.string.req_man_error_order_pending_invoices),Toast.LENGTH_SHORT).show();
            return;
        }

        DataBaseManager.sharedInstance().passPendingOrderToActiveOrderSent(pre_order);

        Bundle bundle = new Bundle();
        bundle.putString("order_id",pre_order.get("id"));
        bundle.putString("invoice_id","CLEAR");
        bundle.putString("pdv_name","");
        bundle.putString("pdv_id",TrackerManager.sharedInstance().getCurrent_pdv().get("pdv_id"));
        bundle.putString("date",DatesHelper.getStringDate(new Date()));
        bundle.putDouble("total", Double.parseDouble(pre_order.get("total")));

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        Fragment fragment = getFragmentManager().findFragmentByTag(FragmentReceiptPreview.TAG);

        if (fragment != null)
            fragmentTransaction.remove(fragment);

        fragmentTransaction.addToBackStack(null);

        FragmentReceiptPreview newFragment = FragmentReceiptPreview.newInstance(Integer.parseInt(pre_order.get("id")), ReceiptAdapter.ORDER);
        newFragment.listener    = this;
        newFragment.isPreOrder  = true;
        newFragment.setArguments(bundle);
        newFragment.show(fragmentTransaction, FragmentReceiptPreview.TAG);
    }

    public void showCancelPreOrderDialog(){
        RequestManager.sharedInstance().showDecisionDialogWithListener(getActivity().getString(R.string.req_man_confirm_cancel_order),
                getActivity(), this);
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(NavigationDrawerFragment.WORK_PLAN);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void prepareRequest(METHOD method, Map<String, String> params) {

        String token      = Session.getSessionActive(getActivity()).getToken();
        String username   = User.getUser(getActivity(), Session.getSessionActive(getActivity()).getUser_id()).getEmail();
        params.put("request", method.toString());
        params.put("user", username);
        params.put("token", token);

        RequestManager.sharedInstance().setListener(this);

        RequestManager.sharedInstance().makeRequestWithDataAndMethod(params, method);
    }

    @Override
    public void decodeResponse(String stringResponse) {
        try {
            JSONObject resp          = new JSONObject(stringResponse);

            if (resp.getString("method").equalsIgnoreCase(METHOD.CANCEL_ORDER.toString())) {
                if (resp.getString("error").isEmpty())
                    OrderSent.updateOrderSentToInactiveAndSent(getActivity(), current_order_id);

                RequestManager.sharedInstance().showConfirmationDialogWithListener("El pedido " + current_order_id + " ha sido cancelado exitosamente", getActivity(), this);
                current_order_id    = "";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void responseFromDecisionDialog(String confirmMessage, String option) {
        if (option.equalsIgnoreCase("OK")){

            current_order_id    =   pre_order.get("id");

            DataBaseManager.sharedInstance().passPendingOrderToInactiveOrderSent(pre_order);

            Map<String,String> params              = new HashMap<String, String>();
            params.put("order_id",current_order_id);

            prepareRequest(METHOD.CANCEL_ORDER, params);
        }
        else{

        }
    }

    @Override
    public void notifyPaymentSucceed() {
        OrderSent.updateOrderSentToInactiveAndSent(getActivity(), current_order_id);
        getActivity().onBackPressed();
    }

    @Override
    public void okFromConfirmationDialog(String message) {
        getActivity().onBackPressed();
    }
}