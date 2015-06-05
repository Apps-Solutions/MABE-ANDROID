package com.sellcom.tracker;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import android.support.v4.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import async_request.DecisionDialogWithListener;
import async_request.METHOD;
import async_request.RequestManager;
import async_request.UIResponseListenerInterface;
import database.DataBaseManager;
import database.models.CheckOut;
import database.models.CustomerWorkPlan;
import database.models.ExtraTasks;
import database.models.MarketingQuest;
import database.models.NoSaleReasonRecord;
import database.models.OrderSent;
import database.models.Product;
import database.models.Session;
import database.models.Signature;
import database.models.User;
import database.models.UserInfo;
import database.models.Warehouse;
import database.models.WholeSalesPrices;
import location.GPSTracker;
import util.Constants;
import util.DatesHelper;
import util.StepVisitAdapter;
import util.TicketGenerator;
import util.TrackerManager;
import util.Utilities;

public class FragmentStepVisit extends Fragment implements AdapterView.OnItemClickListener, UIResponseListenerInterface, DecisionDialogWithListener, TicketGenerator.TicketGeneratorListener {

    @Override
    public void responseFromGenerator(String response) {

    }

    public enum LockType{
        LOCKED_BY_ORDER_SENT,
        LOCKED_BY_PRE_ORDER,
        NO_LOCKED,
    }

    final static public String TAG = "step_visit";
    private             Context context;
                        Fragment fragment;
    FragmentManager     fragmentManager = getFragmentManager();

    LockType            locked_type;

    String              current_order_id    = "";

    String              visit_type_id;

    String              id_visit;

    public FragmentStepVisit() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_step_visit, container, false);

        visit_type_id = getArguments().getString("visit_type_id");

        id_visit = TrackerManager.sharedInstance().getCurrent_pdv().get("visit_id");

        Log.d(TAG,"Visit type id: "+visit_type_id);

        Log.d(TAG,"PDV: "+TrackerManager.sharedInstance().getCurrent_pdv());

        if (view != null) {
            context = getActivity();
            OnInit(view);
        }
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void OnInit(View view) {

        StepVisitAdapter    adapter = new StepVisitAdapter(getActivity(), visit_type_id);

        GridView gridView = (GridView) view.findViewById(R.id.gridViewElements);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.shrink_out, R.anim.slide_from_left, R.anim.shrink_out);

        // 1. Distribución Horizontal
        // 2. Mayoreo
        // 3. Autoservicio

        boolean finished    = true;
        List<Map<String,String>>    ava_prods;


        if(visit_type_id.equals("3")){
            switch(position){
                case 0:
                    ava_prods   = DataBaseManager.sharedInstance().getProductsWithNoShelfReviewInVisit(TrackerManager.sharedInstance().getCurrent_pdv().get("visit_id"));

                    if (ava_prods.size() == 0){
                        Toast.makeText(getActivity(),"Se han enviado cuestionarios para todos los productos",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    fragment = new FragmentMarketingQuestionnaireFlow();
                    ((FragmentMarketingQuestionnaireFlow)fragment).products = ava_prods;
                    fragmentTransaction.replace(R.id.container, fragment, FragmentMarketingQuestionnaireFlow.TAG);
                    ((MainActivity) getActivity()).depthCounter = 4;
                    break;
                case 1:
                    ava_prods   = DataBaseManager.sharedInstance().getProductsWithNoInventoryQuestionnaire(TrackerManager.sharedInstance().getCurrent_pdv().get("visit_id"));

                    if (ava_prods.size() == 0){
                        Toast.makeText(getActivity(),"Se han enviado cuestionarios para todos los productos",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    fragment = new FragmentInventoryQuestionnaire();
                    ((FragmentInventoryQuestionnaire)fragment).prod_array = ava_prods;
                    fragmentTransaction.replace(R.id.container, fragment, FragmentInventoryQuestionnaire.TAG);
                    ((MainActivity) getActivity()).depthCounter = 4;
                    break;
                case 2:
                    ava_prods   = DataBaseManager.sharedInstance().getProductsWithNoWarehouse(TrackerManager.sharedInstance().getCurrent_pdv().get("visit_id"));

                    if (ava_prods.size() == 0){
                        Toast.makeText(getActivity(),"Se han enviado registros de bodega para todos los productos",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    fragment = new FragmentWarehouse();
                    ((FragmentWarehouse)fragment).product_array = ava_prods;

                    fragmentTransaction.replace(R.id.container, fragment, FragmentWarehouse.TAG);
                    ((MainActivity) getActivity()).depthCounter = 4;
                    break;
                case 3:
                    Log.e(TAG, "Authorize Visit");
                    if(finished) {
                        if (Signature.getSignatureInVisit(getActivity(),TrackerManager.sharedInstance().getCurrent_pdv().get("visit_id")) != null){
                            Toast.makeText(getActivity(),"Ya se ha enviado un registro de autorización de salida",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        fragment = new FragmentAuthorizeVisit();
                        fragmentTransaction.replace(R.id.container, fragment, FragmentAuthorizeVisit.TAG);
                        ((MainActivity) getActivity()).depthCounter = 4;
                        break;
                    }
                    else{
                        // Authorize visit in development
                        Toast.makeText(getActivity(),"Módulo en desarrollo",Toast.LENGTH_SHORT).show();
                        return;
                    }
                case 4:
                    List<Map<String,String>> extraTasks = ExtraTasks.getExtraTasksByVisit(context,id_visit);

                    if(extraTasks == null){
                        Toast.makeText(context,getActivity().getString(R.string.form_no_task_generic),Toast.LENGTH_SHORT).show();
                        return;
                    }else{
                        fragment = new FragmentExtraTasks();
                        fragmentTransaction.replace(R.id.container, fragment, FragmentExtraTasks.TAG);
                        ((MainActivity) getActivity()).depthCounter = 4;
                    }
                case 5:
                    prepareEndVisit();
                    return;
                default:
                    break;
            }
        }
        else if(visit_type_id.equals("2")){
            switch(position){
                case 0:

                    ava_prods   = DataBaseManager.sharedInstance().getProductsWithNoShelfReviewInVisit(TrackerManager.sharedInstance().getCurrent_pdv().get("visit_id"));

                    if (ava_prods.size() == 0){
                        Toast.makeText(getActivity(),"Se han enviado cuestionarios para todos los productos",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    fragment = new FragmentMarketingQuestionnaireFlow();
                    ((FragmentMarketingQuestionnaireFlow)fragment).products = ava_prods;
                    fragmentTransaction.replace(R.id.container, fragment, FragmentMarketingQuestionnaireFlow.TAG);
                    ((MainActivity) getActivity()).depthCounter = 4;
                    break;

                case 1:

                    Log.e(TAG, "Wholesale Prices");

                    if (!WholeSalesPrices.getWholeSalesPricesIDInVisit(getActivity(),TrackerManager.sharedInstance().getCurrent_pdv().get("visit_id")).equalsIgnoreCase("X")){
                        Toast.makeText(getActivity(),"Ya se han enviado precios de mayoristas durante esta visita",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    fragment = new FragmentWholesalePricesWholesale();
                    fragmentTransaction.replace(R.id.container, fragment, FragmentOrders.TAG);
                    ((MainActivity) getActivity()).depthCounter = 4;

                    break;

                case 2:
                    Log.e(TAG, "Authorize Visit");
                    if(finished) {
                        if (Signature.getSignatureInVisit(getActivity(),TrackerManager.sharedInstance().getCurrent_pdv().get("visit_id")) != null){
                            Toast.makeText(getActivity(),"Ya se ha enviado un registro de autorización de salida",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        fragment = new FragmentAuthorizeVisit();
                        fragmentTransaction.replace(R.id.container, fragment, FragmentAuthorizeVisit.TAG);
                        ((MainActivity) getActivity()).depthCounter = 4;
                        break;
                    }
                    else{
                        // Authorize visit in development
                        Toast.makeText(getActivity(),"Módulo en desarrollo",Toast.LENGTH_SHORT).show();
                        return;
                    }

                case 3:
                    List<Map<String,String>> extraTasks = ExtraTasks.getExtraTasksByVisit(context,id_visit);

                    if(extraTasks == null){
                        Toast.makeText(context,getActivity().getString(R.string.form_no_task_generic),Toast.LENGTH_SHORT).show();
                        return;
                    }else{
                        fragment = new FragmentExtraTasks();
                        fragmentTransaction.replace(R.id.container, fragment, FragmentExtraTasks.TAG);
                        ((MainActivity) getActivity()).depthCounter = 4;
                    }
                    break;

                case 4:
                    prepareEndVisit();
                    return;
            }
        }
        else {
            String pdv_id;
            switch(position){
                case 0:
                    if (DataBaseManager.sharedInstance().getNotPaidInvoicesFromPDV(TrackerManager.sharedInstance().getCurrent_pdv().get("pdv_id")) == null){
                        Toast.makeText(getActivity(),getActivity().getString(R.string.req_man_no_invoices),Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Log.d(TAG,"Move to cashing state");
                    fragment = new FragmentCashing();
                    fragmentTransaction.replace(R.id.container, fragment, FragmentCashing.TAG);
                    ((MainActivity) getActivity()).depthCounter = 4;

                    break;

                case 1:
                    if (locked_type == LockType.LOCKED_BY_ORDER_SENT){
                        Toast.makeText(getActivity(),getActivity().getString(R.string.req_man_error_preorder_order_already_sent),Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else if (locked_type == LockType.LOCKED_BY_PRE_ORDER){
                        fragment    = new FragmentPreOrders();
                        fragmentTransaction.replace(R.id.container, fragment, FragmentPreOrders.TAG);
                        ((MainActivity) getActivity()).depthCounter = 4;
                        break;
                    }
                    else{
                        Toast.makeText(getActivity(),getActivity().getString(R.string.req_man_error_no_preorder),Toast.LENGTH_SHORT).show();
                        return;
                    }

                case 2:

                    if (DataBaseManager.sharedInstance().getNotPaidInvoicesFromPDV(TrackerManager.sharedInstance().getCurrent_pdv().get("pdv_id")) != null){
                        Toast.makeText(getActivity(),getActivity().getString(R.string.req_man_error_order_pending_invoices),Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (locked_type == LockType.LOCKED_BY_ORDER_SENT){
                        Toast.makeText(getActivity(),getActivity().getString(R.string.req_man_error_order_already_sent),Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else if (locked_type == LockType.LOCKED_BY_PRE_ORDER){
                        Toast.makeText(getActivity(),getActivity().getString(R.string.req_man_error_preorder_available),Toast.LENGTH_SHORT).show();
                        return;
                    }

                    fragment = new FragmentOrders();

                    Bundle bundle   = new Bundle();
                    bundle.putString("options", Constants.FRAGMENTS_OPTIONS.INSIDE_PDV.toString());
                    fragment.setArguments(bundle);
                    fragmentTransaction.replace(R.id.container, fragment, FragmentOrders.TAG);
                    ((MainActivity) getActivity()).depthCounter = 4;

                    break;
                case 3:
                    Log.e(TAG,"CANCEL ORDERS");
                    if (locked_type == LockType.NO_LOCKED) {
                        Toast.makeText(getActivity(), getActivity().getString(R.string.req_man_error_cancel_order), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    else{
                        Log.d(TAG,"MOVE TO CANCELLATION FRAGMENTS");
                        fragment                                        = new FragmentCancellation();
                        ((FragmentCancellation)fragment).locked_type    = locked_type;
                        fragmentTransaction.replace(R.id.container, fragment, FragmentCancellation.TAG);
                        ((MainActivity) getActivity()).depthCounter     = 4;
                    }
                    break;
                case 4:
                    if (DataBaseManager.sharedInstance().getActiveRefundInVisit(TrackerManager.sharedInstance().getCurrent_pdv().get("visit_id")) != null){
                        Toast.makeText(getActivity(),getActivity().getString(R.string.req_man_error_found_refund),Toast.LENGTH_SHORT).show();
                        return;
                    }
                    fragment    = new FragmentRefund();
                    fragmentTransaction.replace(R.id.container, fragment, FragmentRefund.TAG);
                    ((MainActivity) getActivity()).depthCounter = 4;
                    break;
                case 5:
                    Log.e(TAG,"CANCEL REFUND");

                    if (DataBaseManager.sharedInstance().getActiveRefundInVisit(TrackerManager.sharedInstance().getCurrent_pdv().get("visit_id")) == null){
                        Toast.makeText(getActivity(),getActivity().getString(R.string.req_man_error_not_found_refund),Toast.LENGTH_SHORT).show();
                        return;
                    }
                    RequestManager.sharedInstance().showDecisionDialogWithListener(getActivity().getString(R.string.req_man_confirm_cancel_refund),getActivity(), this);
                    return;

                case 6:
                    if (NoSaleReasonRecord.getAllNoSaleReasonRecordInVisit(getActivity(),TrackerManager.sharedInstance().getCurrent_pdv().get("visit_id")) != null){
                        Toast.makeText(getActivity(),getActivity().getString(R.string.req_man_error_found_no_sale_reason),Toast.LENGTH_SHORT).show();
                        return;
                    }
                    fragment    = new FragmentNoReasonForSale();
                    fragmentTransaction.replace(R.id.container, fragment, FragmentNoReasonForSale.TAG);
                    ((MainActivity) getActivity()).depthCounter = 4;
                    break;

                case 7:
                    pdv_id                              = TrackerManager.sharedInstance().getCurrent_pdv().get("pdv_id");
                    Map<String,String>  customer_info   = CustomerWorkPlan.getCustomerWorkPlanByPDV(getActivity(),pdv_id);
                    Map<String,String>  user_info       = UserInfo.getUserInfoFromEmailInMap(getActivity(), User.getUser(getActivity(), Session.getSessionActive(getActivity()).getUser_id()).getEmail());

                    TicketGenerator.generateTicket(getActivity(), this, null, customer_info, user_info, 0, (int) 0);
                    return;

                case 8:
                    Log.e(TAG, "Authorize Visit");
                    if(finished) {
                        if (Signature.getSignatureInVisit(getActivity(),TrackerManager.sharedInstance().getCurrent_pdv().get("visit_id")) != null){
                            Toast.makeText(getActivity(),"Ya se ha enviado un registro de autorización de salida",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        fragment = new FragmentAuthorizeVisit();
                        fragmentTransaction.replace(R.id.container, fragment, FragmentAuthorizeVisit.TAG);
                        ((MainActivity) getActivity()).depthCounter = 4;
                        break;
                    }
                    else{
                        // Authorize visit in development
                        Toast.makeText(getActivity(),"Módulo en desarrollo",Toast.LENGTH_SHORT).show();
                        return;
                    }

                case 9:

                    List<Map<String,String>> extraTasks = ExtraTasks.getExtraTasksByVisit(context,id_visit);

                    if(extraTasks == null){
                        Toast.makeText(context,getActivity().getString(R.string.form_no_task_generic),Toast.LENGTH_SHORT).show();
                        return;
                    }else{
                        fragment = new FragmentExtraTasks();
                        fragmentTransaction.replace(R.id.container, fragment, FragmentExtraTasks.TAG);
                        ((MainActivity) getActivity()).depthCounter = 4;
                    }
                    break;
                case 10:
                    prepareEndVisit();
                    return;
            }
        }
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void sendRefundRequest(List<Map<String, String>> refund_prods, String visit_id){
        JSONArray  products = new JSONArray();

        for(Map<String,String> itemProduct : refund_prods) {
            JSONObject  jsonObj = new JSONObject();
            try {
                jsonObj.put("id_product",itemProduct.get("product_id"));
                jsonObj.put("measure_unit","1");
                jsonObj.put("quantity",itemProduct.get("quantity"));
                jsonObj.put("quality_problem_id","1");
                products.put(jsonObj);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        sendJSONRequestFromProductsInMethod(products,METHOD.SEND_REFUND,visit_id);
    }

    public void sendWarehouseRequest(List<Map<String, String>> warehouse_prods, String visit_id){
        JSONArray  products = new JSONArray();

        for(Map<String,String> itemProduct : warehouse_prods) {
            JSONObject  jsonObj = new JSONObject();
            try {
                jsonObj.put("id_product",itemProduct.get("id_product"));
                jsonObj.put("boxes_out",itemProduct.get("boxes_out"));
                jsonObj.put("final_stock",itemProduct.get("final_stock"));

                products.put(jsonObj);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        sendJSONRequestFromProductsInMethod(products,METHOD.SET_WAREHOUSE_STOCK,visit_id);
    }

    public void sendJSONRequestFromProductsInMethod(JSONArray  products, METHOD method,String visit_id){

        JSONObject requestData = Utilities.getJSONWithCredentials(getActivity());
        try {
            requestData.put("id_visit",visit_id);
            requestData.put("products", products.toString());
            requestData.put("request",method);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestManager.sharedInstance().setListener(this);
        RequestManager.sharedInstance().makeRequestWithJSONDataAndMethod(requestData,method);
    }

    public void prepareEndVisit(){
        Map<String,String> params;
        params = new HashMap<String, String>();
        GPSTracker tracker          = new GPSTracker(getActivity());
        float latitude              = (float)tracker.getLatitude();
        float longitude             = (float)tracker.getLongitude();

        String dateTime             = DatesHelper.sharedInstance().getStringDate(new Date());
        String pdv_id               = TrackerManager.sharedInstance().getCurrent_pdv().get("pdv_id");
        String visit_id             = TrackerManager.sharedInstance().getCurrent_pdv().get("visit_id");

        params.put("latitude",String.valueOf(latitude));
        params.put("longitude",String.valueOf(longitude));
        params.put("date_time", dateTime);

        params.put("visit_id",visit_id);
        params.put("pdv_id", pdv_id);

        DataBaseManager.sharedInstance().registerCheckOut(params);

        if (visit_type_id.equals("1")) {
            final List<Map<String, String>> refund_prods = DataBaseManager.sharedInstance().getRefundProductsInVisit(visit_id);
            if (refund_prods == null)
                prepareRequest(METHOD.SEND_END_VISIT, params);
            else
                sendRefundRequest(refund_prods, visit_id);
        }
        else if (visit_type_id.equals("3")) {
            final List<Map<String, String>> warehouse_prods = Warehouse.getAllWarehouseNoSentInVisit(getActivity(), visit_id);
            if (warehouse_prods.size() == 0)
                prepareRequest(METHOD.SEND_END_VISIT, params);
            else
                sendWarehouseRequest(warehouse_prods, visit_id);
        }
        else{
            prepareRequest(METHOD.SEND_END_VISIT, params);
        }
    }

    public void prepareEndVisitInfo( String visit_id){
        String  pdv_id               = TrackerManager.sharedInstance().getCurrent_pdv().get("pdv_id");
        Map<String,String> checkOutInfo = DataBaseManager.sharedInstance().getCheckoutInformationForVisit(visit_id);
        Map<String,String> params = new HashMap<String, String>();
        params.put("latitude",checkOutInfo.get("latitude"));
        params.put("longitude",checkOutInfo.get("longitude"));
        params.put("date_time", checkOutInfo.get("date_time"));

        params.put("visit_id",visit_id);
        params.put("pdv_id", pdv_id);

        prepareRequest(METHOD.SEND_END_VISIT,params);
    }

    public void closeVisit(){

        Toast.makeText(context, getActivity().getString(R.string.req_man_visit_ended_successfully), Toast.LENGTH_SHORT).show();

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragment = new FragmentWorkPlan();
        fragmentTransaction.replace(R.id.container, fragment, FragmentWorkPlan.TAG);
        ((MainActivity) getActivity()).depthCounter = 2;

        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void prepareRequest(METHOD method, Map<String, String> params) {

        String token      = Session.getSessionActive(getActivity()).getToken();
        String username   = User.getUser(getActivity(), Session.getSessionActive(getActivity()).getUser_id()).getEmail();
        params.put("request", method.toString());
        params.put("user", username);
        params.put("token", token);

        RequestManager.sharedInstance().setListener(this);
        RequestManager.sharedInstance().makeRequestWithDataAndMethod(params, method);
    }

    @Override
    public void decodeResponse(String stringResponse) {
        String  visit_id             = TrackerManager.sharedInstance().getCurrent_pdv().get("visit_id");

        JSONObject resp;

        try {
            resp        = new JSONObject(stringResponse);

            if (resp.getString("method").equalsIgnoreCase(METHOD.SEND_REFUND.toString())) {
                if (resp.getString("error").isEmpty()) {
                    DataBaseManager.sharedInstance().updateRefundToSentInVisit(visit_id);
                    prepareEndVisitInfo(visit_id);
                }
                else{
                    closeVisit();
                }
            }
            if (resp.getString("method").equalsIgnoreCase(METHOD.SET_WAREHOUSE_STOCK.toString())) {
                if (resp.getString("error").isEmpty()) {
                    DataBaseManager.sharedInstance().updateWarehouseToSentInVisit(visit_id);
                    prepareEndVisitInfo(visit_id);
                }
                else{
                    closeVisit();
                }
            }
            else if (resp.getString("method").equalsIgnoreCase(METHOD.SEND_END_VISIT.toString())) {
                if (resp.getString("error").isEmpty()) {
                    CheckOut.updateCheckOutStatusToSent(getActivity(),visit_id);
                }
                else
                    Toast.makeText(getActivity(), "Sin acceso a red, módulo offline", Toast.LENGTH_SHORT).show();

                closeVisit();
            }

            else if (resp.getString("method").equalsIgnoreCase(METHOD.CANCEL_ORDER.toString())) {
                Log.d(TAG,"Order cancelled successfully");
                locked_type = LockType.NO_LOCKED;
                RequestManager.sharedInstance().showConfirmationDialog("El pedido "+current_order_id+" ha sido cancelado exitosamente",getActivity());
                OrderSent.updateOrderSentToInactiveAndSent(getActivity(),current_order_id);
                current_order_id    = "";
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).onSectionAttached(NavigationDrawerFragment.WORK_PLAN);
    }
    public void onStart(){
        super.onStart();
        Log.e("DEBUG", "onStart of Fragment");
        locked_type = LockType.NO_LOCKED;

        if (visit_type_id.equalsIgnoreCase("1")){
            if (DataBaseManager.sharedInstance().getActiveOrderInVisit(TrackerManager.sharedInstance().getCurrent_pdv().get("visit_id")) != null){
                Log.d(TAG,"Locked by order already sent");
                locked_type = LockType.LOCKED_BY_ORDER_SENT;
            }
            else if (DataBaseManager.sharedInstance().getPendingOrderFromPDV(TrackerManager.sharedInstance().getCurrent_pdv().get("pdv_id")) != null){
                Log.d(TAG,"Locked by pre-order");
                locked_type = LockType.LOCKED_BY_PRE_ORDER;
            }
        }
    }

    @Override
    public void responseFromDecisionDialog(String confirmMessage, String option) {
        if (option.equalsIgnoreCase("OK")){

            if (confirmMessage.equalsIgnoreCase(getActivity().getString(R.string.req_man_confirm_cancel_refund)))
                DataBaseManager.sharedInstance().cancelRefundInVisit(TrackerManager.sharedInstance().getCurrent_pdv().get("visit_id"));
        }
        else{

        }
    }
}