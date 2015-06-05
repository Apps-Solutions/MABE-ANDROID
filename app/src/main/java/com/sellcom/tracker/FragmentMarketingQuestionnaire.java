package com.sellcom.tracker;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import util.MarketingQuestionAdapter;

/**
 * Created by hugo.figueroa 25/02/15.
 */

public class FragmentMarketingQuestionnaire extends Fragment{

    final static public String TAG = "TAG_FRAGMENT_MARQUETING_QUESTIONNAIRE";

    private         Fragment fragment;
    private         ListView listView;
    MarketingQuestionAdapter marketingQuestionAdapter;


    public FragmentMarketingQuestionnaire() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_marketing_questionnaire, container, false);

        listView                    = (ListView)view.findViewById(R.id.listView_Questionnaries);
        marketingQuestionAdapter    = new MarketingQuestionAdapter(getActivity());
        listView.setAdapter(marketingQuestionAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                switch (position){
                    case 0:
                        fragment = new FragmentShelfReviewSelfservice();
                        fragmentTransaction.replace(R.id.container, fragment, FragmentShelfReviewSelfservice.TAG);
                        ((MainActivity) getActivity()).depthCounter = 5;
                        break;
                    case 1:
                        fragment = new FragmentQualityIncidencesSelfservice();
                        fragmentTransaction.replace(R.id.container, fragment, FragmentQualityIncidencesSelfservice.TAG);
                        ((MainActivity) getActivity()).depthCounter = 5;
                        break;
                    case 2:
                        fragment = new FragmentOffersPromotionsSelfservice();
                        fragmentTransaction.replace(R.id.container, fragment, FragmentOffersPromotionsSelfservice.TAG);
                        ((MainActivity) getActivity()).depthCounter = 5;
                        break;
                    case 3:
                        fragment = new FragmentCompetitionPromotionSelfservice();
                        fragmentTransaction.replace(R.id.container, fragment, FragmentCompetitionPromotionSelfservice.TAG);
                        ((MainActivity) getActivity()).depthCounter = 5;
                        break;
                    case 4:
                        fragment = new FragmentWarehouseSelfservice();
                        fragmentTransaction.replace(R.id.container, fragment, FragmentWarehouseSelfservice.TAG);
                        ((MainActivity) getActivity()).depthCounter = 5;
                        break;
                    default:
                        break;
                }

                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();


            }
        });


        return view;
    }


}
