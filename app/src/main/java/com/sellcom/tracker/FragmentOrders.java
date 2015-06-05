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
import database.DataBaseManager;
import database.models.Customer;
import database.models.CustomerWorkPlan;
import database.models.OrderSent;
import database.models.OrderSentProduct;
import database.models.Product;
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

/**
 * Created by juanc.jimenez on 15/07/14.
 */
public class FragmentOrders extends Fragment implements View.OnClickListener,
        EnhancedListView.OnDismissCallback,
        AdapterView.OnItemClickListener,
        View.OnFocusChangeListener,
        TextWatcher,
        OrdersAdapter_Map.OnProductDeletedListener,
        UIResponseListenerInterface,
        FragmentClientsDialogList.UpdaterPostDialog,
        FragmentReceiptPreview.NotifyReloadInvoicesArrayListener,
        OrdersAdapter_Map.UpdateFooterInterface,
        TicketGenerator.TicketGeneratorListener{

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
    int                     orderId = 0;
    static Context          context;

    String                  provisional_order_id;


    public final static String TAG  = "ORDERS_FRAG_TAG";
    String  options                  = "";
    String  str_pdv_array;
    boolean canSendOrder            = true;
    boolean receiptAlreadyPrinted   = true;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(NavigationDrawerFragment.ORDERS);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context         = getActivity().getApplicationContext();

        options         = getArguments().getString("options");
        str_pdv_array   = getArguments().getString("pdv_array");
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
            Log.d(TAG,"Numero de productos: "+prod_array.size());

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
            inflater.inflate(R.menu.orders, menu);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem printButton    = menu.findItem(R.id.print_ticket);
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

        if (printButton != null) {
            printButton.setEnabled(!order_prod_list.isEmpty() && canSendOrder);
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
            case R.id.print_ticket:
                Log.d(TAG,"PRINTING RECEIPT");

                String pdv_id                       = TrackerManager.sharedInstance().getCurrent_pdv().get("pdv_id");
                Map<String,String>  customer_info   = CustomerWorkPlan.getCustomerWorkPlanByPDV(getActivity(),pdv_id);
                Map<String,String>  user_info       = UserInfo.getUserInfoFromEmailInMap(getActivity(), User.getUser(getActivity(), Session.getSessionActive(getActivity()).getUser_id()).getEmail());

                TicketGenerator.generateTicket(getActivity(),this,order_prod_list,customer_info,user_info,orderTotalAmount,(int)cantidad);
                getActivity().invalidateOptionsMenu();
                return true;

            case R.id.save_changes:
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
        Log.d(TAG,"SENDING ORDER");
        String token        = Session.getSessionActive(getActivity()).getToken();
        String username     = User.getUser(getActivity(), Session.getSessionActive(getActivity()).getUser_id()).getEmail();
        String pdv_id       = TrackerManager.sharedInstance().getCurrent_pdv().get("pdv_id");
        String visit_id     = TrackerManager.sharedInstance().getCurrent_pdv().get("visit_id");
        String date_time    = DatesHelper.getStringDate(new Date());

        provisional_order_id    = "N_"+Calendar.getInstance().get(Calendar.MILLISECOND);

        DataBaseManager.sharedInstance().saveOrder(date_time,provisional_order_id,pdv_id,visit_id,username,String.valueOf(orderTotalAmount),String.valueOf(orderTotalAmount),"0.0",order_prod_list);

        JSONArray  products = new JSONArray();

        int total_quan  = 0;

        for(Map<String,String> itemProduct : order_prod_list) {
            JSONObject  jsonObj = new JSONObject();
            try {
                jsonObj.put("id_product",itemProduct.get("id"));
                jsonObj.put("price",itemProduct.get("price"));
                jsonObj.put("quantity",itemProduct.get("total_pieces"));
                total_quan  = Integer.parseInt(itemProduct.get("total_pieces"));
                products.put(jsonObj);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        JSONObject requestData = new JSONObject();
        try {
            requestData.put("token",token);
            requestData.put("user",username);
            requestData.put("pdv_id",pdv_id);
            requestData.put("visit_id",visit_id);
            requestData.put("date_time",date_time);
            requestData.put("products", products.toString());
            requestData.put("request",METHOD.SEND_ORDER);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (total_quan == 0) {
            Toast.makeText(getActivity(), "Error, número de productos inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestManager.sharedInstance().setListener(this);
        RequestManager.sharedInstance().makeRequestWithJSONDataAndMethod(requestData,METHOD.SEND_ORDER);
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
    public void prepareRequest(METHOD method, Map<String, String> params) {

    }

    @Override
    public void decodeResponse(String stringResponse) {
        try {
            JSONObject resp          = new JSONObject(stringResponse);

            String order_id = "";

            if (resp.getString("method").equalsIgnoreCase(METHOD.SEND_ORDER.toString())) {
                if (resp.getString("error").isEmpty()){
                    order_id   = resp.getString("order_id");
                    DataBaseManager.sharedInstance().updateOrderToSent(provisional_order_id,order_id);
                }
                else
                    order_id = provisional_order_id;

                final String final_order_id = order_id;

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setCancelable(false);
                builder.setTitle("Atención");
                builder.setMessage("Pedido enviado exitosamente. ¿Desea realizar el pago del pedido?");
                builder.setPositiveButton("PREVENTA", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getActivity().onBackPressed();
                    }
                });
                builder.setNeutralButton("PAGAR", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        showReceiptPreview(final_order_id);
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void showReceiptPreview(String order_id){
        Bundle bundle = new Bundle();
        bundle.putString("order_id",order_id);
        bundle.putString("invoice_id","CLEAR");
        bundle.putString("pdv_name",txt_header.getText().toString());
        bundle.putString("pdv_id",TrackerManager.sharedInstance().getCurrent_pdv().get("pdv_id"));
        bundle.putString("date",DatesHelper.getStringDate(new Date()));
        bundle.putDouble("total", orderTotalAmount);

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        Fragment fragment = getFragmentManager().findFragmentByTag(FragmentReceiptPreview.TAG);

        if (fragment != null)
            fragmentTransaction.remove(fragment);

        fragmentTransaction.addToBackStack(null);

        FragmentReceiptPreview newFragment = FragmentReceiptPreview.newInstance(orderId, ReceiptAdapter.ORDER);
        newFragment.listener    = this;
        newFragment.setArguments(bundle);
        newFragment.show(fragmentTransaction, FragmentReceiptPreview.TAG);
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
    public void responseFromGenerator(String response) {
        if (response.equalsIgnoreCase("OK")){         // No error!!
            receiptAlreadyPrinted   = true;
            getActivity().invalidateOptionsMenu();
            Toast.makeText(getActivity(),getString(R.string.tg_ticket_printed),Toast.LENGTH_SHORT).show();
        }
        else if (response.equalsIgnoreCase("NO_PRINTER")){
            receiptAlreadyPrinted   = true;
            getActivity().invalidateOptionsMenu();
        }
        else{
            receiptAlreadyPrinted   = false;
            Toast.makeText(getActivity(),getString(R.string.tg_error_printing_ticket),Toast.LENGTH_SHORT).show();
        }
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