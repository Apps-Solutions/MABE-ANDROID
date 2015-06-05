package util;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.sellcom.tracker.R;

import java.util.ArrayList;
import java.util.List;

import database.models.Evidence;
import database.models.EvidenceBrandProduct;
import database.models.EvidenceProduct;

/**
 * Created by juanc.jimenez on 08/09/14.
 */
public class EvidenceAdapter extends BaseAdapter {

    List<Evidence> evidence;
    List<EvidenceBrandProduct> evidenceBrandProduct;
    List<EvidenceProduct> evidenceProduct;
    Context context;

    public EvidenceAdapter(Context context, List<Evidence> evidence){
        super();
        this.context = context;
        this.evidence = evidence;
    }

    @Override
    public int getCount() {return 0;

    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_evidence, parent, false);

            holder = new ViewHolder();
            holder.info = (TextView) convertView.findViewById(R.id.info_evidence);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.info.setText(evidence.get(position).getName());

        return convertView;
    }

    static class ViewHolder{
        TextView info;
    }
}
