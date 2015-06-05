package util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sellcom.tracker.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by raymundo.piedra on 04/02/15.
 */
public class SpinnerAdapter extends BaseAdapter {

    public enum SPINNER_TYPE {
        STATES ("states"),
        CITIES ("cities"),
        PRODUCTS ("products"),
        Q_INCIDENCES("incidences"),
        PROMOTIONS ("promotions"),
        EXHIB_TYPE ("exhib_type"),
        RES_TYPE ("res_type"),
        BRANDS ("brands"),
        PROBLEM_TYPE("problem"),
        NO_SALES_REASON("no_sales_reason");
        
        private final String name;

        private SPINNER_TYPE(String s) {
            name = s;
        }

        public boolean equalsName(String otherName){
            return (otherName == null)? false:name.equals(otherName);
        }

        public String toString(){
            return name;
        }

    }

    private Context         mContext;
    private SPINNER_TYPE    type;


    private List<Map<String,String>> mItems = new ArrayList<Map<String,String>>();

    public SpinnerAdapter(Context context, List<Map<String,String>> items, SPINNER_TYPE type) {
        this.mContext            = context;
        this.mItems              = items;
        this.type                = type;
    }

    public int getCount() {
        return mItems .size();
    }

    public Object getItem(int position) {
        return mItems .get(position);
    }

    @Override

    public View getView(int i, View view, ViewGroup viewGroup) {
        Map<String,String> state    = mItems.get(i);
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.item_spinner, null);
        }

        TextView main_text = (TextView) view .findViewById(R.id.txt_spn_item);
        switch (type){
            case STATES:
                main_text.setText(state.get("st_state"));
                break;
            case CITIES:
                main_text.setText(state.get("ct_city"));
                break;
            case PRODUCTS:
                main_text.setText(state.get("name"));
                break;

            default:
                main_text.setText(state.get("description"));
                break;


        }

        return view;
    }

    public long getItemId(int position) {
        return position;
    }
}
