package database.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;

import java.util.ArrayList;

import database.DataBaseAdapter;
import util.Utilities;

/**
 * Created by jcenteno on 09/06/14.
 */
public class ProductPic extends Table {

    public static final String TABLE = "product_pic";

    //fields
    public static final String ID = "id";
    public static final String SOURCE = "source";
    public static final String PRODUCT_ID = "product_id";
    public static final String MAIN = "main";

    public static long insert(Context context, String source, int product_id, int main) {
        ContentValues cv = new ContentValues();
        cv.put(SOURCE, source);
        cv.put(PRODUCT_ID, product_id);
        cv.put(MAIN, main);

        return DataBaseAdapter.getDB(context).insert(TABLE, null, cv);
    }

    public static ArrayList<Bitmap> getAllForProduct(Context context,int id_prod){

        ArrayList<Bitmap> photos = new ArrayList<Bitmap>();
        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, PRODUCT_ID + "=" + id_prod, null, null, null, null);
        if (cursor != null) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                int isMainPhoto = cursor.getInt(cursor.getColumnIndexOrThrow(MAIN));
                String encodedImage = cursor.getString(cursor.getColumnIndexOrThrow(SOURCE));
                Bitmap decodedImage = Utilities.stringToBitmap(encodedImage);
                if (isMainPhoto == 1)
                    photos.add(0, decodedImage);
                else
                    photos.add(decodedImage);

            }
            cursor.close();
        }
        return photos;
    }

    public static ArrayList<Bitmap> getAllThumbnailsForProduct(Context context,int id_prod){

        ArrayList<Bitmap> photos = new ArrayList<Bitmap>();
        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, PRODUCT_ID + "=" + id_prod, null, null, null, null);
        if (cursor != null) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                int isMainPhoto = cursor.getInt(cursor.getColumnIndexOrThrow(MAIN));
                String encodedImage = cursor.getString(cursor.getColumnIndexOrThrow(SOURCE));
                Bitmap decodedImage = Utilities.stringToBitmap(encodedImage);
                Bitmap scaledImage = Bitmap.createScaledBitmap(decodedImage, 200, 200 * decodedImage.getHeight() / decodedImage.getWidth(), false);
                if (isMainPhoto == 1)
                    photos.add(0, scaledImage);
                else
                    photos.add(scaledImage);

            }
            cursor.close();
        }
        return photos;
    }

    public static Bitmap getMainForProduct(Context context,int id_prod){

        Bitmap mainImage = null;
        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, PRODUCT_ID + "=" + id_prod + " AND " + MAIN + "=" + 1, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {

            String encodedImage = cursor.getString(cursor.getColumnIndexOrThrow(SOURCE));
            Bitmap decodedImage = Utilities.stringToBitmap(encodedImage);
            mainImage = Bitmap.createScaledBitmap(decodedImage, 200, 200 * decodedImage.getHeight() / decodedImage.getWidth(), false);

            cursor.close();
        }
        return mainImage;
    }

    public static int delete(Context context, int id) {

        return DataBaseAdapter.getDB(context).delete(TABLE, PRODUCT_ID + "=" + id, null);
    }
}
