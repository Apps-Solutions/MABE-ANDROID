package database.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import database.DataBaseAdapter;

/**
 * Created by hugo.figueroa on 29/04/15.
 */
public class QuestionsSectionForm extends Table {

    public static final String TAG = "questions_section_form_table";

    public static final String TABLE = "questions_section_form";

    public static final String ID               = "id";
    public static final String ID_QUESTION      = "id_question";
    public static final String ID_QUESTION_TYPE = "id_question_type";
    public static final String QUESTION_TYPE    = "question_type";
    public static final String ID_ORDER         = "id_order";
    public static final String QUESTION         = "question";
    public static final String OPTIONS          = "options";
    public static final String CORRECT          = "correct";
    public static final String WEIGHT           = "weight";
    public static final String ANSWER           = "answer";
    public static final String ID_GENERIC_TASK  = "id_generic_task";
    public static final String ID_SECTION       = "id_section";

    public static long insert(Context context, String id_question, String id_question_type,String question_type, String id_order,
                              String question, String options, String correct,String weight, String answer, String id_generic_task,
                              String id_section) {
        ContentValues cv = new ContentValues();

        cv.put(ID_QUESTION, id_question);
        cv.put(ID_QUESTION_TYPE, id_question_type);
        cv.put(QUESTION_TYPE, question_type);
        cv.put(ID_ORDER, id_order);
        cv.put(QUESTION, question);
        cv.put(OPTIONS, options);
        cv.put(CORRECT, correct);
        cv.put(WEIGHT, weight);
        cv.put(ANSWER, answer);
        cv.put(ID_GENERIC_TASK, id_generic_task);
        cv.put(ID_SECTION, id_section);

        return DataBaseAdapter.getDB(context).insert(TABLE, null, cv);
    }


    public static ArrayList<Map<String,String>> getAllInMaps(Context context){

        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, null, null, null ,null, ID_QUESTION);
        if (cursor != null && cursor.getCount() > 0) {
            ArrayList<Map<String,String>> list = new ArrayList<Map<String,String>>();

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                Map<String,String> map  = new HashMap<String, String>();

                map.put(ID_QUESTION,cursor.getString(cursor.getColumnIndexOrThrow(ID_QUESTION)));
                map.put(ID_QUESTION_TYPE,cursor.getString(cursor.getColumnIndexOrThrow(ID_QUESTION_TYPE)));
                map.put(QUESTION_TYPE,cursor.getString(cursor.getColumnIndexOrThrow(QUESTION_TYPE)));
                map.put(ID_ORDER,cursor.getString(cursor.getColumnIndexOrThrow(ID_ORDER)));
                map.put(QUESTION,cursor.getString(cursor.getColumnIndexOrThrow(QUESTION)));
                map.put(OPTIONS,cursor.getString(cursor.getColumnIndexOrThrow(OPTIONS)));
                map.put(CORRECT,cursor.getString(cursor.getColumnIndexOrThrow(CORRECT)));
                map.put(WEIGHT,cursor.getString(cursor.getColumnIndexOrThrow(WEIGHT)));
                map.put(ANSWER,cursor.getString(cursor.getColumnIndexOrThrow(ANSWER)));
                map.put(ID_GENERIC_TASK,cursor.getString(cursor.getColumnIndexOrThrow(ID_GENERIC_TASK)));
                map.put(ID_SECTION,cursor.getString(cursor.getColumnIndexOrThrow(ID_SECTION)));
                list.add(map);
            }

            cursor.close();
            return list;
        }
        return null;
    }

    public static int getCountQuestionByIDSection(Context context,String id_section){

        int size = 0;
        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, ID_SECTION + "=" + id_section, null, null, null,null);
        if (cursor != null && cursor.getCount() > 0) {

            size = cursor.getCount();
            return size;
        }
        return size;
    }


    public static Map<String,String> getIDandAnswerByIDSection(Context context, String id_section) {

        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, ID_SECTION + "=" + id_section, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {

            Map<String,String> map  = new HashMap<String, String>();
            map.put(ID_QUESTION, cursor.getString(cursor.getColumnIndexOrThrow(ID_QUESTION)));
            map.put(ANSWER, cursor.getString(cursor.getColumnIndexOrThrow(ANSWER)));

            cursor.close();

            return map;
        }
        return null;
    }

    public static ArrayList<Map<String,String>> getAllByIDSectionandIDTask(Context context, String id_section , String id_generic_task) {

        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, "id_section = ? AND id_generic_task = ?", new String[] { id_section,id_generic_task}, null, null, ID_ORDER);

        if (cursor != null && cursor.getCount() > 0) {
            ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                Map<String, String> map = new HashMap<String, String>();

                map.put(ID_QUESTION, cursor.getString(cursor.getColumnIndexOrThrow(ID_QUESTION)));
                map.put(ID_QUESTION_TYPE, cursor.getString(cursor.getColumnIndexOrThrow(ID_QUESTION_TYPE)));
                map.put(QUESTION_TYPE,cursor.getString(cursor.getColumnIndexOrThrow(QUESTION_TYPE)));
                map.put(ID_ORDER, cursor.getString(cursor.getColumnIndexOrThrow(ID_ORDER)));
                map.put(QUESTION, cursor.getString(cursor.getColumnIndexOrThrow(QUESTION)));
                map.put(OPTIONS, cursor.getString(cursor.getColumnIndexOrThrow(OPTIONS)));
                map.put(CORRECT, cursor.getString(cursor.getColumnIndexOrThrow(CORRECT)));
                map.put(WEIGHT, cursor.getString(cursor.getColumnIndexOrThrow(WEIGHT)));
                map.put(ANSWER, cursor.getString(cursor.getColumnIndexOrThrow(ANSWER)));
                map.put(ID_GENERIC_TASK,cursor.getString(cursor.getColumnIndexOrThrow(ID_GENERIC_TASK)));
                map.put(ID_SECTION, cursor.getString(cursor.getColumnIndexOrThrow(ID_SECTION)));
                list.add(map);
            }

            cursor.close();
            return list;
        }
        return null;
    }


    public static void updateAnswer(Context context, String id_question,String answer){
        ContentValues cv = new ContentValues();
        cv.put(ANSWER, answer);
        DataBaseAdapter.getDB(context).update(TABLE,cv,ID_QUESTION + " = ?", new String[]{id_question});
    }


    public static void removeAll(Context context){
        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, null, null, null ,null, ID);
        if (cursor != null && cursor.getCount() > 0) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                int id              = cursor.getInt(cursor.getColumnIndexOrThrow(ID));
                QuestionsSectionForm.delete(context, id);
            }
        }
        cursor.close();
    }

    public static int delete(Context context, int id) {
        return DataBaseAdapter.getDB(context).delete(TABLE, ID + "=" + id, null);
    }


}
