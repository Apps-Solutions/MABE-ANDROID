package database.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;

import java.util.HashMap;
import java.util.Map;

import database.DataBaseAdapter;

public class Shelf_Review extends Table {

    public static final String TABLE = "shelf_review";

    public static final String ID           = "id";
    public static final String VISIT_ID     = "visit_id";
    public static final String STATUS       = "status";

    public static long insert(Context context, String visit_id){

        ContentValues cv = new ContentValues();
        cv.put(VISIT_ID, visit_id);
        cv.put(STATUS, "NO_SENT");

        return DataBaseAdapter.getDB(context).insert(TABLE, null, cv);
    }

    public static int delete(Context context, int id) {
        return DataBaseAdapter.getDB(context).delete(TABLE, ID + "=" + id, null);
    }

    public static String getShelf_ReviewIDNoSentInVisit(Context context, String visit_id) {
        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, "visit_id = ? AND status = ?",
                new String[] { visit_id,"NO_SENT"},
                null,
                null,
                ID);

        if (cursor != null && cursor.getCount() !=0) {
            return cursor.getString(cursor.getColumnIndexOrThrow(ID));
        }
        return null;
    }
}
