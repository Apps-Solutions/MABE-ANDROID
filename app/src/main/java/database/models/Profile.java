package database.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.List;

import database.DataBaseAdapter;

/**
 * Created by jcenteno on 14/05/14.
 */
public class Profile extends Table {

    public static final String TABLE = "profile";

    public static final String ID = "id";
    public static final String NAME = "name";

    private int id;
    private String name;
    private List<Integer> permissions;

    public Profile(int id, String name, List<Integer> permissions) {

        this.id = id;
        this.name = name;
        this.permissions = permissions;
    }

    public static long insert(Context context, int id, String name) {
        ContentValues cv = new ContentValues();
        cv.put(ID, id);
        cv.put(NAME, name);

        return DataBaseAdapter.getDB(context).insert(TABLE, null, cv);
    }

    public List<Integer> getPermissions() {
        return permissions;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static Profile getProfile(Context context, String name) {

        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, NAME + "=?", new String[]{name}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {

            int id = cursor.getInt(cursor.getColumnIndexOrThrow(ID));
            List<Integer> modules = Permission.getModules(context, id);

            return new Profile(id, name, modules);
        }
        return null;
    }

    public static String getName(Context context, int id) {

        String name = null;
        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, ID + "=" + id, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()){

            name = cursor.getString(cursor.getColumnIndexOrThrow(NAME));
            cursor.close();
        }
        return name;
    }

    public static boolean exists(Context context, int id) {

        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, ID + "=" + id, null, null, null, null);

        return cursor != null && cursor.moveToFirst();
    }

    public static void addPermission(Context context, String profile, int moduleId) {

        int profileId = getProfile(context, profile).getId();
        Permission.insert(context, profileId, moduleId);
    }
}
