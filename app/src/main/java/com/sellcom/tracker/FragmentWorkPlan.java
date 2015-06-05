package com.sellcom.tracker;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import database.models.CustomerWorkPlan;
import util.DatesHelper;
import util.ParentWorkPlan;


public class FragmentWorkPlan extends Fragment{

    final static public String TAG = "workplan";

    static Context                  context;
    public Fragment                 fragment;
    public FragmentTransaction      fragmentTransaction;
    public FragmentManager          fragmentManager;

    View view;

    public WorkPlanAdapter          expandableListAdapter;
    public ExpandableListView       expandableListView;
    public ImageView                groupExpandlistIconCollapse;
    public ImageView                grouoExpandlistIconExpand;
    public ParentWorkPlan           parent;

    protected List<String>                                  listDataHeader;
    protected HashMap<String, List<Map<String,String>>>     listDataChild;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view                        = inflater.inflate(R.layout.fragment_work_plan, container, false);

        listDataHeader              = new ArrayList<String>();
        listDataChild               = new HashMap<String, List<Map<String,String>>>();

        expandableListView          = (ExpandableListView) view.findViewById(R.id.expList__workplan);

        groupExpandlistIconCollapse =  (ImageView) view.findViewById(R.id.icon_workplan_expandableCollapse);
        grouoExpandlistIconExpand   =  (ImageView) view.findViewById(R.id.icon_workplan_expandableExpand);

        expandableListView.setIndicatorBounds(20,20);
        expandableListAdapter       = new WorkPlanAdapter(getActivity().getApplicationContext(), listDataHeader, listDataChild);

        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return false;
            }
        });
        expandableListView.setAdapter(expandableListAdapter);

        listDataHeader.add(DatesHelper.getStringDateDays(new Date()));

        listDataChild.put(listDataHeader.get(0), CustomerWorkPlan.getAllInMaps(getActivity()));
        expandableListAdapter.notifyDataSetChanged();
        expandableListView.expandGroup(0);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(NavigationDrawerFragment.WORK_PLAN);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).onSectionAttached(NavigationDrawerFragment.WORK_PLAN);

    }

    public class WorkPlanAdapter extends BaseExpandableListAdapter {

        Context context;
        private List<String> listHeader;
        private HashMap<String, List<Map<String,String>>> lisHeaderDataChild;

        public WorkPlanAdapter(Context context, List<String> listHeader, HashMap<String, List<Map<String,String>>> lisHeaderDataChild){

            this.context            = context;
            this.listHeader         = listHeader;
            this.lisHeaderDataChild = lisHeaderDataChild;
        }

        @Override
        public int getGroupCount() {
            return this.listHeader.size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return this.listHeader.get(groupPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return this.lisHeaderDataChild.get(this.listHeader.get(groupPosition))
                    .get(childPosition);
        }

        @Override
        public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            final Map<String,String> childData = (Map<String,String>) getChild(groupPosition, childPosition);

            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.item_workplan, null);
            }
            convertView.setTag(childPosition);

            int real_position   = childPosition+1;
            ((TextView) convertView.findViewById(R.id.txv_clients_preview_num)).setText(""+real_position);

            TextView txtAux     = (TextView) convertView.findViewById(R.id.txt_pdv_name);
            txtAux.setText(childData.get("pdv"));

            txtAux              = (TextView) convertView.findViewById(R.id.txt_pdv_time);
            txtAux.setText(childData.get("scheduled_start"));

            txtAux              = (TextView) convertView.findViewById(R.id.txt_pdv_status);
            txtAux.setText(childData.get("visit_status"));

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Bundle bundle = new Bundle();
                    bundle.putString("visit_id",childData.get("visit_id"));

                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragment = new FragmentCustomerWorkPlan();
                    fragment.setArguments(bundle);

                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    //fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.shrink_out, R.anim.slide_from_left, R.anim.shrink_out);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.replace(R.id.container, fragment, FragmentCustomerWorkPlan.TAG);
                    fragmentTransaction.commit();

                    ((MainActivity) getActivity()).depthCounter = 2;
                }
            });

            return convertView;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return this.lisHeaderDataChild.get(this.listHeader.get(groupPosition)).size();
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

            String headerTitle = (String) getGroup(groupPosition);

            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.group_workplan, null);
            }

            ImageView flecha_grupo = (ImageView)convertView.findViewById(R.id.icon_workplan_expandableExpand);
            TextView lblListHeader = (TextView) convertView.findViewById(R.id.txt_workplan_header);
            flecha_grupo.setImageResource(R.drawable.flecha_cerrada);
            lblListHeader.setTypeface(null, Typeface.BOLD);
            lblListHeader.setText(headerTitle);

            if(isExpanded){
                flecha_grupo.setImageResource(R.drawable.flecha_abierta);

            }else{
                flecha_grupo.setImageResource(R.drawable.flecha_cerrada);
            }
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }

}
