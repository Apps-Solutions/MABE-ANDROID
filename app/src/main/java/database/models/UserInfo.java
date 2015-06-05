package database.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.HashMap;
import java.util.Map;

import database.DataBaseAdapter;

import static util.LogUtil.makeLogTag;

/**
 * Created by jcenteno on 13/05/14.
 */
public class UserInfo extends Table {

    private static final String TAG = makeLogTag(User.class);

    public static final String TABLE = "user_info";

    public static final String ID               = "id";
    public static final String USER             = "user";
    public static final String ROUTE            = "route";
    public static final String ID_VIAMENTE      = "id_viamente";
    public static final String ID_PROFILE       = "people_id";
    public static final String JDE              = "jde";
    public static final String NAME             = "name";
    public static final String LAST_NAME        = "lastname";
    public static final String BRANCH_NAME      = "branch_name";
    public static final String BRANCH_ADDRESS   = "branch_address";
    public static final String BRANCH_CODE      = "branch_code";

    public static long insert(Context context,   String id,
                               String username,
                               String route,
                               String id_viamente,
                               String people_id,
                               String jde,
                               String name,
                               String last_name,
                                String branch_name,
                                String branch_address,
                                String branch_code) {

        if (UserInfo.getAll(context) != null )    // Products stored in database
            UserInfo.removeAll(context);

        ContentValues cv = new ContentValues();
        cv.put(ID, id);
        cv.put(USER, username);
        cv.put(ROUTE, route);
        cv.put(ID_VIAMENTE, id_viamente);
        cv.put(ID_PROFILE, people_id);
        cv.put(JDE, jde);
        cv.put(NAME, name);
        cv.put(LAST_NAME, last_name);
        cv.put(BRANCH_NAME, branch_name);
        cv.put(BRANCH_ADDRESS, branch_address);
        cv.put(BRANCH_CODE, branch_code);

        return DataBaseAdapter.getDB(context).insert(TABLE, null, cv);
    }

/*
    public static long insert(Context context,   String id,
                              String username,
                              String route,
                              String id_viamente,
                              String people_id,
                              String jde,
                              String name,
                              String last_name) {

        if (UserInfo.getAll(context) != null )    // Products stored in database
            UserInfo.removeAll(context);
        ContentValues cv = new ContentValues();
        cv.put(ID, id);
        cv.put(USER, username);
        cv.put(ROUTE, route);
        cv.put(ID_VIAMENTE, id_viamente);
        cv.put(ID_PROFILE, people_id);
        cv.put(JDE, jde);
        cv.put(NAME, name);
        cv.put(LAST_NAME, last_name);

        return DataBaseAdapter.getDB(context).insert(TABLE, null, cv);
    }
*/
    public static Cursor getAll(Context context) {
        Cursor mC = null;
        mC = DataBaseAdapter.getDB(context).query(TABLE, null, null, null, null, null, null);
        if (mC != null) {
            mC.moveToFirst();
        }
        return mC;
    }

    public static Cursor getUser(Context context, String id) {
        Cursor mC = null;
        mC = DataBaseAdapter.getDB(context).query(TABLE, null, ID + "=?", new String[]{id}, null, null, null);
        if (mC != null) {
            mC.moveToFirst();
        }
        return mC;
    }

    public static Map<String,String> getUserInfoFromEmailInMap(Context context, String username) {
        Cursor mC = DataBaseAdapter.getDB(context).rawQuery("SELECT * FROM user_info WHERE user = ?; ", new String[] {username});
        if (mC != null && mC.moveToFirst()) {

            Map<String,String> map  = new HashMap<String, String>();

            map.put(ID,mC.getString(mC.getColumnIndexOrThrow(ID)));
            map.put(USER,mC.getString(mC.getColumnIndexOrThrow(USER)));
            map.put(ROUTE,mC.getString(mC.getColumnIndexOrThrow(ROUTE)));
            map.put(ID_VIAMENTE,mC.getString(mC.getColumnIndexOrThrow(ID_VIAMENTE)));
            map.put(ID_PROFILE,mC.getString(mC.getColumnIndexOrThrow(ID_PROFILE)));
            map.put(JDE,mC.getString(mC.getColumnIndexOrThrow(JDE)));
            map.put(NAME,mC.getString(mC.getColumnIndexOrThrow(NAME)));
            map.put(LAST_NAME,mC.getString(mC.getColumnIndexOrThrow(LAST_NAME)));

            map.put(BRANCH_NAME,mC.getString(mC.getColumnIndexOrThrow(BRANCH_NAME)));
            map.put(BRANCH_ADDRESS,mC.getString(mC.getColumnIndexOrThrow(BRANCH_ADDRESS)));
            map.put(BRANCH_CODE,mC.getString(mC.getColumnIndexOrThrow(BRANCH_CODE)));

            mC.close();

            return map;
        }
        return null;
    }

    public static void removeAll(Context context){
        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, null, null, null ,null, ID);
        if (cursor != null && cursor.getCount() > 0) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                int id              = cursor.getInt(cursor.getColumnIndexOrThrow(ID));
                UserInfo.delete(context,id);
            }
        }
        cursor.close();
    }

    public static int delete(Context context, int id) {
        return DataBaseAdapter.getDB(context).delete(TABLE, ID + "=" + id, null);
    }
}