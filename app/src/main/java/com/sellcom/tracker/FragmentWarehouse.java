package com.sellcom.tracker;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import OfflineMode.Synchronizer;
import async_request.ConfirmationDialogListener;
import async_request.METHOD;
import async_request.RequestManager;
import async_request.UIResponseListenerInterface;
import database.models.InventoryProduct;
import database.models.Product;
import database.models.ProductShelfReview;
import database.models.QualityIncidence;
import database.models.Warehouse;
import util.TrackerManager;
import util.Utilities;
import util.WarehouseAdapter;


public class FragmentWarehouse extends Fragment implements View.OnClickListener, WarehouseAdapter.OnProductDeletedListener, UIResponseListenerInterface, ConfirmationDialogListener {

    final static public String TAG = "FRAGMENT_WAREHOUSE";

    String visit_id;

    LinearLayout warehouse_table_container;

    List<Map<String,String>> warehouse_array;
    public List<Map<String,String>> product_array;
    Map<String,String> sel_product,shelf_review_sel_product;
    int out_boxes_selected_product;

    Spinner     sp_wh_product;
    EditText    et_wh_boxes_out, et_wh_ending_inventory;
    TextView    tv_wh_total_inventory;
    ListView    lst_ware_products;

    WarehouseAdapter    adapter;
    util.SpinnerAdapter spinnerAdapterProduct;


    public FragmentWarehouse() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_warehouse, container, false);

        visit_id                = TrackerManager.sharedInstance().getCurrent_pdv().get("visit_id");
        warehouse_array         = Warehouse.getAllWarehouseNoSentInVisit(getActivity(),visit_id);

        ((TextView)view.findViewById(R.id.tv_inv_client_name)).setText(TrackerManager.sharedInstance().getCurrent_pdv().get("pdv"));

        product_array           = new ArrayList<Map<String, String>>();

        sp_wh_product           = (Spinner) view.findViewById(R.id.sp_wh_product);
        filterProductsInAlreadyInWarehouse();

        lst_ware_products       = (ListView)view.findViewById(R.id.lst_ware_products);
        adapter = new WarehouseAdapter(getActivity(),warehouse_array);
        adapter.setOnProductDeletedListener(this);
        lst_ware_products.setAdapter(adapter);

        sp_wh_product.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sel_product  = product_array.get(sp_wh_product.getSelectedItemPosition());
                shelf_review_sel_product  = ProductShelfReview.getProductShelfReviewNoSentWithProductInVisit(getActivity(), sel_product.get("id"), visit_id);
                if (shelf_review_sel_product != null) {
                    out_boxes_selected_product = Integer.parseInt(shelf_review_sel_product.get("shelf_boxes")) + Integer.parseInt(shelf_review_sel_product.get("exhibition_boxes"));
                    et_wh_boxes_out.setText(""+out_boxes_selected_product);
                }
                else{
                    et_wh_boxes_out.setText("");
                }
                et_wh_ending_inventory.setText("");
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d("nothing", "selected");
                shelf_review_sel_product    = null;
            }
        });
        et_wh_boxes_out             = (EditText) view.findViewById(R.id.et_wh_boxes_out);
        et_wh_ending_inventory      = (EditText) view.findViewById(R.id.et_wh_ending_inventory);
        tv_wh_total_inventory       = (TextView) view.findViewById(R.id.tv_wh_total_inventory);

        Button btn_wh_add_product     = (Button) view.findViewById(R.id.btn_wh_add_product);
        btn_wh_add_product.setOnClickListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (!((MainActivity) getActivity()).isDrawerOpen) {
            menu.clear();
            inflater.inflate(R.menu.add_client, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.save:
                if (warehouse_array.isEmpty())
                    RequestManager.sharedInstance().showErrorDialog("Lista de Productos vacía",getActivity());
                else {
                    recoverData();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    void filterProductsInAlreadyInWarehouse(){

        product_array.clear();

        List<Map<String,String>> all_products = Product.getAllOwnInMaps(getActivity());

        // ITERATE ALL PRODUCTS
        for (Map<String,String> product : all_products){
            boolean add_product = true;
            for (Map<String,String> warehouse : warehouse_array){
                if (product.get("id").equalsIgnoreCase(warehouse.get("id_product")))
                    add_product = false;
            }
            if (add_product)
                product_array.add(product);
        }

        spinnerAdapterProduct   = new util.SpinnerAdapter(getActivity(),product_array,util.SpinnerAdapter.SPINNER_TYPE.PRODUCTS);
        sp_wh_product.setAdapter(spinnerAdapterProduct);
    }

    public void recoverData(){

        JSONArray products = new JSONArray();
        for (Map<String,String> itemProduct : warehouse_array){
            products.put(Utilities.mapToJson(itemProduct));
        }
        JSONObject requestData = Utilities.getJSONWithCredentials(getActivity());
        try {
            requestData.put("id_visit",visit_id);
            requestData.put("products", products.toString());
            requestData.put("request", METHOD.SET_WAREHOUSE_STOCK);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestManager.sharedInstance().setListener(this);
        RequestManager.sharedInstance().makeRequestWithJSONDataAndMethod(requestData,METHOD.SET_WAREHOUSE_STOCK);
    }

    @Override
    public void onClick(View view) {
        if (view.getId()    == R.id.btn_wh_add_product){
            Log.d(TAG,"CLICK");
            if (shelf_review_sel_product == null) {
                Toast.makeText(getActivity(), "Capture primero información de revisión de anaquel antes de capturar bodega", Toast.LENGTH_LONG).show();
                return;
            }
            else{
                // CHECK IF PRODUCT IS ALREADY IN LIST
                boolean addProduct  = true;
                for (Map<String,String> prod : warehouse_array) {
                    if (prod.get("id_product").equalsIgnoreCase(sel_product.get("id")))
                        addProduct  = false;
                }
                // IF NOT, ADD TO WAREHOUSE LIST
                if (addProduct)
                    saveWarehouse();
                else
                    Toast.makeText(getActivity(), "Error, el producto ya ha sido agregado a bodega", Toast.LENGTH_LONG).show();
            }
        }
    }

    public boolean validateData(){
        if (this.et_wh_boxes_out.getText().toString().isEmpty()) {
            et_wh_boxes_out.setError(getString(R.string.error_empty_field));
            return false;
        }
        if (this.et_wh_ending_inventory.getText().toString().isEmpty()) {
            et_wh_ending_inventory.setError(getString(R.string.error_empty_field));
            return false;
        }
        return true;
    }

    public void saveWarehouse(){
        if (!validateData())
            return;

        // Calculate the warehouse inventory
        float wareh_inv = Integer.parseInt(et_wh_ending_inventory.getText().toString());

        int real_boxes_out = Integer.parseInt(et_wh_boxes_out.getText().toString());

        // DISTRIBUTE THE REAL BOXES OUT BETWEEN SHELF AND EXHIBITION,
        // SHELF HAS PRIORITY, (TRY TO USE THE BOXES_OUT TO FILL SHELF IN FIRST PLACE)
        int boxes_used_in_shelf = Math.min(Integer.parseInt(shelf_review_sel_product.get("shelf_boxes")),real_boxes_out);
        int boxes_used_in_exhib = real_boxes_out - boxes_used_in_shelf;

        float shelf_inv = Integer.parseInt(shelf_review_sel_product.get("current_shelf")) + boxes_used_in_shelf;
        int exhib_inv = Integer.parseInt(shelf_review_sel_product.get("current_exhibition")) + boxes_used_in_exhib;

        float total = wareh_inv + shelf_inv + exhib_inv;

        Log.d(TAG,"BOXES OUT: "+real_boxes_out);
        Log.d(TAG,"BOXES OUT SHELF: "+boxes_used_in_shelf);
        Log.d(TAG,"BOXES OUT EXHIB: "+(real_boxes_out-boxes_used_in_shelf));
        Log.d(TAG,"BOXES SHELF: "+shelf_inv);
        Log.d(TAG,"BOXES CURRENT EXHIB : "+Integer.parseInt(shelf_review_sel_product.get("current_exhibition")));
        Log.d(TAG,"WAREHOUSE: "+wareh_inv);
        Log.d(TAG,"TOTAL: "+total);

        Warehouse.insert(getActivity(),visit_id,sel_product.get("id"),sel_product.get("name"),String.format("%d",(int)wareh_inv),
                String.format("%d",(int)(shelf_inv+exhib_inv)),String.format("%.2f", ((wareh_inv/total)*100)),
                String.format("%.2f", ((shelf_inv/total)*100)),String.format("%.2f", ((exhib_inv/total)*100)));

        reloadData();
    }

    @Override
    public void onProductDeleted() {
        reloadData();
    }

    public void reloadData(){
        warehouse_array    = Warehouse.getAllWarehouseNoSentInVisit(getActivity(),visit_id);

        filterProductsInAlreadyInWarehouse();

        adapter = new WarehouseAdapter(getActivity(),warehouse_array);
        adapter.setOnProductDeletedListener(this);
        lst_ware_products.setAdapter(adapter);
    }

    @Override
    public void prepareRequest(METHOD method, Map<String, String> params) {

    }

    @Override
    public void decodeResponse(String stringResponse) {
        try {
            JSONObject resp          = new JSONObject(stringResponse);

            if (resp.getString("method").equalsIgnoreCase(METHOD.SET_WAREHOUSE_STOCK.toString())) {
                if (resp.getString("error").isEmpty())
                    Warehouse.updateAllWarehouseProductsInVisitToSent(getActivity(),visit_id);

                RequestManager.sharedInstance().showConfirmationDialogWithListener(getActivity().getString(R.string.req_man_warehouse_sent), getActivity(), this);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void okFromConfirmationDialog(String message) {
        getActivity().onBackPressed();
    }

}
