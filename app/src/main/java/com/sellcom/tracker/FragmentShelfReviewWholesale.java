package com.sellcom.tracker;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.SpinnerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import async_request.METHOD;
import async_request.RequestManager;
import async_request.UIResponseListenerInterface;
import database.models.Product;
import database.models.Session;
import database.models.Shelf_Review;
import database.models.Shelf_ReviewProduct;
import database.models.User;
import util.*;

/**
 * Created by jonathan.vazquez on 25/02/15.
 */
public class FragmentShelfReviewWholesale extends Fragment implements View.OnClickListener,
                                                                        FragmentAssortmentAndQuality.NotifyAddProduct,
                                                                        ShelfReviewProductAdapter.InteractWithProduct,
                                                                        UIResponseListenerInterface{

    final static public String TAG = "shelf_review_wholesale";

    private ListView            lst_products_added;
    private ImageButton         btn_add_prod;
    private Spinner             spn_product_shelf_review;

    ShelfReviewProductAdapter   lst_products_added_adapter;

    List<Map<String,String>>    prod_array;
    List<Map<String,String>>    prod_added_list;
    Map<String,String>          sel_prod;

    String                      id_visit,username;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_shelf_review, container, false);


        if (view != null) {

            TextView    txt_customer_shelf_review   = (TextView)view.findViewById(R.id.txt_customer_shelf_review);
            txt_customer_shelf_review.setText(TrackerManager.sharedInstance().getCurrent_pdv().get("pdv_name"));

            Log.d(TAG,"Visit id: "+TrackerManager.sharedInstance().getCurrent_pdv().get("visit_id"));
            Log.d(TAG,"PDV name: "+TrackerManager.sharedInstance().getCurrent_pdv().get("pdv_name"));

            prod_array   = Product.getAllInMaps(getActivity());
            prod_added_list                             = new ArrayList<Map<String, String>>();

            spn_product_shelf_review            = (Spinner)view.findViewById(R.id.spn_product_shelf_review);
            btn_add_prod                        = (ImageButton)view.findViewById(R.id.btn_add_product);

            SpinnerAdapter  adapter             = new util.SpinnerAdapter(getActivity(),prod_array, util.SpinnerAdapter.SPINNER_TYPE.PRODUCTS);
            spn_product_shelf_review.setAdapter(adapter);
            spn_product_shelf_review.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    sel_prod = prod_array.get(position);
                    Log.d(TAG, "selected: " + sel_prod.get("name"));
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    Log.d("nothing" , "selected");
                }
            });

            btn_add_prod.setOnClickListener(this);

            lst_products_added_adapter = new ShelfReviewProductAdapter(getActivity(),prod_added_list);
            lst_products_added_adapter.listener = this;
            lst_products_added.setAdapter(lst_products_added_adapter);
        }

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
                if (prod_added_list.isEmpty())
                    RequestManager.sharedInstance().showErrorDialog("Lista de Productos vacía",getActivity());
                else
                    recoverInfoFromUI();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if (view.getId()    == R.id.btn_add_product){
            Log.d(TAG,"Adding product: "+prod_added_list.size());
            if (prod_added_list.isEmpty()) {
                restartProductDetail();
                showDialog();
            }
            else{
                boolean addProduct  = true;
                for (int i=0; i<prod_added_list.size(); i++){
                    Map<String,String> prod = prod_added_list.get(i);
                    if (prod.get("name").equalsIgnoreCase(sel_prod.get("name"))) {
                        addProduct  = false;
                    }
                }
                if (addProduct) {
                    restartProductDetail();
                    showDialog();
                }
                else
                    Toast.makeText(getActivity(), "Este producto ya ha sido agregado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void recoverInfoFromUI(){
        String token        = Session.getSessionActive(getActivity()).getToken();
        username            = User.getUser(getActivity(), Session.getSessionActive(getActivity()).getUser_id()).getEmail();
        id_visit            = TrackerManager.sharedInstance().getCurrent_pdv().get("visit_id");
        String date_time    = DatesHelper.getStringDate(new Date());

        JSONArray products = new JSONArray();

        for(Map<String,String> itemProduct : prod_added_list) {
            JSONObject jsonObj = new JSONObject();
            try {
                jsonObj.put("id_product",itemProduct.get("id"));
                jsonObj.put("current",itemProduct.get("edt_current_existence"));
                jsonObj.put("shelf_boxes",itemProduct.get("edt_box_shelf"));
                jsonObj.put("exhibition_boxes",itemProduct.get("edt_box_exhibit"));
                jsonObj.put("price",itemProduct.get("edt_price_to_public"));
                jsonObj.put("expiration",itemProduct.get("expiration_date"));

                products.put(jsonObj);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        JSONObject requestData = new JSONObject();
        try {
            requestData.put("token",token);
            requestData.put("user",username);
            requestData.put("id_visit",id_visit);
            requestData.put("products", products.toString());
            requestData.put("request",METHOD.SET_SHELF_REVISION);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestManager.sharedInstance().setListener(this);
        RequestManager.sharedInstance().makeRequestWithJSONDataAndMethod(requestData,METHOD.SET_SHELF_REVISION);
    }

    public void restartProductDetail(){
        sel_prod.put("edt_current_existence","");
        sel_prod.put("edt_box_shelf","");
        sel_prod.put("edt_box_exhibit","");
        sel_prod.put("edt_box_total","");
        sel_prod.put("edt_price_to_public", "");
        sel_prod.put("expiration", "");
    }

    public void showDialog(){

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        Fragment fragment = getFragmentManager().findFragmentByTag(FragmentReceiptPreview.TAG);

        if (fragment != null)
            fragmentTransaction.remove(fragment);

        fragmentTransaction.addToBackStack(null);

        FragmentAssortmentAndQuality newFragment  = new FragmentAssortmentAndQuality();
        newFragment.listener                = this;
        newFragment.product                 = sel_prod;
        newFragment.show(fragmentTransaction, FragmentAssortmentAndQuality.TAG);
    }

    @Override
    public void updateInfoProductAdd(boolean isUpdate) {
        if (!isUpdate)
            prod_added_list.add(sel_prod);
        lst_products_added_adapter.notifyDataSetChanged();
    }

    @Override
    public void showProductDetail(int position) {
        sel_prod    = prod_added_list.get(position);
        showDialog();
    }

    @Override
    public void prepareRequest(METHOD method, Map<String, String> params) {

    }

    @Override
    public void decodeResponse(String stringResponse) {
        RequestManager.sharedInstance().showConfirmationDialog("Revisión de anaquel enviada correctamente",getActivity());
        insertShelfReviewInDataBase();
    }

    public void insertShelfReviewInDataBase(){

    }
}
