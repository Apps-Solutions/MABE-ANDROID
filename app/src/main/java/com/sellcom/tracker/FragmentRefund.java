package com.sellcom.tracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import async_request.METHOD;
import async_request.RequestManager;
import async_request.UIResponseListenerInterface;
import database.models.Customer;
import database.models.CustomerWorkPlan;
import database.models.OrderSent;
import database.models.OrderSentProduct;
import database.models.Product;
import database.models.Refund;
import database.models.RefundProduct;
import database.models.Session;
import database.models.User;
import database.models.UserInfo;
import de.timroes.android.listview.EnhancedListView;
import util.Constants;
import util.DatesHelper;
import util.FilterAdapter_Map;
import util.OrdersAdapter_Map;
import util.ReceiptAdapter;
import util.TicketGenerator;
import util.TrackerManager;
import util.Utilities;

public class FragmentRefund extends Fragment implements View.OnClickListener,
        EnhancedListView.OnDismissCallback,
        AdapterView.OnItemClickListener,
        View.OnFocusChangeListener,
        TextWatcher,
        OrdersAdapter_Map.OnProductDeletedListener,
        FragmentClientsDialogList.UpdaterPostDialog,
        FragmentReceiptPreview.NotifyReloadInvoicesArrayListener,
        OrdersAdapter_Map.UpdateFooterInterface{

    TextView                    txt_unit,txt_total,txt_subtotal;
    TextView                    txt_header;
    ImageButton                 clearSearchField, scanButton;
    EditText                    searchProducts;
    ListView                    filterList;
    FilterAdapter_Map           filterAdapter;
    OrdersAdapter_Map           orderAdapter;
    EnhancedListView            productsList;
    List<Map<String,String>>    prod_array;
    List<Map<String,String>>    order_prod_list;


    double                  orderTotalAmount = 0;
    double                  cantidad = 0;
    static Context          context;

    String                  provisional_order_id;


    public final static String TAG  = "orders";
    boolean canSendOrder            = false;
    boolean receiptAlreadyPrinted   = true;

    static String LOG_TAG = "TAG_orders_FRAGMENT";

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(NavigationDrawerFragment.ORDERS);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context         = getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d(TAG,"On create view");

        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        if (view != null) {

            txt_unit        = (TextView)view.findViewById(R.id.txt_unit);
            txt_total       = (TextView)view.findViewById(R.id.txt_total);
            txt_subtotal    = (TextView)view.findViewById(R.id.txt_subtotal);

            txt_header      = (TextView)view.findViewById(R.id.txt_customerByOrder);
            txt_header.setText(TrackerManager.sharedInstance().getCurrent_pdv().get("pdv_name"));

            scanButton          = (ImageButton) view.findViewById(R.id.scan_button);
            scanButton.setVisibility(View.GONE);
            clearSearchField    = (ImageButton) view.findViewById(R.id.clear_search_field);
            searchProducts      = (EditText) view.findViewById(R.id.search_product);
            productsList        = (EnhancedListView) view.findViewById(R.id.products_list);
            filterList          = (ListView) view.findViewById(R.id.product_filter);

            scanButton.setOnClickListener(this);
            clearSearchField.setOnClickListener(this);
            searchProducts.setOnFocusChangeListener(this);
            searchProducts.addTextChangedListener(this);
            productsList.setDismissCallback(this);
            productsList.enableSwipeToDismiss();
            productsList.setRequireTouchBeforeDismiss(false);

            prod_array = Product.getAllOwnInMaps(getActivity());

            filterAdapter = new FilterAdapter_Map(getActivity(), prod_array);
            filterList.setAdapter(filterAdapter);

            order_prod_list = new ArrayList<Map<String, String>>();
            orderAdapter    = new OrdersAdapter_Map(getActivity(), order_prod_list);
            orderAdapter.setOnProductDeletedListener(this);
            orderAdapter.setFooterUpdater(this);
            productsList.setAdapter(orderAdapter);
            filterList.setOnItemClickListener(this);

            toggleFilterList(View.GONE);
        }

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        orderAdapter.hideButtons = true;
        setHasOptionsMenu(true);
    }

    @Override
    public void onFocusChange(View view, boolean isFocused) {
        if (view.getId() == R.id.search_product) {

            if (isFocused) {
                toggleFilterList(View.VISIBLE);
                filterAdapter.filterProducts("");
            } else
                toggleFilterList(View.GONE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

        Map<String,String> product  = (Map<String,String>)filterAdapter.getItem(position);
        product.put("total_pieces","");
        product.put("total_price","");
        product.put("pieces","0");
        product.put("boxes","0");
        product.put("total_pieces","0");
        order_prod_list.add(product);
        orderAdapter.notifyDataSetChanged();

        filterAdapter.removeElement((Map<String,String>) filterAdapter.getItem(position));
        filterAdapter.notifyDataSetChanged();

        clearSearchField.performClick();

        toggleFilterList(View.GONE);

        getActivity().invalidateOptionsMenu();

        updateFooter();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        if (!s.toString().isEmpty())
            clearSearchField.setVisibility(View.VISIBLE);

        filterAdapter.filterProducts(s.toString());
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    public void toggleFilterList(int visibility) {

        filterList.setVisibility(visibility);
        if (visibility == View.GONE)
            Utilities.hideKeyboard(getActivity(), searchProducts);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.clear_search_field:
                searchProducts.setText("");
                clearSearchField.setVisibility(View.GONE);
                searchProducts.clearFocus();
                break;

            case R.id.scan_button:
                new IntentIntegrator(getActivity()).initiateScan();
                break;
        }
    }

    @Override
    public EnhancedListView.Undoable onDismiss(EnhancedListView enhancedListView, int i) {

        final int position = i;
        final Map<String,String> product = order_prod_list.get(position);

        orderAdapter.deleteProduct(product, position);

        return null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        if (!((MainActivity) getActivity()).isDrawerOpen) {
            menu.clear();
            inflater.inflate(R.menu.add_client, menu);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem saveButton     = menu.findItem(R.id.save_changes);
        MenuItem deleteButton   = menu.findItem(R.id.delete_product);

        DecimalFormat form = new DecimalFormat("0.00");
        txt_subtotal.setText("$"+form.format(orderTotalAmount));
        txt_total.setText("$"+form.format(orderTotalAmount));
        if(orderTotalAmount == 0){
            txt_unit.setText("0");
            cantidad = 0;
        }else{
            txt_unit.setText(""+(int)cantidad+" pz");
        }

        if (saveButton != null) {
            saveButton.setEnabled(!order_prod_list.isEmpty() && canSendOrder && receiptAlreadyPrinted);
            if (order_prod_list.isEmpty())
                Log.d(TAG,"Empty");
            if (!canSendOrder)
                Log.d(TAG,"Can't send order");
            if (!receiptAlreadyPrinted)
                Log.d(TAG,"No printed");
        }


        if (deleteButton != null) {
            deleteButton.setEnabled(!order_prod_list.isEmpty());

            if (deleteButton.isEnabled()) deleteButton.setIcon(R.drawable.ic_delete_enabled);
            else deleteButton.setIcon(R.drawable.ic_delete_disabled);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.d(TAG,"Selected:  "+item.getItemId());

        switch (item.getItemId()) {
            case R.id.save:
                Log.d(TAG, "Sending Refund");
                sendOrder();
                return true;
            case R.id.delete_product:
                if (!order_prod_list.isEmpty()) {
                    orderAdapter.hideButtons = false;
                    orderAdapter.notifyDataSetChanged();
                }
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    public void sendOrder(){
        Log.d(TAG,"Send order");
        String token        = Session.getSessionActive(getActivity()).getToken();
        String username     = User.getUser(getActivity(), Session.getSessionActive(getActivity()).getUser_id()).getEmail();
        String visit_id     = TrackerManager.sharedInstance().getCurrent_pdv().get("visit_id");

        JSONArray  products = new JSONArray();

        provisional_order_id    = ""+Calendar.getInstance().get(Calendar.MILLISECOND);
        Refund.insert(getActivity(), "", provisional_order_id,"",visit_id,"");

        for(Map<String,String> itemProduct : order_prod_list) {
            JSONObject  jsonObj = new JSONObject();
            try {
                jsonObj.put("id_product",itemProduct.get("id"));
                jsonObj.put("measure_unit","1");
                jsonObj.put("quantity",itemProduct.get("total_pieces"));

                products.put(jsonObj);

                RefundProduct.insert(getActivity(), provisional_order_id,itemProduct.get("id"),itemProduct.get("jde"),
                        itemProduct.get("name"), itemProduct.get("total_pieces"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        JSONObject requestData = new JSONObject();
        try {
            requestData.put("token",token);
            requestData.put("user",username);
            requestData.put("id_visit",visit_id);
            requestData.put("products", products.toString());
            requestData.put("request",METHOD.SEND_REFUND);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG,"JSON: "+requestData);
        Log.d(TAG,"MAP: "+Refund.getActiveRefundByVisit(getActivity(),visit_id));
        Log.d(TAG,"LIST: "+order_prod_list.toString());
        Log.d(TAG,"MAP_PROD: "+RefundProduct.getAllProductsInRefundInOrder(getActivity(),provisional_order_id));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Atención");
        builder.setMessage("Cambio físico realizado correctamente");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                getActivity().onBackPressed();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onProductDeleted(Map<String,String> product) {

        filterAdapter.addElement(product);
        order_prod_list.remove(product);

        if (order_prod_list.isEmpty()) {
            orderAdapter.hideButtons = true;
            getActivity().invalidateOptionsMenu();
        }

        clearSearchField.performClick();
        updateFooter();
    }

    @Override
    public void updateHeaderText() {
        txt_header.setText(TrackerManager.sharedInstance().getCurrent_pdv().get("pdv_name"));
    }

    @Override
    public void notifyPaymentSucceed() {
        orderAdapter.deleteAllProducts();
        prod_array = Product.getAllInMaps(getActivity());
        Log.d(TAG,"Numero de productos: "+prod_array.size());

        filterAdapter = new FilterAdapter_Map(getActivity(), prod_array);
        filterList.setAdapter(filterAdapter);

        getActivity().onBackPressed();
    }

    @Override
    public void updateFooter() {
        cantidad = 0;
        float total_price  = 0;
        for (int i=0; i<orderAdapter.products.size();i++){
            Map<String,String> prod = orderAdapter.products.get(i);
            if (!prod.get("total_pieces").isEmpty())
                cantidad += Integer.parseInt(prod.get("total_pieces"));

            if (!prod.get("total_price").isEmpty())
                total_price += Float.parseFloat(prod.get("total_price"));
        }
        txt_unit.setText(""+(int)cantidad);
        orderTotalAmount    = total_price;
        String money = String.format("$%.2f", total_price);
        txt_total.setText(money.replace(',', '.'));
        txt_subtotal.setText(txt_total.getText().toString());
    }
}