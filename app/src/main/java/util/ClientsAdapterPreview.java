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

import item_DTO.Item_cashing;
import item_DTO.Item_client_preview;

/**
 * Created by Raiseralex21 on 26/01/15.
 */
public class ClientsAdapterPreview extends BaseAdapter {

    private Context context;
    private List<Map<String,String>> items;

    //TextView para la muestra de los datos de cashing
    private TextView clientNum,
                     clientName;

    public ClientsAdapterPreview(Context context, List<Map<String,String>> items){
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
            vista = inflater.inflate(R.layout.item_clients_preview, null);
        }
        clientName = (TextView) vista.findViewById(R.id.txv_clients_preview_name);
        clientNum  = (TextView) vista.findViewById(R.id.txv_clients_preview_num);

        Map<String,String> pdv_map = items.get(position);
        clientNum.setText(""+((position++) + 1));
        clientName.setText(pdv_map.get("pdv_name"));

        return vista;
    }
}
