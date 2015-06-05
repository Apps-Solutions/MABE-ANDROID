package util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Looper;
import android.util.Log;
import android.view.Window;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.sellcom.tracker.R;
import com.zebra.sdk.comm.BluetoothConnectionInsecure;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;

import async_request.RequestManager;
import database.DataBaseManager;
import database.models.Invoice;
import database.models.OrderSent;
import database.models.OrderSentProduct;
import database.models.Refund;
import database.models.RefundProduct;

/**
 * Created by raymundo.piedra on 05/02/15.
 */
public class TicketGenerator {

    public static void generateTicket(Activity activity, final TicketGeneratorListener listener, final List<Map<String,String>>  orderList, final Map<String,String> costumer_info, final Map<String,String> salesman_info, final double orderTotalAmount, final int total_pieces){

        final String TAG  = "TICKET_GENERATOR_TAG";

        final Activity          act             = activity;
        final ProgressDialog    progressDialog  = new ProgressDialog(activity);

        final boolean           testMode        = false;

        final String PRINTER_MAC_ADDRESS = RequestManager.sharedInstance().getPreferencesValueForKey("BluetoothMacAddress");

        if (PRINTER_MAC_ADDRESS.equalsIgnoreCase("clear")){
            RequestManager.sharedInstance().showErrorDialog(act.getString(R.string.req_man_error_no_printer),act);
            listener.responseFromGenerator("NO_PRINTER");
            return;
        }

        final List<Map<String, String>> refund_prods;
        final List<Map<String,String>>  order_prods;

        String visit_id                                     = TrackerManager.sharedInstance().getCurrent_pdv().get("visit_id");
        String pdv_id                                       = TrackerManager.sharedInstance().getCurrent_pdv().get("pdv_id");
        final List<Map<String, String>> aux_refund_prods    = DataBaseManager.sharedInstance().getRefundProductsInVisit(visit_id);

        if (aux_refund_prods == null)
            refund_prods    = new ArrayList<Map<String, String>>();
        else
            refund_prods    = aux_refund_prods;

        final String order_id                                   = OrderSent.getIDActiveOrderSentInVisit(activity, visit_id);
        final String order_type                                 = OrderSent.getActiveOrderSentTypeInVisit(activity, visit_id);
        if (order_id == null)
            order_prods    = new ArrayList<Map<String, String>>();
        else
            order_prods    = OrderSentProduct.getAllProductsInOrderSent(activity, order_id);

        final List<Map<String,String>> invoices_paid  = Invoice.getAllInvoicesPaidInMapsForPDV(activity,pdv_id);

        Log.d("TICKET_GENERATOR",""+refund_prods);

        progressDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setMessage(activity.getString(R.string.tg_printing_ticket));
        progressDialog.setCancelable(false);
        progressDialog.show();
        new Thread(new Runnable() {
            public void run() {
                try {

                    if (testMode)
                        Thread.sleep(2500);

                    else{

                        Log.d(TAG,"MAC ADDRESS IN PRINT : "+PRINTER_MAC_ADDRESS);

                        Connection thePrinterConn = new BluetoothConnectionInsecure(PRINTER_MAC_ADDRESS);
                        Looper.prepare();
                        thePrinterConn.open();

                        int header_offset   = 645;
                        int prod_offset     = order_prods.size()*30;
                        int ref_offset      = refund_prods.size()*30;
                        int inv_offset      = 240;

                        int total = header_offset + prod_offset + ref_offset + inv_offset;

                        if (order_type != null) {
                            if (order_type.equalsIgnoreCase("CASH"))
                                total += 60;
                            else if (order_type.equalsIgnoreCase("CREDIT"))
                                total += 300;
                            else if (order_type.equalsIgnoreCase("DEPOSIT"))
                                total += 60;
                            else if (order_type.equalsIgnoreCase("PRE_ORDER"))
                                total += 60;
                        }

                        total += 250;

                        //  -------------------------- HEADER DATA (FIXED) -----------------------------//
                        StringBuilder printData = new StringBuilder("! 0 200 200 ");
                        printData.append(total);
                        printData.append(" 1");
                        printData.append("CENTER\r\n");
                        printData.append("SETBOLD 2\r\n");
                        printData.append("ENCODING UTF-8\r\n");
                        printData.append("T 7 1 0 80 RAGASA INDUSTRIAS SA DE CV\r\n");
                        printData.append("SETBOLD 0\r\n");
                        printData.append("T 0 2 0 135 RIN830930A79\r\n");
                        printData.append("T 0 2 0 165 DR JOSE ELEUTERIO GONZALEZ 2815\r\n");
                        printData.append("T 0 2 0 195 COL MITRAS CENTRO MONTERREY N.L. C.P. 64320\r\n");
                        printData.append("SETBOLD 2\r\n");
                        printData.append("T 0 2 0 225 "+ salesman_info.get("branch_name") + "\r\n"); //------------------------------
                        //printData.append("T 0 2 0 225 SUCURSAL PUEBLA\r\n");
                        printData.append("SETBOLD 0\r\n");
                        printData.append("T 0 2 0 255 "+ salesman_info.get("branch_address") + "\r\n"); //------------------------------
                        //printData.append("T 0 2 0 255 CALLE #5 No. 1306 COL REFORMA SUR PUEBLA, PUEBLA\r\n");
                        printData.append("T 0 2 0 285 01 800 NUTRIOLI\r\n");

                        printData.append("LEFT\r\n");
                        printData.append("T 0 2 0 315 \t\t" + DatesHelper.getStringDate(new Date())+"\r\n");
                        printData.append("RIGHT\r\n");
                        printData.append("T 0 2 0 315 "+  salesman_info.get("branch_code") +"\t\r\n"); //------------------------------
                        //printData.append("T 0 2 0 315 "+"DH PUE 001\t\r\n");
                        printData.append("LEFT\r\n");
                        printData.append("T 0 2 0 345 "+"  RUTA       VENDEDOR\r\n");
                        printData.append("RIGHT\r\n");
                        printData.append("T 0 2 0 345 "+"NOMINA\t\r\n");
                        printData.append("LEFT\r\n");
                        printData.append("T 0 2 0 375 "   +salesman_info.get("route")+"     "+salesman_info.get("name")+" "+salesman_info.get("lastname")+"\r\n");
                        printData.append("RIGHT\r\n");
                        printData.append("T 0 2 0 375 "+Utilities.formatNumberWithLength(salesman_info.get("jde"),5)+"\t\r\n");
                        printData.append("CENTER\r\n");
                        printData.append("T 0 2 0 405 www.ragasa.com.mx\r\n");

                        printData.append("SETBOLD 2\r\n");
                        printData.append("LEFT\r\n");
                        printData.append("T 0 2 0 435 CLIENTE\r\n");

                        printData.append("SETBOLD 0\r\n");

                        String name = costumer_info.get("pdv");

                        printData.append("T 0 2 0 465"+" "+Utilities.formatNumberWithLength(costumer_info.get("jde"),5)+"\t"+name+"\r\n");


                        String  address     = costumer_info.get("address");
                        String  addressAux  = costumer_info.get("address");
                        String  rfc         = costumer_info.get("rfc");

                        if (rfc.trim().contains("null"))
                            rfc = " ";

                        if (address.length()>50) {
                            address         = address.substring(0, 40);
                            addressAux      = addressAux.substring(40,Math.min(80,addressAux.length()));
                        }

                        printData.append("SETBOLD 0\r\n");
                        printData.append("T 0 2 0 495     \t"+address+"\r\n");

                        printData.append("SETBOLD 0\r\n");
                        printData.append("T 0 2 0 525     \t"+addressAux+"\r\n");

                        printData.append("T 0 2 0 555     \t"+rfc+"\r\n");

                        printData.append("LEFT\r\n");
                        printData.append("SETBOLD 2\r\n");
                        printData.append("T 0 2 0 585 VENTA\r\n");

                        printData.append("SETBOLD 1\r\n");
                        printData.append("T 0 2 0 615 \tCOD.\t\tARTICULO\t\t\t\tCANT.\t\tPRE.UNIT\r\n");

                        printData.append("RIGHT\r\n");
                        printData.append("T 0 2 0 645 TOTAL      \r\n");

                        //  -------------------------- END HEADER DATA (FIXED) -----------------------------//

                        int offset = 645;       // offset for ticket header
                        float   total_amount  = 0;
                        int     total_prods   = 0;
                        for (int i=0; i<order_prods.size(); i++){
                            offset += 30;
                            Map<String,String>    prod = order_prods.get(i);
                            printData.append("LEFT\r\n");
                            printData.append("SETBOLD 0\r\n");
                            printData.append("T 0 2 0 "+offset+" \t"+prod.get("jde")+"\t"+Utilities.formatProductName(prod.get("name"))+"\t\t "+prod.get("quantity")+"\t\t\t\t"+Utilities.setReceiptMoneyNumberFormat(Float.parseFloat(prod.get("price")), 6)+"\r\n");
                            printData.append("RIGHT\r\n");
                            total_prods     += Integer.parseInt(prod.get("quantity"));
                            total_amount    += Float.parseFloat(prod.get("price"))*(Integer.parseInt(prod.get("quantity")));
                            printData.append("T 0 2 0 "+offset+Utilities.setReceiptMoneyNumberFormat((Float.parseFloat(prod.get("price"))*(Integer.parseInt(prod.get("quantity")))),10)+" \r\n");
                        }

                        offset += 30;
                        printData.append("CENTER\r\n");
                        printData.append("T 0 2 0 "+offset+" SUBTOTAL\r\n");

                        printData.append("RIGHT\r\n");
                        printData.append("SETBOLD 1\r\n");
                        printData.append("T 0 2 0 "+offset+Utilities.setReceiptMoneyNumberFormat(total_amount,10)+" \r\n");

                        /*offset += 30;
                        printData.append("CENTER\r\n");
                        printData.append("SETBOLD 0\r\n");
                        printData.append("T 0 2 0 "+offset+" DESCUENTO\r\n");
                        */

                        printData.append("RIGHT\r\n");
                        printData.append("SETBOLD 0\r\n");
                        printData.append("T 0 2 0 "+offset+"3% "+Utilities.setReceiptMoneyNumberFormat(total_amount,10)+" \r\n");

                        offset += 30;
                        printData.append("CENTER\r\n");
                        printData.append("SETBOLD 1\r\n");
                        printData.append("T 0 2 0 "+offset+" TOTAL\r\n");

                        printData.append("RIGHT\r\n");
                        printData.append("T 0 2 0 "+offset+Utilities.setReceiptMoneyNumberFormat(total_amount*1,10)+" \r\n");

                        offset += 30;
                        printData.append("T 0 2 0 "+offset+"\t    \t\tARTICULOS\t\t"+total_prods+"\r\n");

                        offset += 30;

                        printData.append("SETBOLD 2\r\n");
                        printData.append("LEFT\r\n");
                        printData.append("T 0 2 0 "+offset+"CAMBIOS FISICOS\r\n");

                        offset += 30;
                        printData.append("SETBOLD 1\r\n");
                        printData.append("T 0 2 0 "+offset+"\t\t\tCOD\t\t\t\tARTICULO\r\n");
                        printData.append("RIGHT\r\n");
                        printData.append("T 0 2 0 "+offset+"CANT.\t\t\r\n");
                        offset += 30;

                        int prod_total  = 0;

                        printData.append("SETBOLD 0\r\n");

                        for (int i=0; i<refund_prods.size(); i++){
                            Map<String,String> prod = refund_prods.get(i);
                            printData.append("LEFT\r\n");
                            printData.append("T 0 2 0 "+offset+"\t\t\t"+prod.get("jde")+"\t\t\t\t"+prod.get("name")+"\t\t"+prod.get("quantity")+"\r\n");
                            prod_total  += Integer.parseInt(prod.get("quantity"));
                            offset += 30;
                        }

                        printData.append("T 0 2 0 "+offset+"\t\t\t  TOTAL PRODUCTOS\t\t"+prod_total+"\r\n");

                        if (invoices_paid != null){
                            Map<String,String> invoice  = invoices_paid.get(0);
                            offset += 30;
                            printData.append("LEFT\r\n");
                            printData.append("SETBOLD 2\r\n");
                            printData.append("T 0 2 0 "+offset+"CREDITO\t\t\r\n");
                            offset += 30;
                            printData.append("SETBOLD 1\r\n");
                            printData.append("T 0 2 0 "+offset+"\tFACT\t\tFECHA\t\t\t\t\tVALOR\r\n");
                            offset += 30;
                            printData.append("T 0 2 0 "+offset+"\t\t\t"+invoice.get("folio")+"\t\t\t\t"+invoice.get("inv_date")+"\t\t$"+invoice.get("total")+"\r\n");

                            offset += 30;
                            printData.append("SETBOLD 2\r\n");
                            printData.append("T 0 2 0 "+offset+"COBRANZA\t\t\r\n");
                            offset += 30;
                            printData.append("SETBOLD 1\r\n");
                            printData.append("T 0 2 0 "+offset+"\tFOLIO\t\tFECHA\t\t\t\t\tVALOR\r\n");
                            offset += 30;
                            printData.append("T 0 2 0 "+offset+"\t\t\t"+invoice.get("folio")+"\t\t\t\t"+DatesHelper.getStringDateDays(new Date())+"\t\t$"+invoice.get("total")+"\r\n");

                            offset += 30;
                            printData.append("SETBOLD 2\r\n");
                            printData.append("T 0 2 0 "+offset+"SALDO\t\t\r\n");
                            offset += 30;
                            printData.append("T 0 2 0 "+offset+""+DatesHelper.getStringDateDays(new Date())+"\t\t$ 0.0\r\n");
                        }

                        if (order_type != null){
                            Log.d(TAG,"Order type: "+order_type);

                            if (order_type.equalsIgnoreCase("CASH")){
                                offset += 40;
                                printData.append("SETBOLD 2\r\n");
                                printData.append("CENTER\r\n");
                                printData.append("T 7 1 0 "+offset+"PAGO DE CONTADO\r\n");
                            }

                            if (order_type.equalsIgnoreCase("DEPOSIT")){
                                offset += 40;
                                printData.append("SETBOLD 2\r\n");
                                printData.append("CENTER\r\n");
                                printData.append("T 7 1 0 "+offset+"DEPOSITO BANCARIO\r\n");
                            }

                            //String text = String.format("POR ESTE PAGARE DEBEREMOS Y PAGAREMOS INCONDICIONALMENTE A LA ORDEN DE RAGASA INDUSTRIAS S.A. DE C.V. LA CANTIDAD DE $:%s QUE AMPARA ESTE DOCUMENTO, EL DIA %s, POR EL PRODUCTO RECIBIDO A NUESTRA ENTERA SATISFACCION", Utilities.setReceiptMoneyNumberFormat(total_amount,10),DatesHelper.getStringDateDays(new Date()) );

                            else if (order_type.equalsIgnoreCase("CREDIT")){
                                offset += 65;
                                String date = DatesHelper.getLiteralDate(new Date());
                                //printData.append("T 7 1 0 "+offset+"CREDITO\r\n");
                                printData.append("LEFT\r\n");
                                printData.append("SETBOLD 0\r\n");
                                printData.append("T 0 2 0 "+offset+"POR ESTE PAGARE DEBEREMOS Y PAGAREMOS\r\n");
                                offset += 30;
                                printData.append("T 0 2 0 "+offset+"INCONDICIONALMENTE A LA ORDEN DE RAGASA \r\n");
                                offset += 30;
                                printData.append("T 0 2 0 "+offset+"INDUSTRIAS S.A. DE C.V. LA CANTIDAD DE \r\n");
                                printData.append("RIGHT\r\n");
                                printData.append("SETBOLD 1\r\n");
                                printData.append("T 0 2 0 "+offset+Utilities.setReceiptMoneyNumberFormat(total_amount,7)+"\r\n");
                                offset += 30;
                                printData.append("LEFT\r\n");
                                printData.append("SETBOLD 0\r\n");
                                printData.append("T 0 2 0 "+offset+"QUE AMPARA ESTE DOCUMENTO,\r\n");
                                offset += 30;
                                printData.append("T 0 2 0 "+offset+"EL DIA "+date+"\r\n");
                                printData.append("RIGHT\r\n");
                                printData.append("SETBOLD 1\r\n");
                                printData.append("T 0 2 0 "+offset+date+"\r\n");
                                offset += 30;
                                printData.append("LEFT\r\n");
                                printData.append("SETBOLD 0\r\n");
                                printData.append("T 0 2 0 "+offset+"POR EL PRODUCTO RECIBIDO A NUESTRA ENTERA\r\n");
                                offset += 30;
                                printData.append("T 0 2 0 "+offset+"SATISFACCION\r\n");
                                offset += 30;
                                printData.append("T 0 2 0 "+offset+"ACEPTO Y PAGARE __________________________\r\n");
                            }
                            else if (order_type.equalsIgnoreCase("PRE_ORDER")){
                                offset += 40;
                                printData.append("SETBOLD 2\r\n");
                                printData.append("CENTER\r\n");
                                printData.append("T 7 1 0 "+offset+"PEDIDO POR ENTREGAR\r\n");
                            }
                        }
                        printData.append("PRINT\r\n");
                        thePrinterConn.write(String.valueOf(printData).getBytes());

                        // Make sure the data got to the printer before closing the connection
                        Thread.sleep(500);
//                    myBitmap.recycle();
                        // Close the insecure connection to release resources.
                        thePrinterConn.close();
                        Looper.myLooper().quit();
                    }

                    act.runOnUiThread(new Runnable() {
                        public void run() {
                            listener.responseFromGenerator("OK");
                            progressDialog.dismiss();
                        }
                    });
                } catch (Exception e) {
                    Log.d("TG","Exception");
                    listener.responseFromGenerator("ERROR");
                    progressDialog.dismiss();
                    e.printStackTrace();

                }
            }
        }).start();
    }

    public interface TicketGeneratorListener{
        public void responseFromGenerator(String response);
    }
}
