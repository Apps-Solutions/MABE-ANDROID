package util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sellcom.tracker.R;

/**
 * Created by hugo.figueroa on 14/01/15.
 */
public class StepVisitAdapter extends BaseAdapter{



    private LayoutInflater layoutInflater;
    private static Activity activity;
    //private int linearLayoutWidth;

    private int[] images;
    private int[] names;

    private int steps; //Numero de pasos de la visita
    private String stepType;

    private int[] imagesSelfservice = { R.drawable.ic_captura_inventarios,
            R.drawable.ic_captura_inventarios,
            R.drawable.ic_captura_inventarios,
            //R.drawable.ic_captura_inventarios,
            R.drawable.ic_edit_enabled,
            R.drawable.menu1,
            R.drawable.ic_no_venta};


    private int[] imagesWholesale = {   R.drawable.ic_registro_pago,
                                        R.drawable.ic_registro_pago,
                                        //R.drawable.ic_registro_pago,
                                        //R.drawable.ic_registro_pago,
                                        //R.drawable.ic_registro_pago,
                                        //R.drawable.ic_registro_pago,
                                        //R.drawable.ic_registro_pago,
                                        R.drawable.ic_edit_enabled,
                                        R.drawable.menu1,
                                        R.drawable.ic_no_venta};

    private int[] name_stepsSelfservice = {R.string.svss_marketing_questionnaire,
            //R.string.preparation,
            //R.string.capturing_inventories,
            R.string.svss_inventory_questionnaire,
            R.string.svss_warehouse,
            //R.string.svss_measurement_of_furniture,
            //R.string.return_order,
            R.string.authorize_visit,
            R.string.extra_tasks,
            R.string.end_visit};

    private int[] name_stepsWholesale = {R.string.svss_marketing_questionnaire,
            //R.string.qi_title,
            //R.string.o_offer_title,
            //R.string.cp_title,
            //R.string.wh_title,
            R.string.whp_title,
            R.string.authorize_visit,
            R.string.extra_tasks,
            R.string.end_visit};

    private int[] imagesHorizontal = {
            R.drawable.ic_registro_pago,
            R.drawable.ic_captura_inventarios,
            R.drawable.ic_pedido,
            R.drawable.ic_cancelacion_pedido,
            R.drawable.ic_cambio_fisico,
            R.drawable.ic_cancel_cambio_fisico,
            R.drawable.ic_no_venta_1,
            R.drawable.ic_registro_pago,
            R.drawable.ic_edit_enabled,
            R.drawable.menu1,
            R.drawable.ic_no_venta};

    private int[] name_stepsHorizontal = {
            R.string.payment_record,
            R.string.pre_orders,
            R.string.order,
            R.string.order_cancellation,
            R.string.refund,
            R.string.refund_cancellation,
            R.string.no_reason_for_sale,
            R.string.print_visit_ticket,
            R.string.authorize_visit,
            R.string.extra_tasks,
            R.string.end_visit};

    public StepVisitAdapter(Activity a, String stepType){

        // 1. Distribución Horizontal
        // 2. Mayoreo
        // 3. Autoservicio

        this.stepType = stepType;

        if(stepType.equals("1")) {
            this.steps  = 11;
            images      = imagesHorizontal;
            names       = name_stepsHorizontal;
        }
        else if(stepType.equals("2")) {
            this.steps  = 5;
            images      = imagesWholesale;
            names       = name_stepsWholesale;
        }
        else if(stepType.equals("3")) {
            this.steps  = 6;
            images      = imagesSelfservice;
            names       = name_stepsSelfservice;
        }


        layoutInflater = (LayoutInflater)a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        activity = a;
    }

    @Override
    public int getCount() {
        return steps;
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


        name_step_visit.setText(res.getString(names[position]));

        return view;
    }
}
