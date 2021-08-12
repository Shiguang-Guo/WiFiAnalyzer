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
//import com.vrem.wifianalyzer.MainActivity;
import com.vrem.wifianalyzer.MainContextHelper;

import com.vrem.wifianalyzer.wifi.scanner.ScannerService;

public class OpenHotspotActivity extends AppCompatActivity {
    private Button connectButton;
    private WifiManager wifiManager;
    private ScannerService scanner;
    private String ssid;
    private String password;
    private EditText ssidEditText, passwordEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_hotspot);
//        openButton = (Button) findViewById(R.id.open);
//        getButton = (Button)findViewById(R.id.get);
//        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        connectButton = (Button)findViewById(R.id.connect);
        scanner = MainContextHelper.INSTANCE.getScannerService();
        ssidEditText = (EditText)findViewById(R.id.ssid_input);
        passwordEditText = (EditText)findViewById(R.id.password_input);
        passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        connectButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ssid = ssidEditText.getText().toString();
                password = passwordEditText.getText().toString();
                Log.i("OpenHotspotActivity","you click the get button to connect, ssid = [" + ssid + "], password = " + password);
                scanner.connect(ssid,password);
            }
        });
    }
}