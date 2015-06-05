package com.sellcom.tracker.supervisor;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sellcom.tracker.R;

/**
 * Created by hugo.figueroa 11/05/15.
 */

public class FragmentStoreCheck extends Fragment {

    final static public String                  TAG                 = "store_check";
    private Context                             context;
    private Fragment                            fragment;
    private FragmentManager                     fragmentManager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        fragmentManager = getActivity().getSupportFragmentManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_store_check, container, false);



        return view;
    }


}
