package com.sellcom.tracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import async_request.DecisionDialogWithListener;
import async_request.METHOD;
import async_request.RequestManager;
import async_request.UIResponseListenerInterface;
import database.DataBaseManager;
import database.models.Customer;
import database.models.Product;
import database.models.Session;
import database.models.User;
import util.Constants;
import util.FragmentCamera;

public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, FragmentProducts.OnProductSelectedListener, FragmentClients.OnClientSelectedListener,
        UIResponseListenerInterface, DecisionDialogWithListener {

    String  TAG =                       "MAIN_ACTIVITY_LOG";

    public NavigationDrawerFragment    mNavigationDrawerFragment;
    private FragmentTransaction         fragmentTransaction;
    private FragmentManager             fragmentManager;
    private int                         position;
    String CURRENT_FRAGMENT_TAG;
    private Fragment                    Fragment_Default;
    private CharSequence mTitle;
    private int mIcon;
    Fragment fragment;
    public int depthCounter = 0;
    public boolean isDrawerOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        RequestManager.sharedInstance().setActivity(this);
        DataBaseManager.sharedInstance().setContext(this);

        fragmentManager = getSupportFragmentManager();

        mNavigationDrawerFragment = (NavigationDrawerFragment)getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        mIcon = R.drawable.ic_preventa;

        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        if (getSupportFragmentManager().findFragmentByTag(FragmentProducts.TAG) == null)
            mNavigationDrawerFragment.selectItem(0);

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        fragmentTransaction = fragmentManager.beginTransaction();
        this.position            = position;

        Fragment_Default = null;
        switch (position) {
            case NavigationDrawerFragment.HOME:

                CURRENT_FRAGMENT_TAG = "home";
                if(fragmentManager.findFragmentByTag(CURRENT_FRAGMENT_TAG) != null){
                    fragment = fragmentManager.findFragmentByTag(CURRENT_FRAGMENT_TAG);
                    Fragment_Default = new FragmentHome();
                }else
                    fragment = new FragmentHome();
                break;

            case NavigationDrawerFragment.WORK_PLAN:

                CURRENT_FRAGMENT_TAG = FragmentWorkPlan.TAG;
                if(fragmentManager.findFragmentByTag(CURRENT_FRAGMENT_TAG) != null){
                    fragment = fragmentManager.findFragmentByTag(CURRENT_FRAGMENT_TAG);
                }else{
                    fragment = new FragmentWorkPlan();
                }
                break;

            case NavigationDrawerFragment.CASHING:

                CURRENT_FRAGMENT_TAG = FragmentCashing.TAG;
                fragment    = fragmentManager.findFragmentByTag(FragmentCashing.TAG);
                fragment    = null;

                fragment = new FragmentCashing();
                prepareRequest(METHOD.GET_INVOICES,new HashMap<String, String>());
                return;

            case NavigationDrawerFragment.LIQUIDATION:

                CURRENT_FRAGMENT_TAG = FragmentPartialPay.TAG;
                if(fragmentManager.findFragmentByTag(CURRENT_FRAGMENT_TAG) != null){
                    fragment = fragmentManager.findFragmentByTag(CURRENT_FRAGMENT_TAG);
                }else{
                    fragment = new FragmentPartialPay();
                }
                break;

            case NavigationDrawerFragment.INVENTORY:

                CURRENT_FRAGMENT_TAG = FragmentInventory.TAG;
                if (fragmentManager.findFragmentByTag(CURRENT_FRAGMENT_TAG) != null)
                    fragment = fragmentManager.findFragmentByTag(CURRENT_FRAGMENT_TAG);
                else
                   fragment = new FragmentInventory();
                break;

            case NavigationDrawerFragment.PRODUCTS:

                CURRENT_FRAGMENT_TAG = FragmentProducts.TAG;
                if(fragmentManager.findFragmentByTag(CURRENT_FRAGMENT_TAG) != null)
                    fragment = fragmentManager.findFragmentByTag(CURRENT_FRAGMENT_TAG);
                else
                    fragment = new FragmentProducts();
                break;

            case NavigationDrawerFragment.ORDERS:

                CURRENT_FRAGMENT_TAG = FragmentOrders.TAG;
                fragment    = fragmentManager.findFragmentByTag(FragmentOrders.TAG);
                fragment    = null;

                fragment = new FragmentOrders();
                prepareRequest(METHOD.GET_PDVS,new HashMap<String, String>());
                return;

            case NavigationDrawerFragment.CLIENTS:

                CURRENT_FRAGMENT_TAG = FragmentClientsAddEdit.TAG;
                if (fragmentManager.findFragmentByTag(FragmentClientsAddEdit.TAG) != null)
                    fragment = fragmentManager.findFragmentByTag(FragmentClientsAddEdit.TAG);
                else
                    fragment = new FragmentClientsAddEdit();
                break;

            case NavigationDrawerFragment.SETTINGS:

                CURRENT_FRAGMENT_TAG = FragmentSettings.TAG;
                if(fragmentManager.findFragmentByTag(CURRENT_FRAGMENT_TAG) != null)
                    fragment = fragmentManager.findFragmentByTag(CURRENT_FRAGMENT_TAG);
                else
                    fragment = new FragmentSettings();
                break;

            case NavigationDrawerFragment.FUELING:

                CURRENT_FRAGMENT_TAG = FragmentFueling.TAG;
                if(fragmentManager.findFragmentByTag(CURRENT_FRAGMENT_TAG) != null)
                    fragment = fragmentManager.findFragmentByTag(CURRENT_FRAGMENT_TAG);
                else
                    fragment = new FragmentFueling();
                break;

            case NavigationDrawerFragment.SYNCHRONIZER:

                CURRENT_FRAGMENT_TAG = FragmentSynchronizer.TAG;
                if(fragmentManager.findFragmentByTag(CURRENT_FRAGMENT_TAG) != null)
                    fragment = fragmentManager.findFragmentByTag(CURRENT_FRAGMENT_TAG);
                else
                    fragment = new FragmentSynchronizer();
                break;

            case NavigationDrawerFragment.LOG_OUT:
                logOut();
                return;

            default:
                return;
        }

        prepareTransaction();
    }

    public void returnToHome(){
        mNavigationDrawerFragment.selectItem(0);
    }

    public void prepareTransaction(){
        if (position > 0) {
            fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.shrink_out, R.anim.slide_from_left, R.anim.shrink_out);
            depthCounter = 1;
        } else if (position == 0) {
            depthCounter = 0;
        }
        if(fragmentManager.findFragmentByTag("trafficmap") != null)
            fragmentTransaction.remove(fragmentManager.findFragmentByTag("trafficmap"));

        if(fragmentManager.findFragmentByTag("home") != null)
            fragmentTransaction.remove(fragmentManager.findFragmentByTag("home"));

        if(Fragment_Default != null)
            fragment = Fragment_Default;

        fragmentTransaction.replace(R.id.container, fragment, CURRENT_FRAGMENT_TAG).commit();
    }

    public void logOut() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder
                .setCancelable(false)
                .setMessage(getString(R.string.log_out))
                .setPositiveButton(getString(R.string.done),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.dismiss();
                                prepareRequest(METHOD.LOGOUT,new HashMap<String, String>());
                            }
                        }
                )
                .setNegativeButton(getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        }
                );

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void onSectionAttached(int number) {
        Log.d("onSectionAttached/D", "Cambiando de icono...." + number);
        mTitle = getResources().getStringArray(R.array.drawer_titles)[number];
        mIcon  = getResources().obtainTypedArray(R.array.icons).getResourceId(number, R.drawable.ic_launcher);
    }

    public void restoreActionBar() {

        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
        actionBar.setIcon(mIcon);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        isDrawerOpen = mNavigationDrawerFragment.isDrawerOpen();
        if (!isDrawerOpen) {
            restoreActionBar();
        }
        return false;
    }

    @Override
    public void onBackPressed() {

        try {
            String file = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"+".photo";
            File dir = new File(file);
            if (dir.exists()) {
                File childFile[] = dir.listFiles();
                if (childFile != null) {
                    for (File file1 : childFile) {
                        file1.delete();
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        if (depthCounter == 1) {

            FragmentManager fragmentManager = getSupportFragmentManager();

            Fragment products  = fragmentManager.findFragmentByTag(FragmentProducts.TAG);
            Fragment inventory = fragmentManager.findFragmentByTag(FragmentInventory.TAG);
            Fragment orders    = fragmentManager.findFragmentByTag(FragmentOrders.TAG);
            Fragment settings  = fragmentManager.findFragmentByTag(FragmentSettings.TAG);
            Fragment clients   = fragmentManager.findFragmentByTag(FragmentClientsAddEdit.TAG);
            Fragment workplan  = fragmentManager.findFragmentByTag(FragmentWorkPlan.TAG);
            Fragment cashing   = fragmentManager.findFragmentByTag(FragmentCashing.TAG);
            Fragment liquidation = fragmentManager.findFragmentByTag(FragmentPartialPay.TAG);
            Fragment fueling     = fragmentManager.findFragmentByTag(FragmentFueling.TAG);
            Fragment synchronizer     = fragmentManager.findFragmentByTag(FragmentSynchronizer.TAG);


            if(products != null && products.isAdded()) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                fragmentManager.beginTransaction().remove(products).commit();
                this.recreate();
            }
            if(workplan != null && workplan.isAdded()){
                fragmentManager.beginTransaction().remove(workplan).commit();
                this.recreate();
            }
            if(cashing != null && cashing.isAdded()){
                fragmentManager.beginTransaction().remove(cashing).commit();
                this.recreate();
            }
            if(liquidation != null && liquidation.isAdded()){
                fragmentManager.beginTransaction().remove(liquidation).commit();
                this.recreate();
            }
            if(inventory != null && inventory.isAdded()) {
                fragmentManager.beginTransaction().remove(inventory).commit();
                this.recreate();
            }
            if(orders != null && orders.isAdded()) {
                fragmentManager.beginTransaction().remove(orders).commit();
                this.recreate();
            }
            if(settings != null && settings.isAdded()) {
                fragmentManager.beginTransaction().remove(settings).commit();
                this.recreate();
            }
            if(fueling != null && fueling.isAdded()) {
                fragmentManager.beginTransaction().remove(fueling).commit();
                this.recreate();
            }
            if(clients != null && clients.isAdded()) {
                fragmentManager.beginTransaction().remove(clients).commit();
                this.recreate();
            }
            if(synchronizer != null && synchronizer.isAdded()) {
                fragmentManager.beginTransaction().remove(synchronizer).commit();
                this.recreate();
            }

        } else {

            Fragment home = getSupportFragmentManager().findFragmentByTag("home");
            if(home != null && home.isAdded()){
                //Session.closeSession(getApplicationContext());
                //moveTaskToBack(true);
                RequestManager.sharedInstance().showDecisionDialogWithListener(getString(R.string.req_man_confirm_exit),this, this);
            } else{
                super.onBackPressed();
            }
        }

        if (depthCounter > 0)
            depthCounter--;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode,resultCode,data);

        if (resultCode == RESULT_OK) {
            FragmentManager         manager         = getSupportFragmentManager();
            //FragmentWorkPlan        workplan        = (FragmentWorkPlan)        manager.findFragmentByTag(FragmentWorkPlan.TAG);
            FragmentProducts        products        = (FragmentProducts)        manager.findFragmentByTag(FragmentProducts.TAG);
            FragmentProductsAddEdit productsAddEdit = (FragmentProductsAddEdit) manager.findFragmentByTag(FragmentProductsAddEdit.TAG);
            FragmentCamera          camera          = (FragmentCamera)          manager.findFragmentByTag("camara");
            FragmentInventory       inventory       = (FragmentInventory)       manager.findFragmentByTag(FragmentInventory.TAG);
            FragmentOrders          orders          = (FragmentOrders)          manager.findFragmentByTag(FragmentOrders.TAG);

            if (products != null && products.isVisible()) {

                if (requestCode == IntentIntegrator.REQUEST_CODE) {

                    IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                    products.scanResult(scanningResult);
                }
                else if (camera != null && camera.isAdded()) {
                    Bitmap thumbnail = camera.onResult(data);
                    productsAddEdit.photoResult(thumbnail);
                }

            } else if (productsAddEdit != null && productsAddEdit.isVisible()) {

                if (requestCode == IntentIntegrator.REQUEST_CODE) {

                    IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                    productsAddEdit.scanResult(scanningResult);
                }
                else if (camera != null && camera.isAdded()) {
                    Bitmap thumbnail = camera.onResult(data);
                    productsAddEdit.photoResult(thumbnail);
                }

            } else if (orders != null && orders.isVisible()) {

                if (requestCode == IntentIntegrator.REQUEST_CODE) {

                }
                else if (requestCode == FragmentReceiptPreview.RECEIVER_SIGNATURE) {
                }
            }
        }
    }

    /*@Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        FragmentManager     fragmentManager     = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment            products            = fragmentManager.findFragmentByTag(FragmentProducts.TAG);
        Fragment            clients             = fragmentManager.findFragmentByTag(FragmentClients.TAG);
        Fragment            workplan            = fragmentManager.findFragmentByTag(FragmentWorkPlan.TAG);
        Fragment            cashing             = fragmentManager.findFragmentByTag(FragmentCashing.TAG);

        if (products != null && products.isAdded()) {

            fragmentTransaction.remove( products);
            fragmentTransaction.add(R.id.container,new FragmentProducts(), FragmentProducts.TAG);
            fragmentTransaction.commitAllowingStateLoss();

        } else if (clients != null && clients.isAdded()) {

            fragmentTransaction.remove(clients);
            fragmentTransaction.add(R.id.container,new FragmentClients(), FragmentClients.TAG);
            fragmentTransaction.commitAllowingStateLoss();

        } else if (workplan != null && workplan.isAdded()){

            fragmentTransaction.remove(workplan);
            fragmentTransaction.add(R.id.container, new FragmentWorkPlan(), FragmentWorkPlan.TAG);
            fragmentTransaction.commitAllowingStateLoss();

        } else if (cashing != null && cashing.isAdded()){

            fragmentTransaction.remove(cashing);
            fragmentTransaction.add(R.id.container, new FragmentCashing(), FragmentCashing.TAG);
            fragmentTransaction.commitAllowingStateLoss();
        }
    }
    */

    @Override
    public void onProductSelected(Product product) {

        FragmentProductsDetail productsDetail = (FragmentProductsDetail) getSupportFragmentManager().findFragmentByTag(FragmentProductsDetail.TAG);
        if (productsDetail != null && productsDetail.isAdded()) {
            productsDetail.updateDetail(product);
        }
    }

    @Override
    public void onClientSelected(Customer client) {

        FragmentClientsDetail clientsDetail = (FragmentClientsDetail) getSupportFragmentManager().findFragmentByTag(FragmentClientsDetail.TAG);
        if (clientsDetail != null && clientsDetail.isAdded())
            clientsDetail.updateDetail(client);
    }

    @Override
    public void prepareRequest(METHOD method, Map<String, String> params) {

        String token      = Session.getSessionActive(this).getToken();
        String username   = User.getUser(this, Session.getSessionActive(this).getUser_id()).getEmail();
        params.put("request", method.toString());
        params.put("user", username);
        params.put("token", token);

        RequestManager.sharedInstance().setListener(this);
        RequestManager.sharedInstance().makeRequestWithDataAndMethod(params, method);
    }

    @Override
    public void decodeResponse(String stringResponse) {
        JSONObject resp;

        try {
            resp = new JSONObject(stringResponse);

            if (resp.getString("method").equalsIgnoreCase(METHOD.LOGOUT.toString())){

                Session.closeSession(getApplicationContext());
                Intent i = new Intent(MainActivity.this, Login.class);

                Bundle bundle = new Bundle();
                bundle.putBoolean("isFromLogout",true);
                i.putExtras(bundle);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void responseFromDecisionDialog(String confirmMessage, String option) {
        if (option.equalsIgnoreCase("OK"))
            moveTaskToBack(true);
    }
}

