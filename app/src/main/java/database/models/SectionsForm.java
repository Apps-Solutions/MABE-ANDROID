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
public class SectionsForm extends Table {

    public static final String TAG = "sections_form_table";

    public static final String TABLE = "sections_form";

    public static final String ID               = "id";
    public static final String ID_SECTION       = "id_section";
    public static final String TITLE            = "title";
    public static final String DESCRIPTION      = "description";
    public static final String ID_FORM          = "id_form";
    public static final String ID_GENERIC_TASK  = "id_generic_task";

    public static long insert(Context context, String id_section, String title, String description,
                              String id_form, String id_generic_task) {
        ContentValues cv = new ContentValues();

        cv.put(ID_SECTION, id_section);
        cv.put(TITLE, title);
        cv.put(DESCRIPTION, description);
        cv.put(ID_FORM, id_form);
        cv.put(ID_GENERIC_TASK, id_generic_task);

        return DataBaseAdapter.getDB(context).insert(TABLE, null, cv);
    }


    public static ArrayList<Map<String,String>> getAllInMaps(Context context){

        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, null, null, null ,null, ID_SECTION);
        if (cursor != null && cursor.getCount() > 0) {
            ArrayList<Map<String,String>> list = new ArrayList<Map<String,String>>();

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                Map<String,String> map  = new HashMap<String, String>();

                map.put(ID_SECTION,cursor.getString(cursor.getColumnIndexOrThrow(ID_SECTION)));
                map.put(TITLE,cursor.getString(cursor.getColumnIndexOrThrow(TITLE)));
                map.put(DESCRIPTION,cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION)));
                map.put(ID_FORM,cursor.getString(cursor.getColumnIndexOrThrow(ID_FORM)));
                map.put(ID_GENERIC_TASK,cursor.getString(cursor.getColumnIndexOrThrow(ID_GENERIC_TASK)));
                list.add(map);
            }

            cursor.close();
            return list;
        }
        return null;
    }


    public static int getCountSectionsByIDFormandIDTask(Context context, String id_generic_task ,String id_form){

        int size = 0;
        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, "id_generic_task = ? AND id_form = ?", new String[] { id_generic_task,id_form}, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {

            size = cursor.getCount();
            return size;
        }
        return size;
    }


    public static ArrayList<Map<String,String>> getAllByIDFormandIDTask(Context context, String id_generic_task ,String id_form) {

        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, "id_generic_task = ? AND id_form = ?", new String[] { id_generic_task,id_form}, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            ArrayList<Map<String,String>> list = new ArrayList<Map<String,String>>();

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                Map<String, String> map = new HashMap<String, String>();

                map.put(ID_SECTION, cursor.getString(cursor.getColumnIndexOrThrow(ID_SECTION)));
                map.put(TITLE, cursor.getString(cursor.getColumnIndexOrThrow(TITLE)));
                map.put(DESCRIPTION, cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION)));
                map.put(ID_FORM, cursor.getString(cursor.getColumnIndexOrThrow(ID_FORM)));
                map.put(ID_GENERIC_TASK,cursor.getString(cursor.getColumnIndexOrThrow(ID_GENERIC_TASK)));
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
                SectionsForm.delete(context, id);
            }
        }
        cursor.close();
    }

    public static int delete(Context context, int id) {
        return DataBaseAdapter.getDB(context).delete(TABLE, ID + "=" + id, null);
    }

}
