package com.hbidriver.app.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hbidriver.app.R;
import com.hbidriver.app.activity.AcountActivity;
import com.hbidriver.app.activity.ChartActivity;
import com.hbidriver.app.activity.SaleActivity;
import com.hbidriver.app.adapter.HomeAdapter;
import com.hbidriver.app.banner.MainSliderAdapter;
import com.hbidriver.app.banner.PicassoImageLoadingService;
import com.hbidriver.app.callback.HomeCallback;
import com.hbidriver.app.model.Home;
import com.hbidriver.app.utils.NextActivity;

import java.util.ArrayList;
import java.util.List;

import ss.com.bannerslider.Slider;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements HomeCallback {
    private View view;
    private Slider slider;
    private RecyclerView recyclerView;
    private List<Home> list;
    private HomeAdapter mAdapter;
    private LinearLayoutManager manager;

    String[] name={
            "Chart Room",
            "My Account",
            "Setting",

    };
    int[] image={
            R.drawable.ic_circle_color,
            R.drawable.ic_user_color,
            R.drawable.ic_settings_color
    };

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);

        initGUI();
        setupBanner();
        setData();

        return view;
    }

    private void initGUI(){
        recyclerView = view.findViewById(R.id.recycler_view);
    }

    private void setupBanner(){
        slider = view.findViewById(R.id.banner_slider);
        Slider.init(new PicassoImageLoadingService(getActivity()));

        //delay for testing empty view functionality
        slider.postDelayed(new Runnable() {
            @Override
            public void run() {
                slider.setAdapter(new MainSliderAdapter());
                slider.setSelectedSlide(0);
            }
        }, 1500);
    }

    private void setData(){
        list = new ArrayList<>();
        manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        getData();
        mAdapter = new HomeAdapter(list,this);
        recyclerView.setAdapter(mAdapter);

    }

    private void getData(){
        for(int i = 0; i<name.length; i++){
            list.add(new Home(name[i],image[i]));
        }
    }

    @Override
    public void onItemClick(Home item, int position) {
       if (position==0){
            NextActivity.goActivity(getActivity(), new ChartActivity());
        }else if (position==1){
           NextActivity.goActivity(getActivity(), new AcountActivity());
       }else if (position==2){
           NextActivity.goActivity(getActivity(), new AcountActivity());
       }
    }
}
