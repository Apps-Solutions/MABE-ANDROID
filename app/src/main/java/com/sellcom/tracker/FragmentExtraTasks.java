package com.sellcom.tracker;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import database.models.ExtraTasks;
import database.models.Form;
import util.ExtraTasksAdapter;
import util.TrackerManager;


/**
 * Created by hugo.figueroa on 27/04/15.
 */
public class FragmentExtraTasks extends Fragment {

    final static public String                  TAG                 = "extra_tasks";
    private Context                             context;
    private Fragment                            fragment;
    private FragmentManager                     fragmentManager;

    private ListView                            lv_extra_tasks;
    private List<Map<String,String>>            extraTasks;
    private String                              id_visit,
                                                id_Form;
    private Bundle                              bundle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        fragmentManager = getActivity().getSupportFragmentManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_extra_tasks, container, false);

        bundle  =   new Bundle();
        id_visit = TrackerManager.sharedInstance().getCurrent_pdv().get("visit_id");


        extraTasks = ExtraTasks.getExtraTasksByVisit(context,id_visit);
        //Toast.makeText(context, "Tamaño extra task: " + extraTasks.size(), Toast.LENGTH_SHORT).show();

        lv_extra_tasks              = (ListView)view.findViewById(R.id.lv_extra_tasks);
        lv_extra_tasks.setAdapter(new ExtraTasksAdapter(getActivity(), extraTasks));

        lv_extra_tasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {


                id_Form = Form.getIDFormByIDTask(context, extraTasks.get(position).get("id_generic_task")).get("id_form");

                if(extraTasks.get(position).get("status").equals("SENT")){

                    Toast.makeText(context, getActivity().getString(R.string.form_send_task), Toast.LENGTH_SHORT).show();

                }else if(!Form.getTimestamByIDFormandIDTask(context, extraTasks.get(position).get("id_generic_task"), id_Form).get("timestamp").equals("")
                        && !Form.getAnswersByIDForm(context, extraTasks.get(position).get("id_generic_task"),  id_Form).get("answers").equals("")){


                    Toast.makeText(context, getActivity().getString(R.string.form_send_task), Toast.LENGTH_SHORT).show();

                }else
                 {
                    bundle.putString("id_generic_task",extraTasks.get(position).get("id_generic_task"));
                    bundle.putString("id_form",extraTasks.get(position).get("frm_id_form"));



                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    fragment = new FragmentForms();
                    fragment.setArguments(bundle);

                    fragmentTransaction.replace(R.id.container, fragment, FragmentForms.TAG);
                    ((MainActivity) getActivity()).depthCounter = 5;
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    //Toast.makeText(getActivity(), "Tarea # " + (i+1), Toast.LENGTH_SHORT).show();
                }


            }
        });




        return view;
    }


}
