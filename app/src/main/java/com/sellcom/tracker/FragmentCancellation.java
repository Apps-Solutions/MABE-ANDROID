package com.sellcom.tracker;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import async_request.ConfirmationDialogListener;
import async_request.DecisionDialogWithListener;
import async_request.METHOD;
import async_request.RequestManager;
import async_request.UIResponseListenerInterface;
import database.DataBaseManager;
import database.models.OrderSent;
import database.models.OrderSentProduct;
import database.models.PendingOrderProduct;
import database.models.Session;
import database.models.User;
import util.PendingOrderProductAdapter;
import util.TrackerManager;

public class FragmentCancellation extends Fragment implements DecisionDialogWithListener, UIResponseListenerInterface, ConfirmationDialogListener {

    final static public String TAG = "cancellation";

                        ListView                    lst_product_order;
    public              FragmentStepVisit.LockType  locked_type;

    public              Button                      btn_cancel_order;

    private             Map<String,String>          order_info;
    private             List<Map<String,String>>    prod_order;

    private             String                      current_order_id;

    private TextView    txt_order_id,
                        txt_order_type,
                        txt_order_subtotal,
                        txt_order_tax,
                        txt_order_total,
                        txt_order_date;

    static Context context;

    public FragmentCancellation() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cancellation, container, false);

        txt_order_id            = (TextView)view.findViewById(R.id.txt_order_id);
        txt_order_type          = (TextView)view.findViewById(R.id.txt_order_type);
        txt_order_subtotal      = (TextView)view.findViewById(R.id.txt_order_subtotal);
        txt_order_tax           = (TextView)view.findViewById(R.id.txt_order_tax);
        txt_order_total         = (TextView)view.findViewById(R.id.txt_order_total);
        txt_order_date          = (TextView)view.findViewById(R.id.txt_order_date);

        if (locked_type == FragmentStepVisit.LockType.LOCKED_BY_ORDER_SENT){
            order_info   = DataBaseManager.sharedInstance().getActiveOrderInVisit(TrackerManager.sharedInstance().getCurrent_pdv().get("visit_id"));
            current_order_id        =   order_info.get("order_id");
            prod_order   = OrderSentProduct.getAllProductsInOrderSent(getActivity(),current_order_id);
            txt_order_id.setText(order_info.get("order_id"));
            txt_order_date.setText(order_info.get("date"));

        }
        else if (locked_type == FragmentStepVisit.LockType.LOCKED_BY_PRE_ORDER){
            order_info   =   (DataBaseManager.sharedInstance().getPendingOrderFromPDV(TrackerManager.sharedInstance().getCurrent_pdv().get("pdv_id"))).get(0);
            current_order_id        =   order_info.get("id");
            txt_order_id.setText(order_info.get("id"));
            prod_order   = PendingOrderProduct.getAllFromOrderInMaps(getActivity(), order_info.get("id"));
            txt_order_date.setText(order_info.get("date"));
        }

        if (order_info.get("type").equalsIgnoreCase("CASH"))
            txt_order_type.setText("CONTADO");
        else if (order_info.get("type").equalsIgnoreCase("CREDIT"))
            txt_order_type.setText("CREDITO");
        else if (order_info.get("type").equalsIgnoreCase("DEPOSIT"))
            txt_order_type.setText("DEPÓSITO");
        else
            txt_order_type.setText("PREVENTA");

        DecimalFormat form = new DecimalFormat("0.00");
        float total = Float.parseFloat(order_info.get("subtotal"));

        txt_order_subtotal.setText("$ "+form.format(total));
        txt_order_tax.setText("$ "+order_info.get("tax"));
        txt_order_total.setText("$ "+form.format(total));

        btn_cancel_order        = (Button) view.findViewById(R.id.btn_cancel_order);

        lst_product_order       = (ListView) view.findViewById(R.id.lst_product_order);
        lst_product_order.setAdapter(new PendingOrderProductAdapter(getActivity(),prod_order));

        btn_cancel_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCancelPreOrderDialog();
            }
        });
        return view;
    }

    public void showCancelPreOrderDialog(){
        RequestManager.sharedInstance().showDecisionDialogWithListener(getActivity().getString(R.string.req_man_confirm_cancel_order),getActivity(), this);
    }

    @Override
    public void responseFromDecisionDialog(String confirmMessage, String option) {

        if (option.equalsIgnoreCase("OK")){
            Map params              = new HashMap<String, String>();

            if (locked_type == FragmentStepVisit.LockType.LOCKED_BY_ORDER_SENT){            // Cancelling a previous order
                DataBaseManager.sharedInstance().updateOrderSentToInactive(order_info.get("order_id"));
                params.put("order_id",order_info.get("order_id"));

            }
            else if (locked_type == FragmentStepVisit.LockType.LOCKED_BY_PRE_ORDER){        // Cancelling a pre sale
                DataBaseManager.sharedInstance().passPendingOrderToInactiveOrderSent(order_info);
                params.put("order_id",order_info.get("id"));
            }
            prepareRequest(METHOD.CANCEL_ORDER,params);
        }
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

        JSONObject resp;

        try {
            resp        = new JSONObject(stringResponse);

            if (resp.getString("method").equalsIgnoreCase(METHOD.CANCEL_ORDER.toString())) {
                if (resp.getString("error").isEmpty()) {
                    Log.d(TAG, "Order cancelled successfully");
                    OrderSent.updateOrderSentToInactiveAndSent(getActivity(), current_order_id);
                    RequestManager.sharedInstance().showConfirmationDialogWithListener("El pedido " + current_order_id + " ha sido cancelado exitosamente", getActivity(), this);
                }
                else{
                    Toast.makeText(getActivity(), "Sin acceso a red, módulo offline", Toast.LENGTH_SHORT).show();
                    getActivity().onBackPressed();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void okFromConfirmationDialog(String message) {
        if (message.contains("ha sido cancelado exitosamente")){
            getActivity().onBackPressed();
        }
    }
}
