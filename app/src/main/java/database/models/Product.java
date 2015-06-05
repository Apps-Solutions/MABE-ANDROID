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
import java.util.Map;

import database.DataBaseAdapter;

public class Product extends Table{

    public static final String TAG = "product_table";

    public static final String TABLE = "product";

    public static final String ID                   = "id";
    public static final String BARCODE              = "barcode";
    public static final String NAME                 = "name";
    public static final String DESCRIPTION          = "description";
    public static final String UNIT_PRICE           = "unit_price";
    public static final String COST_PRICE           = "cost_price";
    public static final String TAX                  = "tax";
    public static final String JDE                  = "jde";
    public static final String STOCK                = "stock";
    public static final String STOCK_CENTRAL        = "stock_central";
    public static final String PRODUCT_CATEGORY_ID  = "product_category_id";
    public static final String PRODUCT_BRAND_ID     = "product_brand_id";
    public static final String RIVAL                = "rival";

    public static final double NOT_UPDATE           = -1;

    private int     id;
    private String  jde;
    private String  name;
    private String  brand;
    private String  code;
    private String  description;
    private Double  price;
    private Double  cost;
    private String  category;
    private int     stock;
    private int     stock_central;
    private String  rival;
    private float tax;




    public static long insert(Context context, String barcode, String jde, String name, String description,
                              Double unit_price, Double cost_price, float tax, int stock, int stock_central,
                              int product_category_id, String product_brand_id, int id, String rival) {
        ContentValues cv = new ContentValues();
        cv.put(ID,id);
        cv.put(BARCODE, barcode);
        cv.put(JDE, jde);
        cv.put(NAME, name);
        cv.put(DESCRIPTION, description);
        cv.put(UNIT_PRICE, unit_price);
        cv.put(COST_PRICE, cost_price);
        cv.put(TAX, tax);
        cv.put(STOCK, stock);
        cv.put(STOCK_CENTRAL, stock_central);
        cv.put(PRODUCT_CATEGORY_ID, product_category_id);
        cv.put(PRODUCT_BRAND_ID, product_brand_id);
        cv.put(RIVAL, rival);

        return DataBaseAdapter.getDB(context).insert(TABLE, null, cv);
    }

    public static int update(Context context, int id, String barcode, String name, String description, Double unit_price, Double cost_price, float tax, int stock, int stock_central, int product_category_id, int product_brand_id) {

        ContentValues cv = new ContentValues();

        if (barcode != null)
            cv.put(BARCODE, barcode);
        if (name != null)
            cv.put(NAME, name);
        if (description != null)
            cv.put(DESCRIPTION, description);
        if (unit_price != NOT_UPDATE)
            cv.put(UNIT_PRICE, unit_price);
        if (cost_price != NOT_UPDATE)
            cv.put(COST_PRICE, cost_price);
        if(tax != NOT_UPDATE)
            cv.put(TAX, tax);
        if (stock != NOT_UPDATE)
            cv.put(STOCK, stock);
        if (stock_central != NOT_UPDATE)
            cv.put(STOCK_CENTRAL, stock_central);
        if (product_category_id != NOT_UPDATE)
            cv.put(PRODUCT_CATEGORY_ID, product_category_id);
        if (product_brand_id != NOT_UPDATE)
            cv.put(PRODUCT_BRAND_ID, product_brand_id);

        return DataBaseAdapter.getDB(context).update(TABLE, cv, ID + "=" + id, null);
    }

    public static int delete(Context context, int id) {

        ProductPic.delete(context, id);
        return DataBaseAdapter.getDB(context).delete(TABLE, ID + "=" + id, null);
    }

    public Product(int id, String jde,String name, String brand, Double price, Double cost,
                   float tax, int stock, int stock_central, String code, String description,
                   String category,String rival) {

        this.id             = id;
        this.jde            = jde;
        this.code           = code;
        this.name           = name;
        this.brand          = brand;
        this.description    = description;
        this.price          = price;
        this.cost           = cost;
        this.tax            = tax;
        this.stock          = stock;
        this.stock_central  = stock_central;
        this.category       = category;
        this.rival          = rival;
    }

    public int getId(){
        return id;
    }

    public String getJde() {
        return jde;
    }

    public String getName() {
        return name;
    }

    public String getBrand() {
        return brand;
    }

    public Double getPrice() {
        return price;
    }

    public Double getCost() {
        return cost;
    }

    public int getStock() {
        return stock;
    }

    public int getStock_central() {
        return stock_central;
    }

    public String getDescription() {
        return description;
    }

    public String getCode() {
        return code;
    }

    public String getCategory() {
        return category;
    }

    public float getTax() {
        return tax;
    }

    public String getRival() {
        return rival;
    }



    public static Product getProduct(Context context, int id) {

        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, ID + "=" + id, null, null, null, null);
        if (cursor != null && cursor.getCount() == 1)
        {
            cursor.moveToFirst();

            String jde          = cursor.getString(cursor.getColumnIndexOrThrow(JDE));
            String code         = cursor.getString(cursor.getColumnIndexOrThrow(BARCODE));
            String name         = cursor.getString(cursor.getColumnIndexOrThrow(NAME));

            //int brand_id        = cursor.getInt(cursor.getColumnIndexOrThrow(PRODUCT_BRAND_ID));
            //String brand        = ProductBrand.getName(context, brand_id).toUpperCase();

            String brand        = cursor.getString(cursor.getColumnIndexOrThrow(PRODUCT_BRAND_ID));

            String description  = cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION));
            Double price        = cursor.getDouble(cursor.getColumnIndexOrThrow(UNIT_PRICE));
            Double cost         = cursor.getDouble(cursor.getColumnIndexOrThrow(COST_PRICE));
            float tax           = cursor.getFloat(cursor.getColumnIndexOrThrow(TAX));
            int stock           = cursor.getInt(cursor.getColumnIndexOrThrow(STOCK));
            int stock_central   = cursor.getInt(cursor.getColumnIndexOrThrow(STOCK_CENTRAL));
            int category_id     = cursor.getInt(cursor.getColumnIndexOrThrow(PRODUCT_CATEGORY_ID));
            //String category     = ProductCategory.getProductCategory(context, category_id);
            String category     = "Oil";
            String rival  = cursor.getString(cursor.getColumnIndexOrThrow(RIVAL));


            cursor.close();

            return new Product(id, jde,name, brand, price, cost, tax, stock, stock_central, code, description, category,rival);
        }

        return null;
    }



    public static boolean barcodeExists(Context context, String code) {

        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, BARCODE + "=?", new String[]{code}, null, null, null);

        return cursor != null && cursor.getCount() == 1;
    }

    public static ArrayList<Product> getAll(Context context){

        ArrayList<Product> list = new ArrayList<Product>();
        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, null, null, null ,null, NAME);
        if (cursor != null && cursor.getCount() > 0) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                int id              = cursor.getInt(cursor.getColumnIndexOrThrow(ID));
                String jde          = cursor.getString(cursor.getColumnIndexOrThrow(JDE));
                String name         = cursor.getString(cursor.getColumnIndexOrThrow(NAME));
                String brand        = cursor.getString(cursor.getColumnIndexOrThrow(PRODUCT_BRAND_ID));
                //String brand        = ProductBrand.getName(context, brand_id).toUpperCase();
                //String brand        = "Nutrioli";
                String code         = cursor.getString(cursor.getColumnIndexOrThrow(BARCODE));
                String description  = cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION));
                Double price        = cursor.getDouble(cursor.getColumnIndexOrThrow(UNIT_PRICE));
                Double cost         = cursor.getDouble(cursor.getColumnIndexOrThrow(COST_PRICE));
                float tax           = cursor.getFloat(cursor.getColumnIndexOrThrow(TAX));
                int stock           = cursor.getInt(cursor.getColumnIndexOrThrow(STOCK));
                int stock_central   = cursor.getInt(cursor.getColumnIndexOrThrow(STOCK_CENTRAL));
                int category_id     = cursor.getInt(cursor.getColumnIndexOrThrow(PRODUCT_CATEGORY_ID));
                //String category     = ProductCategory.getProductCategory(context, category_id);
                String category     = "Oil";
                String rival        = cursor.getString(cursor.getColumnIndexOrThrow(RIVAL));

                list.add(new Product(id, jde, name, brand, price, cost, tax, stock, stock_central, code, description, category,rival));
            }

            cursor.close();
        }
        return list;
    }

    public static ArrayList<Map<String,String>> getAllInMaps(Context context){

        ArrayList<Map<String,String>> list = new ArrayList<Map<String,String>>();
        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, null, null, null ,null, NAME);
        if (cursor != null && cursor.getCount() > 0) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                Map<String,String> map_prod = new HashMap<String, String>();
                map_prod.put("id",""+cursor.getInt(cursor.getColumnIndexOrThrow(ID)));
                map_prod.put("jde",cursor.getString(cursor.getColumnIndexOrThrow(JDE)));
                map_prod.put("name",cursor.getString(cursor.getColumnIndexOrThrow(NAME)));
                map_prod.put("brand",cursor.getString(cursor.getColumnIndexOrThrow(PRODUCT_BRAND_ID)));
                map_prod.put("code",cursor.getString(cursor.getColumnIndexOrThrow(BARCODE)));
                map_prod.put("description",cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION)));
                map_prod.put("price",""+cursor.getDouble(cursor.getColumnIndexOrThrow(UNIT_PRICE)));
                map_prod.put("cost",""+cursor.getDouble(cursor.getColumnIndexOrThrow(COST_PRICE)));
                map_prod.put("tax",""+cursor.getFloat(cursor.getColumnIndexOrThrow(TAX)));
                map_prod.put("stock",""+cursor.getFloat(cursor.getColumnIndexOrThrow(STOCK)));
                map_prod.put("stock_central",""+cursor.getFloat(cursor.getColumnIndexOrThrow(STOCK_CENTRAL)));
                map_prod.put("category","Oil");

                list.add(map_prod);
            }

            cursor.close();
        }
        return list;
    }

    public static ArrayList<Map<String,String>> getAllByBrandInMaps(Context context, String brand_id){

        ArrayList<Map<String,String>> list = new ArrayList<Map<String,String>>();
        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, PRODUCT_BRAND_ID + "=" + brand_id, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                Map<String,String> map_prod = new HashMap<String, String>();
                map_prod.put("id",""+cursor.getInt(cursor.getColumnIndexOrThrow(ID)));
                map_prod.put("jde",cursor.getString(cursor.getColumnIndexOrThrow(JDE)));
                map_prod.put("name",cursor.getString(cursor.getColumnIndexOrThrow(NAME)));
                map_prod.put("brand",cursor.getString(cursor.getColumnIndexOrThrow(PRODUCT_BRAND_ID)));
                map_prod.put("code",cursor.getString(cursor.getColumnIndexOrThrow(BARCODE)));
                map_prod.put("description",cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION)));
                map_prod.put("price",""+cursor.getDouble(cursor.getColumnIndexOrThrow(UNIT_PRICE)));
                map_prod.put("cost",""+cursor.getDouble(cursor.getColumnIndexOrThrow(COST_PRICE)));
                map_prod.put("tax",""+cursor.getFloat(cursor.getColumnIndexOrThrow(TAX)));
                map_prod.put("stock",""+cursor.getFloat(cursor.getColumnIndexOrThrow(STOCK)));
                map_prod.put("stock_central",""+cursor.getFloat(cursor.getColumnIndexOrThrow(STOCK_CENTRAL)));
                map_prod.put("category","Oil");
                map_prod.put("rival",cursor.getString(cursor.getColumnIndexOrThrow(RIVAL)));

                list.add(map_prod);
            }

            cursor.close();
        }
        return list;
    }

    public static ArrayList<Map<String,String>> getAllOwnInMaps(Context context){

        ArrayList<Map<String,String>> list = new ArrayList<Map<String,String>>();
        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, RIVAL + "=?", new String[]{"0"}, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                Map<String,String> map_prod = new HashMap<String, String>();
                map_prod.put("id",""+cursor.getInt(cursor.getColumnIndexOrThrow(ID)));
                map_prod.put("jde",cursor.getString(cursor.getColumnIndexOrThrow(JDE)));
                map_prod.put("name",cursor.getString(cursor.getColumnIndexOrThrow(NAME)));
                map_prod.put("brand",cursor.getString(cursor.getColumnIndexOrThrow(PRODUCT_BRAND_ID)));
                map_prod.put("code",cursor.getString(cursor.getColumnIndexOrThrow(BARCODE)));
                map_prod.put("description",cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION)));
                map_prod.put("price",""+cursor.getDouble(cursor.getColumnIndexOrThrow(UNIT_PRICE)));
                map_prod.put("cost",""+cursor.getDouble(cursor.getColumnIndexOrThrow(COST_PRICE)));
                map_prod.put("tax",""+cursor.getFloat(cursor.getColumnIndexOrThrow(TAX)));
                map_prod.put("stock",""+cursor.getFloat(cursor.getColumnIndexOrThrow(STOCK)));
                map_prod.put("stock_central",""+cursor.getFloat(cursor.getColumnIndexOrThrow(STOCK_CENTRAL)));
                map_prod.put("category","Oil");
                map_prod.put("rival",cursor.getString(cursor.getColumnIndexOrThrow(RIVAL)));

                list.add(map_prod);
            }
            cursor.close();
        }
        return list;
    }

    public static ArrayList<Map<String,String>> getAllRivalInMaps(Context context){

        ArrayList<Map<String,String>> list = new ArrayList<Map<String,String>>();
        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, RIVAL + "=?", new String[]{"1"}, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                Map<String,String> map_prod = new HashMap<String, String>();
                map_prod.put("id",""+cursor.getInt(cursor.getColumnIndexOrThrow(ID)));
                map_prod.put("jde",cursor.getString(cursor.getColumnIndexOrThrow(JDE)));
                map_prod.put("name",cursor.getString(cursor.getColumnIndexOrThrow(NAME)));
                map_prod.put("brand",cursor.getString(cursor.getColumnIndexOrThrow(PRODUCT_BRAND_ID)));
                map_prod.put("code",cursor.getString(cursor.getColumnIndexOrThrow(BARCODE)));
                map_prod.put("description",cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION)));
                map_prod.put("price",""+cursor.getDouble(cursor.getColumnIndexOrThrow(UNIT_PRICE)));
                map_prod.put("cost",""+cursor.getDouble(cursor.getColumnIndexOrThrow(COST_PRICE)));
                map_prod.put("tax",""+cursor.getFloat(cursor.getColumnIndexOrThrow(TAX)));
                map_prod.put("stock",""+cursor.getFloat(cursor.getColumnIndexOrThrow(STOCK)));
                map_prod.put("stock_central",""+cursor.getFloat(cursor.getColumnIndexOrThrow(STOCK_CENTRAL)));
                map_prod.put("category","Oil");
                map_prod.put("rival",cursor.getString(cursor.getColumnIndexOrThrow(RIVAL)));

                list.add(map_prod);
            }
            cursor.close();
        }
        return list;
    }

    public static void removeAll(Context context){
        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, null, null, null ,null, NAME);
        if (cursor != null && cursor.getCount() > 0) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                int id              = cursor.getInt(cursor.getColumnIndexOrThrow(ID));
                Product.delete(context,id);
            }
        }
        cursor.close();
    }

    public static Cursor getAllCursor(Context context){
        Cursor c = DataBaseAdapter.getDB(context).query(TABLE,null,null,null,null,null,NAME);
        if (c != null) c.moveToFirst();
        return c;
    }

    public static void insertProductsFromJSONArray(Context context, JSONArray array){
        if (Product.getAll(context).size() != 0 )    // Products stored in database
            Product.removeAll(context);
        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject obj = array.getJSONObject(i);
                Double price;
                if (obj.getString("prod_price").equalsIgnoreCase("null"))
                    price   = 0.0;
                else
                    price = obj.getDouble("prod_price");
                      //insert(Context context, String barcode,     String jde                    String name,                 String description,                  Double unit_price,              Double cost_price,              float tax,                          int stock,                  int stock_central,      int product_category_id,    String product_brand_id)        product_id              rival
                Product.insert(context,         "000",              obj.getString("prod_jde"),    obj.getString("prod_name"),   obj.getString("prod_description"),  price,                          price,                          (float)obj.getDouble("prod_tax"),   0,                          0,                      obj.getInt("prod_id"),      obj.getString("id_brand"),     obj.getInt("prod_id"), obj.getString("rival"));
                //Log.d(TAG,"inserting");
            } catch (JSONException e) {
                    e.printStackTrace();}
        }
    }

}

