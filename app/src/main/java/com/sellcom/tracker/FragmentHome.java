package com.sellcom.tracker;


import android.app.Activity;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import database.models.RecordActions;
import util.Utilities;

/**
 * Created by juanc.jimenez on 08/05/14.
 */
public class FragmentHome extends Fragment{

    private static final int SECTION_NUMBER = 0;

    public static final int TIME          = 0;
    public static final int WEATHER       = 1;
    public static final int NOTES         = 2;
    public static final int NOTIFICATIONS = 3;
    public static final int TRAFFIC       = 4;
    public static final int TRAFFIC_MAP   = 5;

    public int HOME_ITEMS = 5;
    public static String HOME = "home";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Utilities.isHandset(getActivity())) {
            HOME_ITEMS = 5;
        }
        else {
            HOME_ITEMS = 6;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        populateView();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(SECTION_NUMBER);
    }

    public void addFragment(int fragment_id){

        int container = 0;
        Fragment fragment = new Fragment();
        String TAG = "";

        switch (fragment_id){
            case WEATHER:
                container = R.id.fragment_weather_container;
                fragment = new FragmentWeather();
                TAG = "weather";
                break;
            case NOTES:
                /*
                container = R.id.fragment_notes_container;
                fragment = new FragmentNotes();
                TAG = "notes";
                */
                break;
            case NOTIFICATIONS:
                /*container = R.id.fragment_notifications_container;
                fragment = new FragmentNotifications();
                TAG = "notifications";
                */
                break;
            case TIME:
                container = R.id.fragment_time_container;
                fragment = new FragmentTime();
                TAG = "time";
                break;
            case TRAFFIC:
                container = R.id.fragment_traffic_container;
                fragment = new FragmentTraffic();
                TAG = "traffic";
                break;
            case TRAFFIC_MAP:
                container = R.id.fragment_traffic_map_container;
                fragment = new FragmentTrafficMap();
                TAG = "trafficmap";
                break;
        }

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (Utilities.isHandset(getActivity()))
            fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left);
        else
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.add(container, fragment, TAG);
        fragmentTransaction.commit();

        new addTask().execute(fragment_id + 1);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    public void populateView() {
        new addTask().execute(0);
    }

    public class addTask extends AsyncTask<Integer, Void, Integer>{

        @Override
        protected Integer doInBackground(Integer... integers) {

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return integers[0];
        }

        @Override
        protected void onPostExecute(Integer integer) {
            if (integer < HOME_ITEMS)
                addFragment(integer);
        }
    }
}
