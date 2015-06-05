package OfflineMode;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import async_request.METHOD;
import async_request.RequestManager;
import async_request.UIResponseListenerInterface;
import database.DataBaseManager;
import database.models.CheckIn;
import database.models.CheckOut;
import database.models.CustomerWorkPlan;
import database.models.ExtraTasks;
import database.models.Form;
import database.models.Fueling;
import database.models.InventoryProduct;
import database.models.Invoice;
import database.models.NoSaleReasonRecord;
import database.models.Offers;
import database.models.OrderSent;
import database.models.PartialPay;
import database.models.ProductShelfReview;
import database.models.Promotion;
import database.models.QualityIncidence;
import database.models.QuestionsSectionForm;
import database.models.SectionsForm;
import database.models.Session;
import database.models.Shelf_ReviewProduct;
import database.models.Signature;
import database.models.User;
import database.models.Warehouse;
import database.models.WholeSalesPrices;
import database.models.WholeSalesPricesProduct;
import util.Utilities;

public class Synchronizer implements UIResponseListenerInterface, Runnable {

    public interface UploadInformationInterface{
        public void finishedUploadInformation();
    }

    final static public String TAG = "SYNCHRONIZER";
    private         Context                     context;
    private         UploadInformationInterface  listener;

    private         int                         pdv_index;
    private         List<Map<String,String>>    pdv_list;
    private         Map<String,String>          current_pdv;

    private         List<Map<String,String>>    aux_list;
    private         int                         aux_index;
    private         Map<String,String>          aux_holder,
                                                aux_form;

    private         String                      str_id;

    private         boolean                     is_invoices_payment;

    public Context  getContext()            {return context;}
    public void     setContext(Context ctx) {this.context    = ctx;}

    public UploadInformationInterface  getListener()            {return listener;}
    public void     setListener(UploadInformationInterface lst) {this.listener    = lst;}

    public JSONObject   getJSONWithCredentials(){
        String token                = Session.getSessionActive(context).getToken();
        String username             = User.getUser(context, Session.getSessionActive(context).getUser_id()).getEmail();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token",token);
            jsonObject.put("user",username);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public Synchronizer(Context ctx){
        this.context    = ctx;
    }

    public void startSynchronizeProcess(){
        RequestManager.sharedInstance().request_type    = RequestManager.RequestType.UPLOADING_INFORMATION;
        pdv_index   = 0;
        pdv_list    = CustomerWorkPlan.getAllInMaps(context);

        if (pdv_list != null){
            current_pdv = pdv_list.get(pdv_index);
            Log.d(TAG,"PDV: "+current_pdv.toString());
            startVisitSynchronizationProcess();
        }
        else{

        }
    }

    public void startVisitSynchronizationProcess(){
        Map<String,String> checkIn_info = CheckIn.getCheckInNoSentByVisit(context,current_pdv.get("visit_id"));
        if (checkIn_info != null)
            prepareRequest(METHOD.SEND_START_VISIT,checkIn_info);
        else{
            if (current_pdv.get("visit_type_id").equalsIgnoreCase("1"))   // Distribucion horizontal
                startOrderSynchronizationProcess();
            else
                startShelfReviewSynchronizationProcess();
        }
    }

    public void visitSynchronizationProcess(){
        if (pdv_index < pdv_list.size()){
            current_pdv = pdv_list.get(pdv_index);
            startVisitSynchronizationProcess();
        }
        else{   // All visits sent, start with partial settlements
            startPartialPaymentsSynchronizationProcess();
        }
    }

    public void startOrderSynchronizationProcess(){
        Log.d(TAG,"Start order syncronization process");
        is_invoices_payment = false;
        aux_index           = 0;
        aux_list            = DataBaseManager.sharedInstance().getAllNoSentOrdersInVisit(current_pdv.get("visit_id"));
        if (aux_list != null) {
            Log.d(TAG + " StartOrd", aux_list.toString());
            ordersSynchronizationProcess();
        }
        else{
            Log.d(TAG,"Not orders found, go refund");
            startRefundSynchronizationProcess();
        }
    }



    public void startShelfReviewSynchronizationProcess(){
        Log.d(TAG,"Start shelf review syncronization process");
        aux_index           = 0;
        aux_list            = ProductShelfReview.getAllProductShelfReviewsNoSentInVisit(context,current_pdv.get("visit_id"));
        if (aux_list.size() != 0) {

            Log.d(TAG,"List: "+aux_list.toString());

            JSONArray   products = new JSONArray();

            for (Map<String,String> itemProduct : aux_list){
                products.put(Utilities.mapToJson(itemProduct));
            }
            JSONObject requestData = getJSONWithCredentials();
            try {
                requestData.put("id_visit",current_pdv.get("visit_id"));
                requestData.put("products", products.toString());
                requestData.put("request",METHOD.SET_SHELF_REVISION);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestManager.sharedInstance().setListener(this);
            RequestManager.sharedInstance().makeRequestWithJSONDataAndMethod(requestData,METHOD.SET_SHELF_REVISION);

        }
        else{
            startOffersSynchronizationProcess();
        }
    }

    public void ordersSynchronizationProcess(){

        if (aux_index < aux_list.size()){
            aux_holder    = aux_list.get(aux_index);

            Log.d(TAG,"Order info: "+aux_holder.toString());

            if (aux_holder.get("order_id").startsWith("N")){ // SEND ORDER FROM SCRATCH

                Log.d(TAG,"Order form scratch");

                String pdv_id       = aux_holder.get("pdv_id");
                String visit_id     = aux_holder.get("visit_id");
                String date_time    = aux_holder.get("date_time");

                JSONArray   products = new JSONArray();

                for (Map<String,String> itemProduct : DataBaseManager.sharedInstance().getProductsInOrderReadyForSend(aux_holder.get("order_id"))){
                    products.put(Utilities.mapToJson(itemProduct));
                }
                JSONObject requestData = getJSONWithCredentials();
                try {
                    requestData.put("pdv_id",pdv_id);
                    requestData.put("visit_id",visit_id);
                    requestData.put("date_time",date_time);
                    requestData.put("products", products.toString());
                    requestData.put("request",METHOD.SEND_ORDER);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                RequestManager.sharedInstance().setListener(this);
                RequestManager.sharedInstance().makeRequestWithJSONDataAndMethod(requestData,METHOD.SEND_ORDER);
            }
            else{
                Log.d(TAG,"Order valid, check payment status");
                checkPaymentStatus();
            }
        }   // Start refund process
        else{
            startRefundSynchronizationProcess();
        }
    }

    public void checkPaymentStatus(){
        // PAY the order, if necessary
        if (aux_holder.get("paid").equalsIgnoreCase("PAID") && aux_holder.get("sent").equalsIgnoreCase("NO_SENT")){
            Log.d(TAG + " CPStat","Check payment status");
            prepareRequest(METHOD.SEND_PAYMENT,aux_holder);
            return;
        }

        // IF INACTIVE, CANCEL THE ORDER!!
        if (aux_holder.get("status").equalsIgnoreCase("INACTIVE")){
            Map params              = new HashMap<String, String>();
            params.put("order_id",aux_holder.get("order_id"));
            prepareRequest(METHOD.CANCEL_ORDER,params);
        }
        else{
            // Nothing to do, start another entry in order sent (CURRENT VISIT)
            aux_index ++;
            ordersSynchronizationProcess();
        }
    }

    public void startRefundSynchronizationProcess(){
        aux_index     = 0;
        List<Map<String, String>> refund_prods = DataBaseManager.sharedInstance().getRefundProductsInVisit(current_pdv.get("visit_id"));
        if (refund_prods != null) {
            Log.d(TAG,"Start refund syncronization process");
            refundSynchronizationProcess(refund_prods);
        }
        else {
            Log.d(TAG,"No refund, go invoices");
            startInvoicesSynchronizationProcess();
        }
    }

    public void startOffersSynchronizationProcess(){
        aux_list        = Offers.getAllNoSentOffersInVisit(context, current_pdv.get("visit_id"));
        if (aux_list.size() != 0){
            Log.d(TAG,"Start offers syncronization process");
            JSONArray   products = new JSONArray();

            for (Map<String,String> itemProduct : aux_list){
                products.put(Utilities.mapToJson(itemProduct));
            }
            JSONObject requestData = getJSONWithCredentials();
            try {
                requestData.put("id_visit",current_pdv.get("visit_id"));
                requestData.put("promotions", products.toString());
                requestData.put("request",METHOD.SET_PROMOTION);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestManager.sharedInstance().setListener(this);
            RequestManager.sharedInstance().makeRequestWithJSONDataAndMethod(requestData,METHOD.SET_PROMOTION);

        }
        else{
            startQualityIncidencesSynchronizationProcess();
        }
    }


    public void refundSynchronizationProcess(List<Map<String, String>> refund_prods){
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
        String visit_id             = current_pdv.get("visit_id");
        JSONObject requestData      = getJSONWithCredentials();
        try {
            requestData.put("id_visit",visit_id);
            requestData.put("products", products.toString());
            requestData.put("request",METHOD.SEND_REFUND);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestManager.sharedInstance().setListener(this);
        RequestManager.sharedInstance().makeRequestWithJSONDataAndMethod(requestData,METHOD.SEND_REFUND);
    }

    public void startInvoicesSynchronizationProcess(){
        is_invoices_payment = true;
        aux_index           = 0;
        aux_list            = DataBaseManager.sharedInstance().getAllInvoicesPaidAndNoSentInPDV(current_pdv.get("pdv_id"));
        if (aux_list != null) {
            Log.d(TAG,"Start order syncronization process");
            invoicesSynchronizationProcess();
        }
        else {
            Log.d(TAG,"No invoices, start no sale reason");
            startNoSaleReasonSynchronizationProcess();
        }
    }

    public void startNoSaleReasonSynchronizationProcess(){
        aux_holder  = NoSaleReasonRecord.getAllNoSaleReasonRecordInVisit(context,current_pdv.get("visit_id"));
        if (aux_holder != null){
            prepareRequest(METHOD.SET_NO_SALE_REASON,aux_holder);
        }else{
            sendEndVisitAuthorization();
        }
    }

    public void invoicesSynchronizationProcess(){

        if (aux_index < aux_list.size()){
            aux_holder    = aux_list.get(aux_index);

            aux_holder.put("invoice_id", aux_holder.get("id"));

            prepareRequest(METHOD.SEND_PAYMENT,aux_holder);

        }   // End visit
        else{
            sendEndVisitAuthorization();
        }
    }

    public void startQualityIncidencesSynchronizationProcess(){
        aux_list        = QualityIncidence.getAllNoSentQualityIncidencesInVisit(context, current_pdv.get("visit_id"));
        if (aux_list.size() != 0){
            Log.d(TAG,"Start quality incidences syncronization process");
            JSONArray   products = new JSONArray();

            for (Map<String,String> itemProduct : aux_list){
                products.put(Utilities.mapToJson(itemProduct));
            }
            JSONObject requestData = getJSONWithCredentials();
            try {
                requestData.put("id_visit",current_pdv.get("visit_id"));
                requestData.put("products", products.toString());
                requestData.put("request",METHOD.SET_QUALITY_INCIDENCE);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestManager.sharedInstance().setListener(this);
            RequestManager.sharedInstance().makeRequestWithJSONDataAndMethod(requestData,METHOD.SET_QUALITY_INCIDENCE);

        }
        else{
            if (current_pdv.get("visit_type_id").equalsIgnoreCase("2")) {   // Mayoreo
                startSupplierPricesSynchronizationProcess();
            }

            else if (current_pdv.get("visit_type_id").equalsIgnoreCase("3"))   // Autoservicio
                startInventoryQuestionnaireSynchronizationProcess();
        }
    }

    public void startInventoryQuestionnaireSynchronizationProcess(){
        aux_list        = InventoryProduct.getInventoryNoSentInVisit(context,current_pdv.get("visit_id"));
        if (aux_list != null){
            Log.d(TAG,"Start inventory questionnaire syncronization process");
            JSONArray   products = new JSONArray();

            for (Map<String,String> itemProduct : aux_list){
                products.put(Utilities.mapToJson(itemProduct));
            }
            JSONObject requestData = getJSONWithCredentials();
            try {
                requestData.put("id_visit",current_pdv.get("visit_id"));
                requestData.put("products", products.toString());
                requestData.put("request",METHOD.SET_AUTOSALE_STOCK);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestManager.sharedInstance().setListener(this);
            RequestManager.sharedInstance().makeRequestWithJSONDataAndMethod(requestData,METHOD.SET_AUTOSALE_STOCK);

        }
        else{
            startWarehouseSynchronizationProcess();
        }
    }

    public void startSupplierPricesSynchronizationProcess(){
        aux_holder = WholeSalesPrices.getWholeSalesPricesNoSentInVisit(context,current_pdv.get("visit_id"));
        if (aux_holder != null){
            str_id      = aux_holder.get("id");
            aux_list    = WholeSalesPricesProduct.getProductsInVisit(context, current_pdv.get("visit_id"));
            if (aux_list != null){
                JSONArray   products = new JSONArray();

                for (Map<String,String> itemProduct : aux_list){
                    products.put(Utilities.mapToJson(itemProduct));
                }
                JSONObject requestData = getJSONWithCredentials();
                try {
                    requestData.put("id_visit",current_pdv.get("visit_id"));
                    requestData.put("evidence",aux_holder.get("evidence"));
                    requestData.put("comments",aux_holder.get("comments"));

                    requestData.put("products", products.toString());
                    requestData.put("request",METHOD.SET_SUPPLIER_PRICES);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                RequestManager.sharedInstance().setListener(this);
                RequestManager.sharedInstance().makeRequestWithJSONDataAndMethod(requestData,METHOD.SET_SUPPLIER_PRICES);

            }
            else{
                sendEndVisitAuthorization();
            }
        }
        else{
            sendEndVisitAuthorization();
        }
    }

    public void startWarehouseSynchronizationProcess(){
        aux_list    = Warehouse.getAllWarehouseNoSentInVisit(context, current_pdv.get("visit_id"));

        if (aux_list.size() != 0){
            Log.d(TAG,"Start warehouse syncronization process");
            JSONArray products = new JSONArray();
            for (Map<String,String> itemProduct : aux_list){
                products.put(Utilities.mapToJson(itemProduct));
            }
            JSONObject requestData = Utilities.getJSONWithCredentials(context);
            try {
                requestData.put("id_visit",current_pdv.get("visit_id"));
                requestData.put("products", products.toString());
                requestData.put("request", METHOD.SET_WAREHOUSE_STOCK);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestManager.sharedInstance().setListener(this);
            RequestManager.sharedInstance().makeRequestWithJSONDataAndMethod(requestData,METHOD.SET_WAREHOUSE_STOCK);
        }
        else{
            sendEndVisitAuthorization();
        }
    }

    public void sendEndVisitAuthorization(){
        aux_holder = Signature.getSignatureNoSentInVisit(context,current_pdv.get("visit_id"));
        if (aux_holder != null) {
            Log.d(TAG,"Start end authorization process");
            prepareRequest(METHOD.SET_SIGNATURE, aux_holder);
        }
        else{
            Log.d(TAG,"No authorization, send end visit");
            endVisitSynchronizationProcess();
        }
    }

    public void endVisitSynchronizationProcess(){
        Map<String,String> checkOut_info = CheckOut.getCheckOutNoSentByVisit(context,current_pdv.get("visit_id"));
        if (checkOut_info != null) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("latitude", checkOut_info.get("latitude"));
            params.put("longitude", checkOut_info.get("longitude"));
            params.put("date_time", checkOut_info.get("date_time"));

            params.put("visit_id", current_pdv.get("visit_id"));
            params.put("pdv_id", current_pdv.get("pdv_id"));

            prepareRequest(METHOD.SEND_END_VISIT, params);
        }
        else{
            pdv_index++;
            visitSynchronizationProcess();
        }
    }

    public void startPartialPaymentsSynchronizationProcess(){
        Log.d(TAG,"startPartialPaymentsSynchronizationProcess");
        aux_index   = 0;
        aux_list    = PartialPay.getAllPartialPaymentsNoSent(context);
        if (aux_list != null) {
            aux_holder = aux_list.get(aux_index);
            prepareRequest(METHOD.SEND_PARTIAL_SETTLEMENT,aux_holder);
        }
        else{
            startFuelingSynchronizationProcess();
        }
    }

    public void partialSettlementSynchronizationProcess(){

        if (aux_index < aux_list.size()){
            aux_holder    = aux_list.get(aux_index);

            aux_holder = aux_list.get(aux_index);
            prepareRequest(METHOD.SEND_PARTIAL_SETTLEMENT,aux_holder);

        }   // SEND Fueling information
        else{
            startFuelingSynchronizationProcess();
        }
    }

    public void startFuelingSynchronizationProcess(){
        Log.d(TAG,"startFuelingSynchronizationProcess");
        aux_index   = 0;
        aux_list    = Fueling.getAllFuelingNoSent(context);
        if (aux_list != null) {
            aux_holder = aux_list.get(aux_index);
            prepareRequest(METHOD.SEND_FUELING,aux_holder);
        }
        else{
            startExtraTaskSynchronizationProcess();
        }
    }

    public void fuelingSynchronizationProcess(){

        if (aux_index < aux_list.size()){
            aux_holder    = aux_list.get(aux_index);

            prepareRequest(METHOD.SEND_FUELING,aux_holder);

        }   // SEND Fueling information
        else{
            startExtraTaskSynchronizationProcess();
        }
    }

    public void startExtraTaskSynchronizationProcess(){
        Log.d(TAG,"startExtraTaskSynchronizationProcess");
        int sizeArrayQuestions =0;
        aux_index   = 0;
        aux_list    = ExtraTasks.getAllExtraTasksNoSent(context);

        aux_form = new HashMap<String, String>();
        if (aux_list != null) {
            aux_holder = aux_list.get(aux_index);

            if(Form.getAllByIDFormandIDTask(context,aux_holder.get("id_generic_task") ,aux_holder.get("frm_id_form"))!= null){


                String timestamp    = Form.getTimestamByIDFormandIDTask(context,aux_holder.get("id_generic_task"),aux_holder.get("frm_id_form")).get("timestamp");
                String comment      = Form.getCommentByIDForm(context,aux_holder.get("id_generic_task"),aux_holder.get("frm_id_form")).get("comment");
                String answers      = Form.getAnswersByIDForm(context,aux_holder.get("id_generic_task"),aux_holder.get("frm_id_form")).get("answers");

                if(!timestamp.equals("") && !answers.equals("")){
                    aux_form.put("form_id",aux_holder.get("frm_id_form"));
                    aux_form.put("visit_id",aux_holder.get("vi_id_visit"));
                    aux_form.put("date_time",timestamp);
                    aux_form.put("comment",comment);
                    aux_form.put("answers",answers);

                    prepareRequest(METHOD.SET_FORM_TEST,aux_form);
                }else{
                    aux_index ++;
                    extraTaskSynchronizationProcess();
                }

            }else{
                aux_index ++;
                extraTaskSynchronizationProcess();
            }

        }
        else{
            Log.d(TAG,"End of the synchronizer");
            listener.finishedUploadInformation();
        }

    }


    public void extraTaskSynchronizationProcess(){

        if (aux_index < aux_list.size()){
            aux_holder    = aux_list.get(aux_index);

            aux_form = new HashMap<String, String>();
            if(Form.getAllByIDFormandIDTask(context, aux_holder.get("id_generic_task") ,aux_holder.get("frm_id_form"))!= null){

                String timestamp    = Form.getTimestamByIDFormandIDTask(context,aux_holder.get("id_generic_task"),aux_holder.get("frm_id_form")).get("timestamp");
                String comment      = Form.getCommentByIDForm(context,aux_holder.get("id_generic_task"),aux_holder.get("frm_id_form")).get("comment");
                String answers      = Form.getAnswersByIDForm(context,aux_holder.get("id_generic_task"),aux_holder.get("frm_id_form")).get("answers");

                if(!timestamp.equals("") && !answers.equals("")){
                    aux_form.put("form_id",aux_holder.get("frm_id_form"));
                    aux_form.put("visit_id",aux_holder.get("vi_id_visit"));
                    aux_form.put("date_time",timestamp);
                    aux_form.put("comment",comment);
                    aux_form.put("answers",answers);

                    prepareRequest(METHOD.SET_FORM_TEST,aux_form);
                }else{
                    aux_index ++;
                    extraTaskSynchronizationProcess();
                }

            }else{
                aux_index ++;
                extraTaskSynchronizationProcess();
            }

        }
        else{
            Log.d(TAG,"End of the synchronizer");
            listener.finishedUploadInformation();
        }
    }




    @Override
    public void prepareRequest(METHOD method, Map<String, String> params) {
        String token      = Session.getSessionActive(context).getToken();
        String username   = User.getUser(context, Session.getSessionActive(context).getUser_id()).getEmail();
        params.put("request", method.toString());
        params.put("user", username);
        params.put("token", token);

        RequestManager.sharedInstance().setListener(this);
        RequestManager.sharedInstance().makeRequestWithDataAndMethod(params, method);
    }

    @Override
    public void decodeResponse(String stringResponse) {
        JSONObject resp;
        boolean success = true;
        try {
            resp        = new JSONObject(stringResponse);
            if (!resp.getString("error").isEmpty()){
                success = false;
                //Toast.makeText(context, "Sin acceso a red, imposible sincronizar", Toast.LENGTH_SHORT).show();
                //return;
            }
            else{
                if (resp.getString("method").equalsIgnoreCase(METHOD.SEND_START_VISIT.toString())) {
                    if (success == true)
                        DataBaseManager.sharedInstance().updateCheckInStateToSentInVisit(current_pdv.get("visit_id"));
                    startOrderSynchronizationProcess();
                }
                else if (resp.getString("method").equalsIgnoreCase(METHOD.SEND_ORDER.toString())) {
                    if (success == true){
                        final String order_id = resp.getString("order_id");
                        DataBaseManager.sharedInstance().updateOrderToSent(aux_holder.get("order_id"), order_id);
                        aux_holder.put("order_id", order_id);
                    }
                    checkPaymentStatus();
                }
                else if (resp.getString("method").equalsIgnoreCase(METHOD.SEND_PAYMENT.toString())) {
                    if (is_invoices_payment){
                        Log.d(TAG, "Invoice payment successfully");
                        if (success == true)
                            Invoice.updateInvoiceToStatusSent(context, aux_holder.get("id"));
                        aux_index ++;
                        invoicesSynchronizationProcess();
                    }
                    else {
                        Log.d(TAG, "Order payment successfully");
                        if (success == true){
                            OrderSent.updateOrderSentToSent(context, aux_holder.get("order_id"));
                            aux_holder.put("sent", "SENT");
                        }

                        if (aux_holder.get("status").equalsIgnoreCase("INACTIVE")){
                            Map params              = new HashMap<String, String>();
                            params.put("order_id",aux_holder.get("order_id"));
                            prepareRequest(METHOD.CANCEL_ORDER,params);
                        }
                        else{
                            // Nothing to do, start another entry in order sent (in current visit)
                            aux_index ++;
                            ordersSynchronizationProcess();
                        }
                    }
                }
                else if (resp.getString("method").equalsIgnoreCase(METHOD.CANCEL_ORDER.toString())) {
                    Log.d(TAG, "Order cancelled successfully");
                    if (success == true) {
                        OrderSent.updateOrderSentToInactiveAndSent(context, aux_holder.get("order_id"));
                        aux_holder.put("sent", "SENT");
                    }
                    // Nothing to do, start another entry in order sent (in current visit)
                    aux_index ++;
                    ordersSynchronizationProcess();
                }

                if (resp.getString("method").equalsIgnoreCase(METHOD.SET_NO_SALE_REASON.toString())) {
                    if (success == true)
                        NoSaleReasonRecord.updateNoSaleReasonToSentInVisit(context,current_pdv.get("visit_id"));
                    sendEndVisitAuthorization();
                }

                else if (resp.getString("method").equalsIgnoreCase(METHOD.SEND_REFUND.toString())) {
                    Log.d(TAG, "Refund sent successfully");
                    if (success == true)
                        DataBaseManager.sharedInstance().updateRefundToSentInVisit(current_pdv.get("visit_id"));
                    startInvoicesSynchronizationProcess();
                }

                else if (resp.getString("method").equalsIgnoreCase(METHOD.SEND_END_VISIT.toString())) {
                    Log.d(TAG, "End visit sent successfully");
                    if (success == true)
                        CheckOut.updateCheckOutStatusToSent(context,current_pdv.get("visit_id"));
                    visitSynchronizationProcess();
                }

                else if (resp.getString("method").equalsIgnoreCase(METHOD.SEND_PARTIAL_SETTLEMENT.toString())) {
                    Log.d(TAG, "Partial Settlement sent successfully");
                    if (success == true)
                        PartialPay.updatePartialPayToStatusSent(context, aux_holder.get("id"));
                    aux_index ++;
                    partialSettlementSynchronizationProcess();
                }

                else if (resp.getString("method").equalsIgnoreCase(METHOD.SEND_FUELING.toString())) {
                    Log.d(TAG, "Fueling sent successfully");
                    if (success == true)
                        Fueling.updateFuelingToStatusSent(context, aux_holder.get("id"));
                    aux_index ++;
                    fuelingSynchronizationProcess();
                }

                else if (resp.getString("method").equalsIgnoreCase(METHOD.SET_SHELF_REVISION.toString())) {
                    Log.d(TAG, "Shelf review sent successfully");
                    if (success == true)
                        ProductShelfReview.updateAllProductShelfReviewInVisitToSent(context,current_pdv.get("visit_id"));
                    startOffersSynchronizationProcess();
                }
                else if (resp.getString("method").equalsIgnoreCase(METHOD.SET_PROMOTION.toString())) {
                    Log.d(TAG, "Promotions sent successfully");
                    if (success == true)
                        Offers.updateAllOffersInVisitToSent(context,current_pdv.get("visit_id"));
                    startQualityIncidencesSynchronizationProcess();
                }
                else if (resp.getString("method").equalsIgnoreCase(METHOD.SET_QUALITY_INCIDENCE.toString())) {
                    Log.d(TAG, "Quality incidences sent successfully");
                    if (success == true)
                        QualityIncidence.updateAllQualityIncidenceInVisitToSent(context,current_pdv.get("visit_id"));

                    if (current_pdv.get("visit_type_id").equalsIgnoreCase("2")) {   // Mayoreo
                        startSupplierPricesSynchronizationProcess();
                    }

                    else if (current_pdv.get("visit_type_id").equalsIgnoreCase("3"))   // Autoservicio
                        startInventoryQuestionnaireSynchronizationProcess();
                }
                else if (resp.getString("method").equalsIgnoreCase(METHOD.SET_AUTOSALE_STOCK.toString())) {
                    Log.d(TAG, "Autosale stock sent successfully");
                    if (success == true)
                        InventoryProduct.updateInventoryProductToSent(context,current_pdv.get("visit_id"));
                    startWarehouseSynchronizationProcess();
                }
                else if (resp.getString("method").equalsIgnoreCase(METHOD.SET_WAREHOUSE_STOCK.toString())) {
                    Log.d(TAG, "Warehaouse sent successfully");
                    if (success == true)
                        Warehouse.updateAllWarehouseProductsInVisitToSent(context,current_pdv.get("visit_id"));
                    sendEndVisitAuthorization();
                }
                else if (resp.getString("method").equalsIgnoreCase(METHOD.SET_SUPPLIER_PRICES.toString())) {
                    Log.d(TAG, "Supplier prices sent successfully");
                    if (success == true)
                        WholeSalesPrices.updateWholeSalesPricesToStatusSent(context, aux_holder.get("id"));
                        Warehouse.updateAllWarehouseProductsInVisitToSent(context,current_pdv.get("visit_id"));
                    sendEndVisitAuthorization();
                }
                else if (resp.getString("method").equalsIgnoreCase(METHOD.SET_SIGNATURE.toString())) {
                    Log.d(TAG, "Set Signature sent successfully");
                    if (success == true)
                        Signature.updateSignatureToStatusSent(context,aux_holder.get("id"));
                    endVisitSynchronizationProcess();
                }
                else if (resp.getString("method").equalsIgnoreCase(METHOD.SET_FORM_TEST.toString())) {
                    Log.d(TAG, "Set form sent successfully");
                    if (success == true){

                        if(resp.getString("success").equalsIgnoreCase("true")) {
                            aux_index++;
                            aux_form = new HashMap<String, String>();

                            aux_form.put("id_visit", aux_holder.get("vi_id_visit"));
                            aux_form.put("id_generic_task", aux_holder.get("id_generic_task"));
                            aux_form.put("observations", ExtraTasks.getObservationByIDTask(context, aux_holder.get("id_generic_task")).get("observations"));
                            aux_form.put("evidence", ExtraTasks.getEvidenceByIDTask(context, aux_holder.get("id_generic_task")).get("evidence"));

                            Log.e("Evidencia:", "ev:  " + ExtraTasks.getEvidenceByIDTask(context, aux_holder.get("id_generic_task")).get("evidence"));

                            prepareRequest(METHOD.SET_GENERIC_TASK_RESULT, aux_form);
                        }else{
                            aux_index++;
                            extraTaskSynchronizationProcess();
                        }

                    }
                }
                else if (resp.getString("method").equalsIgnoreCase(METHOD.SET_GENERIC_TASK_RESULT.toString())) {
                    Log.d(TAG, "Set generic task sent successfully");
                    if (success == true){

                        ExtraTasks.updateExtraTasksToStatusSent(context,aux_holder.get("id_generic_task"));
                        extraTaskSynchronizationProcess();

                    }
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        startSynchronizeProcess();
    }
}
