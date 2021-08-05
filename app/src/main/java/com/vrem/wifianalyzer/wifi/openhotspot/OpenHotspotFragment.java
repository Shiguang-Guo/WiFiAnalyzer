package com.vrem.wifianalyzer.wifi.openhotspot;

import android.support.v4.app.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.vrem.wifianalyzer.MainContext;
import com.vrem.wifianalyzer.R;
import com.vrem.wifianalyzer.wifi.refresh.RefreshAction;
import com.vrem.wifianalyzer.wifi.refresh.RefreshListener;
import com.vrem.wifianalyzer.wifi.openhotspot.OpenHotspotAdapter;

public class OpenHotspotFragment extends Fragment{
    private SwipeRefreshLayout swipeRefreshLayout;
    private OpenHotspotAdapter openHotspotAdapter;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.open_hotspot, container, false);

//        swipeRefreshLayout = view.findViewById(R.id.openHotspotRefresh);
//        swipeRefreshLayout.setOnRefreshListener(new RefreshListener(this));

//        openHotspotAdapter = new OpenHotspotAdapter();

//        MainContext.INSTANCE.getScannerService().register(openHotspotAdapter);
        return view;
    }
//    @Override
//    public void refresh(){
//
//    }
}
