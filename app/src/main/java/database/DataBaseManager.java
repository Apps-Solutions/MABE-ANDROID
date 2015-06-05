package database;

import android.content.Context;
import android.util.Log;

import com.sellcom.tracker.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import OfflineMode.OfflineManager;
import async_request.METHOD;
import async_request.RequestManager;
import async_request.UIResponseListenerInterface;
import database.models.Brand;
import database.models.CheckIn;
import database.models.CheckOut;
import database.models.CustomerWorkPlan;
import database.models.Exhibition;
import database.models.ExtraTasks;
import database.models.Form;
import database.models.InventoryProduct;
import database.models.Invoice;
import database.models.NoSaleReason;
import database.models.Offers;
import database.models.Order;
import database.models.OrderSent;
import database.models.OrderSentProduct;
import database.models.PendingOrder;
import database.models.PendingOrderProduct;
import database.models.Permission;
import database.models.Product;
import database.models.ProductShelfReview;
import database.models.Profile;
import database.models.Promotion;
import database.models.QualityIncidence;
import database.models.QualityProblemType;
import database.models.Refund;
import database.models.RefundProduct;
import database.models.ResourceType;
import database.models.Session;
import database.models.Shelf_Review;
import database.models.Shelf_ReviewProduct;
import database.models.User;
import database.models.UserInfo;
import database.models.Warehouse;
import util.TrackerManager;
import util.Utilities;

public class DataBaseManager implements UIResponseListenerInterface {

    public interface InitialInformationDownloadInterface{
        public void initialInformationDownloadFinished();
    }

    final static public String TAG = "DATABASE_MANAGER";

    private static 	DataBaseManager                     manager;
    private         Context                             context;
    private         InitialInformationDownloadInterface listener;

    private         List<Map<String,String>>            pdv_list;
    private         List<Map<String,String>>            id_forms_extra_tasks;
    private         int                                 index   = 0;
    private         int                                 auxForms    = 0;

    public Context  getContext()            {return context;}
    public void     setContext(Context ctx) {this.context    = ctx;}


    private DataBaseManager(){
    }
    public static synchronized DataBaseManager sharedInstance(){
        if (manager == null)
            manager = new DataBaseManager();
        return manager;
    }

    public void clearAppInformation(boolean isInitial){
        OrderSent.clearOrderSent(context);
        OrderSentProduct.clearOrderSentProduct(context);

        PendingOrder.clearPendingOrder(context);
        PendingOrderProduct.clearPendingOrderProduct(context);

        if (isInitial)
            Invoice.clearInvoices(context);

        Refund.clearRefund(context);
        RefundProduct.clearRefundProduct(context);

        CheckIn.clearCheckIn(context);
    }


    public void startInitialInformationDownload(Context ctx, InitialInformationDownloadInterface listener, boolean isInitial){
        this.context    = ctx;
        this.listener   = listener;
        clearAppInformation(isInitial);
        RequestManager.sharedInstance().request_type    = RequestManager.RequestType.INITIAL_INFORMATION;
        prepareRequest(METHOD.GET_PRODUCTS, new HashMap<String, String>());
    }

    public void requestPendingOrders(){
        if (index == 0){
            PendingOrder.removeAll(context);
            PendingOrderProduct.removeAll(context);
        }
        if (index<pdv_list.size()){
            Map<String,String> pdv  = pdv_list.get(index);
            Map<String,String> params = new HashMap<String, String>();
            params.put("pdv_id",pdv.get("pdv_id"));
            prepareRequest(METHOD.GET_PENDING_ORDER, params);
            index ++;
        }
        else{
            index   = 0;
            requestInvoices();
        }
    }

    public void requestInvoices(){
        if (index == 0){
            Invoice.removeAll(context);
        }
        if (index<pdv_list.size()){
            Map<String,String> pdv  = pdv_list.get(index);
            Map<String,String> params = new HashMap<String, String>();
            params.put("pdv_id",pdv.get("pdv_id"));
            prepareRequest(METHOD.GET_INVOICES, params);
            index ++;
        }
        else{
            index   = 0;
            endInitialConfigurationDownloads();
        }
    }

    public void endInitialConfigurationDownloads(){
        SimpleDateFormat sdf    = new SimpleDateFormat("dd/M/yyyy");
        String strDate          = sdf.format(new Date());
        RequestManager.sharedInstance().saveInPreferencesKeyAndValue("initial_info_timestamp", strDate);
        RequestManager.sharedInstance().request_type    = RequestManager.RequestType.NORMAL;
        listener.initialInformationDownloadFinished();
    }

    @Override
    public void prepareRequest(METHOD method, Map<String, String> params) {
        String token      = Session.getSessionActive(this.context).getToken();
        String username   = User.getUser(this.context, Session.getSessionActive(this.context).getUser_id()).getEmail();
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
            resp        = new JSONObject(stringResponse);

            if (resp.getString("error").isEmpty()){
                if (resp.getString("method").equalsIgnoreCase(METHOD.GET_PRODUCTS.toString())){
                    Product.insertProductsFromJSONArray(context, resp.getJSONArray("products"));
                    prepareRequest(METHOD.GET_RESOURCE_TYPE,new HashMap<String, String>());
                }
                else if (resp.getString("method").equalsIgnoreCase(METHOD.GET_RESOURCE_TYPE.toString())){
                    ResourceType.insertResourcesFromJSONArray(context, resp.getJSONArray("resource"));
                    prepareRequest(METHOD.GET_PROMOTION_TYPE,new HashMap<String, String>());
                }
                else if (resp.getString("method").equalsIgnoreCase(METHOD.GET_PROMOTION_TYPE.toString())){
                    Promotion.insertPromotionsFromJSONArray(context, resp.getJSONArray("exhibition"));
                    prepareRequest(METHOD.GET_EXHIBITION_TYPE,new HashMap<String, String>());
                }
                else if (resp.getString("method").equalsIgnoreCase(METHOD.GET_EXHIBITION_TYPE.toString())){
                    Exhibition.insertExhibitionsFromJSONArray(context, resp.getJSONArray("exhibition"));
                    prepareRequest(METHOD.GET_QUALITY_PROBLEM_TYPE,new HashMap<String, String>());
                }
                else if (resp.getString("method").equalsIgnoreCase(METHOD.GET_QUALITY_PROBLEM_TYPE.toString())){
                    QualityProblemType.insertQualityProblemsFromJSONArray(context, resp.getJSONArray("problems"));
                    prepareRequest(METHOD.GET_BRAND,new HashMap<String, String>());
                }
                else if (resp.getString("method").equalsIgnoreCase(METHOD.GET_BRAND.toString())){
                    Brand.insertBrandsFromJSONArray(context, resp.getJSONArray("brand"));
                    prepareRequest(METHOD.GET_NO_SALE_REASONS, new HashMap<String, String>());
                }
                else if (resp.getString("method").equalsIgnoreCase(METHOD.GET_NO_SALE_REASONS.toString())){
                    NoSaleReason.insertNoSaleReasonFromJSONArray(context, resp.getJSONArray("resons"));
                    prepareRequest(METHOD.GET_USER_INFO, new HashMap<String, String>());
                }
                else if (resp.getString("method").equalsIgnoreCase(METHOD.GET_USER_INFO.toString())){
                    JSONObject  userInfo     = resp.getJSONObject("user");
                    JSONObject  personInfo   = userInfo.getJSONObject("contact");

                    try {

                        UserInfo.insert(context, userInfo.getString("id_user"),
                                userInfo.getString("user"),
                                userInfo.getString("route"),
                                userInfo.getString("id_viamente"),
                                userInfo.getString("id_profile"),
                                userInfo.getString("jde"),
                                personInfo.getString("name"),
                                personInfo.getString("lastname"),
                                userInfo.getString("branch_name"),
                                userInfo.getString("branch_address"),
                                userInfo.getString("branch_code"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                     }
/*

                    UserInfo.insert(context, userInfo.getString("id_user"),
                            userInfo.getString("user"),
                            userInfo.getString("route"),
                            userInfo.getString("id_viamente"),
                            userInfo.getString("id_profile"),
                            userInfo.getString("jde"),
                            personInfo.getString("name"),
                            personInfo.getString("lastname"));
*/
                    prepareRequest(METHOD.GET_WORKPLAN, new HashMap<String, String>());
                }
                else if (resp.getString("method").equalsIgnoreCase(METHOD.GET_WORKPLAN.toString())){
                    CustomerWorkPlan.insertCustomerWorkPlanFromJSONArray(context, resp.getJSONArray("workplan"));
                    /*
                    pdv_list    = CustomerWorkPlan.getAllInMaps(context);
                    if (pdv_list == null)
                        RequestManager.sharedInstance().showErrorDialog(context.getString(R.string.req_man_visit_not_valid_workplan),context);
                    else
                        requestPendingOrders();
                    */

                    auxForms    = 0;

                    id_forms_extra_tasks = new ArrayList<Map<String, String>>();
                    Map<String,String> id_form;

                    id_forms_extra_tasks = ExtraTasks.getAllIDFormExtraTasks(context);



                    if(id_forms_extra_tasks == null){

                        pdv_list    = CustomerWorkPlan.getAllInMaps(context);
                        if (pdv_list == null)
                            RequestManager.sharedInstance().showErrorDialog(context.getString(R.string.req_man_visit_not_valid_workplan),context);
                        else
                            requestPendingOrders();
                        //RequestManager.sharedInstance().showErrorDialog(context.getString(R.string.req_man_visit_not_valid_workplan),context);
                    }else{
                        id_form = new HashMap<String, String>();
                        id_form.put("form_id",id_forms_extra_tasks.get(auxForms).get("frm_id_form"));
                        auxForms +=1;
                        prepareRequest(METHOD.GET_FORM,id_form);
                    }

                }
                else if(resp.getString("method").equalsIgnoreCase(METHOD.GET_FORM.toString())){

                    Form.insertFormFromJSONObject(context,resp.getString("form"),id_forms_extra_tasks.get((auxForms-1)).get("id_generic_task"));


                    if(auxForms < id_forms_extra_tasks.size()){
                        Map<String,String> id_form = new HashMap<String, String>();
                        id_form.put("form_id",id_forms_extra_tasks.get(auxForms).get("frm_id_form"));
                        auxForms +=1;
                        prepareRequest(METHOD.GET_FORM,id_form);
                    }else{

                        pdv_list    = CustomerWorkPlan.getAllInMaps(context);
                        if (pdv_list == null)
                            RequestManager.sharedInstance().showErrorDialog(context.getString(R.string.req_man_visit_not_valid_workplan),context);
                        else
                            requestPendingOrders();

                    }


                }
                else if (resp.getString("method").equalsIgnoreCase(METHOD.GET_PENDING_ORDER.toString())){
                    if (!resp.getString("order").trim().equalsIgnoreCase("{}")) {
                        JSONObject pending_order = resp.getJSONObject("order");
                        PendingOrder.savePendingOrder(context, pending_order);
                        Log.d(TAG, "" + PendingOrder.getAllInMaps(context));
                    }
                    requestPendingOrders();
                }
                else if (resp.getString("method").equalsIgnoreCase(METHOD.GET_INVOICES.toString())){
                    if (!resp.getString("invoices").trim().equalsIgnoreCase("{}")) {
                        JSONArray invoices = resp.getJSONArray("invoices");
                        Invoice.insertInvoicesOrdersFromJSONArray(context, invoices);
                    }
                    requestInvoices();
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void registerCheckIn(Map<String,String> data){
        CustomerWorkPlan.setVisitAsStarted(context,data.get("visit_id"));
        CheckIn.insert(context, data.get("latitude"), data.get("longitude"), data.get("date_time"), data.get("visit_id"), data.get("pdv_id"));
    }

    public void updateCheckInStateToSentInVisit(String visit_id){
        CheckIn.updateCheckInStatusToSent(context,visit_id);
    }

    public Map<String,String> getActiveOrderInVisit(String visit_id){
        return OrderSent.getActiveOrderSentInVisit(context,visit_id);
    }

    public List<Map<String,String>> getPendingOrderFromPDV(String pdv_id){
        return PendingOrder.getPendingOrderFromPDV(context,pdv_id);
    }

    public List<Map<String,String>> getNotPaidInvoicesFromPDV(String pdv_id){
        return Invoice.getAllInvoicesNotPaidInMapsForPDV(context,pdv_id);
    }

    public Map<String,String> getActiveRefundInVisit(String visit_id){
        return Refund.getActiveRefundByVisit(context, visit_id);
    }

    public void registerCheckOut(Map<String,String> data){
        CustomerWorkPlan.setVisitAsEnded(context,data.get("visit_id"));
        CheckOut.insert(context, data.get("latitude"), data.get("longitude"), data.get("date_time"), data.get("visit_id"), data.get("pdv_id"), "0");
    }

    public void updateRefundToSentInVisit(String visit_id){
        Refund.updateRefundToSent(context,visit_id);
    }

    public Map<String,String> getCheckoutInformationForVisit(String visit_id){
        return CheckOut.getCheckOutByVisit(context,visit_id);
    }

    public void cancelRefundInVisit(String visit_id){
        String refund_id = Refund.getActiveRefundIdByVisit(context,visit_id);
        RefundProduct.removeAllProductsInRefund(context, refund_id);
        Refund.deleteRefundInVisit(context,visit_id);
    }

    public void passPendingOrderToInactiveOrderSent(Map<String,String> pending_order){
        OrderSent.insertInactive(context,pending_order.get("date"),pending_order.get("id"),pending_order.get("id_pdv"),TrackerManager.sharedInstance().getCurrent_pdv().get("visit_id"),"",pending_order.get("total"),pending_order.get("subtotal"),pending_order.get("tax"));
        PendingOrder.deletePendingOrderFromPDV(context,pending_order.get("id_pdv"));
    }

    public void passPendingOrderToActiveOrderSent(Map<String,String> pending_order){
        OrderSent.insert(context,pending_order.get("date"),pending_order.get("id"),pending_order.get("id_pdv"),TrackerManager.sharedInstance().getCurrent_pdv().get("visit_id"),"",pending_order.get("total"),pending_order.get("subtotal"),pending_order.get("tax"));
        PendingOrder.deletePendingOrderFromPDV(context,pending_order.get("id_pdv"));

        List<Map<String,String>> products = PendingOrderProduct.getAllFromOrderInMaps(context,pending_order.get("id"));

        for(Map<String,String> itemProduct : products) {
            OrderSentProduct.insert(context, pending_order.get("id"), itemProduct.get("name"), "",
                    itemProduct.get("id"), itemProduct.get("price"),itemProduct.get("quantity"));
        }

        PendingOrderProduct.removeAllInOrder(context,pending_order.get("id"));

    }

    public void updateOrderSentToInactive(String order_id){
        OrderSent.updateOrderSentToInactive(context,order_id);
    }

    public void saveOrder(String order_date, String order_id, String pdv_id, String visit_id, String username,String total, String subtotal, String tax, List<Map<String,String>> products) {
        OrderSent.insert(context,order_date,order_id,pdv_id,visit_id,username,total,subtotal,tax);

        for(Map<String,String> itemProduct : products) {
            OrderSentProduct.insert(context, order_id, itemProduct.get("name"), itemProduct.get("jde"),
                itemProduct.get("id"), itemProduct.get("price"),itemProduct.get("total_pieces"));

        }
    }

    public List<Map<String,String>> getRefundProductsInVisit(String visit_id){
        String refund_order_id  = Refund.getActiveRefundIdByVisit(context, visit_id);
        if (refund_order_id != null) {
            return RefundProduct.getAllProductsInRefundInOrder(context, refund_order_id);
        }
    return null;
    }

    public List<Map<String,String>> getAllNoSentOrdersInVisit(String visit_id){
        return OrderSent.getAllNoSentOrderSentInVisit(context, visit_id);
    }

    public List<Map<String,String>> getProductsInOrderReadyForSend(String order_id){
        return OrderSentProduct.getAllProductsInOrderSentToSynchronizer(context, order_id);
    }

    public void updateOrderToSent(String offline_order_id,String order_id){
        OrderSent.updateIdInOrderSent(context,offline_order_id,order_id);
        OrderSentProduct.update(context,offline_order_id,order_id);
    }

    public List<Map<String,String>> getAllInvoicesPaidAndNoSentInPDV(String pdv_id){
        return Invoice.getAllInvoicesPaidAndNoSentInMapsForPDV(context,pdv_id);
    }

    /*------------------------------------- AutoSales *---------------------------------------*/
    public void saveShelfReview(Map<String,String> data){
        ProductShelfReview.insert(context,data.get("id_product"),data.get("visit_id"),
                                    data.get("current_shelf"),data.get("current_exhibition"),data.get("shelf_boxes"),
                                    data.get("exhibition_boxes"),data.get("expiration"),data.get("price"),
                data.get("front"),data.get("rival1"),data.get("rival2"),data.get("total"));
    }

    public List<Map<String,String>> getProductsWithNoShelfReviewInVisit(String visit_id){
        List<Map<String,String>> available_products = new ArrayList<Map<String, String>>();
        List<Map<String,String>> products   = Product.getAllOwnInMaps(context);
        for (Map<String,String> map : products){
            if(ProductShelfReview.getProductShelfReviewIDWithProductInVisit(context,map.get("id"),visit_id) == null)
            available_products.add(map);
        }
        return available_products;
    }

    public List<Map<String,String>> getRivalProductsWithNoOfferInVisit(String visit_id){
        List<Map<String,String>> available_products = new ArrayList<Map<String, String>>();
        List<Map<String,String>> products   = Product.getAllRivalInMaps(context);
        for (Map<String,String> map : products){
            if(Offers.getNoSentOfferIDInVisit(context,visit_id, map.get("id")) == null)
                available_products.add(map);
        }
        return available_products;
    }

    public List<Map<String,String>> getQualityIncidencesInProductAvailableInVisit(String id_product,String visit_id){
/*        List<Map<String,String>> available_incidences   = new ArrayList<Map<String, String>>();
        List<Map<String,String>> quality_incidences     = QualityProblemType.getAllInMaps(context);
        for (Map<String,String> map : quality_incidences){
            if(QualityIncidence.getQualityProblemTypeIDInProductAndVisit(context, id_product, visit_id, map.get("id")) == null)
                available_incidences.add(map);
        }
*/
        return QualityProblemType.getAllInMaps(context);
    }

    public void updateWarehouseToSentInVisit(String visit_id){
        Warehouse.updateAllWarehouseProductsInVisitToSent(context,visit_id);
    }

    public List<Map<String,String>> getProductsWithNoInventoryQuestionnaire(String visit_id){
        List<Map<String,String>> available_products = new ArrayList<Map<String, String>>();
        List<Map<String,String>> products   = Product.getAllOwnInMaps(context);
        for (Map<String,String> map : products){
            if(InventoryProduct.getInventoryProductInVisit(context,visit_id,map.get("id")) == null)
                available_products.add(map);
        }
        return available_products;
    }

    public List<Map<String,String>> getProductsWithNoWarehouse(String visit_id){
        List<Map<String,String>> available_products = new ArrayList<Map<String, String>>();
        List<Map<String,String>> products   = Product.getAllOwnInMaps(context);
        for (Map<String,String> map : products){
            if(Warehouse.getWarehouseProductInVisit(context,visit_id,map.get("id")) == null)
                available_products.add(map);
        }
        return available_products;
    }

}
