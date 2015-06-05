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
public class EvidenceProduct extends Table {

    public static final String TABLE = "evidence_product";

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String IMAGE_PRODUCT = "image_product";
    public static final String ID_BRAND_PRODUCT = "id_product_brand";

    private int id;
    private String name;
    private String image_product;
    private int id_product_brand;

    public EvidenceProduct(int id, String name, String image_product, int id_product_brand){
        this.id = id;
        this.name = name;
        this.image_product = image_product;
        this.id_product_brand = id_product_brand;
    }

    public static long insert(Context context, String name, String image_product, int id_product_brand) {
        ContentValues cv = new ContentValues();
        cv.put(NAME, name);
        cv.put(IMAGE_PRODUCT, image_product);
        cv.put(ID_BRAND_PRODUCT, id_product_brand);

        return DataBaseAdapter.getDB(context).insert(TABLE, null, cv);
    }

    public static List<EvidenceProduct> getByProductBrandId(Context context, int id) {

        List<EvidenceProduct> evidenceProducts = new ArrayList<EvidenceProduct>();
        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, ID_BRAND_PRODUCT + "=" + id, null, null, null, null);

        if (cursor != null) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                int current_id = cursor.getInt(cursor.getColumnIndexOrThrow(ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(NAME));
                String image_product = cursor.getString(cursor.getColumnIndexOrThrow(IMAGE_PRODUCT));
                int id_product_brand = cursor.getInt(cursor.getColumnIndexOrThrow(ID_BRAND_PRODUCT));

                evidenceProducts.add(new EvidenceProduct(current_id, name, image_product, id_product_brand));
            }
        }
        return evidenceProducts;
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

    public int getProductBrandId(){
        return id_product_brand;
    }

    public String getImageProduct(){
        return image_product;
    }
}
