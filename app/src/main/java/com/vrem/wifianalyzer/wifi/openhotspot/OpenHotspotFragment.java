package com.vrem.wifianalyzer.wifi.openhotspot;

//import android.support.v4.app.Fragment;
//import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.content.Context;
import com.vrem.wifianalyzer.MainContext;
import com.vrem.wifianalyzer.R;
import com.vrem.wifianalyzer.wifi.refresh.RefreshAction;
import com.vrem.wifianalyzer.wifi.refresh.RefreshListener;
import com.vrem.wifianalyzer.wifi.openhotspot.OpenHotspotAdapter;
import com.vrem.wifianalyzer.MainContext;
import java.lang.reflect.Method;

public class OpenHotspotFragment extends Fragment{
    private Button openButton;
    private WifiManager wifiManager;
    private boolean flag = false;
//    private SwipeRefreshLayout swipeRefreshLayout;
//    private OpenHotspotAdapter openHotspotAdapter;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.open_hotspot, container, false);
        openButton = view.findViewById(R.id.open_hotspot);
//        swipeRefreshLayout = view.findViewById(R.id.openHotspotRefresh);
//        swipeRefreshLayout.setOnRefreshListener(new RefreshListener(this));

//        openHotspotAdapter = new OpenHotspotAdapter();

//        MainContext.INSTANCE.getScannerService().register(openHotspotAdapter);
//        wifiManager = (WifiManager) MainContext.INSTANCE.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        return view;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
    }
//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        openButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.i("TAG","you are opening hotspot");
//                flag = !flag;
//                if(setHotspotEnabled(flag)){
//                    Log.i("TAG","success!");
//                }
//                else
//                    Log.i("TAG","fail!");
//            }
//        });
//    }
//    public boolean setHotspotEnabled(boolean enabled){
//        if(enabled){
//            wifiManager.setWifiEnabled(false);
//        }
//        try {
//            WifiConfiguration wifiConfiguration = new WifiConfiguration();
//            wifiConfiguration.SSID = "myssid";
//            wifiConfiguration.preSharedKey="123456";
//            Method method = wifiManager.getClass().getMethod(
//                    "setHotspotEnabled", WifiConfiguration.class, Boolean.TYPE
//            );
//            return (Boolean) method.invoke(wifiManager,wifiConfiguration,enabled);
//        }catch (Exception e){
//            return false;
//        }
//    }
}
