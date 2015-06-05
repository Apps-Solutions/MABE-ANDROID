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

import item_DTO.Item_cashing;

/**
 * Created by Raiseralex21 on 22/01/15.
 */
public class CashingAdapter extends BaseAdapter {

    private Context context;
    private List<Item_cashing> items;
    private Item_cashing itemC;
    public int miPosition = 0;

    //TextView para la muestra de los datos de cashing
    private TextView cashingNum,
                     cashingFactura,
                     cashingDate,
                     cashingTotal,
                     cashingCliente;

    private ImageView cashingPagar;


    public CashingAdapter(){
        super();
    }

    public CashingAdapter(Context context, List<Item_cashing> items){
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
        //Objetos pertenecientes a ListView
        cashingNum     = (TextView) vista.findViewById(R.id.txv_cashingNumerico);
        cashingFactura = (TextView) vista.findViewById(R.id.txv_cashingFactura);
        cashingDate    = (TextView) vista.findViewById(R.id.txv_cashingDate);
        cashingTotal   = (TextView) vista.findViewById(R.id.txv_cashingTotal);
        cashingCliente = (TextView) vista.findViewById(R.id.txv_cashingClient);


        //Llenado de items para ListView de FragmentCashing
        Item_cashing itemC = items.get(position);
            //Position comienza desde 0, se suma para cambiar a 1
            cashingNum.setText(""+((position++) + 1));
            cashingFactura.setText(itemC.getFactura());
            cashingCliente.setText(itemC.getCliente());
            cashingDate.setText(itemC.getDate());
            cashingTotal.setText(itemC.getTotal());

        return vista;
    }

}
