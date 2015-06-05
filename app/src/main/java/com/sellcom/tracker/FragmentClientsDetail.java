package com.sellcom.tracker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import database.models.Customer;

/**
 * Created by juanc.jimenez on 04/08/14.
 */
public class FragmentClientsDetail extends Fragment {

    final static public String TAG = "clients_detail";

    private Customer client;
    private TextView clientName, clientPhone, clientEmail, clientTIN, clientAddress;

    public static FragmentClientsDetail newInstance(int clientId) {

        Bundle bundle = new Bundle();
        bundle.putInt("id", clientId);
        FragmentClientsDetail fragmentClientsDetail = new FragmentClientsDetail();
        fragmentClientsDetail.setArguments(bundle);
        return fragmentClientsDetail;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if(bundle != null) {
            int id = bundle.getInt("id", 0);
            client = Customer.getCustomer(getActivity(), id);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_clients_detail, container, false);

        if (view != null) {

            clientName = (TextView) view.findViewById(R.id.client_name);
            clientPhone = (TextView) view.findViewById(R.id.client_phone);
            clientEmail = (TextView) view.findViewById(R.id.client_email);
            clientAddress = (TextView) view.findViewById(R.id.client_address);
            clientTIN = (TextView) view.findViewById(R.id.client_rfc);

            updateDetail(client);
        }
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void updateDetail(Customer client) {

        if (client != null) {

            clientName.setText(client.getInfo().getFirstName());
            clientPhone.setText(client.getInfo().getPhone());
            clientEmail.setText(client.getInfo().getEmail());
            clientTIN.setText(client.getInfo().getRfc());

            String fullAddress = client.getInfo().getAddress1()
                    + ", " + client.getInfo().getCity()
                    + ", " + client.getInfo().getState()
                    + ", " + client.getInfo().getCountry() + "\n" + client.getInfo().getZipCode();

            clientAddress.setText(fullAddress);

            SupportMapFragment mapFragment = SupportMapFragment.newInstance(new GoogleMapOptions().camera(CameraPosition.fromLatLngZoom(new LatLng(19.4143522,-99.1305552), 13)));
            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.map_container, mapFragment).commit();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (!((MainActivity) getActivity()).isDrawerOpen) {
            menu.clear();
            inflater.inflate(R.menu.edit, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Bundle bundle = new Bundle();
        bundle.putInt("id", client.getId());
        FragmentClientsAddEdit clientsAddEdit = new FragmentClientsAddEdit();
        clientsAddEdit.setArguments(bundle);

        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        //fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.shrink_out, R.anim.slide_from_left, R.anim.shrink_out);
        fragmentTransaction.replace(R.id.container, clientsAddEdit, FragmentClientsAddEdit.TAG).commit();

        ((MainActivity) getActivity()).depthCounter = 2;
        return true;
    }
}
