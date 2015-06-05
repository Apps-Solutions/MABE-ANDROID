package com.sellcom.tracker;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import async_request.ConfirmationDialogListener;
import async_request.METHOD;
import async_request.RequestManager;
import async_request.UIResponseListenerInterface;
import database.models.Brand;
import database.models.PartialPay;
import database.models.Product;
import database.models.Session;
import database.models.User;
import database.models.WholeSalesPrices;
import database.models.WholeSalesPricesProduct;
import util.SpinnerAdapter;
import util.TrackerManager;
import util.Utilities;
import util.WholeSalePricesAdapter;

/**
 * Created by jonathan.vazquez on 26/02/15.
 */
public class FragmentWholesalePricesWholesale extends Fragment implements View.OnClickListener, UIResponseListenerInterface,FragmentSignature.setSignatureImg, ConfirmationDialogListener {
    final static public String TAG = "quality_incidences_wholesale";

    ImageButton btn_whp_add;
    Spinner     spn_whp_brand, spn_whp_product;
    EditText    edt_whp_price, edt_whp_chief_comment;

    FragmentManager     fragmentManager;
    FragmentTransaction fragmentTransaction;
    ImageView           img_signature;
    TextView            txv_signature_hint;
    FragmentSignature   fragmentSignature;
    private Bitmap      bitmapSignature = null;
    private String      stringSignature = null;
    String str_id;

    SpinnerAdapter  product_adapter, brand_adapter;
    WholeSalePricesAdapter      products_adapter;

    ListView                    lst_products_added;

    List<Map<String,String>>    brand_list;
    List<Map<String,String>>    product_list;
    List<Map<String,String>>    prod_added_list;
    Map<String,String>          sel_prod;

    String                      visit_id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_wholesale_prices, container, false);


        if (view != null) {

            Log.d(TAG,"On Create View");

            visit_id     = TrackerManager.sharedInstance().getCurrent_pdv().get("visit_id");

            fragmentManager = getActivity().getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();

            btn_whp_add             = (ImageButton) view.findViewById(R.id.btn_whp_add);
            btn_whp_add.setOnClickListener(this);

            prod_added_list         = WholeSalesPricesProduct.getProductsInVisit(getActivity(),visit_id);
            Log.d(TAG,prod_added_list.toString());
            lst_products_added      = (ListView)view.findViewById(R.id.lst_products_added);
            products_adapter        = new WholeSalePricesAdapter(getActivity(),prod_added_list);
            lst_products_added.setAdapter(products_adapter);

            product_list            = Product.getAllOwnInMaps(getActivity());
            spn_whp_product         = (Spinner) view.findViewById(R.id.spn_whp_product);

            spn_whp_brand           = (Spinner) view.findViewById(R.id.spn_whp_brand);
            brand_list              = Brand.getAllInMaps(getActivity());
            brand_adapter           = new SpinnerAdapter(getActivity(),brand_list, SpinnerAdapter.SPINNER_TYPE.BRANDS);
            spn_whp_brand.setAdapter(brand_adapter);
            spn_whp_brand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    Map<String,String> brand = brand_list.get(i);
                    product_list.clear();
                    product_list    = Product.getAllByBrandInMaps(getActivity(),brand.get("id"));
                    product_adapter         = new SpinnerAdapter(getActivity(), product_list, SpinnerAdapter.SPINNER_TYPE.PRODUCTS);
                    spn_whp_product.setAdapter(product_adapter);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            spn_whp_product.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    sel_prod    = product_list.get(i);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            edt_whp_price           = (EditText) view.findViewById(R.id.edt_whp_price);
            edt_whp_chief_comment   = (EditText) view.findViewById(R.id.edt_whp_chief_comment);

            img_signature               = (ImageView) view.findViewById(R.id.img_signature);
            img_signature.setOnClickListener(this);
            txv_signature_hint          = (TextView) view.findViewById(R.id.txv_signature_hint);

        }

        return view;
    }

    @Override
    public void onClick(View view) {
        if (view.getId()    == R.id.btn_whp_add){
            if (prod_added_list.isEmpty()) {
                Log.d(TAG,"Insert 0");
                restartProductDetail();
                WholeSalesPricesProduct.insert(getActivity(),"X",visit_id,sel_prod.get("id"),sel_prod.get("name"),sel_prod.get("price"));
                prod_added_list.add(sel_prod);
                products_adapter.notifyDataSetChanged();
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
                    Log.d(TAG,"Insert 0");
                    restartProductDetail();
                    WholeSalesPricesProduct.insert(getActivity(),"X",visit_id,sel_prod.get("id"),sel_prod.get("name"),sel_prod.get("price"));
                    prod_added_list.add(sel_prod);
                    products_adapter.notifyDataSetChanged();
                }
                else
                    Toast.makeText(getActivity(), "Este producto ya ha sido agregado", Toast.LENGTH_SHORT).show();
            }
        }
        if (view.getId()    == R.id.img_signature){
            txv_signature_hint.setText(""+ getResources().getString(R.string.signature_hint));
            fragmentSignature = new FragmentSignature();
            fragmentSignature.setSetSignatureImg(this);
            fragmentTransaction.replace(R.id.container, fragmentSignature, FragmentSignature.TAG);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            ((MainActivity) getActivity()).depthCounter = 5;
        }
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

    public boolean validateData(){
        if (prod_added_list.isEmpty()) {
            Toast.makeText(getActivity(), "Lista de productos vacía", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(img_signature.getDrawable() == null){
            Toast.makeText(getActivity(),getResources().getString(R.string.missing_signature),Toast.LENGTH_SHORT).show();
            return false;
        }
        int index = 1;
        for(Map<String,String> itemProduct : prod_added_list) {
            if (itemProduct.get("price").isEmpty()){
                Toast.makeText(getActivity(),"Error en el precio del producto "+ index,Toast.LENGTH_SHORT).show();
                return false;
            }

        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.save:

                if (validateData()){

                    String token        = Session.getSessionActive(getActivity()).getToken();
                    String username     = User.getUser(getActivity(), Session.getSessionActive(getActivity()).getUser_id()).getEmail();
                    String comment      = Utilities.validateWithBlankSpaceDefault(edt_whp_chief_comment.getText().toString());

                    long id         = WholeSalesPrices.insert(getActivity(),visit_id,comment,stringSignature);
                    str_id   = ""+id;

                    JSONArray products = new JSONArray();

                    for(Map<String,String> itemProduct : prod_added_list) {
                        JSONObject jsonObj = new JSONObject();
                        try {
                            jsonObj.put("id_product",itemProduct.get("id_product"));
                            jsonObj.put("price",itemProduct.get("price"));

                            products.put(jsonObj);

                            WholeSalesPricesProduct.update(getActivity(),visit_id,itemProduct.get("id"),str_id);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    JSONObject requestData = new JSONObject();
                    try {
                        requestData.put("token",token);
                        requestData.put("user",username);
                        requestData.put("id_visit",visit_id);
                        requestData.put("evidence",stringSignature);
                        requestData.put("comment",comment);
                        requestData.put("products", products.toString());
                        requestData.put("request", METHOD.SET_SUPPLIER_PRICES);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    RequestManager.sharedInstance().setListener(this);
                    RequestManager.sharedInstance().makeRequestWithJSONDataAndMethod(requestData,METHOD.SET_SUPPLIER_PRICES);

                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    public void restartProductDetail(){
        sel_prod.put("price","");
        sel_prod.put("id_product",sel_prod.get("id"));
    }

    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    @Override
    public void prepareRequest(METHOD method, Map<String, String> params) {

    }

    @Override
    public void decodeResponse(String stringResponse) {
        try {
            JSONObject resp          = new JSONObject(stringResponse);

            if (resp.getString("method").equalsIgnoreCase(METHOD.SET_SUPPLIER_PRICES.toString())) {
                if (resp.getString("error").isEmpty())
                    WholeSalesPrices.updateWholeSalesPricesToStatusSent(getActivity(),str_id);
                RequestManager.sharedInstance().showConfirmationDialogWithListener("Precios de mayoristas enviados correctamente", getActivity(), this);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void okFromConfirmationDialog(String message) {
        getActivity().onBackPressed();
    }

    @Override
    public void getSignatureImgBase64(String imgSignature) {
        stringSignature = imgSignature;
        bitmapSignature = decodeBase64(stringSignature);
        txv_signature_hint.setText("");
        img_signature.setImageBitmap(bitmapSignature);
    }
}