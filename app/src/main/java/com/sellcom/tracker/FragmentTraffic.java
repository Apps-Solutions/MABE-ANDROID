package com.sellcom.tracker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import util.Utilities;

/**
 * Created by juanc.jimenez on 14/05/14.
 */
public class FragmentTraffic extends Fragment {

    LinearLayout buttonShowTraffic;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_traffic, container, false);

        if (view != null && Utilities.isHandset(getActivity())) {
            buttonShowTraffic = (LinearLayout) view.findViewById(R.id.button_show_traffic);
            buttonShowTraffic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    Fragment prev = getFragmentManager().findFragmentByTag("dialog");
                    if (prev != null) {
                        ft.remove(prev);
                    }
                    ft.addToBackStack(null);

                    FragmentTrafficMap trafficMap = FragmentTrafficMap.newInstance();
//                    trafficMap.getActivity().getWindow().setBackgroundDrawable(
//                            new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    trafficMap.show(ft, "dialog");
                }
            });
        }
        return view;
    }
}
