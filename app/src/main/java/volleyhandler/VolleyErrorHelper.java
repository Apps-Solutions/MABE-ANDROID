package volleyhandler;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sellcom.tracker.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.Map;

import database.models.RecordActions;
import static database.models.RecordActions.deleteParam;

/**
 * Created by dmolinero on 12/05/14.
 */
public class VolleyErrorHelper {
    //Enviar los que estan en DB
    public static void sendAll(final Context _context){
        Cursor cParams = null;
        try {
            cParams = RecordActions.getParamsComplete(_context);
            if (cParams != null && cParams.getCount() > 0) {
                for(int ii=1; ii<=cParams.getCount();ii++){
                        Log.i("Numero de peticiones no enviadas", "n: " + cParams.getCount());
                        final String id_recover = cParams.getString(cParams.getColumnIndexOrThrow(RecordActions.ID));
                        String name_webserciceRecover = cParams.getString(cParams.getColumnIndexOrThrow(RecordActions.NAME_WEBSERVICE));
                        byte[] bytesRecover = cParams.getBlob(cParams.getColumnIndexOrThrow(RecordActions.DATA));

                        ByteArrayInputStream bs = new ByteArrayInputStream(bytesRecover); // bytes es el byte[]
                        ObjectInputStream is = new ObjectInputStream(bs);
                        final Map<String, String> paramsRecover = (Map<String, String>) is.readObject();
                        is.close();
                        if(name_webserciceRecover!=null) {
                            if (name_webserciceRecover.equals("login")) {
                                String user = paramsRecover.get("user");
                                String pass = paramsRecover.get("password");
                                Log.i("usuario rcover", "u:" + user);
                                Log.i("passwaord rcover", "p:" + pass);
                            }
                        }
                        //Nuevo intento de envio,,, manejo de error y borrado de BD
                        StringRequest req = new StringRequest(Request.Method.POST,ApplicationController.URL, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String responseString) {
                                VolleyLog.v("Response:%n %s", responseString);
                                Log.i("respuesta","r:"+responseString);

                                boolean success = false;
                                JSONObject response;
                                //Data received, parse it to JSONObject and obtain fields
                                try {
                                    response = new JSONObject(responseString);
                                    success = response.getBoolean("success");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                deleteParam(_context, id_recover);
        //                      Para re-intentar en caso de que success sea false
        //                        if (success) deleteParam(_context, id_recover);
        //                        else VolleyErrorHelper.setAgainParams(_context,id_recover);
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
        //                        VolleyLog.e("Error: ", error.getMessage());
        //                        Log.e("Error: ", "e:" +error.getMessage());
                                Log.e("Error Volley", "e:" + VolleyErrorHelper.getMessage(error, _context));
                                VolleyErrorHelper.setAgainParams(_context,id_recover);
                            }
                        }){
                            @Override
                            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                                return paramsRecover;
                            }
                        };
                        req.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, 1, 1.0f));
                        // add the request object to the queue to be executed
                        RequestQueue queue = Volley.newRequestQueue(_context);
                        queue.add(req);
                    if(ii+1<=cParams.getCount()) cParams.moveToNext();
                }
            }
            if (cParams != null) cParams.close();
        }catch (Exception e){
            e.printStackTrace();
            if (cParams != null) cParams.close();
        }
    }


    //Re-enviar
    public static void setAgainParams(final Context _context,String id){
        Cursor elementSend = null;
        try {
            elementSend = RecordActions.getParamsByTimeStamp(_context, id);
            if (elementSend != null && elementSend.getCount() > 0) {
               final String id_recover = elementSend.getString(elementSend.getColumnIndexOrThrow(RecordActions.ID));
                String name_webserciceRecover = elementSend.getString(elementSend.getColumnIndexOrThrow(RecordActions.NAME_WEBSERVICE));
                byte[] bytesRecover = elementSend.getBlob(elementSend.getColumnIndexOrThrow(RecordActions.DATA));

                ByteArrayInputStream bs = new ByteArrayInputStream(bytesRecover); // bytes es el byte[]
                ObjectInputStream is = new ObjectInputStream(bs);
                final Map<String, String> paramsRecover = (Map<String, String>) is.readObject();
                is.close();

                if(name_webserciceRecover.equals("login")){
                    String user =  paramsRecover.get("user");
                    String pass =  paramsRecover.get("password");
                    Log.i("usuario rcover","u:"+user);
                    Log.i("passwaord rcover","p:"+pass);

                }
                //Nuevo intento de envio,,, manejo de error y borrado de BD
                StringRequest req = new StringRequest(Request.Method.POST,ApplicationController.URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String responseString) {
                        VolleyLog.v("Response:%n %s", responseString);
                        Log.i("respuesta","r:"+responseString);

                        boolean success = false;
                        JSONObject response;
                        //Data received, parse it to JSONObject and obtain fields
                        try {
                            response = new JSONObject(responseString);
                            success = response.getBoolean("success");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        deleteParam(_context, id_recover);
//                      Para re-intentar en caso de que success sea false
//                        if (success) deleteParam(_context, id_recover);
//                        else VolleyErrorHelper.setAgainParams(_context,id_recover);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        VolleyLog.e("Error: ", error.getMessage());
//                        Log.e("Error: ", "e:" +error.getMessage());
                            Log.e("Error Volley", "e:" + VolleyErrorHelper.getMessage(error, _context));
                            VolleyErrorHelper.setAgainParams(_context,id_recover);

                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                        return paramsRecover;
                    }
                };

                req.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, 1, 1.0f));
                // add the request object to the queue to be executed
                RequestQueue queue = Volley.newRequestQueue(_context);
                queue.add(req);

                elementSend.close();
            }
        }catch (Exception e){
            e.printStackTrace();
            if (elementSend!=null)
                elementSend.close();
        }
    }



    /**
     * Returns appropriate message which is to be displayed to the user
     * against the specified error object.
     *
     //* @param error
     //* @param context
     //* @return
     */
    public static String getMessage(Object error, Context context) {
        if (error instanceof TimeoutError) {
            return context.getResources().getString(R.string.generic_server_down);
        }
        else if (isServerProblem(error)) {
//            return handleServerError(error, context);
            return "is server problem";
        }
        else if (isNetworkProblem(error)) {
            return context.getResources().getString(R.string.no_internet);
        }
        return context.getResources().getString(R.string.generic_error);
    }

    /**
     * Determines whether the error is related to network
     //* @param error
     //* @return
     */
    private static boolean isNetworkProblem(Object error) {
        return (error instanceof NetworkError) || (error instanceof NoConnectionError);
    }
    /**
     * Determines whether the error is related to server
     //* @param error
     //* @return
     */
    private static boolean isServerProblem(Object error) {
        return (error instanceof ServerError) || (error instanceof AuthFailureError);
    }
    /**
     * Handles the server error, tries to determine whether to show a stock message or to
     * show a message retrieved from the server.
     *
     //* @param err
     //* @param context
     //* @return
     */
//    private static String handleServerError(Object err, Context context) {
//        VolleyError error = (VolleyError) err;
//
//        NetworkResponse response = error.networkResponse;
//
//        if (response != null) {
//            switch (response.statusCode) {
//                case 404:
//                case 422:
//                case 401:
//                    try {
//                        // server might return error like this { "error": "Some error occured" }
//                        // Use "Gson" to parse the result
//                        HashMap<String, String> result = new Gson().fromJson(new String(response.data),
//                                new TypeToken<Map<String, String>>() {
//                                }.getType());
//
//                        if (result != null && result.containsKey("error")) {
//                            return result.get("error");
//                        }
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    // invalid request
//                    return error.getMessage();
//
//                default:
//                    return context.getResources().getString(R.string.generic_server_down);
//            }
//        }
//        return context.getResources().getString(R.string.generic_error);
//    }
}