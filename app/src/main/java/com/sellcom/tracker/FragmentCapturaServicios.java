package com.sellcom.tracker;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.EditText;

import com.sellcom.tracker.MainActivity;
import com.sellcom.tracker.R;

import java.util.Map;

import async_request.METHOD;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentCapturaServicios extends Fragment {

    final static public String TAG = "captura_servicios";

    EditText num_producto, modelo_producto,fecha_compra_producto;

    public FragmentCapturaServicios() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_captura_servicios, container, false);

        num_producto = (EditText)view.findViewById(R.id.num_producto);
        modelo_producto = (EditText)view.findViewById(R.id.modelo_producto);
        //fecha_compra_producto = (EditText)view.findViewById(R.id.fecha_compra_producto);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                num_producto.setText("");
                modelo_producto.setText("");
                //fecha_compra_producto.setText("");
                Toast.makeText(getActivity(),"Enviado",Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (!((MainActivity) getActivity()).isDrawerOpen) {
            menu.clear();
            inflater.inflate(R.menu.add_client, menu);
        }
    }

}
