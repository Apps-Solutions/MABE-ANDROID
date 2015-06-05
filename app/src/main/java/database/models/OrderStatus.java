package database.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import database.DataBaseAdapter;

/**
 * Created by juanc.jimenez on 18/07/14.
 */
public class OrderStatus extends Table {

    public static final String TABLE = "order_status";

    public static final String ID = "id";
    public static final String STATUS = "status";


    public static long insert(Context context, String status) {
        ContentValues cv = new ContentValues();
        cv.put(STATUS, status);

        return DataBaseAdapter.getDB(context).insert(TABLE, null, cv);
    }

    public static String getStatus(Context context, int id) {

        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, ID + "=" + id, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String status = cursor.getString(cursor.getColumnIndexOrThrow(STATUS));
            cursor.close();

            return status;
        }
        return null;
    }
}
