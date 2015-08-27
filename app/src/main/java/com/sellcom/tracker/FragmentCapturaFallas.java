package com.sellcom.tracker;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.LinearLayout;



/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentCapturaFallas extends Fragment {

    LinearLayout  lin_fallas,lin_tipo_servicio,lin_status;
    String type;

    public FragmentCapturaFallas() {
        // Required empty public constructor
    }
    final static public String TAG = "captura_servicios";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_captura_fallas, container, false);

        lin_fallas = (LinearLayout)view.findViewById(R.id.lin_fallas);
        lin_tipo_servicio = (LinearLayout)view.findViewById(R.id.lin_tipo_servicio);
        lin_status = (LinearLayout)view.findViewById(R.id.lin_status);

        type = getArguments().getString("type");

        if(type.equals("1")){
            lin_fallas.setVisibility(View.VISIBLE);
            lin_tipo_servicio.setVisibility(View.GONE);
            lin_status.setVisibility(View.GONE);
        }else if(type.equals("2")){
            lin_fallas.setVisibility(View.GONE);
            lin_tipo_servicio.setVisibility(View.VISIBLE);
            lin_status.setVisibility(View.GONE);
        }else if(type.equals("3")){
            lin_fallas.setVisibility(View.GONE);
            lin_tipo_servicio.setVisibility(View.GONE);
            lin_status.setVisibility(View.VISIBLE);
        }

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
                //fecha_compra_producto.setText("");
                Toast.makeText(getActivity(), "Enviado", Toast.LENGTH_SHORT).show();
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
