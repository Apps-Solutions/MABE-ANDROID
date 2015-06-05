package com.sellcom.tracker;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import async_request.ConfirmationDialogListener;
import async_request.DecisionDialogWithListener;
import async_request.METHOD;
import async_request.RequestManager;
import async_request.UIResponseListenerInterface;
import database.models.InventoryProduct;
import database.models.OrderSent;
import database.models.Product;
import database.models.Session;
import database.models.Shelf_Review;
import database.models.Shelf_ReviewProduct;
import database.models.User;
import util.DatesHelper;
import util.InventoryQuestionnaireAdapter;
import util.TrackerManager;
import util.SpinnerAdapter;

/**
 * Created by hugo.figueroa 26/02/15.
 */
public class FragmentInventoryQuestionnaire extends Fragment implements View.OnClickListener,

        UIResponseListenerInterface, ConfirmationDialogListener {

    final static public String TAG = "FRAGMENT_INVENTORY_QUESTIONNAIRE";

    private TextView            tv_inv_client_name;
    private ImageButton         btn_inv_add_product;
    private Spinner             sp_inv_product;
    private ListView            lst_inv_products_added;

    InventoryQuestionnaireAdapter   lst_products_added_adapter;

    public List<Map<String,String>>    prod_array;
    List<Map<String,String>>    prod_added_list;
    Map<String,String>          sel_prod;

    String                      id_visit,username;



    public FragmentInventoryQuestionnaire() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_inventory_questionnaire, container, false);

        if (view != null) {

            tv_inv_client_name      = (TextView) view.findViewById(R.id.tv_inv_client_name);
            btn_inv_add_product     = (ImageButton) view.findViewById(R.id.btn_inv_add_product);
            sp_inv_product          = (Spinner) view.findViewById(R.id.sp_inv_product);
            lst_inv_products_added  = (ListView) view.findViewById(R.id.lst_inv_products_added);

            tv_inv_client_name.setText(TrackerManager.sharedInstance().getCurrent_pdv().get("pdv"));

            //prod_array      = Product.getAllOwnInMaps(getActivity());
            prod_added_list = new ArrayList<Map<String, String>>();

            util.SpinnerAdapter spinnerAdapterProblemProduct = new util.SpinnerAdapter(getActivity(),
                    prod_array,
                    util.SpinnerAdapter.SPINNER_TYPE.PRODUCTS);
            sp_inv_product.setAdapter(spinnerAdapterProblemProduct);

            sp_inv_product.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    sel_prod = prod_array.get(position);
                    restartProductDetail();
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    Log.d("nothing" , "selected");
                }
            });

            btn_inv_add_product.setOnClickListener(this);

            lst_products_added_adapter = new InventoryQuestionnaireAdapter(getActivity(),prod_added_list);
            lst_inv_products_added.setAdapter(lst_products_added_adapter);

        }
        return view;
    }

    public void addListAdapter(){
        lst_products_added_adapter = new InventoryQuestionnaireAdapter(getActivity(),prod_added_list);
        lst_inv_products_added.setAdapter(lst_products_added_adapter);
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
                else {
                    recoverInfoFromUI();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if (view.getId()    == R.id.btn_inv_add_product){
            if (prod_added_list.isEmpty()) {
                prod_added_list.add(sel_prod);
                lst_products_added_adapter.notifyDataSetChanged();
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
                    prod_added_list.add(sel_prod);
                    lst_products_added_adapter.notifyDataSetChanged();
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

        JSONArray products = new JSONArray();
        for(Map<String,String> itemProduct : prod_added_list) {
            JSONObject jsonObj = new JSONObject();
            InventoryProduct.insert(getActivity(),id_visit,itemProduct.get("id"),itemProduct.get("stock"),itemProduct.get("system_stock"),itemProduct.get("comment"));
            try {
                jsonObj.put("id_product",itemProduct.get("id"));
                jsonObj.put("stock",itemProduct.get("stock"));
                jsonObj.put("system_stock",itemProduct.get("system_stock"));
                jsonObj.put("comment",itemProduct.get("comment"));
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
            requestData.put("request",METHOD.SET_AUTOSALE_STOCK);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestManager.sharedInstance().setListener(this);
        RequestManager.sharedInstance().makeRequestWithJSONDataAndMethod(requestData,METHOD.SET_AUTOSALE_STOCK);
    }

    public void restartProductDetail(){
        sel_prod.put("stock","0");
        sel_prod.put("system_stock","0");
        sel_prod.put("change", "0");
    }

    @Override
    public void prepareRequest(METHOD method, Map<String, String> params) {

    }

    @Override
    public void decodeResponse(String stringResponse) {
        try {
            JSONObject resp          = new JSONObject(stringResponse);

            if (resp.getString("method").equalsIgnoreCase(METHOD.SET_AUTOSALE_STOCK.toString())) {
                if (resp.getString("error").isEmpty())
                    InventoryProduct.updateInventoryProductToSent(getActivity(),id_visit);

                RequestManager.sharedInstance().showConfirmationDialogWithListener(getActivity().getString(R.string.req_man_autosale_stock), getActivity(), this);
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


