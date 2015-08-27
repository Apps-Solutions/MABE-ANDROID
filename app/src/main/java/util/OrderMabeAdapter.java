package util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sellcom.tracker.R;

/**
 * Created by hugo.figueroa on 29/06/15.
 */
public class OrderMabeAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private static Activity activity;

    private int[] images = {R.drawable.ic_captura_inventarios,
                            R.drawable.ic_captura_inventarios,
                            R.drawable.ic_captura_inventarios,
                            R.drawable.ic_captura_inventarios,
                            R.drawable.ic_captura_inventarios,
                            R.drawable.ic_captura_inventarios,
                            R.drawable.ic_captura_inventarios};

    private String[] names = {"Captura de Servicios",
                                "Localización",
                                "Captura de Fallas",
                                "Captura de Refacciones",
                                "Tipo de Servicio",
                                "Cierre/Cambio de Estatus",
                                "Cierre de la Orden"};

    public OrderMabeAdapter(Activity a){

        layoutInflater = (LayoutInflater)a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        activity = a;

    }


    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        LinearLayout linearLayout;
        ImageView image_step_visit;
        TextView name_step_visit;

        if(view == null){
            view = layoutInflater.inflate(R.layout.item_step_visit,null);
        }

        name_step_visit = (TextView)view.findViewById(R.id.name_step_visit);
        if (position%2 == 0) {
            view.setBackgroundResource(R.drawable.border_general_edittext);
            name_step_visit.setTextColor(activity.getResources().getColor(R.color.black_gray));
        }else{
            view.setBackgroundColor(activity.getResources().getColor(R.color.black_gray));
            name_step_visit.setTextColor(activity.getResources().getColor(R.color.white));
        }


        Resources res = activity.getResources();

        image_step_visit = (ImageView)view.findViewById(R.id.image_step_visit);
        image_step_visit.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        image_step_visit.setImageResource(images[position]);


        name_step_visit.setText(names[position]);

        return view;
    }
}
