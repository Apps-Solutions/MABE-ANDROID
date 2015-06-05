package database.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import database.DataBaseAdapter;

/**
 * Created by jcenteno on 15/05/14.
 */
public class Module extends Table {

    public static final String TABLE = "module";

    public static final String ID = "id";
    public static final String NAME = "name";

    public static long insert(Context context, int id, String name) {
        ContentValues cv = new ContentValues();
        cv.put(ID, id);
        cv.put(NAME, name);

        return DataBaseAdapter.getDB(context).insert(TABLE, null, cv);
    }

    public static boolean exists(Context context, String name) {

        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, NAME + "=" + name, null, null, null, null);

        return cursor != null && cursor.moveToFirst();
    }

    public static String getName(Context context, int id) {

        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, ID + "=" + id, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {

            String name = cursor.getString(cursor.getColumnIndexOrThrow(NAME));
            cursor.close();
            return name;
        }
        return null;
    }

    public static int getId(Context context, String name) {

        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, NAME + "=?", new String[]{name}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {

            int id = cursor.getInt(cursor.getColumnIndexOrThrow(ID));
            cursor.close();
            return id;
        }
        return -1;
    }

}
