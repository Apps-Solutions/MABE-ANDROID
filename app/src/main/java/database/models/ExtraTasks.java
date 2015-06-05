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
import java.util.List;
import java.util.Map;

import database.DataBaseAdapter;

/**
 * Created by hugo.figueroa on 28/04/15.
 */
public class ExtraTasks extends Table{


    public static final String TAG = "extra_tasks_table";

    public static final String TABLE = "extra_tasks";

    public static final String ID_GENERIC_TASK            = "id_generic_task";

    public static final String SUBTITLE                   = "subtitle";
    public static final String DESCRIPTION                = "description";
    public static final String OBLIGATORY                 = "obligatory";
    public static final String ORDER                      = "id_order";
    public static final String DATE_START                 = "date_start";
    public static final String DATE_FINAL                 = "date_final";
    public static final String DATE_END                   = "date_end";
    public static final String FRM_ID_FORM                = "frm_id_form";
    public static final String VI_ID_VISIT                = "vi_id_visit";
    public static final String DATE_FINAL_ALLOCATION      = "date_final_allocation";
    public static final String OBSERVATIONS               = "observations";
    public static final String EVIDENCE                   = "evidence";
    public static final String STATUS                     = "status";

    public static long insert(Context context, String id_generic_task,String subtitle, String description, String obligatory,
                              String id_order, String date_start, String date_final, String date_end,
                              String frm_id_form,String vi_id_visit,String date_final_allocation) {
        ContentValues cv = new ContentValues();

        cv.put(ID_GENERIC_TASK, id_generic_task);
        cv.put(SUBTITLE, subtitle);
        cv.put(DESCRIPTION, description);
        cv.put(OBLIGATORY, obligatory);
        cv.put(ORDER, id_order);
        cv.put(DATE_START, date_start);
        cv.put(DATE_FINAL, date_final);
        cv.put(DATE_END, date_end);
        cv.put(FRM_ID_FORM, frm_id_form);
        cv.put(VI_ID_VISIT, vi_id_visit);
        cv.put(DATE_FINAL_ALLOCATION, date_final_allocation);

        cv.put(OBSERVATIONS, "");
        cv.put(EVIDENCE, "");
        cv.put(STATUS,      "NO_SENT");

        return DataBaseAdapter.getDB(context).insert(TABLE, null, cv);
    }


    public static Map<String,String> getObservationByIDTask(Context context, String id_generic_task) {

        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, ID_GENERIC_TASK + "=" + id_generic_task, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {

            Map<String,String> map  = new HashMap<String, String>();
            map.put(OBSERVATIONS, cursor.getString(cursor.getColumnIndexOrThrow(OBSERVATIONS)));

            cursor.close();

            return map;
        }
        return null;
    }

    public static Map<String,String> getEvidenceByIDTask(Context context, String id_generic_task) {

        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, ID_GENERIC_TASK + "=" + id_generic_task, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {

            Map<String,String> map  = new HashMap<String, String>();
            map.put(EVIDENCE, cursor.getString(cursor.getColumnIndexOrThrow(EVIDENCE)));

            cursor.close();

            return map;
        }
        return null;
    }

    public static Map<String,String> getIDFormByIDTask(Context context, String id_generic_task) {

        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, ID_GENERIC_TASK + "=" + id_generic_task, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {

            Map<String,String> map  = new HashMap<String, String>();
            map.put(FRM_ID_FORM, cursor.getString(cursor.getColumnIndexOrThrow(FRM_ID_FORM)));

            cursor.close();

            return map;
        }
        return null;
    }



    public static void updateExtraTasksToStatusSent(Context context, String id_generic_task){
        ContentValues cv = new ContentValues();
        cv.put(STATUS, "SENT");
        DataBaseAdapter.getDB(context).update(TABLE, cv, ID_GENERIC_TASK + "=" + id_generic_task, null);
    }


    public static ArrayList<Map<String,String>> getAllExtraTasksNoSent(Context context){
        ArrayList<Map<String,String>> list = new ArrayList<Map<String,String>>();
        Cursor cursor = DataBaseAdapter.getDB(context).rawQuery("SELECT * FROM extra_tasks WHERE status = ?; ", new String[] {"NO_SENT"});
        if (cursor != null && cursor.getCount() > 0) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                Map<String,String> map_task = new HashMap<String, String>();

                map_task.put(ID_GENERIC_TASK,cursor.getString(cursor.getColumnIndexOrThrow(ID_GENERIC_TASK)));
                map_task.put(SUBTITLE,cursor.getString(cursor.getColumnIndexOrThrow(SUBTITLE)));
                map_task.put(DESCRIPTION,cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION)));
                map_task.put(OBLIGATORY,cursor.getString(cursor.getColumnIndexOrThrow(OBLIGATORY)));
                map_task.put(ORDER,cursor.getString(cursor.getColumnIndexOrThrow(ORDER)));
                map_task.put(DATE_START,cursor.getString(cursor.getColumnIndexOrThrow(DATE_START)));
                map_task.put(DATE_FINAL,cursor.getString(cursor.getColumnIndexOrThrow(DATE_FINAL)));
                map_task.put(DATE_END,cursor.getString(cursor.getColumnIndexOrThrow(DATE_END)));
                map_task.put(FRM_ID_FORM,cursor.getString(cursor.getColumnIndexOrThrow(FRM_ID_FORM)));
                map_task.put(VI_ID_VISIT,cursor.getString(cursor.getColumnIndexOrThrow(VI_ID_VISIT)));
                map_task.put(DATE_FINAL_ALLOCATION,cursor.getString(cursor.getColumnIndexOrThrow(DATE_FINAL_ALLOCATION)));
                map_task.put(OBSERVATIONS,cursor.getString(cursor.getColumnIndexOrThrow(OBSERVATIONS)));
                map_task.put(EVIDENCE,cursor.getString(cursor.getColumnIndexOrThrow(EVIDENCE)));
                list.add(map_task);
            }
            cursor.close();
            return list;
        }
        return null;
    }

    public static Map<String,String> getExtraTasksNoSentInVisit(Context context, String id_generic_task){
        Cursor cursor = DataBaseAdapter.getDB(context).rawQuery("SELECT * FROM extra_tasks WHERE id_generic_task = ? AND status = ?; ", new String[] {id_generic_task,"NO_SENT"});
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            Map<String,String> map_task = new HashMap<String, String>();

            map_task.put(ID_GENERIC_TASK,cursor.getString(cursor.getColumnIndexOrThrow(ID_GENERIC_TASK)));
            map_task.put(SUBTITLE,cursor.getString(cursor.getColumnIndexOrThrow(SUBTITLE)));
            map_task.put(DESCRIPTION,cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION)));
            map_task.put(OBLIGATORY,cursor.getString(cursor.getColumnIndexOrThrow(OBLIGATORY)));
            map_task.put(ORDER,cursor.getString(cursor.getColumnIndexOrThrow(ORDER)));
            map_task.put(DATE_START,cursor.getString(cursor.getColumnIndexOrThrow(DATE_START)));
            map_task.put(DATE_FINAL,cursor.getString(cursor.getColumnIndexOrThrow(DATE_FINAL)));
            map_task.put(DATE_END,cursor.getString(cursor.getColumnIndexOrThrow(DATE_END)));
            map_task.put(FRM_ID_FORM,cursor.getString(cursor.getColumnIndexOrThrow(FRM_ID_FORM)));
            map_task.put(VI_ID_VISIT,cursor.getString(cursor.getColumnIndexOrThrow(VI_ID_VISIT)));
            map_task.put(DATE_FINAL_ALLOCATION,cursor.getString(cursor.getColumnIndexOrThrow(DATE_FINAL_ALLOCATION)));
            map_task.put(OBSERVATIONS,cursor.getString(cursor.getColumnIndexOrThrow(OBSERVATIONS)));
            map_task.put(EVIDENCE,cursor.getString(cursor.getColumnIndexOrThrow(EVIDENCE)));

            cursor.close();
            return map_task;
        }
        return null;
    }

    public static void updateObservationByIDTask(Context context, String id_generic_task,String observation){
        ContentValues cv = new ContentValues();
        cv.put(OBSERVATIONS, observation);
        DataBaseAdapter.getDB(context).update(TABLE,cv,ID_GENERIC_TASK + " = ?", new String[]{id_generic_task});
    }

    public static void updateEvidenceByIDTask(Context context, String id_generic_task,String evidence){
        ContentValues cv = new ContentValues();
        cv.put(EVIDENCE, evidence);
        DataBaseAdapter.getDB(context).update(TABLE,cv,ID_GENERIC_TASK + " = ?", new String[]{id_generic_task});
    }



    public static ArrayList<Map<String,String>> getAllInMaps(Context context){

        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, null, null, null ,null, ID_GENERIC_TASK);
        if (cursor != null && cursor.getCount() > 0) {
            ArrayList<Map<String,String>> list = new ArrayList<Map<String,String>>();

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                Map<String,String> map_task  = new HashMap<String, String>();

                map_task.put(ID_GENERIC_TASK,cursor.getString(cursor.getColumnIndexOrThrow(ID_GENERIC_TASK)));
                map_task.put(SUBTITLE,cursor.getString(cursor.getColumnIndexOrThrow(SUBTITLE)));
                map_task.put(DESCRIPTION,cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION)));
                map_task.put(OBLIGATORY,cursor.getString(cursor.getColumnIndexOrThrow(OBLIGATORY)));
                map_task.put(ORDER,cursor.getString(cursor.getColumnIndexOrThrow(ORDER)));
                map_task.put(DATE_START,cursor.getString(cursor.getColumnIndexOrThrow(DATE_START)));
                map_task.put(DATE_FINAL,cursor.getString(cursor.getColumnIndexOrThrow(DATE_FINAL)));
                map_task.put(DATE_END,cursor.getString(cursor.getColumnIndexOrThrow(DATE_END)));
                map_task.put(FRM_ID_FORM,cursor.getString(cursor.getColumnIndexOrThrow(FRM_ID_FORM)));
                map_task.put(VI_ID_VISIT,cursor.getString(cursor.getColumnIndexOrThrow(VI_ID_VISIT)));
                map_task.put(DATE_FINAL_ALLOCATION,cursor.getString(cursor.getColumnIndexOrThrow(DATE_FINAL_ALLOCATION)));
                map_task.put(OBSERVATIONS,cursor.getString(cursor.getColumnIndexOrThrow(OBSERVATIONS)));
                map_task.put(EVIDENCE,cursor.getString(cursor.getColumnIndexOrThrow(EVIDENCE)));
                list.add(map_task);
            }

            cursor.close();
            return list;
        }
        return null;
    }

    public static ArrayList<Map<String,String>> getAllIDFormExtraTasks(Context context){

        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, null, null, null ,null, ID_GENERIC_TASK);
        if (cursor != null && cursor.getCount() > 0) {
            ArrayList<Map<String,String>> list = new ArrayList<Map<String,String>>();

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                Map<String,String> map_task  = new HashMap<String, String>();

                map_task.put(ID_GENERIC_TASK,cursor.getString(cursor.getColumnIndexOrThrow(ID_GENERIC_TASK)));
                map_task.put(SUBTITLE,cursor.getString(cursor.getColumnIndexOrThrow(SUBTITLE)));
                map_task.put(DESCRIPTION,cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION)));
                map_task.put(OBLIGATORY,cursor.getString(cursor.getColumnIndexOrThrow(OBLIGATORY)));
                map_task.put(ORDER,cursor.getString(cursor.getColumnIndexOrThrow(ORDER)));
                map_task.put(DATE_START,cursor.getString(cursor.getColumnIndexOrThrow(DATE_START)));
                map_task.put(DATE_FINAL,cursor.getString(cursor.getColumnIndexOrThrow(DATE_FINAL)));
                map_task.put(DATE_END,cursor.getString(cursor.getColumnIndexOrThrow(DATE_END)));
                map_task.put(FRM_ID_FORM,cursor.getString(cursor.getColumnIndexOrThrow(FRM_ID_FORM)));
                map_task.put(VI_ID_VISIT,cursor.getString(cursor.getColumnIndexOrThrow(VI_ID_VISIT)));
                map_task.put(DATE_FINAL_ALLOCATION,cursor.getString(cursor.getColumnIndexOrThrow(DATE_FINAL_ALLOCATION)));
                map_task.put(OBSERVATIONS,cursor.getString(cursor.getColumnIndexOrThrow(OBSERVATIONS)));
                map_task.put(EVIDENCE,cursor.getString(cursor.getColumnIndexOrThrow(EVIDENCE)));
                list.add(map_task);
            }

            cursor.close();
            return list;
        }
        return null;
    }



    public static Map<String,String> getAllByIDExtraTask(Context context, String id_generic_task) {

        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, ID_GENERIC_TASK + "=" + id_generic_task, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {

            Map<String,String> map_task  = new HashMap<String, String>();

            map_task.put(ID_GENERIC_TASK,cursor.getString(cursor.getColumnIndexOrThrow(ID_GENERIC_TASK)));
            map_task.put(SUBTITLE,cursor.getString(cursor.getColumnIndexOrThrow(SUBTITLE)));
            map_task.put(DESCRIPTION,cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION)));
            map_task.put(OBLIGATORY,cursor.getString(cursor.getColumnIndexOrThrow(OBLIGATORY)));
            map_task.put(ORDER,cursor.getString(cursor.getColumnIndexOrThrow(ORDER)));
            map_task.put(DATE_START,cursor.getString(cursor.getColumnIndexOrThrow(DATE_START)));
            map_task.put(DATE_FINAL,cursor.getString(cursor.getColumnIndexOrThrow(DATE_FINAL)));
            map_task.put(DATE_END,cursor.getString(cursor.getColumnIndexOrThrow(DATE_END)));
            map_task.put(FRM_ID_FORM,cursor.getString(cursor.getColumnIndexOrThrow(FRM_ID_FORM)));
            map_task.put(VI_ID_VISIT,cursor.getString(cursor.getColumnIndexOrThrow(VI_ID_VISIT)));
            map_task.put(DATE_FINAL_ALLOCATION,cursor.getString(cursor.getColumnIndexOrThrow(DATE_FINAL_ALLOCATION)));
            map_task.put(OBSERVATIONS,cursor.getString(cursor.getColumnIndexOrThrow(OBSERVATIONS)));
            map_task.put(EVIDENCE,cursor.getString(cursor.getColumnIndexOrThrow(EVIDENCE)));

            cursor.close();

            return map_task;
        }
        return null;
    }

    public static List<Map<String,String>> getExtraTasksByVisit(Context context, String visit_id) {

        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, VI_ID_VISIT + "=" + visit_id, null, null, null, ORDER);
        if (cursor != null && cursor.getCount() > 0) {
            List<Map<String, String>> list = new ArrayList<Map<String, String>>();

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {


                Map<String, String> map_task = new HashMap<String, String>();

                map_task.put(ID_GENERIC_TASK, cursor.getString(cursor.getColumnIndexOrThrow(ID_GENERIC_TASK)));
                map_task.put(SUBTITLE,cursor.getString(cursor.getColumnIndexOrThrow(SUBTITLE)));
                map_task.put(DESCRIPTION, cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION)));
                map_task.put(OBLIGATORY, cursor.getString(cursor.getColumnIndexOrThrow(OBLIGATORY)));
                map_task.put(ORDER, cursor.getString(cursor.getColumnIndexOrThrow(ORDER)));
                map_task.put(DATE_START, cursor.getString(cursor.getColumnIndexOrThrow(DATE_START)));
                map_task.put(DATE_FINAL, cursor.getString(cursor.getColumnIndexOrThrow(DATE_FINAL)));
                map_task.put(DATE_END, cursor.getString(cursor.getColumnIndexOrThrow(DATE_END)));
                map_task.put(FRM_ID_FORM, cursor.getString(cursor.getColumnIndexOrThrow(FRM_ID_FORM)));
                map_task.put(DATE_FINAL_ALLOCATION, cursor.getString(cursor.getColumnIndexOrThrow(DATE_FINAL_ALLOCATION)));
                map_task.put(OBSERVATIONS,cursor.getString(cursor.getColumnIndexOrThrow(OBSERVATIONS)));
                map_task.put(EVIDENCE,cursor.getString(cursor.getColumnIndexOrThrow(EVIDENCE)));
                map_task.put(STATUS,cursor.getString(cursor.getColumnIndexOrThrow(STATUS)));
                list.add(map_task);

            }

            cursor.close();
            return list;

        }
        return null;
    }

    public static Map<String,String> getObservationandEvidenceByIDExtraTask(Context context, String id_generic_task) {

        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, ID_GENERIC_TASK + "=" + id_generic_task, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {

            Map<String,String> map_task  = new HashMap<String, String>();
            map_task.put(OBSERVATIONS,cursor.getString(cursor.getColumnIndexOrThrow(OBSERVATIONS)));
            map_task.put(EVIDENCE,cursor.getString(cursor.getColumnIndexOrThrow(EVIDENCE)));

            cursor.close();

            return map_task;
        }
        return null;
    }


    public static void removeAll(Context context){
        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, null, null, null ,null, ID_GENERIC_TASK);
        if (cursor != null && cursor.getCount() > 0) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                int id              = cursor.getInt(cursor.getColumnIndexOrThrow(ID_GENERIC_TASK));
                ExtraTasks.delete(context,id);
            }
        }
        cursor.close();
    }

    public static int delete(Context context, int id) {
        return DataBaseAdapter.getDB(context).delete(TABLE, ID_GENERIC_TASK + "=" + id, null);
    }

}
