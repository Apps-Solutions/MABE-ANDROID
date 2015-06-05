package com.sellcom.tracker;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TableLayout;
import android.widget.TableRow;


/**
 * Created by hugo.figueroa 26/02/15.
 */
public class FragmentWarehouseSelfservice extends Fragment {

    final static public String TAG = "FRAGMENT_WAREHOUSE_SELFSERVICE";

    Spinner     sp_wh_product;
    EditText    et_wh_boxes_out, et_wh_ending_inventory, et_wh_boxes_outTB, et_wh_ending_inventoryTB;
    TextView    tv_wh_total_inventory2, tv_wh_productTB, tv_wh_total_inventoryTB;
    TableLayout table_wh_products;
    TableRow    tableR_wh_head, tableR_wh_body;


    public FragmentWarehouseSelfservice() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_warehouse, container, false);

        sp_wh_product               = (Spinner) view.findViewById(R.id.sp_wh_product);
        et_wh_boxes_out             = (EditText) view.findViewById(R.id.et_wh_boxes_out);
        et_wh_ending_inventory      = (EditText) view.findViewById(R.id.et_wh_ending_inventory);
        tv_wh_total_inventory2      = (TextView) view.findViewById(R.id.tv_wh_total_inventory);

        return view;
    }


}
