package com.sellcom.tracker;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

import database.models.Customer;
import de.timroes.android.listview.EnhancedListView;
import util.ClientsAdapter;
import util.Utilities;

/**
 * Created by juanc.jimenez on 31/07/14.
 */
public class FragmentClients extends Fragment implements SearchView.OnQueryTextListener,
        ClientsAdapter.OnClientDeletedListener,
        AdapterView.OnItemClickListener,
        EnhancedListView.OnDismissCallback{

    final static public String TAG = "clients";
    public static boolean hideButtons = true;

    private EnhancedListView clientList;
    private ClientsAdapter adapter;
    private List<Customer> list;
    private OnClientSelectedListener clientSelectedListener;
    private int clientSelected;
    private boolean onLandscape;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(Utilities.isHandset(getActivity())) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }else{
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }

        onLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_clients, container, false);


        if (view != null) {
            clientList = (EnhancedListView) view.findViewById(R.id.client_list);

            list = new ArrayList<Customer>();
            adapter = new ClientsAdapter(getActivity(), list);
            adapter.setOnClientDeletedListener(this);
            clientList.setAdapter(adapter);

            clientList.setDismissCallback(this);
            clientList.enableSwipeToDismiss();
            clientList.setRequireTouchBeforeDismiss(false);

            populateList();
        }

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if(onLandscape) {

            if (list.isEmpty())
                fragmentTransaction.add(R.id.children_container, new FragmentClientsAddEdit(), FragmentClientsAddEdit.TAG);
            else {
                fragmentTransaction.add(R.id.children_container, FragmentClientsDetail.newInstance(list.get(0).getId()), FragmentClientsDetail.TAG);
                clientSelected = 0;
            }

            fragmentTransaction.commit();
        }
        else{

            if (fragmentManager.findFragmentByTag(FragmentClientsDetail.TAG) != null)
                fragmentTransaction.remove(fragmentManager.findFragmentByTag(FragmentClientsDetail.TAG));

            if (fragmentManager.findFragmentByTag(FragmentClientsAddEdit.TAG) != null)
                fragmentTransaction.remove(fragmentManager.findFragmentByTag(FragmentClientsAddEdit.TAG));

            fragmentTransaction.commit();
        }
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(NavigationDrawerFragment.CLIENTS);

        try {
            clientSelectedListener = (OnClientSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnClientSelectedListener");

        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (!((MainActivity) getActivity()).isDrawerOpen) {
            menu.clear();
            inflater.inflate(R.menu.clients, menu);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem searchClient = menu.findItem(R.id.search_client);
        MenuItem deleteButton = menu.findItem(R.id.delete);

        if (searchClient != null) {

            SearchView searchView = (SearchView)searchClient.getActionView();
            int searchImgId = getResources().getIdentifier("android:id/search_button", null, null);
            ImageView v = (ImageView) searchView.findViewById(searchImgId);
            v.setImageResource(R.drawable.ic_search);
            searchView.setOnQueryTextListener(this);

            searchClient.setEnabled(!list.isEmpty());
        }

        if (deleteButton != null) {
            deleteButton.setEnabled(!list.isEmpty());

            if (deleteButton.isEnabled()) deleteButton.setIcon(R.drawable.ic_delete_enabled);
            else deleteButton.setIcon(R.drawable.ic_delete_disabled);
        }
    }

    private void populateList() {
        list.clear();
        list.addAll(Customer.getAll(getActivity()));
        adapter.notifyDataSetChanged();
    }

    @Override
    public EnhancedListView.Undoable onDismiss(EnhancedListView enhancedListView, int i) {

        final int position = i;
        final Customer client = list.get(position);
        list.remove(position);
        adapter.notifyDataSetChanged();

        onClientDeleted(i);

        return new EnhancedListView.Undoable() {
            @Override
            public void undo() {
                list.add(position, client);
                adapter.notifyDataSetChanged();

                getActivity().invalidateOptionsMenu();
            }

            @Override
            public String getTitle() {
                return getString(R.string.product_deleted);
            }

            @Override
            public void discard() {
                Customer.delete(getActivity(), client.getId());
            }
        };
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        FragmentClientsAddEdit clientsAddEdit = (FragmentClientsAddEdit) fragmentManager.findFragmentByTag(FragmentClientsAddEdit.TAG);
        switch (item.getItemId()) {
            case R.id.add:
                //fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.shrink_out, R.anim.slide_from_left, R.anim.shrink_out);

                if (onLandscape) {
                    fragmentTransaction.replace(R.id.children_container, new FragmentClientsAddEdit(), FragmentClientsAddEdit.TAG).commit();
                } else {
                    if (clientsAddEdit == null) {
                        clientsAddEdit = new FragmentClientsAddEdit();
                    }
                    fragmentTransaction.addToBackStack(null);
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    fragmentTransaction.replace(R.id.container, clientsAddEdit, FragmentClientsAddEdit.TAG).commit();
                    ((MainActivity) getActivity()).depthCounter = 2;
                }
                break;
            case R.id.delete:
                hideButtons = !hideButtons;
                adapter.notifyDataSetChanged();
                break;
            case R.id.edit:
                //fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.shrink_out, R.anim.slide_from_left, R.anim.shrink_out);
                fragmentTransaction.replace(
                        R.id.children_container,
                        FragmentClientsAddEdit.newInstance(list.get(clientSelected).getId()),
                        FragmentClientsAddEdit.TAG).commit();
                break;
            case R.id.save:
                clientsAddEdit.saveClient();
                populateList();
                break;
            case R.id.discard:
                //fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.shrink_out, R.anim.slide_from_left, R.anim.shrink_out);

                if (list.isEmpty())
                    clientsAddEdit.clearInputs();
                else
                    fragmentTransaction.replace(
                            R.id.children_container,
                            FragmentClientsDetail.newInstance(list.get(clientSelected).getId()),
                            FragmentClientsDetail.TAG).commit();
                break;
        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {

        adapter.filterProducts(s);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        adapter.filterProducts(s);
        clientList.setSelectionAfterHeaderView();
        return false;
    }

    @Override
    public void onClientDeleted(int position) {

        if (onLandscape) {

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            if (list.isEmpty()) {

                //fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.shrink_out, R.anim.slide_from_left, R.anim.shrink_out);
                fragmentTransaction.replace(R.id.children_container, new FragmentClientsAddEdit(), FragmentClientsAddEdit.TAG).commit();

            } else if(clientSelected == position) {

                FragmentClientsDetail clientsDetail = (FragmentClientsDetail) fragmentManager.findFragmentByTag(FragmentClientsDetail.TAG);

                if (clientsDetail == null) {
                    clientsDetail = FragmentClientsDetail.newInstance(list.get(0).getId());
                    fragmentTransaction.replace(R.id.children_container, clientsDetail, FragmentClientsDetail.TAG).commit();
                }
                clientSelectedListener.onClientSelected(list.get(0));
                clientSelected = 0;
            }
        } else if (list.isEmpty()) {
            hideButtons = true;
            getActivity().invalidateOptionsMenu();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        if (onLandscape) {

            FragmentClientsDetail clientsDetail = (FragmentClientsDetail) getActivity().getSupportFragmentManager().findFragmentByTag(FragmentClientsDetail.TAG);

            if (clientsDetail == null) {
                clientsDetail = FragmentClientsDetail.newInstance(list.get(i).getId());
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                //transaction.setCustomAnimations(R.anim.slide_from_right, R.anim.shrink_out, R.anim.slide_from_left, R.anim.shrink_out);
                transaction.replace(R.id.children_container, clientsDetail, FragmentClientsDetail.TAG).commit();
            }
            clientSelectedListener.onClientSelected(list.get(i));
            clientSelected = i;

        } else {
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            //fragmentTransaction.setCustomAnimations(R.anim.grow_in, R.anim.abc_fade_out, R.anim.abc_fade_in, R.anim.shrink_out);
            fragmentTransaction.addToBackStack(null);

            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            fragmentTransaction.replace(R.id.container, FragmentClientsDetail.newInstance(list.get(i).getId()), FragmentClientsDetail.TAG).commit();

            ((MainActivity) getActivity()).depthCounter = 2;
        }
    }

    public interface OnClientSelectedListener {
        public void onClientSelected(Customer client);
    }
}
