package com.sellcom.tracker;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by juanc.jimenez on 14/05/14.
 */
public class FragmentNotifications extends Fragment {

    ListView notificationsList;
    TextView emptyView;
    ArrayList<String> list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list = new ArrayList<String>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        if (view != null) {
            notificationsList = (ListView) view.findViewById(R.id.notifications_list);
            emptyView = (TextView) view.findViewById(R.id.empty_view);

            checkForEmptyList();
        }
        return view;
    }

    public void checkForEmptyList() {
        if (list.isEmpty())
            emptyView.setVisibility(View.VISIBLE);
        else
            emptyView.setVisibility(View.GONE);
    }
}
