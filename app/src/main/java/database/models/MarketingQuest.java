package database.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import database.DataBaseAdapter;

/**
 * Created by hugo.figueroa on 04/03/15.
 */
public class MarketingQuest extends Table {

    public static final String TAG = "marketing_quest_table";

    public static final String TABLE = "marketing_quest";

    //fields
    public static final String ID                       = "id";
    public static final String PRODUCT_ID               = "product_id";
    public static final String VISIT_ID                 = "visit_id";
    public static final String SR_CURRENT               = "sr_current";
    public static final String SR_SHELF_BOXES           = "sr_shelf_boxes";
    public static final String SR_EXHIBITION_BOXES      = "sr_exhibition_boxes";
    public static final String SR_PRICE                 = "sr_price";
    public static final String SR_EXPIRATION            = "sr_expiration";
    public static final String QI_ID_PROBLEM_TYPE       = "qi_id_problem_type";
    public static final String QI_DESCRIPTION           = "qi_description";
    public static final String QI_BATCH                 = "qi_batch";
    public static final String QI_PRODUCT_LINE          = "qi_product_line";
    public static final String QI_QUANTITY              = "qi_quantity";
    public static final String QI_EVIDENCE              = "qi_evidence";
    public static final String OP_PUBLIC_PRICE          = "op_public_price";
    public static final String OP_PROMOTION_PRICE       = "op_promotion_price";
    public static final String OP_PROMOTION_START       = "op_promotion_start";
    public static final String OP_PROMOTION_END         = "op_promotion_end";
    public static final String OP_ID_GRAPHIC_MATERIAL   = "op_id_graphic_material";
    public static final String OP_ACTIVATION            = "op_activation";
    public static final String OP_ID_PROMOTION_TYPE     = "op_id_promotion_type";
    public static final String OP_MECHANICS             = "op_mechanics";
    public static final String OP_DIFFERED_SKU          = "op_differed_sku";
    public static final String OP_EXHIBITION            = "op_exhibition";
    public static final String OP_ID_EXHIBITION_TYPE    = "op_id_exhibition_type";
    public static final String OP_EXISTENCE             = "op_existence";
    public static final String OP_ID_RESOURCE_TYPE      = "op_id_resource_type";
    public static final String OP_EVIDENCE              = "op_evidence";
    public static final String CP_PRODUCT_ID            = "cp_product_id";
    public static final String CP_PUBLIC_PRICE          = "cp_public_price";
    public static final String CP_PROMOTION_PRICE       = "cp_promotion_price";
    public static final String CP_PROMOTION_START       = "cp_promotion_start";
    public static final String CP_PROMOTION_END         = "cp_promotion_end";
    public static final String CP_ID_GRAPHIC_MATERIAL   = "cp_id_graphic_material";
    public static final String CP_ACTIVATION            = "cp_activation";
    public static final String CP_ID_PROMOTION_TYPE     = "cp_id_promotion_type";
    public static final String CP_MECHANICS             = "cp_mechanics";
    public static final String CP_DIFFERED_SKU          = "cp_differed_sku";
    public static final String CP_EXHIBITION            = "cp_exhibition";
    public static final String CP_ID_EXHIBITION_TYPE    = "cp_id_exhibition_type";
    public static final String CP_EXISTENCE             = "cp_existence";
    public static final String CP_ID_RESOURCE_TYPE      = "cp_id_resource_type";
    public static final String CP_EVIDENCE              = "cp_evidence";
    public static final String WS_BOXES_OUT             = "ws_boxes_out";
    public static final String WS_FINAL_STOCK           = "ws_final_stock";
    public static final String S_R                      = "s_r";
    public static final String Q_I                      = "q_i";
    public static final String O_P                      = "o_p";
    public static final String C_P                      = "c_p";
    public static final String W_S                      = "w_s";
/*
    private int     id;
    private int     product_id;
    private int     visit_id;
    private int     sr_current;
    private int     sr_shelf_boxes;
    private int     sr_exhibition_boxes;
    private double  sr_price;
    private int     sr_expiration;
    private int     qi_id_problem_type;
    private String  qi_description;
    private int     qi_batch;
    private int     qi_product_line;
    private int     qi_quantity;
    private String  qi_evidence;
    private double  op_public_price;
    private double  op_promotion_price;
    private int     op_promotion_start;
    private int     op_promotion_end;
    private String  op_id_graphic_material;
    private int     op_activation;
    private int     op_id_promotion_type;
    private String  op_mechanics;
    private int     op_differed_sku;
    private int     op_exhibition;
    private int     op_id_exhibition_type;
    private int     op_existence;
    private int     op_id_resource_type;
    private String  op_evidence;
    private int     cp_product_id;
    private double  cp_public_price;
    private double  cp_promotion_price;
    private int     cp_promotion_start;
    private int     cp_promotion_end;
    private String  cp_id_graphic_material;
    private int     cp_activation;
    private int     cp_id_promotion_type;
    private String  cp_mechanics;
    private int     cp_differed_sku;
    private int     cp_exhibition;
    private int     cp_id_exhibition_type;
    private int     cp_existence;
    private int     cp_id_resource_type;
    private String  cp_evidence;
    private int     ws_boxes_out;
    private int     ws_final_stock;
    private int     s_r;
    private int     q_i;
    private int     o_p;
    private int     c_p;
    private int     w_s;

*/
    public static long insert(Context context,int product_id,int visit_id,int sr_current,int sr_shelf_boxes,int sr_exhibition_boxes,
                              double sr_price,int sr_expiration,int qi_id_problem_type,String qi_description,int qi_batch,
                              int qi_product_line,int qi_quantity,String qi_evidence,double op_public_price,double op_promotion_price,
                              int op_promotion_start,int op_promotion_end,String op_id_graphic_material,int op_activation,
                              int op_id_promotion_type,String op_mechanics,int op_differed_sku,int op_exhibition,
                              int op_id_exhibition_type,int op_existence,int op_id_resource_type,String op_evidence,int cp_product_id,
                              double cp_public_price,double cp_promotion_price,int cp_promotion_start,int cp_promotion_end,
                              String cp_id_graphic_material,int cp_activation,int cp_id_promotion_type,String cp_mechanics,
                              int cp_differed_sku,int cp_exhibition,int cp_id_exhibition_type,int cp_existence,int cp_id_resource_type,
                              String cp_evidence,int ws_boxes_out,int ws_final_stock,int s_r,int q_i,int o_p,int c_p,int w_s) {

        if (MarketingQuest.getByVisitIdAndProductId(context,""+product_id,""+visit_id) != null){
            return 1;
        }

        ContentValues cv = new ContentValues();

        cv.put(PRODUCT_ID,product_id);
        cv.put(VISIT_ID,visit_id);
        cv.put(SR_CURRENT,sr_current);
        cv.put(SR_SHELF_BOXES,sr_shelf_boxes);
        cv.put(SR_EXHIBITION_BOXES,sr_exhibition_boxes);
        cv.put(SR_PRICE,sr_price);
        cv.put(SR_EXPIRATION,sr_expiration);
        cv.put(QI_ID_PROBLEM_TYPE,qi_id_problem_type);
        cv.put(QI_DESCRIPTION,qi_description);
        cv.put(QI_BATCH,qi_batch);
        cv.put(QI_PRODUCT_LINE,qi_product_line);
        cv.put(QI_QUANTITY,qi_quantity);
        cv.put(QI_EVIDENCE,qi_evidence);
        cv.put(OP_PUBLIC_PRICE,op_public_price);
        cv.put(OP_PROMOTION_PRICE,op_promotion_price);
        cv.put(OP_PROMOTION_START,op_promotion_start);
        cv.put(OP_PROMOTION_END,op_promotion_end);
        cv.put(OP_ID_GRAPHIC_MATERIAL,op_id_graphic_material);
        cv.put(OP_ACTIVATION,op_activation);
        cv.put(OP_ID_PROMOTION_TYPE,op_id_promotion_type);
        cv.put(OP_MECHANICS,op_mechanics);
        cv.put(OP_DIFFERED_SKU,op_differed_sku);
        cv.put(OP_EXHIBITION,op_exhibition);
        cv.put(OP_ID_EXHIBITION_TYPE,op_id_exhibition_type);
        cv.put(OP_EXISTENCE,op_existence);
        cv.put(OP_ID_RESOURCE_TYPE,op_id_resource_type);
        cv.put(OP_EVIDENCE,op_evidence);
        cv.put(CP_PRODUCT_ID,cp_product_id);
        cv.put(CP_PUBLIC_PRICE,cp_public_price);
        cv.put(CP_PROMOTION_PRICE,cp_promotion_price);
        cv.put(CP_PROMOTION_START,cp_promotion_start);
        cv.put(CP_PROMOTION_END,cp_promotion_end);
        cv.put(CP_ID_GRAPHIC_MATERIAL,cp_id_graphic_material);
        cv.put(CP_ACTIVATION,cp_activation);
        cv.put(CP_ID_PROMOTION_TYPE,cp_id_promotion_type);
        cv.put(CP_MECHANICS,cp_mechanics);
        cv.put(CP_DIFFERED_SKU,cp_differed_sku);
        cv.put(CP_EXHIBITION,cp_exhibition);
        cv.put(CP_ID_EXHIBITION_TYPE,cp_id_exhibition_type);
        cv.put(CP_EXISTENCE,cp_existence);
        cv.put(CP_ID_RESOURCE_TYPE,cp_id_resource_type);
        cv.put(CP_EVIDENCE,cp_evidence);
        cv.put(WS_BOXES_OUT,ws_boxes_out);
        cv.put(WS_FINAL_STOCK,ws_final_stock);
        cv.put(S_R,s_r);
        cv.put(Q_I,q_i);
        cv.put(O_P,o_p);
        cv.put(C_P,c_p);
        cv.put(W_S,w_s);

        return DataBaseAdapter.getDB(context).insert(TABLE, null, cv);
    }

    public static void updateMarketingQuest(Context context, String key, String value){

        ContentValues cv = new ContentValues();

        cv.put(key, value);

        DataBaseAdapter.getDB(context).update(TABLE, cv,null,null);
    }

    public static ArrayList<Map<String,String>> getAllInMaps(Context context){

        ArrayList<Map<String,String>> list = new ArrayList<Map<String,String>>();
        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, null, null, null ,null,ID);
        if (cursor != null && cursor.getCount() > 0) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                Map<String,String> map_mark_quest = new HashMap<String, String>();
                map_mark_quest.put("id",""+cursor.getInt(cursor.getColumnIndexOrThrow(ID)));
                map_mark_quest.put("product_id",""+cursor.getInt(cursor.getColumnIndexOrThrow(PRODUCT_ID)));
                map_mark_quest.put("visit_id",""+cursor.getInt(cursor.getColumnIndexOrThrow(VISIT_ID)));
                map_mark_quest.put("sr_current",""+cursor.getInt(cursor.getColumnIndexOrThrow(SR_CURRENT)));
                map_mark_quest.put("sr_shelf_boxes",""+cursor.getInt(cursor.getColumnIndexOrThrow(SR_SHELF_BOXES)));
                map_mark_quest.put("sr_exhibition_boxes",""+cursor.getInt(cursor.getColumnIndexOrThrow(SR_EXHIBITION_BOXES)));
                map_mark_quest.put("sr_price",""+cursor.getDouble(cursor.getColumnIndexOrThrow(SR_PRICE)));
                map_mark_quest.put("sr_expiration",""+cursor.getInt(cursor.getColumnIndexOrThrow(SR_EXPIRATION)));
                map_mark_quest.put("qi_id_problem_type",""+cursor.getInt(cursor.getColumnIndexOrThrow(QI_ID_PROBLEM_TYPE)));
                map_mark_quest.put("qi_description",""+cursor.getString(cursor.getColumnIndexOrThrow(QI_DESCRIPTION)));
                map_mark_quest.put("qi_batch",""+cursor.getInt(cursor.getColumnIndexOrThrow(QI_BATCH)));
                map_mark_quest.put("qi_product_line",""+cursor.getInt(cursor.getColumnIndexOrThrow(QI_PRODUCT_LINE)));
                map_mark_quest.put("qi_quantity",""+cursor.getInt(cursor.getColumnIndexOrThrow(QI_QUANTITY)));
                map_mark_quest.put("qi_evidence",""+cursor.getString(cursor.getColumnIndexOrThrow(QI_EVIDENCE)));
                map_mark_quest.put("op_public_price",""+cursor.getDouble(cursor.getColumnIndexOrThrow(OP_PUBLIC_PRICE)));
                map_mark_quest.put("op_promotion_price",""+cursor.getDouble(cursor.getColumnIndexOrThrow(OP_PROMOTION_PRICE)));
                map_mark_quest.put("op_promotion_start",""+cursor.getInt(cursor.getColumnIndexOrThrow(OP_PROMOTION_START)));
                map_mark_quest.put("op_promotion_end",""+cursor.getInt(cursor.getColumnIndexOrThrow(OP_PROMOTION_END)));
                map_mark_quest.put("op_id_graphic_material",""+cursor.getString(cursor.getColumnIndexOrThrow(OP_ID_GRAPHIC_MATERIAL)));
                map_mark_quest.put("op_activation",""+cursor.getInt(cursor.getColumnIndexOrThrow(OP_ACTIVATION)));
                map_mark_quest.put("op_id_promotion_type",""+cursor.getInt(cursor.getColumnIndexOrThrow(OP_ID_PROMOTION_TYPE)));
                map_mark_quest.put("op_mechanics",""+cursor.getString(cursor.getColumnIndexOrThrow(OP_MECHANICS)));
                map_mark_quest.put("op_differed_sku",""+cursor.getInt(cursor.getColumnIndexOrThrow(OP_DIFFERED_SKU)));
                map_mark_quest.put("op_exhibition",""+cursor.getInt(cursor.getColumnIndexOrThrow(OP_EXHIBITION)));
                map_mark_quest.put("op_id_exhibition_type",""+cursor.getInt(cursor.getColumnIndexOrThrow(OP_ID_EXHIBITION_TYPE)));
                map_mark_quest.put("op_existence",""+cursor.getInt(cursor.getColumnIndexOrThrow(OP_EXISTENCE)));
                map_mark_quest.put("op_id_resource_type",""+cursor.getInt(cursor.getColumnIndexOrThrow(OP_ID_RESOURCE_TYPE)));
                map_mark_quest.put("op_evidence",""+cursor.getString(cursor.getColumnIndexOrThrow(OP_EVIDENCE)));
                map_mark_quest.put("cp_product_id",""+cursor.getInt(cursor.getColumnIndexOrThrow(CP_PRODUCT_ID)));
                map_mark_quest.put("cp_public_price",""+cursor.getDouble(cursor.getColumnIndexOrThrow(CP_PUBLIC_PRICE)));
                map_mark_quest.put("cp_promotion_price",""+cursor.getDouble(cursor.getColumnIndexOrThrow(CP_PROMOTION_PRICE)));
                map_mark_quest.put("cp_promotion_start",""+cursor.getInt(cursor.getColumnIndexOrThrow(CP_PROMOTION_START)));
                map_mark_quest.put("cp_promotion_end",""+cursor.getInt(cursor.getColumnIndexOrThrow(CP_PROMOTION_END)));
                map_mark_quest.put("cp_id_graphic_material",""+cursor.getString(cursor.getColumnIndexOrThrow(CP_ID_GRAPHIC_MATERIAL)));
                map_mark_quest.put("cp_activation",""+cursor.getInt(cursor.getColumnIndexOrThrow(CP_ACTIVATION)));
                map_mark_quest.put("cp_id_promotion_type",""+cursor.getInt(cursor.getColumnIndexOrThrow(CP_ID_PROMOTION_TYPE)));
                map_mark_quest.put("cp_mechanics",""+cursor.getString(cursor.getColumnIndexOrThrow(CP_MECHANICS)));
                map_mark_quest.put("cp_differed_sku",""+cursor.getInt(cursor.getColumnIndexOrThrow(CP_DIFFERED_SKU)));
                map_mark_quest.put("cp_exhibition",""+cursor.getInt(cursor.getColumnIndexOrThrow(CP_EXHIBITION)));
                map_mark_quest.put("cp_id_exhibition_type",""+cursor.getInt(cursor.getColumnIndexOrThrow(CP_ID_EXHIBITION_TYPE)));
                map_mark_quest.put("cp_existence",""+cursor.getInt(cursor.getColumnIndexOrThrow(CP_EXISTENCE)));
                map_mark_quest.put("cp_id_resource_type",""+cursor.getInt(cursor.getColumnIndexOrThrow(CP_ID_RESOURCE_TYPE)));
                map_mark_quest.put("cp_evidence",""+cursor.getString(cursor.getColumnIndexOrThrow(CP_EVIDENCE)));
                map_mark_quest.put("ws_boxes_out",""+cursor.getInt(cursor.getColumnIndexOrThrow(WS_BOXES_OUT)));
                map_mark_quest.put("ws_final_stock",""+cursor.getInt(cursor.getColumnIndexOrThrow(WS_FINAL_STOCK)));
                map_mark_quest.put("s_r",""+cursor.getInt(cursor.getColumnIndexOrThrow(S_R)));
                map_mark_quest.put("q_i",""+cursor.getInt(cursor.getColumnIndexOrThrow(Q_I)));
                map_mark_quest.put("o_p",""+cursor.getInt(cursor.getColumnIndexOrThrow(O_P)));
                map_mark_quest.put("c_p",""+cursor.getInt(cursor.getColumnIndexOrThrow(C_P)));
                map_mark_quest.put("w_s",""+cursor.getInt(cursor.getColumnIndexOrThrow(W_S)));

                list.add(map_mark_quest);
            }

            cursor.close();
        }
        return list;
    }

    public static ArrayList<Map<String,String>> getByVisitIdAndProductId(Context context, String product_id,String visit_id){

        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, new String [] {ID,SR_CURRENT, SR_SHELF_BOXES,
                        SR_EXHIBITION_BOXES, SR_PRICE, SR_EXPIRATION,QI_ID_PROBLEM_TYPE,QI_DESCRIPTION,QI_BATCH,QI_PRODUCT_LINE,
                        QI_QUANTITY,QI_EVIDENCE,OP_PUBLIC_PRICE,OP_PROMOTION_PRICE,OP_PROMOTION_START,OP_PROMOTION_END,
                        OP_ID_GRAPHIC_MATERIAL,OP_ACTIVATION,OP_ID_PROMOTION_TYPE,OP_MECHANICS,OP_DIFFERED_SKU,
                        OP_EXHIBITION,OP_ID_EXHIBITION_TYPE,OP_EXISTENCE,OP_ID_RESOURCE_TYPE,OP_EVIDENCE,CP_PRODUCT_ID,
                        CP_PUBLIC_PRICE,CP_PROMOTION_PRICE,CP_PROMOTION_START,CP_PROMOTION_END,CP_ID_GRAPHIC_MATERIAL,
                        CP_ACTIVATION,CP_ID_PROMOTION_TYPE,CP_MECHANICS,CP_DIFFERED_SKU,CP_EXHIBITION,CP_ID_EXHIBITION_TYPE,
                        CP_EXISTENCE,CP_ID_RESOURCE_TYPE,CP_EVIDENCE,WS_BOXES_OUT,WS_FINAL_STOCK,S_R,Q_I,O_P,C_P,W_S},
                "product_id = ? AND visit_id = ?",
                new String[] { product_id, visit_id},
                null,
                null,
                ID);
        if (cursor != null && cursor.getCount() > 0) {
            ArrayList<Map<String,String>> list = new ArrayList<Map<String,String>>();
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                Map<String,String> map_mark_quest = new HashMap<String, String>();
                map_mark_quest.put("id",""+cursor.getInt(cursor.getColumnIndexOrThrow(ID)));
                //map_mark_quest.put("product_id",""+cursor.getInt(cursor.getColumnIndexOrThrow(PRODUCT_ID)));
                //map_mark_quest.put("visit_id",""+cursor.getInt(cursor.getColumnIndexOrThrow(VISIT_ID)));
                map_mark_quest.put("sr_current",""+cursor.getInt(cursor.getColumnIndexOrThrow(SR_CURRENT)));
                map_mark_quest.put("sr_shelf_boxes",""+cursor.getInt(cursor.getColumnIndexOrThrow(SR_SHELF_BOXES)));
                map_mark_quest.put("sr_exhibition_boxes",""+cursor.getInt(cursor.getColumnIndexOrThrow(SR_EXHIBITION_BOXES)));
                map_mark_quest.put("sr_price",""+cursor.getDouble(cursor.getColumnIndexOrThrow(SR_PRICE)));
                map_mark_quest.put("sr_expiration",""+cursor.getInt(cursor.getColumnIndexOrThrow(SR_EXPIRATION)));
                map_mark_quest.put("qi_id_problem_type",""+cursor.getInt(cursor.getColumnIndexOrThrow(QI_ID_PROBLEM_TYPE)));
                map_mark_quest.put("qi_description",""+cursor.getString(cursor.getColumnIndexOrThrow(QI_DESCRIPTION)));
                map_mark_quest.put("qi_batch",""+cursor.getInt(cursor.getColumnIndexOrThrow(QI_BATCH)));
                map_mark_quest.put("qi_product_line",""+cursor.getInt(cursor.getColumnIndexOrThrow(QI_PRODUCT_LINE)));
                map_mark_quest.put("qi_quantity",""+cursor.getInt(cursor.getColumnIndexOrThrow(QI_QUANTITY)));
                map_mark_quest.put("qi_evidence",""+cursor.getString(cursor.getColumnIndexOrThrow(QI_EVIDENCE)));
                map_mark_quest.put("op_public_price",""+cursor.getDouble(cursor.getColumnIndexOrThrow(OP_PUBLIC_PRICE)));
                map_mark_quest.put("op_promotion_price",""+cursor.getDouble(cursor.getColumnIndexOrThrow(OP_PROMOTION_PRICE)));
                map_mark_quest.put("op_promotion_start",""+cursor.getInt(cursor.getColumnIndexOrThrow(OP_PROMOTION_START)));
                map_mark_quest.put("op_promotion_end",""+cursor.getInt(cursor.getColumnIndexOrThrow(OP_PROMOTION_END)));
                map_mark_quest.put("op_id_graphic_material",""+cursor.getString(cursor.getColumnIndexOrThrow(OP_ID_GRAPHIC_MATERIAL)));
                map_mark_quest.put("op_activation",""+cursor.getInt(cursor.getColumnIndexOrThrow(OP_ACTIVATION)));
                map_mark_quest.put("op_id_promotion_type",""+cursor.getInt(cursor.getColumnIndexOrThrow(OP_ID_PROMOTION_TYPE)));
                map_mark_quest.put("op_mechanics",""+cursor.getString(cursor.getColumnIndexOrThrow(OP_MECHANICS)));
                map_mark_quest.put("op_differed_sku",""+cursor.getInt(cursor.getColumnIndexOrThrow(OP_DIFFERED_SKU)));
                map_mark_quest.put("op_exhibition",""+cursor.getInt(cursor.getColumnIndexOrThrow(OP_EXHIBITION)));
                map_mark_quest.put("op_id_exhibition_type",""+cursor.getInt(cursor.getColumnIndexOrThrow(OP_ID_EXHIBITION_TYPE)));
                map_mark_quest.put("op_existence",""+cursor.getInt(cursor.getColumnIndexOrThrow(OP_EXISTENCE)));
                map_mark_quest.put("op_id_resource_type",""+cursor.getInt(cursor.getColumnIndexOrThrow(OP_ID_RESOURCE_TYPE)));
                map_mark_quest.put("op_evidence",""+cursor.getString(cursor.getColumnIndexOrThrow(OP_EVIDENCE)));
                map_mark_quest.put("cp_product_id",""+cursor.getInt(cursor.getColumnIndexOrThrow(CP_PRODUCT_ID)));
                map_mark_quest.put("cp_public_price",""+cursor.getDouble(cursor.getColumnIndexOrThrow(CP_PUBLIC_PRICE)));
                map_mark_quest.put("cp_promotion_price",""+cursor.getDouble(cursor.getColumnIndexOrThrow(CP_PROMOTION_PRICE)));
                map_mark_quest.put("cp_promotion_start",""+cursor.getInt(cursor.getColumnIndexOrThrow(CP_PROMOTION_START)));
                map_mark_quest.put("cp_promotion_end",""+cursor.getInt(cursor.getColumnIndexOrThrow(CP_PROMOTION_END)));
                map_mark_quest.put("cp_id_graphic_material",""+cursor.getString(cursor.getColumnIndexOrThrow(CP_ID_GRAPHIC_MATERIAL)));
                map_mark_quest.put("cp_activation",""+cursor.getInt(cursor.getColumnIndexOrThrow(CP_ACTIVATION)));
                map_mark_quest.put("cp_id_promotion_type",""+cursor.getInt(cursor.getColumnIndexOrThrow(CP_ID_PROMOTION_TYPE)));
                map_mark_quest.put("cp_mechanics",""+cursor.getString(cursor.getColumnIndexOrThrow(CP_MECHANICS)));
                map_mark_quest.put("cp_differed_sku",""+cursor.getInt(cursor.getColumnIndexOrThrow(CP_DIFFERED_SKU)));
                map_mark_quest.put("cp_exhibition",""+cursor.getInt(cursor.getColumnIndexOrThrow(CP_EXHIBITION)));
                map_mark_quest.put("cp_id_exhibition_type",""+cursor.getInt(cursor.getColumnIndexOrThrow(CP_ID_EXHIBITION_TYPE)));
                map_mark_quest.put("cp_existence",""+cursor.getInt(cursor.getColumnIndexOrThrow(CP_EXISTENCE)));
                map_mark_quest.put("cp_id_resource_type",""+cursor.getInt(cursor.getColumnIndexOrThrow(CP_ID_RESOURCE_TYPE)));
                map_mark_quest.put("cp_evidence",""+cursor.getString(cursor.getColumnIndexOrThrow(CP_EVIDENCE)));
                map_mark_quest.put("ws_boxes_out",""+cursor.getInt(cursor.getColumnIndexOrThrow(WS_BOXES_OUT)));
                map_mark_quest.put("ws_final_stock",""+cursor.getInt(cursor.getColumnIndexOrThrow(WS_FINAL_STOCK)));
                map_mark_quest.put("s_r",""+cursor.getInt(cursor.getColumnIndexOrThrow(S_R)));
                map_mark_quest.put("q_i",""+cursor.getInt(cursor.getColumnIndexOrThrow(Q_I)));
                map_mark_quest.put("o_p",""+cursor.getInt(cursor.getColumnIndexOrThrow(O_P)));
                map_mark_quest.put("c_p",""+cursor.getInt(cursor.getColumnIndexOrThrow(C_P)));
                map_mark_quest.put("w_s",""+cursor.getInt(cursor.getColumnIndexOrThrow(W_S)));

                list.add(map_mark_quest);
            }

            cursor.close();
            return list;
        }
        return null;
    }

    public static ArrayList<Map<String,String>> getByVisitIdAndCompProductId(Context context, String product_id,String visit_id){

        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, new String [] {ID,SR_CURRENT, SR_SHELF_BOXES,
                        SR_EXHIBITION_BOXES, SR_PRICE, SR_EXPIRATION,QI_ID_PROBLEM_TYPE,QI_DESCRIPTION,QI_BATCH,QI_PRODUCT_LINE,
                        QI_QUANTITY,QI_EVIDENCE,OP_PUBLIC_PRICE,OP_PROMOTION_PRICE,OP_PROMOTION_START,OP_PROMOTION_END,
                        OP_ID_GRAPHIC_MATERIAL,OP_ACTIVATION,OP_ID_PROMOTION_TYPE,OP_MECHANICS,OP_DIFFERED_SKU,
                        OP_EXHIBITION,OP_ID_EXHIBITION_TYPE,OP_EXISTENCE,OP_ID_RESOURCE_TYPE,OP_EVIDENCE,CP_PRODUCT_ID,
                        CP_PUBLIC_PRICE,CP_PROMOTION_PRICE,CP_PROMOTION_START,CP_PROMOTION_END,CP_ID_GRAPHIC_MATERIAL,
                        CP_ACTIVATION,CP_ID_PROMOTION_TYPE,CP_MECHANICS,CP_DIFFERED_SKU,CP_EXHIBITION,CP_ID_EXHIBITION_TYPE,
                        CP_EXISTENCE,CP_ID_RESOURCE_TYPE,CP_EVIDENCE,WS_BOXES_OUT,WS_FINAL_STOCK,S_R,Q_I,O_P,C_P,W_S},
                "cp_product_id = ? AND visit_id = ?",
                new String[] { product_id, visit_id},
                null,
                null,
                ID);
        if (cursor != null && cursor.getCount() > 0) {
            ArrayList<Map<String,String>> list = new ArrayList<Map<String,String>>();
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                Map<String,String> map_mark_quest = new HashMap<String, String>();
                map_mark_quest.put("id",""+cursor.getInt(cursor.getColumnIndexOrThrow(ID)));
                //map_mark_quest.put("product_id",""+cursor.getInt(cursor.getColumnIndexOrThrow(PRODUCT_ID)));
                //map_mark_quest.put("visit_id",""+cursor.getInt(cursor.getColumnIndexOrThrow(VISIT_ID)));
                map_mark_quest.put("sr_current",""+cursor.getInt(cursor.getColumnIndexOrThrow(SR_CURRENT)));
                map_mark_quest.put("sr_shelf_boxes",""+cursor.getInt(cursor.getColumnIndexOrThrow(SR_SHELF_BOXES)));
                map_mark_quest.put("sr_exhibition_boxes",""+cursor.getInt(cursor.getColumnIndexOrThrow(SR_EXHIBITION_BOXES)));
                map_mark_quest.put("sr_price",""+cursor.getDouble(cursor.getColumnIndexOrThrow(SR_PRICE)));
                map_mark_quest.put("sr_expiration",""+cursor.getInt(cursor.getColumnIndexOrThrow(SR_EXPIRATION)));
                map_mark_quest.put("qi_id_problem_type",""+cursor.getInt(cursor.getColumnIndexOrThrow(QI_ID_PROBLEM_TYPE)));
                map_mark_quest.put("qi_description",""+cursor.getString(cursor.getColumnIndexOrThrow(QI_DESCRIPTION)));
                map_mark_quest.put("qi_batch",""+cursor.getInt(cursor.getColumnIndexOrThrow(QI_BATCH)));
                map_mark_quest.put("qi_product_line",""+cursor.getInt(cursor.getColumnIndexOrThrow(QI_PRODUCT_LINE)));
                map_mark_quest.put("qi_quantity",""+cursor.getInt(cursor.getColumnIndexOrThrow(QI_QUANTITY)));
                map_mark_quest.put("qi_evidence",""+cursor.getString(cursor.getColumnIndexOrThrow(QI_EVIDENCE)));
                map_mark_quest.put("op_public_price",""+cursor.getDouble(cursor.getColumnIndexOrThrow(OP_PUBLIC_PRICE)));
                map_mark_quest.put("op_promotion_price",""+cursor.getDouble(cursor.getColumnIndexOrThrow(OP_PROMOTION_PRICE)));
                map_mark_quest.put("op_promotion_start",""+cursor.getInt(cursor.getColumnIndexOrThrow(OP_PROMOTION_START)));
                map_mark_quest.put("op_promotion_end",""+cursor.getInt(cursor.getColumnIndexOrThrow(OP_PROMOTION_END)));
                map_mark_quest.put("op_id_graphic_material",""+cursor.getString(cursor.getColumnIndexOrThrow(OP_ID_GRAPHIC_MATERIAL)));
                map_mark_quest.put("op_activation",""+cursor.getInt(cursor.getColumnIndexOrThrow(OP_ACTIVATION)));
                map_mark_quest.put("op_id_promotion_type",""+cursor.getInt(cursor.getColumnIndexOrThrow(OP_ID_PROMOTION_TYPE)));
                map_mark_quest.put("op_mechanics",""+cursor.getString(cursor.getColumnIndexOrThrow(OP_MECHANICS)));
                map_mark_quest.put("op_differed_sku",""+cursor.getInt(cursor.getColumnIndexOrThrow(OP_DIFFERED_SKU)));
                map_mark_quest.put("op_exhibition",""+cursor.getInt(cursor.getColumnIndexOrThrow(OP_EXHIBITION)));
                map_mark_quest.put("op_id_exhibition_type",""+cursor.getInt(cursor.getColumnIndexOrThrow(OP_ID_EXHIBITION_TYPE)));
                map_mark_quest.put("op_existence",""+cursor.getInt(cursor.getColumnIndexOrThrow(OP_EXISTENCE)));
                map_mark_quest.put("op_id_resource_type",""+cursor.getInt(cursor.getColumnIndexOrThrow(OP_ID_RESOURCE_TYPE)));
                map_mark_quest.put("op_evidence",""+cursor.getString(cursor.getColumnIndexOrThrow(OP_EVIDENCE)));
                map_mark_quest.put("cp_product_id",""+cursor.getInt(cursor.getColumnIndexOrThrow(CP_PRODUCT_ID)));
                map_mark_quest.put("cp_public_price",""+cursor.getDouble(cursor.getColumnIndexOrThrow(CP_PUBLIC_PRICE)));
                map_mark_quest.put("cp_promotion_price",""+cursor.getDouble(cursor.getColumnIndexOrThrow(CP_PROMOTION_PRICE)));
                map_mark_quest.put("cp_promotion_start",""+cursor.getInt(cursor.getColumnIndexOrThrow(CP_PROMOTION_START)));
                map_mark_quest.put("cp_promotion_end",""+cursor.getInt(cursor.getColumnIndexOrThrow(CP_PROMOTION_END)));
                map_mark_quest.put("cp_id_graphic_material",""+cursor.getString(cursor.getColumnIndexOrThrow(CP_ID_GRAPHIC_MATERIAL)));
                map_mark_quest.put("cp_activation",""+cursor.getInt(cursor.getColumnIndexOrThrow(CP_ACTIVATION)));
                map_mark_quest.put("cp_id_promotion_type",""+cursor.getInt(cursor.getColumnIndexOrThrow(CP_ID_PROMOTION_TYPE)));
                map_mark_quest.put("cp_mechanics",""+cursor.getString(cursor.getColumnIndexOrThrow(CP_MECHANICS)));
                map_mark_quest.put("cp_differed_sku",""+cursor.getInt(cursor.getColumnIndexOrThrow(CP_DIFFERED_SKU)));
                map_mark_quest.put("cp_exhibition",""+cursor.getInt(cursor.getColumnIndexOrThrow(CP_EXHIBITION)));
                map_mark_quest.put("cp_id_exhibition_type",""+cursor.getInt(cursor.getColumnIndexOrThrow(CP_ID_EXHIBITION_TYPE)));
                map_mark_quest.put("cp_existence",""+cursor.getInt(cursor.getColumnIndexOrThrow(CP_EXISTENCE)));
                map_mark_quest.put("cp_id_resource_type",""+cursor.getInt(cursor.getColumnIndexOrThrow(CP_ID_RESOURCE_TYPE)));
                map_mark_quest.put("cp_evidence",""+cursor.getString(cursor.getColumnIndexOrThrow(CP_EVIDENCE)));
                map_mark_quest.put("ws_boxes_out",""+cursor.getInt(cursor.getColumnIndexOrThrow(WS_BOXES_OUT)));
                map_mark_quest.put("ws_final_stock",""+cursor.getInt(cursor.getColumnIndexOrThrow(WS_FINAL_STOCK)));
                map_mark_quest.put("s_r",""+cursor.getInt(cursor.getColumnIndexOrThrow(S_R)));
                map_mark_quest.put("q_i",""+cursor.getInt(cursor.getColumnIndexOrThrow(Q_I)));
                map_mark_quest.put("o_p",""+cursor.getInt(cursor.getColumnIndexOrThrow(O_P)));
                map_mark_quest.put("c_p",""+cursor.getInt(cursor.getColumnIndexOrThrow(C_P)));
                map_mark_quest.put("w_s",""+cursor.getInt(cursor.getColumnIndexOrThrow(W_S)));

                list.add(map_mark_quest);
            }

            cursor.close();
            return list;
        }
        return null;
    }

    public static void removeAll(Context context){
        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, null, null, null ,null, ID);
        if (cursor != null && cursor.getCount() > 0) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                int id              = cursor.getInt(cursor.getColumnIndexOrThrow(ID));
                MarketingQuest.delete(context,id);
            }
        }
        cursor.close();
    }

    public static int delete(Context context, int id) {
        return DataBaseAdapter.getDB(context).delete(TABLE, ID + "=" + id, null);
    }

/*
    public MarketingQuest(int id,int product_id,int visit_id,int sr_current,int sr_shelf_boxes,int sr_exhibition_boxes,double sr_price,int sr_expiration,
                          int qi_id_problem_type,String qi_description,int qi_batch,int qi_product_line,int qi_quantity,String qi_evidence,double op_public_price,double op_promotion_price,
                          int op_promotion_start,int op_promotion_end,String op_id_graphic_material,int op_activation,int op_id_promotion_type,String op_mechanics,
                          int op_differed_sku,int op_exhibition,int op_id_exhibition_type,int op_existence,int op_id_resource_type,String op_evidence,int cp_product_id,
                          double cp_public_price,double cp_promotion_price,int cp_promotion_start,int cp_promotion_end,String cp_id_graphic_material,int cp_activation,
                          int cp_id_promotion_type,String cp_mechanics,int cp_differed_sku,int cp_exhibition,int cp_id_exhibition_type,int cp_existence,
                          int cp_id_resource_type,String cp_evidence,int ws_boxes_out,int ws_final_stock,int s_r,int q_i,int o_p,int c_p,int w_s){

        this.id                     = id;
        this.product_id             = product_id;
        this.visit_id               = visit_id;
        this.sr_current             = sr_current;
        this.sr_shelf_boxes         = sr_shelf_boxes;
        this.sr_exhibition_boxes    = sr_exhibition_boxes;
        this.sr_price               = sr_price;
        this.sr_expiration          = sr_expiration;
        this.qi_id_problem_type     = qi_id_problem_type;
        this.qi_description         = qi_description;
        this.qi_batch               = qi_batch;
        this.qi_product_line        = qi_product_line;
        this.qi_quantity            = qi_quantity;
        this.qi_evidence            = qi_evidence;
        this.op_public_price        = op_public_price;
        this.op_promotion_price     = op_promotion_price;
        this.op_promotion_start     = op_promotion_start;
        this.op_promotion_end       = op_promotion_end;
        this.op_id_graphic_material = op_id_graphic_material;
        this.op_activation          = op_activation;
        this.op_id_promotion_type   = op_id_promotion_type;
        this.op_mechanics           = op_mechanics;
        this.op_differed_sku        = op_differed_sku;
        this.op_exhibition          = op_exhibition;
        this.op_id_exhibition_type  = op_id_exhibition_type;
        this.op_existence           = op_existence;
        this.op_id_resource_type    = op_id_resource_type;
        this.op_evidence            = op_evidence;
        this.cp_product_id          = cp_product_id;
        this.cp_public_price        = cp_public_price;
        this.cp_promotion_price     = cp_promotion_price;
        this.cp_promotion_start     = cp_promotion_start;
        this.cp_promotion_end       = cp_promotion_end;
        this.cp_id_graphic_material = cp_id_graphic_material;
        this.cp_activation          = cp_activation;
        this.cp_id_promotion_type   = cp_id_promotion_type;
        this.cp_mechanics           = cp_mechanics;
        this.cp_differed_sku        = cp_differed_sku;
        this.cp_exhibition          = cp_exhibition;
        this.cp_id_exhibition_type  = cp_id_exhibition_type;
        this.cp_existence           = cp_existence;
        this.cp_id_resource_type    = cp_id_resource_type;
        this.cp_evidence            = cp_evidence;
        this.ws_boxes_out           = ws_boxes_out;
        this.ws_final_stock         = ws_final_stock;
        this.s_r                    = s_r;
        this.q_i                    = q_i;
        this.o_p                    = o_p;
        this.c_p                    = c_p;
        this.w_s                    = w_s;

    }
*/

/*
    public static MarketingQuest getByVisitIdAndProductId(Context context, String product_id,String visit_id) {

        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, new String [] {ID,SR_CURRENT, SR_SHELF_BOXES,
                        SR_EXHIBITION_BOXES, SR_PRICE, SR_EXPIRATION,IQ_ID_PROBLEM_TYPE,IQ_DESCRIPTION,IQ_BATCH,IQ_PRODUCT_LINE,
                        IQ_QUANTITY,IQ_EVIDENCE,OP_PUBLIC_PRICE,OP_PROMOTION_PRICE,OP_PROMOTION_START,OP_PROMOTION_END,
                        OP_ID_GRAPHIC_MATERIAL,OP_ACTIVATION,OP_ID_PROMOTION_TYPE,OP_MECHANICS,OP_DIFFERED_SKU,
                        OP_EXHIBITION,OP_ID_EXHIBITION_TYPE,OP_EXISTENCE,OP_ID_RESOURCE_TYPE,OP_EVIDENCE,CP_PRODUCT_ID,
                        CP_PUBLIC_PRICE,CP_PROMOTION_PRICE,CP_PROMOTION_START,CP_PROMOTION_END,CP_ID_GRAPHIC_MATERIAL,
                        CP_ACTIVATION,CP_ID_PROMOTION_TYPE,CP_MECHANICS,CP_DIFFERED_SKU,CP_EXHIBITION,CP_ID_EXHIBITION_TYPE,
                        CP_EXISTENCE,CP_ID_RESOURCE_TYPE,CP_EVIDENCE,WS_BOXES_OUT,WS_FINAL_STOCK,S_R,Q_I,O_P,C_P,W_S},
                "product_id = ? AND visit_id = ?",
                new String[] { product_id, product_id},
                null,
                null,
                ID);

        if (cursor != null && cursor.moveToFirst()) {

            int     id                      = cursor.getInt(cursor.getColumnIndexOrThrow(ID));
            int     sr_current              = cursor.getInt(cursor.getColumnIndexOrThrow(SR_CURRENT));
            int     sr_shelf_boxes          = cursor.getInt(cursor.getColumnIndexOrThrow(SR_SHELF_BOXES));
            int     sr_exhibition_boxes     = cursor.getInt(cursor.getColumnIndexOrThrow(SR_EXHIBITION_BOXES));
            double  sr_price                = cursor.getDouble(cursor.getColumnIndexOrThrow(SR_PRICE));
            int     sr_expiration           = cursor.getInt(cursor.getColumnIndexOrThrow(SR_EXPIRATION));
            int     qi_id_problem_type      = cursor.getInt(cursor.getColumnIndexOrThrow(QI_ID_PROBLEM_TYPE));
            String  qi_description          = cursor.getString(cursor.getColumnIndexOrThrow(QI_DESCRIPTION));
            int     qi_batch                = cursor.getInt(cursor.getColumnIndexOrThrow(QI_BATCH));
            int     qi_product_line         = cursor.getInt(cursor.getColumnIndexOrThrow(QI_PRODUCT_LINE));
            int     qi_quantity             = cursor.getInt(cursor.getColumnIndexOrThrow(QI_QUANTITY));
            String  qi_evidence             = cursor.getString(cursor.getColumnIndexOrThrow(QI_EVIDENCE));
            double  op_public_price         = cursor.getDouble(cursor.getColumnIndexOrThrow(OP_PUBLIC_PRICE));
            double  op_promotion_price      = cursor.getDouble(cursor.getColumnIndexOrThrow(OP_PROMOTION_PRICE));
            int     op_promotion_start      = cursor.getInt(cursor.getColumnIndexOrThrow(OP_PROMOTION_START));
            int     op_promotion_end        = cursor.getInt(cursor.getColumnIndexOrThrow(OP_PROMOTION_END));
            String  op_id_graphic_material  = cursor.getString(cursor.getColumnIndexOrThrow(OP_ID_GRAPHIC_MATERIAL));
            int     op_activation           = cursor.getInt(cursor.getColumnIndexOrThrow(OP_ACTIVATION));
            int     op_id_promotion_type    = cursor.getInt(cursor.getColumnIndexOrThrow(OP_ID_PROMOTION_TYPE));
            String  op_mechanics            = cursor.getString(cursor.getColumnIndexOrThrow(OP_MECHANICS));
            int     op_differed_sku         = cursor.getInt(cursor.getColumnIndexOrThrow(OP_DIFFERED_SKU));
            int     op_exhibition           = cursor.getInt(cursor.getColumnIndexOrThrow(OP_EXHIBITION));
            int     op_id_exhibition_type   = cursor.getInt(cursor.getColumnIndexOrThrow(OP_ID_EXHIBITION_TYPE));
            int     op_existence            = cursor.getInt(cursor.getColumnIndexOrThrow(OP_EXISTENCE));
            int     op_id_resource_type     = cursor.getInt(cursor.getColumnIndexOrThrow(OP_ID_RESOURCE_TYPE));
            String  op_evidence             = cursor.getString(cursor.getColumnIndexOrThrow(OP_EVIDENCE));
            int     cp_product_id           = cursor.getInt(cursor.getColumnIndexOrThrow(CP_PRODUCT_ID));
            double  cp_public_price         = cursor.getDouble(cursor.getColumnIndexOrThrow(CP_PUBLIC_PRICE));
            double  cp_promotion_price      = cursor.getDouble(cursor.getColumnIndexOrThrow(CP_PROMOTION_PRICE));
            int     cp_promotion_start      = cursor.getInt(cursor.getColumnIndexOrThrow(CP_PROMOTION_START));
            int     cp_promotion_end        = cursor.getInt(cursor.getColumnIndexOrThrow(CP_PROMOTION_END));
            String  cp_id_graphic_material  = cursor.getString(cursor.getColumnIndexOrThrow(CP_ID_GRAPHIC_MATERIAL));
            int     cp_activation           = cursor.getInt(cursor.getColumnIndexOrThrow(CP_ACTIVATION));
            int     cp_id_promotion_type    = cursor.getInt(cursor.getColumnIndexOrThrow(CP_ID_PROMOTION_TYPE));
            String  cp_mechanics            = cursor.getString(cursor.getColumnIndexOrThrow(CP_MECHANICS));
            int     cp_differed_sku         = cursor.getInt(cursor.getColumnIndexOrThrow(CP_DIFFERED_SKU));
            int     cp_exhibition           = cursor.getInt(cursor.getColumnIndexOrThrow(CP_EXHIBITION));
            int     cp_id_exhibition_type   = cursor.getInt(cursor.getColumnIndexOrThrow(CP_ID_EXHIBITION_TYPE));
            int     cp_existence            = cursor.getInt(cursor.getColumnIndexOrThrow(CP_EXISTENCE));
            int     cp_id_resource_type     = cursor.getInt(cursor.getColumnIndexOrThrow(CP_ID_RESOURCE_TYPE));
            String  cp_evidence             = cursor.getString(cursor.getColumnIndexOrThrow(CP_EVIDENCE));
            int     ws_boxes_out            = cursor.getInt(cursor.getColumnIndexOrThrow(WS_BOXES_OUT));
            int     ws_final_stock          = cursor.getInt(cursor.getColumnIndexOrThrow(WS_FINAL_STOCK));
            int     s_r                     = cursor.getInt(cursor.getColumnIndexOrThrow(S_R));
            int     q_i                     = cursor.getInt(cursor.getColumnIndexOrThrow(Q_I));
            int     o_p                     = cursor.getInt(cursor.getColumnIndexOrThrow(O_P));
            int     c_p                     = cursor.getInt(cursor.getColumnIndexOrThrow(C_P));
            int     w_s                     = cursor.getInt(cursor.getColumnIndexOrThrow(W_S));


            cursor.close();

            return new MarketingQuest(id,Integer.parseInt(product_id),Integer.parseInt(visit_id),sr_current,sr_shelf_boxes,sr_exhibition_boxes,sr_price,sr_expiration,
                    qi_id_problem_type,qi_description,qi_batch,qi_product_line,qi_quantity,qi_evidence,op_public_price,op_promotion_price,
                    op_promotion_start,op_promotion_end,op_id_graphic_material,op_activation,op_id_promotion_type,op_mechanics,
                    op_differed_sku,op_exhibition,op_id_exhibition_type,op_existence,op_id_resource_type,op_evidence,cp_product_id,
                    cp_public_price,cp_promotion_price,cp_promotion_start,cp_promotion_end,cp_id_graphic_material,cp_activation,
                    cp_id_promotion_type,cp_mechanics,cp_differed_sku,cp_exhibition,cp_id_exhibition_type,cp_existence,
                    cp_id_resource_type,cp_evidence,ws_boxes_out,ws_final_stock,s_r,q_i,o_p,c_p,w_s);
        }
        return null;
    }
*/




}
