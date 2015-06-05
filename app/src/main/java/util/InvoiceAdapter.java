package util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sellcom.tracker.R;

import java.util.List;
import java.util.Map;


/**
 * Created by raymundo.piedra on 28/01/15.
 */
public class InvoiceAdapter extends BaseAdapter {

    private Context context;
    private List<Map<String,String>> items;

    private TextView cashingNum,
            cashingFactura,
            cashingDate,
            cashingTotal,
            cashingCliente;


    public InvoiceAdapter(){
        super();
    }

    public InvoiceAdapter(Context context, List<Map<String,String>> items){
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        View vista = convertView;

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            vista = inflater.inflate(R.layout.item_cashing, null);
        }

        cashingNum     = (TextView) vista.findViewById(R.id.txv_cashingNumerico);
        cashingFactura = (TextView) vista.findViewById(R.id.txv_cashingFactura);
        cashingDate    = (TextView) vista.findViewById(R.id.txv_cashingDate);
        cashingTotal   = (TextView) vista.findViewById(R.id.txv_cashingTotal);
        cashingCliente = (TextView) vista.findViewById(R.id.txv_cashingClient);

        Map<String,String> invoice = items.get(position);
        cashingNum.setText(""+((position++) + 1));
        cashingFactura.setText(invoice.get("folio"));
        cashingCliente.setText(invoice.get("pdv_name"));
        cashingDate.setText(invoice.get("inv_date"));
        cashingTotal.setText("$"+invoice.get("total"));

        return vista;
    }

}

