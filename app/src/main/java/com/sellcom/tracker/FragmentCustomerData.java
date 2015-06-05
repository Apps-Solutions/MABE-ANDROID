package com.sellcom.tracker;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hugo.figueroa 16/01/15.
 */
public class FragmentCustomerData extends Fragment implements View.OnClickListener{

    final static public String TAG = "costumer_data";
    private Context context;
    private ImageView imgv_CustomerData;
    private Button btn_CustomerData;
    private TextView txt_CustomerName, txt_CustomerAddress, txt_CustomerOrder,
            txt_CustomerBill, txt_CustomerQuantity, txt_CustomerCollect,
            txt_CustomerPaymentReception, txt_CustomerPaymentType, txt_CustomerStatus,
            txt_CustomerPreviousOrder, txt_CustomerProduct, txt_CustomerPreviousQuantity,
            txt_CustomerTypeCustomer, txt_CustomerMarketActions;

    final Map<String, String> params = new HashMap<String, String>();

    public FragmentCustomerData() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_customer_data, container, false);

        if (view != null) {
            context = getActivity();
            txt_CustomerName = (TextView)view.findViewById(R.id.txt_CustomerName);
            txt_CustomerAddress = (TextView)view.findViewById(R.id.txt_CustomerAddress);
            txt_CustomerOrder = (TextView)view.findViewById(R.id.txt_CustomerOrder);
            txt_CustomerBill = (TextView)view.findViewById(R.id.txt_CustomerBill);
            txt_CustomerQuantity = (TextView)view.findViewById(R.id.txt_CustomerQuantity);
            txt_CustomerCollect = (TextView)view.findViewById(R.id.txt_CustomerCollect);
            txt_CustomerPaymentReception = (TextView)view.findViewById(R.id.txt_CustomerPaymentReception);
            txt_CustomerPaymentType = (TextView)view.findViewById(R.id.txt_CustomerPaymentType);
            txt_CustomerStatus = (TextView)view.findViewById(R.id.txt_CustomerStatus);
            txt_CustomerPreviousOrder = (TextView)view.findViewById(R.id.txt_CustomerPreviousOrder);
            txt_CustomerProduct = (TextView)view.findViewById(R.id.txt_CustomerProduct);
            txt_CustomerPreviousQuantity = (TextView)view.findViewById(R.id.txt_CustomerPreviousQuantity);
            txt_CustomerTypeCustomer = (TextView)view.findViewById(R.id.txt_CustomerTypeCustomer);
            txt_CustomerMarketActions = (TextView)view.findViewById(R.id.txt_CustomerMarketActions);


            btn_CustomerData = (Button)view.findViewById(R.id.btn_CustomerData);
            btn_CustomerData.setOnClickListener(this);

            imgv_CustomerData = (ImageView)view.findViewById(R.id.imgv_CustomerData);
            imgv_CustomerData.setOnClickListener(this);
        }

        return view;
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btn_CustomerData:
                Toast.makeText(context,"Presionaste el Boton",Toast.LENGTH_SHORT).show();
                break;
            case R.id.imgv_CustomerData:
                Toast.makeText(context,"Presionaste la Imagen",Toast.LENGTH_SHORT).show();
                break;
        }

    }
}
