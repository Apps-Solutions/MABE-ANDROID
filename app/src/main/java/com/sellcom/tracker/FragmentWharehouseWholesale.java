package com.sellcom.tracker;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * Created by jonathan.vazquez on 26/02/15.
 */
public class FragmentWharehouseWholesale extends Fragment {

    final static public String TAG = "wharehouse_wholesale";

    Spinner sp_wh_product;
    EditText et_wh_boxes_out, et_wh_ending_inventory, et_wh_boxes_outTB, et_wh_ending_inventoryTB;
    TextView tv_wh_total_inventory2, tv_wh_productTB, tv_wh_total_inventoryTB;
    TableLayout table_wh_products;
    TableRow tableR_wh_head, tableR_wh_body;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_warehouse, container, false);


        if (view != null) {
            sp_wh_product = (Spinner) view.findViewById(R.id.sp_wh_product);
            et_wh_boxes_out = (EditText) view.findViewById(R.id.et_wh_boxes_out);
            et_wh_ending_inventory = (EditText) view.findViewById(R.id.et_wh_ending_inventory);
            tv_wh_total_inventory2 = (TextView) view.findViewById(R.id.tv_wh_total_inventory);
        }
        return view;
    }
}

/*
sp_wh_product
et_wh_boxes_out
et_wh_ending_inventory
tv_wh_total_inventory2
table_wh_products
tableR_wh_head
tableR_wh_body
tv_wh_productTB
et_wh_boxes_outTB
et_wh_ending_inventoryTB
tv_wh_total_inventoryTB
 */