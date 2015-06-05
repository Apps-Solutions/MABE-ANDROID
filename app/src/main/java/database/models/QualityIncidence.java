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

public class QualityIncidence extends Table{

    public static final String TAG = "quality_incidence_table";

    public static final String TABLE = "quality_incidence";

    public static final String ID                   = "id";
    public static final String ID_VISIT             = "id_visit";
    public static final String ID_PRODUCT           = "id_product";
    public static final String QUANTITY             = "quantity";
    public static final String PRODUCT_LINE         = "product_line";
    public static final String ID_PROBLEM_TYPE      = "id_problem_type";
    public static final String DESCRIPTION          = "description";
    public static final String BATCH                = "batch";
    public static final String EVIDENCE             = "evidence";

    public static final String NAME_PROBLEM_TYPE    = "name_problem_type";
    public static final String STATUS               = "status";


    public static long insert(Context context, String id_visit, String id_product, String id_problem_type,String name_problem_type,
                              String description,String batch, String product_line,
                              String quantity, String evidence){
        ContentValues cv = new ContentValues();
        cv.put(ID_VISIT, id_visit);
        cv.put(ID_PRODUCT, id_product);
        cv.put(ID_PROBLEM_TYPE, id_problem_type);
        cv.put(NAME_PROBLEM_TYPE, name_problem_type);

        cv.put(DESCRIPTION, description);
        cv.put(BATCH, batch);
        cv.put(PRODUCT_LINE, product_line);
        cv.put(QUANTITY, quantity);
        cv.put(EVIDENCE, evidence);
        cv.put(STATUS,"NO_SENT");

        return DataBaseAdapter.getDB(context).insert(TABLE, null, cv);
    }

    public static int delete(Context context, int id) {
        return DataBaseAdapter.getDB(context).delete(TABLE, ID + "=" + id, null);
    }

    public static List<Map<String,String>> getAllNoSentQualityIncidencesInVisit(Context context, String id_visit) {

        ArrayList<Map<String,String>> list = new ArrayList<Map<String,String>>();

        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, "id_visit = ? AND status=?",
                new String[] { id_visit,"NO_SENT"},
                null,
                null,
                ID_PRODUCT);

        if (cursor != null && cursor.getCount() != 0)
        {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                Map<String, String> map = new HashMap<String, String>();
                map.put(ID, cursor.getString(cursor.getColumnIndexOrThrow(ID)));
                map.put(ID_PRODUCT, cursor.getString(cursor.getColumnIndexOrThrow(ID_PRODUCT)));
                map.put(QUANTITY, cursor.getString(cursor.getColumnIndexOrThrow(QUANTITY)));
                map.put(PRODUCT_LINE, cursor.getString(cursor.getColumnIndexOrThrow(PRODUCT_LINE)));
                map.put(ID_PROBLEM_TYPE, cursor.getString(cursor.getColumnIndexOrThrow(ID_PROBLEM_TYPE)));
                map.put(DESCRIPTION, cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION)));
                map.put(BATCH, cursor.getString(cursor.getColumnIndexOrThrow(BATCH)));
                map.put(EVIDENCE, cursor.getString(cursor.getColumnIndexOrThrow(EVIDENCE)));
                list.add(map);
            }
            cursor.close();
        }
        return list;
    }

    public static String getQualityProblemTypeIDInProductBatchAndVisit(Context context, String id_visit, String id_product, String batch,String id_problem_type) {

        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, "id_visit = ? AND id_product = ? AND batch = ? AND id_problem_type = ?",
                new String[] { id_visit, id_product,batch,id_problem_type},
                null,
                null,
                ID_PRODUCT);

        if (cursor != null && cursor.getCount() != 0)
        {
            cursor.moveToFirst();
            String id = cursor.getString(cursor.getColumnIndexOrThrow(ID_PROBLEM_TYPE));
            cursor.close();
            return id;
        }
        return null;
    }

    public static List<Map<String,String>> getAllNoSentQualityIncidenceProductInVisit(Context context, String id_visit,String id_product) {

        ArrayList<Map<String,String>> list = new ArrayList<Map<String,String>>();

        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, "id_visit = ? AND id_product=? AND status=?",
                new String[] { id_visit,id_product,"NO_SENT"},
                null,
                null,
                ID_PRODUCT);

        if (cursor != null && cursor.getCount() != 0)
        {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                Map<String, String> map = new HashMap<String, String>();
                map.put(ID, ""+cursor.getInt(cursor.getColumnIndexOrThrow(ID)));
                map.put(ID_PRODUCT, cursor.getString(cursor.getColumnIndexOrThrow(ID_PRODUCT)));
                map.put(ID_PROBLEM_TYPE, cursor.getString(cursor.getColumnIndexOrThrow(ID_PROBLEM_TYPE)));
                map.put(NAME_PROBLEM_TYPE, cursor.getString(cursor.getColumnIndexOrThrow(NAME_PROBLEM_TYPE)));
                map.put(DESCRIPTION, cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION)));
                map.put(BATCH, cursor.getString(cursor.getColumnIndexOrThrow(BATCH)));
                map.put(PRODUCT_LINE, cursor.getString(cursor.getColumnIndexOrThrow(PRODUCT_LINE)));
                map.put(QUANTITY, cursor.getString(cursor.getColumnIndexOrThrow(QUANTITY)));
                map.put(EVIDENCE, cursor.getString(cursor.getColumnIndexOrThrow(EVIDENCE)));

                list.add(map);
            }
            cursor.close();
        }
        return list;
    }

    public static void updateQualityIncidenceToSent(Context context, String id){
        ContentValues cv = new ContentValues();
        cv.put(STATUS, "SENT");
        DataBaseAdapter.getDB(context).update(TABLE, cv,"id = ?", new String[] {id});
    }

    public static void updateAllQualityIncidenceInVisitToSent(Context context, String id_visit){
        ContentValues cv = new ContentValues();
        cv.put(STATUS, "SENT");
        DataBaseAdapter.getDB(context).update(TABLE, cv,"id_visit = ?", new String[] {id_visit});
    }
}

