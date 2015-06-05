package com.sellcom.tracker;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import async_request.RequestManager;
import util.ClientsAdapterPreview;
import util.TrackerManager;

/**
 * Created by Raiseralex21 on 26/01/15.
 */
public class FragmentClientsDialogList extends DialogFragment implements View.OnClickListener{

    final static public String TAG = "fragment_dialog";
    static Context context;
    public Fragment fragment;
    public FragmentTransaction fragmentTransaction;
    View view;
    List<Map<String,String>> pdv_array;
    UpdaterPostDialog listener;

    public ListView listViewClients;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();

        pdv_array = new ArrayList<Map<String, String>>();
        try {
            JSONArray j_pdv_array = new JSONArray(getArguments().getString("pdv_array"));
            for (int i=0; i<j_pdv_array.length(); i++){
                JSONObject obj = j_pdv_array.getJSONObject(i);
                pdv_array.add(RequestManager.sharedInstance().jsonToMap(obj));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.dialog_list_clients, container, false);
        listViewClients = (ListView) view.findViewById(R.id.listv_clietnes);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setCancelable(false);
        ClientsAdapterPreview clientsAdapterPreview = new ClientsAdapterPreview(context,pdv_array );
        listViewClients.setAdapter(clientsAdapterPreview);

        listViewClients.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dismiss();

                //Toast.makeText(context, "" + pdv_array.get(position).get("pdv_name"), Toast.LENGTH_SHORT).show();
                TrackerManager.sharedInstance().setCurrent_pdv(pdv_array.get(position));
                listener.updateHeaderText();
            }
        });

        return view;
    }
    @Override
    public void onClick(View v) {

    }

    public interface UpdaterPostDialog {
        public void updateHeaderText();
    }
}
