package util;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sellcom.tracker.R;
/**
 * Created by hugo.figueroa on 25/02/15.
 */
public class MarketingQuestionAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private int elements = 5;
    private Activity activity;
    private int arrayImages[] = {R.drawable.h_notes,
            R.drawable.h_notes,
            R.drawable.h_notes,
            R.drawable.h_notes,
            R.drawable.h_notes};

    private int arrayName[] = {R.string.mq_shelf_review,
            R.string.mq_quality_incidence,
            R.string.mq_offers_and_or_promotions,
            R.string.mq_competition_promotions,
            R.string.mq_warehouse};

    public MarketingQuestionAdapter(Activity a){

        layoutInflater = (LayoutInflater)a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        activity = a;

    }

    @Override
    public int getCount() {
        return elements;
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

        View vista = view;
        if(view == null){
            vista = layoutInflater.inflate(R.layout.item_marketing_questionnarie,null);
        }


        ImageView imageView = (ImageView)vista.findViewById(R.id.img_marketing_questionnaire);
        imageView.setImageResource(arrayImages[position]);

        TextView textView = (TextView)vista.findViewById(R.id.txt_marketing_questionnaire);
        textView.setText(activity.getString(arrayName[position]));

        return vista;
    }
}