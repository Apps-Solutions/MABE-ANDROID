package database.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import database.DataBaseAdapter;

/**
 * Created by juanc.jimenez on 09/09/14.
 */
public class Evidence extends Table {

    public static final String TABLE = "evidence";

    public static final String ID = "id";
    public static final String NAME = "name";

    private int id;
    private String name;

    public Evidence(int id, String name){
        this.id = id;
        this.name = name;
    }

    public static long insert(Context context, String name, int brand_id) {
        ContentValues cv = new ContentValues();
        cv.put(NAME, name);

        return DataBaseAdapter.getDB(context).insert(TABLE, null, cv);
    }

    public static List<Evidence> getAll(Context context) {

        List<Evidence> evidence = new ArrayList<Evidence>();
        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, null, null, null, null, null);

        if (cursor != null) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                int id = cursor.getInt(cursor.getColumnIndexOrThrow(ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(NAME));

                evidence.add(new Evidence(id, name));
            }
        }
        return evidence;
    }

    public static int delete(Context context, int id) {
        return DataBaseAdapter.getDB(context).delete(TABLE, ID + "=" + id, null);
    }

    public String getName(){
        return name;
    }

    public int getId(){
        return id;
    }
}
