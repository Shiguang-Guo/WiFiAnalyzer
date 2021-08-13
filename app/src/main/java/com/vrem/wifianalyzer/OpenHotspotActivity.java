package com.vrem.wifianalyzer;

import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.vrem.wifianalyzer.MainContext;
import com.vrem.wifianalyzer.WifiListAdapter;
import com.vrem.wifianalyzer.settings.Settings;
import com.vrem.wifianalyzer.wifi.band.WiFiBand;
import com.vrem.wifianalyzer.wifi.band.WiFiChannel;
import com.vrem.wifianalyzer.wifi.model.SortBy;
import com.vrem.wifianalyzer.wifi.model.WiFiData;
import com.vrem.wifianalyzer.wifi.model.WiFiDetail;
import com.vrem.wifianalyzer.wifi.predicate.WiFiBandPredicate;
import com.vrem.wifianalyzer.wifi.scanner.ScannerService;
import com.vrem.wifianalyzer.wifi.band.WiFiChannels;
import com.vrem.wifianalyzer.wifi.model.WiFiData;

import org.apache.commons.collections4.Predicate;

import java.util.ArrayList;
import java.util.List;

public class OpenHotspotActivity extends AppCompatActivity {
    private Button connectButton, getButton;
    private WifiManager wifiManager;
    private ScannerService scanner;
    private String ssid;
    private String password;
    private EditText ssidEditText, passwordEditText;
    private ListView wifiList;
    private List<String> dataList = new ArrayList<>();
    private WifiListAdapter wifiListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_hotspot);
        getButton = (Button)findViewById(R.id.get);
        connectButton = (Button)findViewById(R.id.connect);

        scanner = MainContext.INSTANCE.getScannerService();
        ssidEditText = (EditText)findViewById(R.id.ssid_input);
        passwordEditText = (EditText)findViewById(R.id.password_input);
        passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        wifiList = (ListView) findViewById(R.id.wifi_list);
        wifiListAdapter = new WifiListAdapter(this, dataList);
        for (int i = 1; i <= 5; i++) {
            dataList.add("显示内容" + i);
        }
        wifiList.setAdapter(wifiListAdapter);
        wifiList.setSelection(4);
        connectButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ssid = ssidEditText.getText().toString();
                password = passwordEditText.getText().toString();
                Log.i("OpenHotspotActivity","you click the get button to connect, ssid = [" + ssid + "], password = " + password);
                scanner.connect(ssid,password);
            }
        });
        getButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                WiFiData wiFiData= scanner.getWiFiData();
                Settings settings = MainContext.INSTANCE.getSettings();
                WiFiBand wiFiBand = settings.getWiFiBand();
                Predicate<WiFiDetail> predicate = new WiFiBandPredicate(wiFiBand);
                List<WiFiDetail> wifiDetails = wiFiData.getWiFiDetails(predicate, SortBy.STRENGTH);
                dataList.clear();
                for(WiFiDetail detail:wifiDetails){
                    dataList.add(detail.getTitle());
                }
                wifiListAdapter.notifyDataSetChanged();
            }
        });
    }
}