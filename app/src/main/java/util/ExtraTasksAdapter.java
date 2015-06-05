package util;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sellcom.tracker.R;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by hugo.figueroa on 27/04/15.
 */
public class ExtraTasksAdapter extends BaseAdapter{

    private LayoutInflater                  layoutInflater;
    private Context                         context;
    private List<Map<String,String>> extraTasks;
    private String                          obligatory;


    public ExtraTasksAdapter(Activity a, List<Map<String,String>>   extraTasks){

        layoutInflater = (LayoutInflater)a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.extraTasks = extraTasks;

    }


    @Override
    public int getCount() {
        return extraTasks.size();
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

        TextView txv_title_extra_tasks,
                txv_description_extra_tasks,
                txv_obligatory_extra_tasks;

        if(view == null){
            view = layoutInflater.inflate(R.layout.item_extra_tasks,null);
        }

        txv_title_extra_tasks = (TextView) view.findViewById(R.id.txv_title_extra_task);
        txv_description_extra_tasks = (TextView) view.findViewById(R.id.txv_description_extra_task);
        txv_obligatory_extra_tasks = (TextView) view.findViewById(R.id.txv_obligatory_extra_task);

        try{

            if(extraTasks.get(position).get("subtitle").toString().equals("")){
                txv_title_extra_tasks.setText("Sin título");
            }else{
                txv_title_extra_tasks.setText(extraTasks.get(position).get("subtitle"));
            }

        } catch (Exception e) {
            txv_title_extra_tasks.setText("Sin título");
            e.printStackTrace();
        }



        txv_description_extra_tasks.setText(extraTasks.get(position).get("description"));

        obligatory = extraTasks.get(position).get("obligatory");

        if(obligatory.equals("1")){
            txv_obligatory_extra_tasks.setText("SI");
        }else if(obligatory.equals("0")){
            txv_obligatory_extra_tasks.setText("NO");
        }





        return view;
    }
}
