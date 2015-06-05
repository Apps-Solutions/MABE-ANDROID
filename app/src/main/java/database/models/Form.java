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
 * Created by hugo.figueroa on 29/04/15.
 */
public class Form extends Table {

    public static final String TAG = "form_table";

    public static final String TABLE = "form";

    public static final String ID               = "id";
    public static final String ID_GENERIC_TASK  = "id_generic_task";
    public static final String ID_FORM          = "id_form";
    public static final String FORM             = "form";
    public static final String DESCRIPTION      = "description";
    public static final String EXPIRATION       = "expiration";
    public static final String ID_FORM_TYPE     = "id_form_type";
    public static final String FORM_TYPE        = "form_type";
    public static final String TIMESTAMP        = "timestamp";
    public static final String COMMENT          = "comment";
    public static final String ANSWERS          = "answers";

    public static long insert(Context context, String id_generic_task, String id_form, String form, String description,
                              String expiration, String id_form_type, String form_type, String timestamp) {
        ContentValues cv = new ContentValues();


        cv.put(ID_GENERIC_TASK, id_generic_task);
        cv.put(ID_FORM, id_form);
        cv.put(FORM, form);
        cv.put(DESCRIPTION, description);
        cv.put(EXPIRATION, expiration);
        cv.put(ID_FORM_TYPE, id_form_type);
        cv.put(FORM_TYPE, form_type);
        cv.put(TIMESTAMP, timestamp);

        cv.put(COMMENT, "");
        cv.put(ANSWERS, "");

        return DataBaseAdapter.getDB(context).insert(TABLE, null, cv);
    }


    public static void updateAnswersTimestampCommentToIDForm(Context context,String id_generic_task, String id_form, String answers, String timestamp, String comment){
        ContentValues cv = new ContentValues();
        cv.put(ANSWERS, answers);
        cv.put(TIMESTAMP, timestamp);
        cv.put(COMMENT, comment);
        DataBaseAdapter.getDB(context).update(TABLE, cv, ID_FORM + "=" + id_form + " and " + ID_GENERIC_TASK + "=" + id_generic_task, null);
    }

    public static Map<String,String> getTimestamByIDFormandIDTask(Context context, String id_generic_task, String id_form) {

        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, "id_generic_task = ? AND id_form = ?", new String[] { id_generic_task,id_form}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {

            Map<String,String> map  = new HashMap<String, String>();
            map.put(TIMESTAMP, cursor.getString(cursor.getColumnIndexOrThrow(TIMESTAMP)));

            cursor.close();

            return map;
        }
        return null;
    }


    public static Map<String,String> getIDFormByIDTask(Context context, String id_generic_task){

        //Cursor cursor = DataBaseAdapter.getDB(context).rawQuery("SELECT id_form FROM form WHERE id_generic_task = ?; ", new String[] {id_generic_task});
        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, ID_GENERIC_TASK + "=" + id_generic_task, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {

            Map<String,String> map  = new HashMap<String, String>();
            map.put(ID_FORM,cursor.getString(cursor.getColumnIndexOrThrow(ID_FORM)));
            cursor.close();

            return map;
        }
        return null;
    }



    public static Map<String,String> getCommentByIDForm(Context context,String id_generic_task, String id_form) {

        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, "id_generic_task = ? AND id_form = ?", new String[] { id_generic_task,id_form}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {

            Map<String,String> map  = new HashMap<String, String>();
            map.put(COMMENT, cursor.getString(cursor.getColumnIndexOrThrow(COMMENT)));

            cursor.close();

            return map;
        }
        return null;
    }

    public static Map<String,String> getAnswersByIDForm(Context context, String id_generic_task, String id_form) {

        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, "id_generic_task = ? AND id_form = ?", new String[] { id_generic_task,id_form}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {

            Map<String,String> map  = new HashMap<String, String>();
            map.put(ANSWERS, cursor.getString(cursor.getColumnIndexOrThrow(ANSWERS)));

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

                map.put(ID_GENERIC_TASK,cursor.getString(cursor.getColumnIndexOrThrow(ID_GENERIC_TASK)));
                map.put(ID_FORM,cursor.getString(cursor.getColumnIndexOrThrow(ID_FORM)));
                map.put(FORM,cursor.getString(cursor.getColumnIndexOrThrow(FORM)));
                map.put(DESCRIPTION,cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION)));
                map.put(EXPIRATION,cursor.getString(cursor.getColumnIndexOrThrow(EXPIRATION)));
                map.put(ID_FORM_TYPE,cursor.getString(cursor.getColumnIndexOrThrow(ID_FORM_TYPE)));
                map.put(FORM_TYPE,cursor.getString(cursor.getColumnIndexOrThrow(FORM_TYPE)));
                map.put(TIMESTAMP,cursor.getString(cursor.getColumnIndexOrThrow(TIMESTAMP)));
                map.put(COMMENT,cursor.getString(cursor.getColumnIndexOrThrow(COMMENT)));
                map.put(ANSWERS,cursor.getString(cursor.getColumnIndexOrThrow(ANSWERS)));
                list.add(map);
            }

            cursor.close();
            return list;
        }
        return null;
    }


    public static void insertFormFromJSONObject(Context context, String array,String id_generic_task){

            try {

                JSONObject objArrayForm = new JSONObject(array);


                    Form.insert(context, id_generic_task,
                            objArrayForm.getString("id_form"),
                            objArrayForm.getString("form"),
                            objArrayForm.getString("description"),
                            objArrayForm.getString("expiration"),
                            objArrayForm.getString("id_form_type"),
                            objArrayForm.getString("form_type"),
                            objArrayForm.getString("timestamp"));


                    JSONArray jsonArraySections = objArrayForm.getJSONArray("content");
                    if(!jsonArraySections.get(0).equals(0)){


                        for(int j = 0; j < jsonArraySections.length(); j++){

                            JSONObject objArraySection = jsonArraySections.getJSONObject(j);
                            Log.v("SECCION: "+j, objArraySection.toString());

                            SectionsForm.insert(context,
                                    objArraySection.getString("id_section"),
                                    objArraySection.getString("title"),
                                    objArraySection.getString("description"),
                                    objArrayForm.getString("id_form"),
                                    id_generic_task);


                            JSONArray jsonArrayQuestions = objArraySection.getJSONArray("questions");
                            if(!jsonArraySections.get(0).equals(0)){


                                for(int k = 0; k < jsonArrayQuestions.length(); k++){

                                    JSONObject objArrayQuestion = jsonArrayQuestions.getJSONObject(k);
                                    Log.v("PREGUNTA: "+k, objArrayQuestion.toString());

                                    QuestionsSectionForm.insert(context,
                                            objArrayQuestion.getString("id_question"),
                                            objArrayQuestion.getString("id_question_type"),
                                            objArrayQuestion.getString("question_type"),
                                            objArrayQuestion.getString("order"),
                                            objArrayQuestion.getString("question"),
                                            objArrayQuestion.getString("options"),
                                            objArrayQuestion.getString("correct"),
                                            objArrayQuestion.getString("weight"),
                                            objArrayQuestion.getString("answer"),
                                            id_generic_task,
                                            objArraySection.getString("id_section"));

                                    Log.v("TIPO DE PREGUNTA: "+k, objArrayQuestion.getString("question_type"));


                                }
                            }

                        }


                    }



                //Log.d(TAG, "inserting");
            } catch (JSONException e) {
                e.printStackTrace();}

    }

    public static List<Map<String,String>> getAllByIDFormandIDTask(Context context, String id_generic_task, String id_form) {

        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, "id_generic_task = ? AND id_form = ?", new String[] { id_generic_task,id_form}, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            ArrayList<Map<String,String>> list = new ArrayList<Map<String,String>>();

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                Map<String,String> map  = new HashMap<String, String>();

                map.put(ID_GENERIC_TASK,cursor.getString(cursor.getColumnIndexOrThrow(ID_GENERIC_TASK)));
                map.put(ID_FORM,cursor.getString(cursor.getColumnIndexOrThrow(ID_FORM)));
                map.put(FORM,cursor.getString(cursor.getColumnIndexOrThrow(FORM)));
                map.put(DESCRIPTION,cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION)));
                map.put(EXPIRATION,cursor.getString(cursor.getColumnIndexOrThrow(EXPIRATION)));
                map.put(ID_FORM_TYPE,cursor.getString(cursor.getColumnIndexOrThrow(ID_FORM_TYPE)));
                map.put(FORM_TYPE,cursor.getString(cursor.getColumnIndexOrThrow(FORM_TYPE)));
                map.put(TIMESTAMP,cursor.getString(cursor.getColumnIndexOrThrow(TIMESTAMP)));
                map.put(COMMENT,cursor.getString(cursor.getColumnIndexOrThrow(COMMENT)));
                map.put(ANSWERS,cursor.getString(cursor.getColumnIndexOrThrow(ANSWERS)));

                list.add(map);
            }

            cursor.close();
            return list;
        }
        return null;
    }



    public static void removeAll(Context context){
        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, null, null, null ,null, ID);
        if (cursor != null && cursor.getCount() > 0) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                int id              = cursor.getInt(cursor.getColumnIndexOrThrow(ID));
                Form.delete(context, id);
            }
        }
        cursor.close();
    }

    public static int delete(Context context, int id) {
        return DataBaseAdapter.getDB(context).delete(TABLE, ID + "=" + id, null);
    }

}
