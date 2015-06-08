package async_request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.sellcom.tracker.R;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import util.ErrorDecoder;

public class RequestManager implements ResponseListenerInterface {

    public enum RequestType {
        NORMAL,
        INITIAL_INFORMATION,
        UPLOADING_INFORMATION
    }

    public  final   boolean                                 TEST_MODE          = false;

    public  final   String 	                                LOG_TAG_MANAGER    = "requestManager";
    public  final   String 	                                LOG_TAG_REQUEST    = "asyncRequest";

    // DEV
    //public  final   String 	                                API_URL 	       = "http://54.187.219.128/ragasa/dev/api.php";

    // PREVENTA
    //public  final  String                                   API_URL            = "http://54.187.219.128/preventa/api.php";

    // MABE
    public  final  String                                   API_URL           = "http://54.187.219.128/mabe/api.php";

    // PROD
    //public  final   String 	                                API_URL 	       = "http://54.187.193.46/sicmobile/api.php";


    private static 	RequestManager   						manager;
    private         Activity                                activity;
    private         UIResponseListenerInterface             listener;
    private         METHOD                                  method;

    private         ProgressDialog                          progressDialog  = null;

    private 		String 									user_token;
    public 		    String 									user_name;

    public          RequestType                             request_type    = RequestType.NORMAL;

    private RequestManager(){
    }
    public static synchronized RequestManager sharedInstance(){
        if (manager == null)
            manager = new RequestManager();
        return manager;
    }

    public void saveInPreferencesKeyAndValue(String key, String value){
        SharedPreferences sharedPref        = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor     = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void endSession(){

        Log.d(LOG_TAG_REQUEST,"Ending session");

        SharedPreferences sharedPref        = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor     = sharedPref.edit();
        editor.putString("user_email","CLEAR");
        editor.putString("password","CLEAR");
        editor.putString("token","CLEAR");
        editor.putString("initial_info_timestamp","CLEAR");
        editor.commit();
    }


    public String getPreferencesValueForKey(String key){
        SharedPreferences sharedPref        = activity.getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getString(key,"CLEAR");
    }

    public Map<String,String> jsonToMap(JSONObject obj) throws JSONException {
        Map<String, String> map = new HashMap<String, String>();
        Iterator<?> keys        = obj.keys();

        while( keys.hasNext() ){
            String key = (String)keys.next();
            String value = obj.getString(key);
            map.put(key, value);
        }
        return  map;
    }

    public void                         setActivity(Activity activity)  {this.activity    = activity;}
    public Activity                     getActivity()                   {return activity;}

    public void                         setUser_token(String user_token){this.user_token    = user_token;}
    public String                       getUser_token()                 {return user_token;}
    public void                         clearUser_token()               {user_token    = null;
    }

    public void                         setListener(UIResponseListenerInterface listener){   this.listener = listener;}
    public UIResponseListenerInterface  getListener(){return listener;}

    public void showErrorDialog(String errorMessage, Context context){
        Log.d(LOG_TAG_MANAGER,"Error message: "+errorMessage);
        String message = ErrorDecoder.decodeErrorMessage(errorMessage,context);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Error");
        builder.setMessage(message);
        builder.setNeutralButton("OK",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void showConfirmationDialog(String confirmMessage, Context context){
        Log.d(LOG_TAG_MANAGER,"Error message: "+confirmMessage);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Atención");
        builder.setMessage(confirmMessage);
        builder.setNeutralButton("OK",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void showConfirmationDialogWithListener(final String confirmMessage, Context context, final ConfirmationDialogListener listener){
        Log.d(LOG_TAG_MANAGER,"Error message: "+confirmMessage);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Atención");
        builder.setMessage(confirmMessage);
        builder.setNeutralButton("OK",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                listener.okFromConfirmationDialog(confirmMessage);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void showDecisionDialogWithListener(final String confirmMessage, Context context, final DecisionDialogWithListener listener){
        Log.d(LOG_TAG_MANAGER,"Error message: "+confirmMessage);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Atención");
        builder.setMessage(confirmMessage);
        builder.setPositiveButton(context.getString(R.string.done),new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                listener.responseFromDecisionDialog(confirmMessage,"OK");
            }
        });
        builder.setNegativeButton(context.getString(R.string.cancel),new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                listener.responseFromDecisionDialog(confirmMessage,"CANCEL");
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void showLoadingDialog(){
        String dialogMessage    = "";

        if (request_type == RequestType.NORMAL){
            switch (method){
                case LOGIN:
                    dialogMessage   = activity.getString(R.string.req_man_login);
                    break;
                case GET_WORKPLAN:
                    dialogMessage   = activity.getString(R.string.req_man_retrieving_pdvs);
                    break;
                case GET_PDV_INFO:
                    dialogMessage   = activity.getString(R.string.req_man_retrieving_pdv_detail);
                    break;
                case SEND_START_VISIT:
                    dialogMessage   = activity.getString(R.string.req_man_starting_visit);
                    break;
                case GET_PRODUCTS:
                    dialogMessage   = activity.getString(R.string.req_man_retrieving_products_list);
                    break;
                case SEND_ORDER:
                    dialogMessage   = activity.getString(R.string.req_man_uploading_order_info);
                    break;
                case GET_INVOICES:
                    dialogMessage   = activity.getString(R.string.req_man_get_invoices);
                    break;
                case SEND_PAYMENT:
                    dialogMessage   = activity.getString(R.string.req_man_send_payment);
                    break;
                case SEND_PARTIAL_SETTLEMENT:
                    dialogMessage   = activity.getString(R.string.req_man_send_partial_settlement);
                    break;
                case SEND_END_VISIT:
                    dialogMessage   = activity.getString(R.string.req_man_starting_visit);
                    break;
                case GET_PDVS:
                    dialogMessage   = activity.getString(R.string.req_man_get_pdvs);
                    break;
                case LOGOUT:
                    dialogMessage   = activity.getString(R.string.req_man_logout);
                    break;
                case SET_PROSPECT:
                    dialogMessage   = activity.getString(R.string.req_man_set_client);
                    break;
                case GET_STATES:
                    dialogMessage   = activity.getString(R.string.req_man_get_states);
                    break;
                case GET_CITIES:
                    dialogMessage   = activity.getString(R.string.req_man_get_cities);
                    break;
                case GET_RESOURCE_TYPE:
                    dialogMessage   = activity.getString(R.string.req_man_get_resources);
                    break;
                case GET_PROMOTION_TYPE:
                    dialogMessage   = activity.getString(R.string.req_man_get_promotions);
                    break;
                case GET_EXHIBITION_TYPE:
                    dialogMessage   = activity.getString(R.string.req_man_get_exhibitions);
                    break;
                case GET_QUALITY_PROBLEM_TYPE:
                    dialogMessage   = activity.getString(R.string.req_man_get_quality);
                    break;
                case GET_BRAND:
                    dialogMessage   = activity.getString(R.string.req_man_get_brand);
                    break;
                case GET_NO_SALE_REASONS:
                    dialogMessage   = activity.getString(R.string.req_man_get_no_sales);
                    break;
                case GET_USER_INFO:
                    dialogMessage   = activity.getString(R.string.req_man_get_user_info);
                    break;
                case GET_PENDING_ORDER:
                    dialogMessage   = activity.getString(R.string.req_man_get_pending_order);
                    break;
                case SET_NO_SALE_REASON:
                    dialogMessage   = activity.getString(R.string.req_man_set_no_sale_reason);
                    break;
                case SET_SHELF_REVISION:
                    dialogMessage   = activity.getString(R.string.req_man_set_questionnaire);
                    break;
                case SET_SUPPLIER_PRICES:
                    dialogMessage   = activity.getString(R.string.req_man_set_supplier_prices);
                    break;
                case SET_SIGNATURE:
                    dialogMessage   = activity.getString(R.string.req_man_set_end_visit_authorization);
                    break;
                case SET_FORM_TEST:
                    dialogMessage   = activity.getString(R.string.req_man_sending_form_data);
                    break;
                case SET_GENERIC_TASK_RESULT:
                    dialogMessage   = activity.getString(R.string.req_man_sending_extra_task_data);
                    break;

                default:
                    break;
            }
        }

        else if (request_type == RequestType.INITIAL_INFORMATION){
            dialogMessage   = activity.getString(R.string.req_man_down_initial_info);
        }

        else if (request_type == RequestType.UPLOADING_INFORMATION){
            dialogMessage   = activity.getString(R.string.req_man_send_information);
        }

        progressDialog = new ProgressDialog(activity);
        progressDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setMessage(dialogMessage);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void dismissProgressDialog(){
        progressDialog.dismiss();
        progressDialog  = null;
    }

    public void makeRequestWithDataAndMethod(Map<String,String> reqData, METHOD method){
        this.method                 = method;
        showLoadingDialog();
        final AsyncRequest req      = new AsyncRequest(activity, reqData, this);

        /* DUMMY request mode info */
        req.method                  = method;
        /***************************/
        req.execute(null,null,null);
    }

    public void makeRequestWithJSONDataAndMethod(JSONObject reqData, METHOD method){
        Log.d(LOG_TAG_MANAGER,"MakeRequestJSON");
        this.method                         = method;
        showLoadingDialog();
        final AsyncRequestWithJSON req      = new AsyncRequestWithJSON(activity, reqData, this);

        /* DUMMY request mode info */
        req.method                  = method;
        /***************************/
        req.execute(null,null,null);
    }

    @Override
    public void responseServiceToManager(JSONObject jsonResponse) {
        String method   = "";
        dismissProgressDialog();
        try {
            method  = jsonResponse.getString("method");
            Log.d(LOG_TAG_MANAGER, jsonResponse.toString());
            listener.decodeResponse(jsonResponse.toString());

        } catch (JSONException e) {
            e.printStackTrace();
            if(method.equalsIgnoreCase(METHOD.SEND_START_VISIT.toString()) ||
                    method.equalsIgnoreCase(METHOD.SEND_END_VISIT.toString())){
                Log.d(LOG_TAG_MANAGER, jsonResponse.toString());
                listener.decodeResponse(jsonResponse.toString());
            }
            else
                showErrorDialog(activity.getString(R.string.req_man_error_contacting_service), this.activity);
        }
    }

    public class AsyncRequest extends AsyncTask<Void,Void,JSONObject>{
        Activity 					activity;
        Map<String, String> 		requestData;
        ResponseListenerInterface   listener;
        METHOD                      method;

        public AsyncRequest(Activity activity, Map<String, String> requestData, ResponseListenerInterface listener){
            this.activity 	       	= activity;
            this.requestData        = requestData;
            this.listener    		= listener;
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            Log.d(LOG_TAG_REQUEST,"Request data:"+requestData.toString());
            JSONObject jsonResponse = null;
            if (TEST_MODE){

            }
            else{
                HttpParams httpParameters   = new BasicHttpParams();
                int timeoutConnection       = 20000;
                int timeoutSocket           = 20000;
                HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
                HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

                HttpClient httpclient   = new DefaultHttpClient(httpParameters);
                HttpPost httppost       = new HttpPost(API_URL);

                try {
                    List<NameValuePair> params = new ArrayList<NameValuePair>(requestData.size());

                    for (Map.Entry<String, String> entry : requestData.entrySet())
                        params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));

                    httppost.setEntity(new UrlEncodedFormEntity(params));

                    HttpResponse response       = httpclient.execute(httppost);
                    String       strResponse    = EntityUtils.toString(response.getEntity());
                    Log.d(LOG_TAG_REQUEST,"Response: "+strResponse);
                    try {
                        jsonResponse            = new JSONObject(strResponse);
                        jsonResponse.put("method",method.toString());
                        jsonResponse.put("error", "");
                        Log.d(LOG_TAG_REQUEST,"jsonResponse: "+jsonResponse.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } catch (ClientProtocolException e) {
                    try {
                        jsonResponse    = new JSONObject();
                        jsonResponse.put("error", "Network");
                        jsonResponse.put("success", "false");
                        jsonResponse.put("method",method.toString());
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                } catch (IOException e) {
                    try {
                        jsonResponse    = new JSONObject();
                        jsonResponse.put("error", "Info");
                        jsonResponse.put("success", "false");
                        jsonResponse.put("method",method.toString());
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
            }
            return jsonResponse;
        }

        protected void onPostExecute(JSONObject jsonResponse) {
            //Log.d(LOG_TAG_REQUEST,"jsonResponse_post: "+jsonResponse.toString());
            listener.responseServiceToManager(jsonResponse);
        }
    }

    public class AsyncRequestWithJSON extends AsyncTask<Void,Void,JSONObject>{

        Activity 					activity;
        JSONObject 		            requestData;
        ResponseListenerInterface   listener;

        /* DUMMY request mode info */
        METHOD                      method;

        public AsyncRequestWithJSON(Activity activity, JSONObject requestData, ResponseListenerInterface listener){
            this.activity 	       	= activity;
            this.requestData        = requestData;
            this.listener    		= listener;

        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            Log.d(LOG_TAG_REQUEST,"Request data:"+requestData.toString());
            JSONObject jsonResponse = null;
            if (TEST_MODE){
                try {
                    Thread.sleep(500);
                    jsonResponse = new JSONObject();
                    try {
                        jsonResponse.put("method",method.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (method == METHOD.LOGIN){
                    }
                    else if (method == METHOD.GET_WORKPLAN){
                    }
                    else if (method == METHOD.GET_PDV_INFO){
                    }
                    else if (method == METHOD.SEND_START_VISIT){
                    }
                    else if (method == METHOD.GET_PRODUCTS){

                    }
                    else if (method == METHOD.SEND_ORDER){

                    }
                    else if (method == METHOD.SEND_PAYMENT){

                    }
                    else if (method == METHOD.GET_INVOICES){

                    }
                    else if (method == METHOD.SEND_PARTIAL_SETTLEMENT){

                    }
                    else if (method == METHOD.SEND_END_VISIT){

                    }
                    else if (method == METHOD.GET_PDVS){

                    }
                    else if (method == METHOD.LOGOUT){

                    }
                    else {

                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else{
                HttpClient httpclient           = new DefaultHttpClient();
                HttpPost httppost               = new HttpPost(API_URL);
                ArrayList<NameValuePair> params = new ArrayList<NameValuePair>(6);
                try {
                    Log.d(LOG_TAG_REQUEST,"Req: "+requestData.toString());

                    try {

                        if (method == METHOD.SEND_ORDER){
                            params.add(new BasicNameValuePair("request", requestData.get("request").toString()));
                            params.add(new BasicNameValuePair("user", requestData.get("user").toString()));
                            params.add(new BasicNameValuePair("token", requestData.get("token").toString()));
                            params.add(new BasicNameValuePair("date_time", requestData.get("date_time").toString()));
                            params.add(new BasicNameValuePair("pdv_id", requestData.get("pdv_id").toString()));
                            params.add(new BasicNameValuePair("visit_id", requestData.get("visit_id").toString()));
                            String str = (requestData.get("products").toString()).replace("\\\"","\"");
                            params.add(new BasicNameValuePair("products", str));
                        }
                        else if (method == METHOD.SET_SHELF_REVISION || method == METHOD.SET_QUALITY_INCIDENCE ||
                                 method == METHOD.SET_AUTOSALE_STOCK|| method == METHOD.SEND_REFUND ||
                                 method == METHOD.SET_WAREHOUSE_STOCK){
                            Log.d(LOG_TAG_REQUEST,"SHELF_REVISION");
                            params.add(new BasicNameValuePair("request", requestData.get("request").toString()));
                            params.add(new BasicNameValuePair("user", requestData.get("user").toString()));
                            params.add(new BasicNameValuePair("token", requestData.get("token").toString()));
                            params.add(new BasicNameValuePair("id_visit", requestData.get("id_visit").toString()));
                            String str = (requestData.get("products").toString()).replace("\\\"","\"");
                            params.add(new BasicNameValuePair("products", str));
                        }else if (method == METHOD.SET_PROMOTION){
                            Log.d(LOG_TAG_REQUEST,"SET_PROMOTION");
                            params.add(new BasicNameValuePair("request", requestData.get("request").toString()));
                            params.add(new BasicNameValuePair("user", requestData.get("user").toString()));
                            params.add(new BasicNameValuePair("token", requestData.get("token").toString()));
                            params.add(new BasicNameValuePair("id_visit", requestData.get("id_visit").toString()));
                            //String str = (requestData.get("promotions").toString()).replace("\\\"","\"");
                            params.add(new BasicNameValuePair("promotions", requestData.get("promotions").toString()));
                        }
                        else if (method == METHOD.SET_SUPPLIER_PRICES){
                            Log.d(LOG_TAG_REQUEST,"SET SUPPLIER PRICES");
                            params.add(new BasicNameValuePair("request", requestData.get("request").toString()));
                            params.add(new BasicNameValuePair("user", requestData.get("user").toString()));
                            params.add(new BasicNameValuePair("token", requestData.get("token").toString()));
                            params.add(new BasicNameValuePair("id_visit", requestData.get("id_visit").toString()));
                            String str = (requestData.get("products").toString()).replace("\\\"","\"");
                            params.add(new BasicNameValuePair("products", str));
                        }
                        else if (method == METHOD.SET_FORM_TEST){
                            Log.d(LOG_TAG_REQUEST,"SET_FORM_TEST");
                            params.add(new BasicNameValuePair("request", requestData.get("request").toString()));
                            params.add(new BasicNameValuePair("user", requestData.get("user").toString()));
                            params.add(new BasicNameValuePair("token", requestData.get("token").toString()));
                            params.add(new BasicNameValuePair("visit_id", requestData.get("visit_id").toString()));
                            params.add(new BasicNameValuePair("form_id", requestData.get("form_id").toString()));
                            params.add(new BasicNameValuePair("date_time", requestData.get("date_time").toString()));
                            params.add(new BasicNameValuePair("comment", requestData.get("comment").toString()));
                            String str = (requestData.get("answers").toString()).replace("\\\"","\"");
                            params.add(new BasicNameValuePair("answers", str));
                        }

                        httppost.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    HttpResponse response       = httpclient.execute(httppost);

                    String       strResponse    = EntityUtils.toString(response.getEntity());
                    Log.d(LOG_TAG_REQUEST,"Response: "+strResponse);
                    try {
                        jsonResponse            = new JSONObject(strResponse);
                        jsonResponse.put("method",method.toString());
                        jsonResponse.put("error", "");
                        Log.d(LOG_TAG_REQUEST,"jsonResponse: "+jsonResponse.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } catch (ClientProtocolException e) {
                    try {
                        jsonResponse    = new JSONObject();
                        jsonResponse.put("error", "Network");
                        jsonResponse.put("method",method.toString());
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                } catch (IOException e) {
                    try {
                        jsonResponse    = new JSONObject();
                        jsonResponse.put("error", "Info");
                        jsonResponse.put("method",method.toString());
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
            }
            return jsonResponse;
        }

        protected void onPostExecute(JSONObject jsonResponse) {
            //Log.d(LOG_TAG_REQUEST,"jsonResponse_post: "+jsonResponse.toString());
            listener.responseServiceToManager(jsonResponse);
        }
    }
}