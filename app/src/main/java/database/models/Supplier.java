package database.models;

import android.content.ContentValues;
import android.content.Context;

import database.DataBaseAdapter;

/**
 * Created by juanc.jimenez on 30/06/14.
 */
public class Supplier extends Table {

    public static final String TABLE = "supplier";

    public static final String ID = "id";
    public static final String COMPANY_NAME = "company_name";
    public static final String ACCOUNT = "account";
    public static final String PEOPLE_ID = "people_id";

    public static long insert(Context context, int id, String company_name, int account, int people_id) {
        ContentValues cv = new ContentValues();
        cv.put(ID, id);
        cv.put(COMPANY_NAME, company_name);
        cv.put(ACCOUNT, account);
        cv.put(PEOPLE_ID, people_id);

        return DataBaseAdapter.getDB(context).insert(TABLE, null, cv);
    }
}
