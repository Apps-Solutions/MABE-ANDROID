package async_request;

/**
 * Created by raymundo.piedra on 22/01/15.
 */

/*
    Dando de alta un nuevo elemento....
    -
 */
public enum METHOD {
    LOGIN ("login"),
    GET_WORKPLAN ("get_workplan"),
    GET_PDV_INFO ("get_pdv_info"),
    SEND_START_VISIT ("send_start_visit"),
    GET_CASHING ("get_cashing"),
    GET_PRODUCTS ("get_products"),
    SEND_ORDER ("send_order"),
    SEND_PAYMENT ("send_payment"),
    GET_INVOICES ("get_invoices"),
    SEND_PARTIAL_SETTLEMENT ("send_partial_settlement"),
    SEND_FUELING ("set_fuel_charge"),
    SEND_END_VISIT ("send_end_visit"),
    GET_PDVS ("get_pdvs"),
    LOGOUT ("logout"),
    SET_PROSPECT("set_prospect"),
    GET_PENDING_ORDER("get_pending_order"),
    SEND_REFUND ("set_refund"),
    GET_FORM ("get_form"),
    SET_FORM_TEST("set_form_test"),
    SET_GENERIC_TASK_RESULT("set_generic_task_result"),

    SET_SIGNATURE("set_signature"),

    SET_SUPPLIER_PRICES("set_supplier_price"),
    SET_AUTOSALE_STOCK("set_autosale_stock"),
    SET_NO_SALE_REASON("set_no_sale_reason"),

    GET_INITIAL_INFORMATION(""),

    // MAYOREO //
    SET_SHELF_REVISION("set_shelf_revision"),
    SET_QUALITY_INCIDENCE("set_quality_incidence"),
    SET_PROMOTION("set_promotion"),
    SET_WAREHOUSE_STOCK("set_warehouse_stock"),
    SET_NO_METHOD(""),

    // Catálogos
    GET_CITIES ("get_cities"),
    GET_STATES ("get_states"),
    GET_RESOURCE_TYPE("get_resource_type"),
    GET_PROMOTION_TYPE("get_promotion_type"),
    GET_EXHIBITION_TYPE("get_exhibition_type"),
    GET_QUALITY_PROBLEM_TYPE("get_quality_problem_type"),
    GET_BRAND("get_brand"),
    GET_USER_INFO("get_info_user"),
    GET_NO_SALE_REASONS("get_no_sale_reasons"),

    //////////////////////////////////////

    PROMOTION("promotion"),
    QUALITY_INCIDENCE("quality_incidence"),
    MEASUREMENT_FURNITURE("measurement_furniture"),
    CANCEL_ORDER ("cancel_order"),
    MARKETING_QUESTIONNAIRE_FLOW("marketing_questionnaire_flow");

    private final String name;

    private METHOD(String s) {
        name = s;
    }

    public boolean equalsName(String otherName){
        return (otherName == null)? false:name.equals(otherName);
    }

    public String toString(){
        return name;
    }

}


