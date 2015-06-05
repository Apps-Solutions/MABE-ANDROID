package util;

import android.content.Context;
import android.database.Cursor;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sellcom.tracker.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import database.models.Note;

/**
 * Created by juanc.jimenez on 09/05/14.
 */
public class NotesAdapter extends ArrayAdapter<String> {
    private Context context;
    private ArrayList<String> ids;

    public NotesAdapter(Context context, ArrayList<String> ids) {

        super(context, R.layout.item_notes, ids);
        this.context = context;
        this.ids = ids;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Calendar calendar = Calendar.getInstance();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView;
        if (convertView == null) {
            itemView = inflater.inflate(R.layout.item_notes, parent, false);
        }
        else {
            itemView = convertView;
        }

        TextView noteText = (TextView) itemView.findViewById(R.id.note_text);
        TextView noteDateText = (TextView) itemView.findViewById(R.id.note_date_text);

        Cursor note = Note.getNote(context, ids.get(position));

        noteText.setText(note.getString(note.getColumnIndexOrThrow(Note.DESCRIPTION)));

        String timestamp = note.getString(note.getColumnIndexOrThrow(Note.TIMESTAMP));
        calendar.setTimeInMillis(Long.valueOf(timestamp));
        timestamp = DateFormat.format("dd/MM/yy    h:mm a", calendar).toString();

        noteDateText.setText(timestamp);

        return itemView;
    }
}
