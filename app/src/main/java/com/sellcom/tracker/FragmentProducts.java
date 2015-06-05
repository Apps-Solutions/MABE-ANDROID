package com.sellcom.tracker;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SearchView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

import database.models.Product;
import de.timroes.android.listview.EnhancedListView;
import util.ProductsAdapter;
import util.Utilities;

/**
 * Created by juanc.jimenez on 29/05/14.
 */
public class FragmentProducts extends Fragment implements View.OnClickListener,
        AdapterView.OnItemClickListener,
        EnhancedListView.OnDismissCallback,
        SearchView.OnQueryTextListener,
        SearchView.OnCloseListener,
        AbsListView.OnScrollListener,
        ProductsAdapter.OnProductDeletedListener{

    final static public String TAG = "product";
    public static boolean isScrolling;
    public static boolean hideButtons = true;
    private SearchView searchProducts;
    private EnhancedListView productsList;
    private ProductsAdapter dataAdapter;
    private List<Product> list;
    private OnProductSelectedListener productSelectedListener;
    private boolean onLandscape;
    private int productSelected;

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

        View view = inflater.inflate(R.layout.fragment_products, container, false);

        if (view != null) {
            searchProducts = (SearchView) view.findViewById(R.id.search_product);
            ImageButton scanButton = (ImageButton) view.findViewById(R.id.scanButton);
            productsList = (EnhancedListView) view.findViewById(R.id.products_list);

            LinearLayout ll = (LinearLayout)searchProducts.getChildAt(0);
            LinearLayout ll2 = (LinearLayout)ll.getChildAt(2);
            LinearLayout ll3 = (LinearLayout)ll2.getChildAt(1);

            AutoCompleteTextView autoComplete = ((AutoCompleteTextView)ll3.getChildAt(0));
            autoComplete.setHintTextColor(Color.parseColor("#AAFFFFFF"));

            searchProducts.setOnQueryTextListener(this);
            scanButton.setOnClickListener(this);
            productsList.setOnScrollListener(this);
            productsList.setOnItemClickListener(this);
            productsList.setDismissCallback(this);
            productsList.enableSwipeToDismiss();
            productsList.setRequireTouchBeforeDismiss(false);

            list = new ArrayList<Product>();
            dataAdapter = new ProductsAdapter(getActivity(), list);
            dataAdapter.setOnProductDeletedListener(this);
            productsList.setAdapter(dataAdapter);

            populateList();
        }

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if(onLandscape) {

            if (list.isEmpty())
                fragmentTransaction.add(R.id.product_content, new FragmentProductsAddEdit(), FragmentClientsAddEdit.TAG);
            else {
                fragmentTransaction.add(R.id.product_content, FragmentProductsDetail.newInstance(list.get(0).getId()), FragmentProductsDetail.TAG);
                productSelected = 0;
            }

            fragmentTransaction.commit();

        }
        else{

            if (fragmentManager.findFragmentByTag(FragmentProductsDetail.TAG) != null)
                fragmentTransaction.remove(fragmentManager.findFragmentByTag(FragmentProductsDetail.TAG));

            if (fragmentManager.findFragmentByTag(FragmentProductsAddEdit.TAG) != null)
                fragmentTransaction.remove(fragmentManager.findFragmentByTag(FragmentProductsAddEdit.TAG));

            if (fragmentManager.findFragmentByTag("camara") != null)
                fragmentTransaction.remove(fragmentManager.findFragmentByTag("camara"));

            fragmentTransaction.commit();
        }

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void populateList() {
        list.clear();
        list.addAll(Product.getAll(getActivity()));
        dataAdapter.notifyDataSetChanged();
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        isScrolling = !(scrollState == SCROLL_STATE_IDLE);
    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i2, int i3) {

    }

    @Override
    public void onClick(View view) {
        new IntentIntegrator(getActivity()).initiateScan();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        if (onLandscape) {

            FragmentProductsDetail productsDetail = (FragmentProductsDetail) getActivity().getSupportFragmentManager().findFragmentByTag(FragmentProductsDetail.TAG);

            if (productsDetail == null ) {
                productsDetail = FragmentProductsDetail.newInstance(list.get(i).getId());
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                //transaction.setCustomAnimations(R.anim.slide_from_right, R.anim.shrink_out, R.anim.slide_from_left, R.anim.shrink_out);
                transaction.replace(R.id.product_content, productsDetail, FragmentProductsDetail.TAG).commit();
            }
            productSelectedListener.onProductSelected(list.get(i));
            productSelected = i;

        }else {
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            //fragmentTransaction.setCustomAnimations(R.anim.grow_in, R.anim.abc_fade_out, R.anim.abc_fade_in, R.anim.shrink_out);
            fragmentTransaction.addToBackStack(null);

            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            fragmentTransaction.replace(R.id.container, FragmentProductsDetail.newInstance(list.get(i).getId()), FragmentProductsDetail.TAG).commit();

            ((MainActivity) getActivity()).depthCounter = 2;
        }
    }

    @Override
    public EnhancedListView.Undoable onDismiss(EnhancedListView enhancedListView, int i) {

        final int position = i;
        final Product product = list.get(position);
        list.remove(position);
        dataAdapter.notifyDataSetChanged();

        onProductDeleted(i);

        return new EnhancedListView.Undoable() {
            @Override
            public void undo() {
                list.add(position, product);
                dataAdapter.notifyDataSetChanged();

                getActivity().invalidateOptionsMenu();
            }

            @Override
            public String getTitle() {
                return getString(R.string.product_deleted);
            }

            @Override
            public void discard() {
                Product.delete(getActivity(), product.getId());
            }
        };
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        dataAdapter.filterProducts(s);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        dataAdapter.filterProducts(s);
        productsList.setSelectionAfterHeaderView();
        return false;
    }

    @Override
    public boolean onClose() {
        dataAdapter.filterProducts("");
        productsList.setSelectionAfterHeaderView();
        return false;
    }

    public void scanResult(IntentResult scanResult) {
        if (scanResult != null) {
            searchProducts.setQuery(scanResult.getContents(), true);
        }
    }

    @Override
    public void onProductDeleted(int position) {

        if (onLandscape) {

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            if (list.isEmpty()) {

                //fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.shrink_out, R.anim.slide_from_left, R.anim.shrink_out);
                fragmentTransaction.replace(R.id.product_content, new FragmentProductsAddEdit(), FragmentProductsAddEdit.TAG).commit();

            } else if(productSelected == position) {

                FragmentProductsDetail productsDetail = (FragmentProductsDetail) fragmentManager.findFragmentByTag(FragmentProductsDetail.TAG);

                if (productsDetail == null) {
                    productsDetail = FragmentProductsDetail.newInstance(list.get(0).getId());
                    fragmentTransaction.replace(R.id.product_content, productsDetail, FragmentProductsDetail.TAG).commit();
                }
                productSelectedListener.onProductSelected(list.get(0));
                productSelected = 0;
            }
        } else if (list.isEmpty()) {
            hideButtons = true;
            getActivity().invalidateOptionsMenu();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
        if (!((MainActivity) getActivity()).isDrawerOpen) {
            menu.clear();
            inflater.inflate(R.menu.products, menu);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem deleteButton = menu.findItem(R.id.delete_product);

        if (deleteButton != null) {
            deleteButton.setEnabled(!list.isEmpty());

            if (deleteButton.isEnabled()) deleteButton.setIcon(R.drawable.ic_delete_enabled);
            else deleteButton.setIcon(R.drawable.ic_delete_disabled);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        FragmentProductsAddEdit productsAddEdit = (FragmentProductsAddEdit) fragmentManager.findFragmentByTag(FragmentProductsAddEdit.TAG);

        switch (item.getItemId()) {
            case R.id.add:
                //fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.shrink_out, R.anim.slide_from_left, R.anim.shrink_out);

                if (onLandscape) {
                    fragmentTransaction.replace(R.id.product_content, new FragmentProductsAddEdit(), FragmentProductsAddEdit.TAG).commit();
                } else {
                    if (productsAddEdit == null) {
                        productsAddEdit = new FragmentProductsAddEdit();
                    }
                    fragmentTransaction.addToBackStack(null);
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    fragmentTransaction.replace(R.id.container, productsAddEdit, FragmentProductsAddEdit.TAG).commit();
                    ((MainActivity) getActivity()).depthCounter = 2;
                }
            break;
            case R.id.delete:
                hideButtons = !hideButtons;
                dataAdapter.notifyDataSetChanged();
                break;
            case R.id.edit:
                //fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.shrink_out, R.anim.slide_from_left, R.anim.shrink_out);
                fragmentTransaction.replace(
                        R.id.product_content,
                        FragmentProductsAddEdit.newInstance(list.get(productSelected).getId()),
                        FragmentProductsAddEdit.TAG).commit();
                break;
            case R.id.save:
                productsAddEdit.saveProduct();
                populateList();
                break;
            case R.id.discard:
                if (onLandscape) {
                    //fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.shrink_out, R.anim.slide_from_left, R.anim.shrink_out);

                    if (list.isEmpty())
                        productsAddEdit.clearFields();
                    else
                        fragmentTransaction.replace(
                                R.id.product_content,
                                FragmentProductsDetail.newInstance(list.get(productSelected).getId()),
                                FragmentProductsDetail.TAG).commit();
                }
                break;
        }
        return true;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(NavigationDrawerFragment.PRODUCTS);

        try {
            productSelectedListener = (OnProductSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnProductSelectedListener");

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        productsList.discardUndo();
    }

    public interface OnProductSelectedListener {
        public void onProductSelected(Product product);
    }
}
