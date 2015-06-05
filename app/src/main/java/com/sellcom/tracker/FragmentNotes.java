package com.sellcom.tracker;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import android.text.format.DateFormat;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import database.models.Note;
import de.timroes.android.listview.EnhancedListView;
import util.NotesAdapter;

/**
 * Created by juanc.jimenez on 12/05/14.
 */
public class FragmentNotes extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener, EnhancedListView.OnDismissCallback{

    LinearLayout newNoteButton;
    EnhancedListView notesList;
    TextView emptyView;
    NotesAdapter dataAdapter;
    ArrayList<String> list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        list = new ArrayList<String>();
        dataAdapter = new NotesAdapter(getActivity(), list);
        populateList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notes, container, false);

        if (view != null) {

            newNoteButton = (LinearLayout) view.findViewById(R.id.new_note_button);
            notesList = (EnhancedListView) view.findViewById(R.id.notes_list);
            emptyView = (TextView) view.findViewById(R.id.empty_view);

            newNoteButton.setOnClickListener(this);
            notesList.setAdapter(dataAdapter);
            notesList.setOnItemClickListener(this);
            notesList.setDismissCallback(this);
            notesList.enableSwipeToDismiss();
            notesList.setRequireTouchBeforeDismiss(false);
            checkForEmptyList();
        }

        return view;
    }

    @Override
    public void onClick(View view) {

        newNoteDialog();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {

        editNoteDialog(i);
    }

    public void newNoteDialog() {

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View dialogView = inflater.inflate(R.layout.dialog_new_note, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity(), R.style.AnimatedDialog);
        alertDialogBuilder.setView(dialogView);

        final EditText userInput = (EditText) dialogView.findViewById(R.id.note_input_text);
        Button okButton = (Button) dialogView.findViewById(R.id.ok_button);
        Button cancelButton = (Button) dialogView.findViewById(R.id.cancel_button);

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!userInput.getText().toString().isEmpty()) {

                    list.add(0, String.valueOf(saveNote(userInput.getText().toString())));
                    dataAdapter.notifyDataSetChanged();
                    checkForEmptyList();
                }
                alertDialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                   alertDialog.cancel();
            }
        });
    }

    public void editNoteDialog(final int edited_item) {

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View dialogView = inflater.inflate(R.layout.dialog_new_note, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity(), R.style.AnimatedDialog);
        alertDialogBuilder.setView(dialogView);

        final EditText userInput = (EditText) dialogView.findViewById(R.id.note_input_text);
        Button okButton = (Button) dialogView.findViewById(R.id.ok_button);
        Button cancelButton = (Button) dialogView.findViewById(R.id.cancel_button);

        Cursor note = Note.getNote(getActivity(), list.get(edited_item));
        userInput.setText(note.getString(note.getColumnIndexOrThrow(Note.DESCRIPTION)));

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteNote(Integer.valueOf(list.get(edited_item)));
                list.remove(edited_item);

                if (!userInput.getText().toString().isEmpty()) {
                    list.add(0, String.valueOf(saveNote(userInput.getText().toString())));
                }

                dataAdapter.notifyDataSetChanged();
                alertDialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });
    }

    public long saveNote(String message) {

        long timestamp = new Date().getTime();
        System.out.println("Today is " + timestamp);
        return Note.insert(getActivity(), message, 1, String.valueOf(timestamp));
    }

    public void deleteNote(int note_id) {
        Note.delete(getActivity(), note_id);
    }

    public String getDate(long timestamp) {

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        return DateFormat.format("dd/MM/yy    h:mm a", cal).toString();
    }

    public void checkForEmptyList() {
        if (list.isEmpty())
            emptyView.setVisibility(View.VISIBLE);
        else
            emptyView.setVisibility(View.GONE);
    }

    public void populateList() {

        Cursor notesCursor = Note.getAll(getActivity());
        for (notesCursor.moveToFirst(); !notesCursor.isAfterLast(); notesCursor.moveToNext()){
            list.add(notesCursor.getString(notesCursor.getColumnIndexOrThrow(Note.ID)));
        }
        dataAdapter.notifyDataSetChanged();
    }

    @Override
    public EnhancedListView.Undoable onDismiss(EnhancedListView enhancedListView, int i) {

        final String note = dataAdapter.getItem(i);
        final int note_id = Integer.valueOf(list.get(i));
        dataAdapter.remove(note);
        dataAdapter.notifyDataSetChanged();
        checkForEmptyList();
        return new EnhancedListView.Undoable() {
            @Override
            public void undo() {
                dataAdapter.add(note);
                dataAdapter.notifyDataSetChanged();
                checkForEmptyList();
            }

            @Override
            public String getTitle() {
                return getString(R.string.note_deleted);
            }

            @Override
            public void discard() {
                deleteNote(note_id);
            }
        };
    }

    @Override
    public void onPause() {
        super.onPause();
        notesList.discardUndo();
    }
}
