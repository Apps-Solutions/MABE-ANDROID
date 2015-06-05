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
public class EvidenceBrandProduct extends Table {

    public static final String TABLE = "evidence_brand_product";

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String ID_EVIDENCE = "id_evidence";

    private int id;
    private String name;
    private int id_evidence;

    public EvidenceBrandProduct(int id, String name, int id_evidence){
        this.id = id;
        this.name = name;
        this.id_evidence = id_evidence;
    }

    public static long insert(Context context, String name, int id_evidence) {
        ContentValues cv = new ContentValues();
        cv.put(NAME, name);
        cv.put(ID_EVIDENCE, id_evidence);

        return DataBaseAdapter.getDB(context).insert(TABLE, null, cv);
    }

    public static List<EvidenceBrandProduct> getByEvidenceId(Context context, int id) {

        List<EvidenceBrandProduct> evidenceBrandProduct = new ArrayList<EvidenceBrandProduct>();
        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, ID_EVIDENCE + "=" + id, null, null, null, null);

        if (cursor != null) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                int current_id = cursor.getInt(cursor.getColumnIndexOrThrow(ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(NAME));
                int id_evidence = cursor.getInt(cursor.getColumnIndexOrThrow(ID_EVIDENCE));

                evidenceBrandProduct.add(new EvidenceBrandProduct(current_id, name, id_evidence));
            }
        }
        return evidenceBrandProduct;
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

    public int getIdEvidence(){
        return id_evidence;
    }

}
