package database.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import database.DataBaseAdapter;

/**
 * Created by hugo.figueroa on 19/01/15.
 *
 */
public class CustomerWorkPlan extends Table{

    public static final String TAG = "customer_workplan_table";

    public static final String TABLE = "customer_workplan";

    public static final String RFC              = "rfc";
    public static final String JDE              = "jde";
    public static final String VISIT_STATUS     = "visit_status";
    public static final String PDV              = "pdv";
    public static final String REAL_START       = "real_start";
    public static final String VISIT_STATUS_ID  = "visit_status_id";
    public static final String PDV_ID           = "pdv_id";
    public static final String REAL_END         = "real_end";
    public static final String ADDRESS          = "address";
    public static final String SCHEDULED_START  = "scheduled_start";
    public static final String ID               = "visit_id";
    public static final String LATITUDE         = "latitude";
    public static final String LONGITUDE        = "longitude";
    public static final String VISIT_TYPE       = "visit_type";
    public static final String VISIT_TYPE_ID    = "visit_type_id";
    public static final String PDV_PHONE        = "pdv_phone";

    public static long insert(Context context,
                              String id, String visit_status_id, String pdv_id, String visit_type_id,
                              String pdv, String visit_status, String visit_type, String scheduled_start,
                              String real_start,String real_end,String latitude,String longitude,
                              String address,String rfc,String jde, String pdv_phone) {
        ContentValues cv = new ContentValues();
        cv.put(ID, id);
        cv.put(VISIT_STATUS_ID, visit_status_id);
        cv.put(PDV_ID, pdv_id);
        cv.put(VISIT_TYPE_ID, visit_type_id);
        cv.put(PDV, pdv);
        cv.put(VISIT_STATUS, visit_status);
        cv.put(VISIT_TYPE, visit_type);
        cv.put(SCHEDULED_START, scheduled_start);
        cv.put(REAL_START, real_start);
        cv.put(REAL_END, real_end);
        cv.put(LATITUDE, latitude);
        cv.put(LONGITUDE, longitude);

        if (address.equalsIgnoreCase("null"))
            cv.put(ADDRESS, "");
        else
            cv.put(ADDRESS, address);

        cv.put(RFC, rfc);
        cv.put(JDE, jde);
        if (jde.equalsIgnoreCase("null"))
            cv.put(JDE, "");
        else
            cv.put(JDE, jde);

        if (pdv_phone.equalsIgnoreCase("null"))
            cv.put(PDV_PHONE, "");
        else
            cv.put(PDV_PHONE, pdv_phone);

        return DataBaseAdapter.getDB(context).insert(TABLE, null, cv);
    }

    public static Map<String,String> getCustomerWorkPlanByVisit(Context context, String visit_id) {

        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, ID + "=" + visit_id, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {

            Map<String,String> map  = new HashMap<String, String>();

            map.put(ID,cursor.getString(cursor.getColumnIndexOrThrow(ID)));
            map.put(VISIT_STATUS_ID,cursor.getString(cursor.getColumnIndexOrThrow(VISIT_STATUS_ID)));
            map.put(PDV_ID,cursor.getString(cursor.getColumnIndexOrThrow(PDV_ID)));
            map.put(VISIT_TYPE_ID,cursor.getString(cursor.getColumnIndexOrThrow(VISIT_TYPE_ID)));
            map.put(PDV,cursor.getString(cursor.getColumnIndexOrThrow(PDV)));
            map.put(VISIT_STATUS,cursor.getString(cursor.getColumnIndexOrThrow(VISIT_STATUS)));
            map.put(VISIT_TYPE,cursor.getString(cursor.getColumnIndexOrThrow(VISIT_TYPE)));
            map.put(SCHEDULED_START,cursor.getString(cursor.getColumnIndexOrThrow(SCHEDULED_START)));
            map.put(REAL_START,cursor.getString(cursor.getColumnIndexOrThrow(REAL_START)));
            map.put(REAL_END,cursor.getString(cursor.getColumnIndexOrThrow(REAL_END)));
            map.put(LATITUDE,cursor.getString(cursor.getColumnIndexOrThrow(LATITUDE)));
            map.put(LONGITUDE,cursor.getString(cursor.getColumnIndexOrThrow(LONGITUDE)));
            map.put(ADDRESS,cursor.getString(cursor.getColumnIndexOrThrow(ADDRESS)));
            map.put(RFC,cursor.getString(cursor.getColumnIndexOrThrow(RFC)));
            map.put(JDE,cursor.getString(cursor.getColumnIndexOrThrow(JDE)));
            map.put(PDV_PHONE,cursor.getString(cursor.getColumnIndexOrThrow(PDV_PHONE)));

            cursor.close();

            return map;
        }
        return null;
    }

    public static Map<String,String> getCustomerWorkPlanByPDV(Context context, String pdv_id) {

        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, PDV_ID + "=" + pdv_id, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {

            Map<String,String> map  = new HashMap<String, String>();

            map.put(ID,cursor.getString(cursor.getColumnIndexOrThrow(ID)));
            map.put(VISIT_STATUS_ID,cursor.getString(cursor.getColumnIndexOrThrow(VISIT_STATUS_ID)));
            map.put(PDV_ID,cursor.getString(cursor.getColumnIndexOrThrow(PDV_ID)));
            map.put(VISIT_TYPE_ID,cursor.getString(cursor.getColumnIndexOrThrow(VISIT_TYPE_ID)));
            map.put(PDV,cursor.getString(cursor.getColumnIndexOrThrow(PDV)));
            map.put(VISIT_STATUS,cursor.getString(cursor.getColumnIndexOrThrow(VISIT_STATUS)));
            map.put(VISIT_TYPE,cursor.getString(cursor.getColumnIndexOrThrow(VISIT_TYPE)));
            map.put(SCHEDULED_START,cursor.getString(cursor.getColumnIndexOrThrow(SCHEDULED_START)));
            map.put(REAL_START,cursor.getString(cursor.getColumnIndexOrThrow(REAL_START)));
            map.put(REAL_END,cursor.getString(cursor.getColumnIndexOrThrow(REAL_END)));
            map.put(LATITUDE,cursor.getString(cursor.getColumnIndexOrThrow(LATITUDE)));
            map.put(LONGITUDE,cursor.getString(cursor.getColumnIndexOrThrow(LONGITUDE)));
            map.put(ADDRESS,cursor.getString(cursor.getColumnIndexOrThrow(ADDRESS)));
            map.put(RFC,cursor.getString(cursor.getColumnIndexOrThrow(RFC)));
            map.put(JDE,cursor.getString(cursor.getColumnIndexOrThrow(JDE)));
            map.put(PDV_PHONE,cursor.getString(cursor.getColumnIndexOrThrow(PDV_PHONE)));

            cursor.close();

            return map;
        }
        return null;
    }


    public static ArrayList<Map<String,String>> getAllInMaps(Context context){

        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, null, null, null ,null, ID);
        if (cursor != null && cursor.getCount() > 0) {
            ArrayList<Map<String,String>> list = new ArrayList<Map<String,String>>();

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                Map<String,String> map  = new HashMap<String, String>();

                map.put(ID,cursor.getString(cursor.getColumnIndexOrThrow(ID)));
                map.put(VISIT_STATUS_ID,cursor.getString(cursor.getColumnIndexOrThrow(VISIT_STATUS_ID)));
                map.put(PDV_ID,cursor.getString(cursor.getColumnIndexOrThrow(PDV_ID)));
                map.put(VISIT_TYPE_ID,cursor.getString(cursor.getColumnIndexOrThrow(VISIT_TYPE_ID)));
                map.put(PDV,cursor.getString(cursor.getColumnIndexOrThrow(PDV)));
                map.put(VISIT_STATUS,cursor.getString(cursor.getColumnIndexOrThrow(VISIT_STATUS)));
                map.put(VISIT_TYPE,cursor.getString(cursor.getColumnIndexOrThrow(VISIT_TYPE)));
                map.put(SCHEDULED_START,cursor.getString(cursor.getColumnIndexOrThrow(SCHEDULED_START)));
                map.put(REAL_START,cursor.getString(cursor.getColumnIndexOrThrow(REAL_START)));
                map.put(REAL_END,cursor.getString(cursor.getColumnIndexOrThrow(REAL_END)));
                map.put(LATITUDE,cursor.getString(cursor.getColumnIndexOrThrow(LATITUDE)));
                map.put(LONGITUDE,cursor.getString(cursor.getColumnIndexOrThrow(LONGITUDE)));
                map.put(ADDRESS,cursor.getString(cursor.getColumnIndexOrThrow(ADDRESS)));
                map.put(RFC,cursor.getString(cursor.getColumnIndexOrThrow(RFC)));
                map.put(JDE,cursor.getString(cursor.getColumnIndexOrThrow(JDE)));
                map.put(PDV_PHONE,cursor.getString(cursor.getColumnIndexOrThrow(PDV_PHONE)));
                list.add(map);
            }

            cursor.close();
            return list;
        }
        return null;
    }

    public static void insertCustomerWorkPlanFromJSONArray(Context context, JSONArray array){
        if (CustomerWorkPlan.getAllInMaps(context) != null )    // Products stored in database
            CustomerWorkPlan.removeAll(context);

        if(ExtraTasks.getAllInMaps(context) != null)
            ExtraTasks.removeAll(context);

        if (Form.getAllInMaps(context) != null )
            Form.removeAll(context);

        if (SectionsForm.getAllInMaps(context) != null)
            SectionsForm.removeAll(context);

        if (QuestionsSectionForm.getAllInMaps(context) != null)
            QuestionsSectionForm.removeAll(context);


        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject obj = array.getJSONObject(i);

                CustomerWorkPlan.insert(context,obj.getString("visit_id"),
                        obj.getString("visit_status_id"),
                        obj.getString("pdv_id"),
                        obj.getString("visit_type_id"),
                        obj.getString("pdv"),
                        obj.getString("visit_status"),
                        obj.getString("visit_type"),
                        obj.getString("scheduled_start"),
                        obj.getString("real_start"),
                        obj.getString("real_end"),
                        obj.getString("latitude"),
                        obj.getString("longitude"),
                        obj.getString("address"),
                        obj.getString("pdv_rfc"),
                        obj.getString("pdv_jde"),
                        obj.getString("pdv_phone")
                );




                JSONArray jsonArray = obj.getJSONArray("tasks_generics");

                if(!jsonArray.get(0).equals(0)){
                    Log.v(TAG,jsonArray.toString());

                    for(int j = 0; j < jsonArray.length(); j++){
                        JSONObject objArray = jsonArray.getJSONObject(j);

                        if(ExtraTasks.getAllByIDExtraTask(context,objArray.getString("id_generic_task")) == null) { //Si la tarea_extra ya esta en la BD no hay que agregarlo

                            ExtraTasks.insert(context,
                                objArray.getString("id_generic_task"),
                                objArray.getString("subtitle"),
                                objArray.getString("description"),
                                objArray.getString("obligatory"),
                                objArray.getString("order"),
                                objArray.getString("date_start"),
                                objArray.getString("date_final"),
                                objArray.getString("date_end"),
                                objArray.getString("frm_id_form"),
                                objArray.getString("vi_id_visit"),
                                objArray.getString("date_final_allocation"));

                        }
                    }


                }
                /*
                if(jsonArray.get(0).equals(0))
                Log.v(TAG,"vacio");
                else
                    Log.v(TAG,jsonArray.toString());
                */

                //Log.d(TAG, "inserting");
            } catch (JSONException e) {
                e.printStackTrace();}
        }
    }

    public static void setVisitAsStarted(Context context,String visit_id){
        ContentValues cv = new ContentValues();
        cv.put(VISIT_STATUS, "En Proceso");
        cv.put(VISIT_STATUS_ID, "2");

        DataBaseAdapter.getDB(context).update(TABLE, cv, ID + "=" + visit_id, null);
    }

    public static void setVisitAsEnded(Context context,String visit_id){
        ContentValues cv = new ContentValues();
        cv.put(VISIT_STATUS, "Terminada");
        cv.put(VISIT_STATUS_ID, "3");

        DataBaseAdapter.getDB(context).update(TABLE, cv, ID + "=" + visit_id, null);
    }

    public static void removeAll(Context context){
        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, null, null, null ,null, ID);
        if (cursor != null && cursor.getCount() > 0) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                int id              = cursor.getInt(cursor.getColumnIndexOrThrow(ID));
                CustomerWorkPlan.delete(context,id);
            }
        }
        cursor.close();
    }

    public static int delete(Context context, int id) {
        return DataBaseAdapter.getDB(context).delete(TABLE, ID + "=" + id, null);
    }

}
