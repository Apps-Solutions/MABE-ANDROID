package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import database.models.Brand;
import database.models.CancelledOrder;
import database.models.CheckIn;
import database.models.CheckOut;
import database.models.Customer;
import database.models.CustomerWorkPlan;
import database.models.Evidence;
import database.models.EvidenceBrandProduct;
import database.models.EvidenceProduct;
import database.models.Exhibition;
import database.models.ExtraTasks;
import database.models.Form;
import database.models.Fueling;
import database.models.InventoryProduct;
import database.models.Invoice;
import database.models.MarketingQuest;
import database.models.Module;
import database.models.NoSaleReason;
import database.models.NoSaleReasonRecord;
import database.models.Note;
import database.models.Offers;
import database.models.Order;
import database.models.OrderDetail;
import database.models.OrderSent;
import database.models.OrderSentProduct;
import database.models.OrderStatus;
import database.models.PartialPay;
import database.models.PaymentType;
import database.models.PendingOrder;
import database.models.PendingOrderProduct;
import database.models.People;
import database.models.Permission;
import database.models.Product;
import database.models.ProductBrand;
import database.models.ProductCategory;
import database.models.ProductPic;
import database.models.ProductShelfReview;
import database.models.Profile;
import database.models.Promotion;
import database.models.QualityIncidence;
import database.models.QualityProblemType;
import database.models.QuestionsSectionForm;
import database.models.Receipt;
import database.models.Receiving;
import database.models.ReceivingItem;
import database.models.RecordActions;
import database.models.Refund;
import database.models.RefundProduct;
import database.models.ResourceType;
import database.models.Sale;
import database.models.SaleItem;
import database.models.SectionsForm;
import database.models.Session;
import database.models.Shelf_Review;
import database.models.Shelf_ReviewProduct;
import database.models.Signature;
import database.models.Supplier;
import database.models.User;
import database.models.UserInfo;
import database.models.Warehouse;
import database.models.WholeSalesPrices;
import database.models.WholeSalesPricesProduct;
import util.LogUtil;

import static util.LogUtil.makeLogTag;

/**
 * Created by jcenteno on 12/05/14.
 */
public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String TAG = makeLogTag(DataBaseHelper.class);
    private static final String DATABASE_NAME = "tracker.db";

    // NOTE: carefully update onUpgrade() when bumping database versions to make
    // sure user data is saved.

    private static final int VER_1            = 7;  // 1.1 Versión 6 - se añaden tareas genéricas |
                                                        // Versión 7 - se agregan columnas a la tabla de UserInfo
    private static final int DATABASE_VERSION = VER_1;
    public static final int NOT_UPDATE        = -1;
    private final Context mContext;

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        LogUtil.addCheckpoint("DatabaseHelper : onCreate");

        db.execSQL("CREATE TABLE "+ User.TABLE+" ("
                +"id integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
                +"username varchar(250) NOT NULL,"
                +"password varchar(250) NOT NULL,"
                +"profile_id integer NOT NULL,"
                +"people_id integer NOT NULL,"
                +"FOREIGN KEY (profile_id) REFERENCES profile (id),"
                +"FOREIGN KEY (people_id) REFERENCES profile (id))");

        db.execSQL("CREATE TABLE "+ Module.TABLE+" ("
                +"id integer NOT NULL PRIMARY KEY,"
                +"name varchar(250) NOT NULL)");

        db.execSQL("CREATE TABLE "+ Permission.TABLE+" ("
                +"id integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
                +"profile_id integer NOT NULL,"
                +"module_id integer NOT NULL,"
                +"FOREIGN KEY (profile_id) REFERENCES profile (id),"
                +"FOREIGN KEY (module_id) REFERENCES module (id))");

        db.execSQL("CREATE TABLE "+ Profile.TABLE+" ("
                +"id integer NOT NULL PRIMARY KEY,"
                +"name varchar(250) NOT NULL)");

        db.execSQL("CREATE TABLE "+ Note.TABLE+" ("
                +"id integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
                +"description text NOT NULL,"
                +"user_id integer NOT NULL,"
                +"timestamp varchar(250) NOT NULL,"
                +"FOREIGN KEY (user_id) REFERENCES user (id))");

        db.execSQL("CREATE TABLE "+ RecordActions.TABLE+" ("
                +"id integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
                +"data text NOT NULL,"
                +"name_webservice varchar(250) NOT NULL,"
                +"user_id integer NOT NULL,"
                +"FOREIGN KEY (user_id) REFERENCES user (id))");

        db.execSQL("CREATE TABLE "+Product.TABLE+" ("
                +"id integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
                +"barcode varchar(250),"
                +"jde varchar(20),"
                +"name varchar(250) NOT NULL,"
                +"description text,"
                +"unit_price integer,"
                +"cost_price integer,"
                +"tax float,"
                +"stock integer,"
                +"stock_central integer,"
                +"product_category_id integer NOT NULL,"
                +"product_brand_id integer NOT NULL,"
                +"rival varchar(250),"
                +"FOREIGN KEY (product_category_id) REFERENCES product_category (id),"
                +"FOREIGN KEY (product_brand_id) REFERENCES product_brand (id))");

        db.execSQL("CREATE TABLE "+ ProductBrand.TABLE+" ("
                +"id integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
                +"name varchar(250) NOT NULL)");

        db.execSQL("CREATE TABLE "+ ProductCategory.TABLE+" ("
                +"id integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
                +"name varchar(250) NOT NULL,"
                +"product_category_id integer,"
                +"FOREIGN KEY (product_category_id) REFERENCES product_category (id))");

        db.execSQL("CREATE TABLE "+ ProductPic.TABLE+" ("
                +"id integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
                +"source text NOT NULL,"
                +"product_id integer NOT NULL,"
                +"main integer NOT NULL,"
                +"FOREIGN KEY (product_id) REFERENCES product (id))");

        db.execSQL("CREATE TABLE "+ Customer.TABLE+" ("
                +"id integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
                +"account integer NOT NULL,"
                +"taxable integer NOT NULL,"
                +"people_id integer NOT NULL,"
                +"FOREIGN KEY (people_id) REFERENCES people (id))");

        db.execSQL("CREATE TABLE "+ PaymentType.TABLE+" ("
                +"id integer NOT NULL PRIMARY KEY,"
                +"name varchar(250) NOT NULL)");

        db.execSQL("CREATE TABLE "+ Receipt.TABLE+" ("
                +"id integer NOT NULL PRIMARY KEY,"
                +"image text NOT NULL)");

        db.execSQL("CREATE TABLE "+ People.TABLE +" (" +
                "    id integer NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "    first_name varchar(250) NOT NULL," +
                "    last_name varchar(250) NOT NULL," +
                "    email varchar(250) NOT NULL," +
                "    rfc varchar(250)," +
                "    phone_number varchar(250)," +
                "    address_1 varchar(250)," +
                "    address_2 varchar(250)," +
                "    city varchar(250)," +
                "    state varchar(250)," +
                "    country varchar(250)," +
                "    comments varchar(250)," +
                "    zip_code integer," +
                "    photo blob" +
                ")");

        db.execSQL("CREATE TABLE "+ Session.TABLE+" (" +
                "    id integer NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "    status integer NOT NULL," +
                "    timestamp datetime NOT NULL DEFAULT (datetime('now','localtime'))," +
                "    token varchar(250) NOT NULL," +
                "    device varchar(250) NOT NULL," +
                "    user_id integer NOT NULL," +
                "    FOREIGN KEY (user_id) REFERENCES user (id)" +
                ")");

        db.execSQL("CREATE TABLE "+ Receiving.TABLE+" ("
                +"id integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
                +"timestamp datetime DEFAULT (datetime('now','localtime')),"
                +"payment_type_id integer NOT NULL,"
                +"session_id integer NOT NULL,"
                +"supplier_id integer NOT NULL,"
                +"receipt_id integer NOT NULL,"
                +"FOREIGN KEY (payment_type_id) REFERENCES payment_type (id),"
                +"FOREIGN KEY (session_id) REFERENCES session (id),"
                +"FOREIGN KEY (supplier_id) REFERENCES supplier (id),"
                +"FOREIGN KEY (receipt_id) REFERENCES receipt (id))");

        db.execSQL("CREATE TABLE "+ ReceivingItem.TABLE+" ("
                +"id integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
                +"receiving_id integer NOT NULL,"
                +"product_id integer NOT NULL,"
                +"quantity integer NOT NULL,"
                +"item_cost double,"
                +"FOREIGN KEY (receiving_id) REFERENCES receiving (id),"
                +"FOREIGN KEY (product_id) REFERENCES product (id))");

        db.execSQL("CREATE TABLE "+ Sale.TABLE+" ("
                +"id integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
                +"timestamp datetime DEFAULT (datetime('now','localtime')),"
                +"payment_type_id integer NOT NULL,"
                +"session_id integer NOT NULL,"
                +"customer_id integer NOT NULL,"
                +"receipt_id integer NOT NULL,"
                +"FOREIGN KEY (payment_type_id) REFERENCES payment_type (id),"
                +"FOREIGN KEY (session_id) REFERENCES session (id),"
                +"FOREIGN KEY (customer_id) REFERENCES customer (id),"
                +"FOREIGN KEY (receipt_id) REFERENCES receipt (id))");

        db.execSQL("CREATE TABLE "+ SaleItem.TABLE +" (" +
                "id integer NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "quantity integer NOT NULL," +
                "price_unit double NOT NULL," +
                "discount_percent float," +
                "sale_id integer NOT NULL," +
                "product_id integer NOT NULL," +
                "FOREIGN KEY (sale_id) REFERENCES sale (id)," +
                "FOREIGN KEY (product_id) REFERENCES product (id))");

        //Table of evidence module
        db.execSQL("CREATE TABLE "+ Evidence.TABLE +" (" +
                "id integer NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "name varchar(250) NOT NULL)");

        db.execSQL("CREATE TABLE "+ EvidenceBrandProduct.TABLE +" (" +
                "id integer NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "name varchar(250) NOT NULL," +
                "id_evidence integer NOT NULL," +
                "FOREIGN KEY (id_evidence) REFERENCES evidence (id))");


        db.execSQL("CREATE TABLE "+ EvidenceProduct.TABLE +" (" +
                "id integer NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "name varchar(250) NOT NULL," +
                "image_product varchar(250) NOT NULL," +
                "id_product_brand integer NOT NULL, " +
                "comment varchar(250) NOT NULL, " +
                "FOREIGN KEY (id_product_brand) REFERENCES evidence_brand_product (id))");

        db.execSQL("CREATE TABLE "+ Supplier.TABLE +" (" +
                "id integer NOT NULL PRIMARY KEY," +
                "company_name varchar(250) NOT NULL," +
                "account integer NOT NULL," +
                "people_id integer NOT NULL," +
                "FOREIGN KEY (people_id) REFERENCES people (id))");

        db.execSQL("CREATE TABLE "+ Order.TABLE+" ("
                +"id integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
                +"order_date datetime DEFAULT (datetime('now','localtime')),"
                +"required_date datetime NOT NULL,"
                +"shipped_date datetime NOT NULL,"
                +"customer_id integer NOT NULL,"
                +"session_id integer NOT NULL,"
                +"order_status_id integer NOT NULL,"
                +"receipt_id integer NOT NULL,"
                +"FOREIGN KEY (customer_id) REFERENCES customer (id),"
                +"FOREIGN KEY (order_status_id) REFERENCES order_status (id),"
                +"FOREIGN KEY (session_id) REFERENCES session (id),"
                +"FOREIGN KEY (receipt_id) REFERENCES receipt (id))");

        db.execSQL("CREATE TABLE "+ OrderDetail.TABLE +" (" +
                "    id integer NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "    order_id integer NOT NULL," +
                "    product_id integer NOT NULL," +
                "    unit_price float NOT NULL," +
                "    quantity integer NOT NULL," +
                "    discount float NOT NULL," +
                "    FOREIGN KEY (order_id) REFERENCES orders (id)," +
                "    FOREIGN KEY (product_id) REFERENCES product (id))");

        db.execSQL("CREATE TABLE "+ OrderStatus.TABLE +" (" +
                "    id integer NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "    status varchar(250) NOT NULL)");

        db.execSQL("CREATE TABLE "+ OrderSent.TABLE+" ("
                +"id integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
                +"date_time varchar(250) NOT NULL,"
                +"order_id varchar(250) NOT NULL,"
                +"pdv_id varchar(250) NOT NULL,"
                +"visit_id varchar(250) NOT NULL,"

                +"total varchar(250) NOT NULL,"
                +"subtotal varchar(250) NOT NULL,"
                +"tax varchar(250) NOT NULL,"

                +"method_id varchar(250) NOT NULL,"

                +"username varchar(250) NOT NULL,"
                +"status varchar(250) NOT NULL,"
                +"sent varchar(250) NOT NULL,"
                +"paid varchar(250) NOT NULL,"
                +"type varchar(250) NOT NULL,"
                +"payment_date varchar(250) NOT NULL)");

        db.execSQL("CREATE TABLE "+ OrderSentProduct.TABLE+" ("
                +"id integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
                +"order_id varchar(250) NOT NULL,"
                +"name varchar(250) NOT NULL,"
                +"jde varchar(250) NOT NULL,"
                +"quantity int NOT NULL,"
                +"id_product varchar(250) NOT NULL,"
                +"price varchar(250) NOT NULL)");


        db.execSQL("CREATE TABLE "+ Shelf_Review.TABLE+" ("
                +"  id integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
                +"  visit_id varchar(250) NOT NULL,"
                +"  status varchar(250) NOT NULL)");

        db.execSQL("CREATE TABLE "+ Shelf_ReviewProduct.TABLE+" ("
                +"id integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
                +"id_product varchar(250) NOT NULL,"
                +"shelf_review_id varchar(250),"
                +"current varchar(250),"
                +"shelf_boxes varchar(20),"
                +"exhibition_boxes varchar(250) NOT NULL,"
                +"expiration varchar(250) NOT NULL,"
                +"price varchar(250) NOT NULL)");

        db.execSQL("CREATE TABLE "+ ResourceType.TABLE+" ("
                +"id varchar(250) NOT NULL PRIMARY KEY,"
                +"description varchar(250),"
                +"timestamp varchar(20),"
                +"status varchar(250) NOT NULL)");

        db.execSQL("CREATE TABLE "+ Promotion.TABLE+" ("
                +"id varchar(250) NOT NULL PRIMARY KEY,"
                +"description varchar(250),"
                +"timestamp varchar(20),"
                +"status varchar(250) NOT NULL)");

        db.execSQL("CREATE TABLE "+ Exhibition.TABLE+" ("
                +"id varchar(250) NOT NULL PRIMARY KEY,"
                +"description varchar(250),"
                +"timestamp varchar(20),"
                +"status varchar(250) NOT NULL)");

        db.execSQL("CREATE TABLE "+ QualityProblemType.TABLE+" ("
                +"id varchar(250) NOT NULL PRIMARY KEY,"
                +"description varchar(250),"
                +"timestamp varchar(20),"
                +"status varchar(250) NOT NULL)");

        db.execSQL("CREATE TABLE "+ Brand.TABLE+" ("
                +"id varchar(250) NOT NULL PRIMARY KEY,"
                +"rival varchar(250),"
                +"description varchar(250),"
                +"timestamp varchar(20),"
                +"status varchar(250) )");

        db.execSQL("CREATE TABLE "+ NoSaleReason.TABLE+" ("
                +"id varchar(250) NOT NULL PRIMARY KEY,"
                +"description varchar(250))");

        db.execSQL("CREATE TABLE "+ PendingOrder.TABLE+" ("
                +"id varchar(250) NOT NULL PRIMARY KEY,"
                +"id_pdv varchar(250),"
                +"total varchar(250),"
                +"subtotal varchar(20),"
                +"tax varchar(20),"
                +"user varchar(20),"
                +"date varchar(250) )");

        db.execSQL("CREATE TABLE "+ PendingOrderProduct.TABLE+" ("
                +"id varchar(250) NOT NULL,"
                +"price varchar(250),"
                +"name varchar(250),"
                +"tax varchar(250),"
                +"order_id varchar(20),"
                +"id_product_presentation varchar(20),"
                +"quantity varchar(250) )");

        db.execSQL("CREATE TABLE "+ CustomerWorkPlan.TABLE + " (" +
                "rfc                varchar(250),"+
                "jde                varchar(250),"+
                "visit_status       varchar(250),"+
                "pdv                varchar(250),"+
                "real_start         varchar(250),"+
                "visit_status_id    varchar(250),"+
                "pdv_id             varchar(250),"+
                "real_end           varchar(250),"+
                "address            varchar(250),"+
                "pdv_phone          varchar(250),"+
                "scheduled_start    varchar(250),"+
                "visit_id           varchar(50)  NOT NULL PRIMARY KEY,"+
                "latitude           varchar(250),"+
                "longitude          varchar(250),"+
                "visit_type         varchar(250),"+
                "visit_type_id      varchar(250))");

        db.execSQL("CREATE TABLE "+ UserInfo.TABLE + " (" +
                "id varchar(50) NOT NULL PRIMARY KEY,"+
                "user varchar(250) NOT NULL,"+
                "route varchar(250) NOT NULL,"+
                "id_viamente varchar(250) NOT NULL,"+
                "people_id varchar(250) NOT NULL,"+
                "name varchar(250) NOT NULL,"+
                "lastname varchar(250) NOT NULL,"+
                "branch_name varchar(250) NOT NULL,"+
                "branch_address varchar(250) NOT NULL,"+
                "branch_code varchar(250) NOT NULL,"+
                "jde varchar(250) NOT NULL)");

        db.execSQL("CREATE TABLE "+ CancelledOrder.TABLE + " (" +
                "id varchar(50) NOT NULL PRIMARY KEY,"+
                "id_pdv varchar(250) NOT NULL,"+
                "cancellation_order_date varchar(250) NOT NULL,"+
                "order_date_time varchar(250) NOT NULL)");

        db.execSQL("CREATE TABLE "+ MarketingQuest.TABLE + " (" +
                "id integer NOT NULL PRIMARY KEY AUTOINCREMENT,"+
                "product_id integer NOT NULL, "+
                "visit_id integer NOT NULL, "+
                "sr_current integer NOT NULL, "+
                "sr_shelf_boxes integer NOT NULL, "+
                "sr_exhibition_boxes integer NOT NULL, "+
                "sr_price double NOT NULL, "+
                "sr_expiration integer NOT NULL, "+
                "qi_id_problem_type integer NOT NULL, "+
                "qi_description varchar(250) NOT NULL, "+
                "qi_batch integer NOT NULL, "+
                "qi_product_line integer NOT NULL, "+
                "qi_quantity integer NOT NULL, "+
                "qi_evidence varchar(250) NOT NULL, "+
                "op_public_price double NOT NULL, "+
                "op_promotion_price double NOT NULL, "+
                "op_promotion_start integer NOT NULL, "+
                "op_promotion_end integer NOT NULL, "+
                "op_id_graphic_material varchar(250) NOT NULL, "+
                "op_activation integer NOT NULL, "+
                "op_id_promotion_type integer NOT NULL, "+
                "op_mechanics varchar(250) NOT NULL, "+
                "op_differed_sku integer NOT NULL, "+
                "op_exhibition integer NOT NULL, "+
                "op_id_exhibition_type integer NOT NULL, "+
                "op_existence integer NOT NULL, "+
                "op_id_resource_type integer NOT NULL, "+
                "op_evidence varchar(250) NOT NULL, "+
                "cp_product_id integer NOT NULL, "+
                "cp_public_price double NOT NULL, "+
                "cp_promotion_price double NOT NULL, "+
                "cp_promotion_start integer NOT NULL, "+
                "cp_promotion_end integer NOT NULL, "+
                "cp_id_graphic_material varchar(250) NOT NULL, "+
                "cp_activation integer NOT NULL, "+
                "cp_id_promotion_type integer NOT NULL, "+
                "cp_mechanics varchar(250) NOT NULL, "+
                "cp_differed_sku integer NOT NULL, "+
                "cp_exhibition integer NOT NULL, "+
                "cp_id_exhibition_type integer NOT NULL, "+
                "cp_existence integer NOT NULL, "+
                "cp_id_resource_type integer NOT NULL, "+
                "cp_evidence varchar(250) NOT NULL, "+
                "ws_boxes_out integer NOT NULL, "+
                "ws_final_stock integer NOT NULL,"+
                "s_r integer NOT NULL,"+
                "q_i integer NOT NULL,"+
                "o_p integer NOT NULL,"+
                "c_p integer NOT NULL,"+
                "w_s integer NOT NULL)");

        db.execSQL("CREATE TABLE " + CheckIn.TABLE + "( "+
                "id integer NOT NULL PRIMARY KEY AUTOINCREMENT, "+
                "latitude varchar(250) NOT NULL, "+
                "longitude varchar(250) NOT NULL, "+
                "date_time varchar(250) NOT NULL, "+
                "visit_id integer NOT NULL, "+
                "pdv_id integer NOT NULL, "+
                "status varchar(250) NOT NULL)");

        db.execSQL("CREATE TABLE " + CheckOut.TABLE + "( "+
                "id integer NOT NULL PRIMARY KEY AUTOINCREMENT, "+
                "latitude varchar(250) NOT NULL, "+
                "longitude varchar(250) NOT NULL, "+
                "date_time varchar(250) NOT NULL, "+
                "visit_id integer NOT NULL, "+
                "pdv_id integer NOT NULL, "+
                "status varchar(250) NOT NULL)");

        db.execSQL("CREATE TABLE " + Invoice.TABLE + "( "+
                "id varchar(250) NOT NULL PRIMARY KEY, "+
                "pdv_id varchar(250) NOT NULL, "+
                "pdv_name varchar(250) NOT NULL, "+
                "folio varchar(250) NOT NULL, "+
                "inv_date varchar(250) NOT NULL, "+
                "total varchar(250) NOT NULL," +
                "paid varchar(250)," +
                "status varchar(250)," +
                "method_id varchar(250)," +
                "payment_date varchar(250) NOT NULL"+
                ")");

        db.execSQL("CREATE TABLE "+ Refund.TABLE+" ("
                +"id integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
                +"order_date varchar(250) NOT NULL,"
                +"order_id varchar(250) NOT NULL,"
                +"pdv_id varchar(250) NOT NULL,"
                +"visit_id varchar(250) NOT NULL,"
                +"status varchar(250) NOT NULL,"
                +"sent varchar(250) NOT NULL,"
                +"username varchar(250) NOT NULL)");

        db.execSQL("CREATE TABLE "+ RefundProduct.TABLE+" ("
                +"id integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
                +"order_id varchar(250) NOT NULL,"
                +"quantity int NOT NULL,"
                +"product_id varchar(250) NOT NULL,"
                +"jde varchar(250) NOT NULL,"
                +"name varchar(250) NOT NULL)");

        db.execSQL("CREATE TABLE "+ PartialPay.TABLE+" ("
                +"id integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
                +"number varchar(250) NOT NULL,"
                +"total varchar(250) NOT NULL,"
                +"date varchar(250) NOT NULL,"
                +"evidence varchar(250) NOT NULL,"
                +"status varchar(250) NOT NULL)");

        db.execSQL("CREATE TABLE "+ Fueling.TABLE+" ("
                +"id integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
                +"fuel_type varchar(250) NOT NULL,"
                +"place varchar(250) NOT NULL,"
                +"km varchar(250) NOT NULL,"
                +"liter varchar(250) NOT NULL,"
                +"total varchar(250) NOT NULL,"
                +"folio varchar(250) NOT NULL,"
                +"evidence varchar(250),"
                +"comments varchar(250),"
                +"status varchar(250) NOT NULL)");

        db.execSQL("CREATE TABLE "+ QualityIncidence.TABLE+" ("
                +"id integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
                +"id_visit varchar(250) NOT NULL,"
                +"id_product varchar(250) NOT NULL,"
                +"id_problem_type varchar(250) NOT NULL,"
                +"name_problem_type varchar(250) NOT NULL,"
                +"description varchar(250) NOT NULL,"
                +"batch varchar(250) NOT NULL,"
                +"product_line varchar(250) NOT NULL,"
                +"quantity varchar(250) NOT NULL,"
                +"evidence varchar(250),"
                +"status varchar(250) NOT NULL)");

        db.execSQL("CREATE TABLE "+ ProductShelfReview.TABLE+" ("
                +"id integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
                +"id_visit varchar(250) NOT NULL,"
                +"id_product varchar(250) NOT NULL,"
                +"current varchar(250),"
                +"shelf_boxes varchar(20),"
                +"shelf_review_id varchar(250),"
                +"exhibition_boxes varchar(250) NOT NULL,"
                +"expiration varchar(250) NOT NULL,"
                +"price varchar(250) NOT NULL,"

                +"current_shelf varchar(250) NOT NULL,"
                +"current_exhibition varchar(250) NOT NULL,"

                +"front varchar(250) NOT NULL,"
                +"rival1 varchar(250) NOT NULL,"
                +"rival2 varchar(250) NOT NULL,"
                +"total varchar(250) NOT NULL,"

                +"status varchar(250) NOT NULL)");

        db.execSQL("CREATE TABLE "+ Offers.TABLE+" ("
                +"id integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
                +"id_visit varchar(250) NOT NULL,"
                +"id_product varchar(250) NOT NULL,"
                +"own_id_product varchar(250),"
                +"name_product varchar(250),"
                +"public_price varchar(250),"
                +"promotion_price varchar(250) NOT NULL,"
                +"promotion_start varchar(250) NOT NULL,"
                +"promotion_end varchar(250) NOT NULL,"
                +"id_graphic_material varchar(250),"
                +"activation varchar(250) NOT NULL,"
                +"id_promotion_type varchar(250) NOT NULL,"
                +"mechanics varchar(250),"
                +"differed_sku varchar(250),"
                +"sku varchar(250),"
                +"exhibition varchar(250) NOT NULL,"
                +"id_exhibition_type varchar(250) NOT NULL,"
                +"existence varchar(250) NOT NULL,"
                +"id_resource_type varchar(250),"
                +"evidence varchar(250),"
                +"status varchar(250) NOT NULL,"
                +"type varchar(250) NOT NULL)");

        db.execSQL("CREATE TABLE "+ InventoryProduct.TABLE+" ("
                +"id integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
                +"id_visit varchar(250) NOT NULL,"
                +"id_product varchar(250) NOT NULL,"
                +"stock varchar(250),"
                +"system_stock varchar(20),"
                +"comment varchar(250),"
                +"status varchar(250) NOT NULL)");

        db.execSQL("CREATE TABLE "+ Warehouse.TABLE+" ("
                +"id integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
                +"id_visit varchar(250) NOT NULL,"
                +"id_product varchar(250) NOT NULL,"
                +"name_product varchar(250) NOT NULL,"
                +"boxes_out varchar(250) NOT NULL,"
                +"final_stock varchar(250) NOT NULL,"

                +"ware_inv varchar(250) NOT NULL,"
                +"shelf_inv varchar(250) NOT NULL,"
                +"exhib_inv varchar(250) NOT NULL,"

                +"status varchar(250) NOT NULL)");

        db.execSQL("CREATE TABLE "+ NoSaleReasonRecord.TABLE+" ("
                +"id integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
                +"id_visit varchar(250) NOT NULL,"
                +"id_reason varchar(250) NOT NULL,"
                +"comments varchar(250),"
                +"signature varchar(20),"
                +"status varchar(250) NOT NULL)");

        /*---------------------Version 2 ---------------------*/

        db.execSQL("CREATE TABLE "+ Signature.TABLE+" ("
                +"id integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
                +"id_visit varchar(250) NOT NULL,"
                +"comments varchar(250) NOT NULL,"
                +"evidence varchar(250),"
                +"status varchar(250) NOT NULL)");

        /*---------------------Version 3 ---------------------*/

        db.execSQL("CREATE TABLE "+ WholeSalesPrices.TABLE+" ("
                +"id integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
                +"id_visit varchar(250) NOT NULL,"
                +"comments varchar(250) NOT NULL,"
                +"evidence varchar(250),"
                +"status varchar(250) NOT NULL)");

        db.execSQL("CREATE TABLE "+ WholeSalesPricesProduct.TABLE+" ("
                +"id integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
                +"id_order varchar(250) NOT NULL,"
                +"id_visit varchar(250) NOT NULL,"
                +"id_product varchar(250) NOT NULL,"
                +"name varchar(250) NOT NULL,"
                +"price varchar(250) NOT NULL)");

        db.execSQL("CREATE TABLE "+ ExtraTasks.TABLE+" ("
                +"id_generic_task integer NOT NULL PRIMARY KEY,"
                +"subtitle varchar(250) NOT NULL,"
                +"description varchar(250) NOT NULL,"
                +"obligatory integer NOT NULL,"
                +"id_order integer NOT NULL,"
                +"date_start integer NOT NULL,"
                +"date_final integer NOT NULL,"
                +"date_end integer NOT NULL,"
                +"frm_id_form integer NOT NULL,"
                +"vi_id_visit integer NOT NULL,"
                +"date_final_allocation integer NOT NULL,"
                +"observations varchar(250),"
                +"evidence varchar(250),"
                +"status varchar(250) NOT NULL)");

        db.execSQL("CREATE TABLE "+ Form.TABLE+" ("
                +"id integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
                +"id_generic_task integer NOT NULL,"
                +"id_form integer NOT NULL,"
                +"form varchar(250) NOT NULL,"
                +"description varchar(250) NOT NULL,"
                +"expiration varchar(250),"
                +"id_form_type integer NOT NULL,"
                +"form_type varchar(250) NOT NULL,"
                +"timestamp varchar(250),"
                +"comment varchar(250),"
                +"answers varchar(250))");

        db.execSQL("CREATE TABLE "+ SectionsForm.TABLE+" ("
                +"id integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
                +"id_section integer NOT NULL,"
                +"title varchar(250) NOT NULL,"
                +"description varchar(250) NOT NULL,"
                +"id_form integer NOT NULL,"
                +"id_generic_task integer NOT NULL,"
                +"FOREIGN KEY (id) REFERENCES form (id))");

        db.execSQL("CREATE TABLE "+ QuestionsSectionForm.TABLE+" ("
                +"id integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
                +"id_question integer NOT NULL,"
                +"id_question_type integer NOT NULL,"
                +"question_type varchar(250) NOT NULL,"
                +"id_order integer NOT NULL,"
                +"question varchar(250) NOT NULL,"
                +"options varchar(250),"
                +"correct varchar(250),"
                +"weight varchar(250),"
                +"answer varchar(250),"
                +"id_generic_task integer NOT NULL,"
                +"id_section integer NOT NULL,"
                +"FOREIGN KEY (id) REFERENCES sections_form (id))");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // NOTE: This switch statement is designed to handle cascading database
        // updates, starting at the current version and falling through to all
        // future upgrade cases. Only use "break;" when you want to drop and
        // recreate the entire database.

        Log.d(TAG, "after upgrade logic, at version " + oldVersion);
        /* Changes in version 1-2 */
        if (oldVersion != newVersion) {

            db.execSQL("DROP TABLE IF EXISTS " + User.TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + Permission.TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + Profile.TABLE);

            db.execSQL("DROP TABLE IF EXISTS " + Module.TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + Note.TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + RecordActions.TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + Product.TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + ProductBrand.TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + ProductCategory.TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + ProductPic.TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + Customer.TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + Customer.TABLE);

            db.execSQL("DROP TABLE IF EXISTS " + PaymentType.TABLE);

            db.execSQL("DROP TABLE IF EXISTS " + Receipt.TABLE);

            db.execSQL("DROP TABLE IF EXISTS " + People.TABLE);

            db.execSQL("DROP TABLE IF EXISTS " + Session.TABLE);

            db.execSQL("DROP TABLE IF EXISTS " + Receiving.TABLE);

            db.execSQL("DROP TABLE IF EXISTS " + ReceivingItem.TABLE);

            db.execSQL("DROP TABLE IF EXISTS " + Sale.TABLE);

            db.execSQL("DROP TABLE IF EXISTS " + SaleItem.TABLE);

            db.execSQL("DROP TABLE IF EXISTS " + Evidence.TABLE);

            db.execSQL("DROP TABLE IF EXISTS " + EvidenceBrandProduct.TABLE);


            db.execSQL("DROP TABLE IF EXISTS " + EvidenceProduct.TABLE);

            db.execSQL("DROP TABLE IF EXISTS " + Supplier.TABLE);

            db.execSQL("DROP TABLE IF EXISTS " + Order.TABLE);

            db.execSQL("DROP TABLE IF EXISTS " + OrderDetail.TABLE);

            db.execSQL("DROP TABLE IF EXISTS " + OrderStatus.TABLE);

            db.execSQL("DROP TABLE IF EXISTS " + OrderSent.TABLE);

            db.execSQL("DROP TABLE IF EXISTS " + OrderSentProduct.TABLE);

            db.execSQL("DROP TABLE IF EXISTS " + Shelf_Review.TABLE);

            db.execSQL("DROP TABLE IF EXISTS " + Shelf_ReviewProduct.TABLE);

            db.execSQL("DROP TABLE IF EXISTS " + ResourceType.TABLE);

            db.execSQL("DROP TABLE IF EXISTS " + Promotion.TABLE);

            db.execSQL("DROP TABLE IF EXISTS " + Exhibition.TABLE);

            db.execSQL("DROP TABLE IF EXISTS " + QualityProblemType.TABLE);

            db.execSQL("DROP TABLE IF EXISTS " + Brand.TABLE);

            db.execSQL("DROP TABLE IF EXISTS " + NoSaleReason.TABLE);

            db.execSQL("DROP TABLE IF EXISTS " + PendingOrder.TABLE);

            db.execSQL("DROP TABLE IF EXISTS " + PendingOrderProduct.TABLE);

            db.execSQL("DROP TABLE IF EXISTS " + CustomerWorkPlan.TABLE);

            db.execSQL("DROP TABLE IF EXISTS " + UserInfo.TABLE);

            db.execSQL("DROP TABLE IF EXISTS " + CancelledOrder.TABLE);

            db.execSQL("DROP TABLE IF EXISTS " + MarketingQuest.TABLE);

            db.execSQL("DROP TABLE IF EXISTS " + CheckIn.TABLE);

            db.execSQL("DROP TABLE IF EXISTS " + CheckOut.TABLE);

            db.execSQL("DROP TABLE IF EXISTS " + Invoice.TABLE);

            db.execSQL("DROP TABLE IF EXISTS " + Refund.TABLE);

            db.execSQL("DROP TABLE IF EXISTS " + RefundProduct.TABLE);

            db.execSQL("DROP TABLE IF EXISTS " + PartialPay.TABLE);

            db.execSQL("DROP TABLE IF EXISTS " + Fueling.TABLE);

            db.execSQL("DROP TABLE IF EXISTS " + QualityIncidence.TABLE);

            db.execSQL("DROP TABLE IF EXISTS " + ProductShelfReview.TABLE);

            db.execSQL("DROP TABLE IF EXISTS " + Offers.TABLE);

            db.execSQL("DROP TABLE IF EXISTS " + InventoryProduct.TABLE);

            db.execSQL("DROP TABLE IF EXISTS " + Warehouse.TABLE);

            db.execSQL("DROP TABLE IF EXISTS " + NoSaleReasonRecord.TABLE);

            db.execSQL("DROP TABLE IF EXISTS " + Signature.TABLE);

            db.execSQL("DROP TABLE IF EXISTS " + WholeSalesPrices.TABLE);

            db.execSQL("DROP TABLE IF EXISTS " + WholeSalesPricesProduct.TABLE);

            db.execSQL("DROP TABLE IF EXISTS " + ExtraTasks.TABLE);

            db.execSQL("DROP TABLE IF EXISTS " + Form.TABLE);

            db.execSQL("DROP TABLE IF EXISTS " + SectionsForm.TABLE);

            db.execSQL("DROP TABLE IF EXISTS " + QuestionsSectionForm.TABLE);

            onCreate(db);

        }
    }

    public static void deleteDatabase(Context context) {
        context.deleteDatabase(DATABASE_NAME);
    }
}
