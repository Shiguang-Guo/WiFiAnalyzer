/*
 * WiFiAnalyzer
 * Copyright (C) 2019  VREM Software Development <VREMSoftwareDevelopment@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.vrem.wifianalyzer.wifi.scanner;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import com.vrem.wifianalyzer.settings.Settings;
import com.vrem.wifianalyzer.wifi.model.WiFiData;

import org.apache.commons.collections4.Closure;
import org.apache.commons.collections4.IterableUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

class Scanner implements ScannerService {
    private final List<UpdateNotifier> updateNotifiers;
    private final WifiManager wifiManager;
    private final Settings settings;
    private Transformer transformer;
    private WiFiData wiFiData;
    private Cache cache;
    private PeriodicScan periodicScan;
    private int networkId;
    Scanner(@NonNull WifiManager wifiManager, @NonNull Handler handler, @NonNull Settings settings) {
        this.updateNotifiers = new ArrayList<>();
        this.wifiManager = wifiManager;
        this.settings = settings;
        this.wiFiData = WiFiData.EMPTY;
        this.setTransformer(new Transformer());
        this.setCache(new Cache());
        this.periodicScan = new PeriodicScan(this, handler, settings);
    }

    @Override
    public void update() {
        enableWiFi();
        scanResults();
        wiFiData = transformer.transformToWiFiData(cache.getScanResults(), wiFiInfo(), wifiConfiguration());
        IterableUtils.forEach(updateNotifiers, new UpdateClosure());
    }

    @Override
    @NonNull
    public WiFiData getWiFiData() {
        return wiFiData;
    }

    @Override
    public void register(@NonNull UpdateNotifier updateNotifier) {
        updateNotifiers.add(updateNotifier);
    }

    @Override
    public void unregister(@NonNull UpdateNotifier updateNotifier) {
        updateNotifiers.remove(updateNotifier);
    }

    @Override
    public void pause() {
        periodicScan.stop();
    }

    @Override
    public boolean isRunning() {
        return periodicScan.isRunning();
    }

    @Override
    public void resume() {
        periodicScan.start();
    }

    @Override
    public void stop() {
        if (settings.isWiFiOffOnExit()) {
            try {
                wifiManager.setWifiEnabled(false);
            } catch (Exception e) {
                // critical error: do not die
            }
        }
    }

    @Override
    public boolean setWifiApEnabled(boolean enabled,String ssid, String password){
        if(enabled)
            wifiManager.setWifiEnabled(false);
        try {
            WifiConfiguration apConfig = new WifiConfiguration();
            apConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            apConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            apConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            apConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            apConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            apConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            apConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            apConfig.SSID = ssid;
            apConfig.preSharedKey=password;
            Log.i("Scanner","before");
            Method method = wifiManager.getClass().getMethod(
                    "setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE
            );
            Log.i("Scanner","我运行到了这儿");
            return (Boolean) method.invoke(wifiManager,apConfig,enabled);
        }catch (Exception e){
            Log.d("Scanner","setWifiApEnabled failed:" + e.getMessage());
            return false;
        }
    }
    private WifiConfiguration newWifiConfig(String ssid, String password, boolean isClient) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        if (isClient) {//作为客户端, 连接服务端wifi热点时要加双引号
            config.SSID = "\"" + ssid + "\"";
            config.preSharedKey = "\"" + password + "\"";
        } else {//作为服务端, 开放wifi热点时不需要加双引号
            config.SSID = ssid;
            config.preSharedKey = password;
        }
        config.hiddenSSID = true;
        config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
//        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCiphers.TKIP);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        config.status = WifiConfiguration.Status.ENABLED;
        return config;
    }
    @Override
    public boolean connect(@NonNull String ssid, @NonNull String password){
        Log.i("Scanner","you try to connect the wifi, ssid = [" + ssid + "]");
        boolean isConnected = isConnected(ssid);
        if(isConnected) return true;
        networkId = wifiManager.addNetwork(newWifiConfig(ssid,password,true));
        boolean result = wifiManager.enableNetwork(networkId, true);
        Log.i("Scanner", "connect: network enabled = " + result);
        return result;
    }
    @Override
    public boolean isConnected(String ssid){
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if(wifiInfo == null)
            return false;
        switch (wifiInfo.getSupplicantState()) {
            case AUTHENTICATING:
            case ASSOCIATING:
            case ASSOCIATED:
            case FOUR_WAY_HANDSHAKE:
            case GROUP_HANDSHAKE:
            case COMPLETED:
                return wifiInfo.getSSID().replace("\"", "").equals(ssid);
            default:
                return false;
        }
    }
    @NonNull
    PeriodicScan getPeriodicScan() {
        return periodicScan;
    }

    void setPeriodicScan(@NonNull PeriodicScan periodicScan) {
        this.periodicScan = periodicScan;
    }

    void setCache(@NonNull Cache cache) {
        this.cache = cache;
    }

    void setTransformer(@NonNull Transformer transformer) {
        this.transformer = transformer;
    }

    @NonNull
    List<UpdateNotifier> getUpdateNotifiers() {
        return updateNotifiers;
    }

    private void enableWiFi() {
        try {
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
            }
        } catch (Exception e) {
            // critical error: do not die
        }
    }

    @SuppressWarnings("deprecation")
    private void scanResults() {
        try {
            if (wifiManager.startScan()) {
                List<ScanResult> scanResults = wifiManager.getScanResults();
                if (scanResults != null) {
                    cache.add(scanResults);
                }
            }
        } catch (Exception e) {
            // critical error: do not die
        }
    }

    private WifiInfo wiFiInfo() {
        try {
            return wifiManager.getConnectionInfo();
        } catch (Exception e) {
            // critical error: do not die
            return null;
        }
    }

    private List<WifiConfiguration> wifiConfiguration() {
        try {
            return wifiManager.getConfiguredNetworks();
        } catch (Exception e) {
            // critical error: do not die
            return new ArrayList<>();
        }
    }

    private class UpdateClosure implements Closure<UpdateNotifier> {
        @Override
        public void execute(UpdateNotifier updateNotifier) {
            updateNotifier.update(wiFiData);
        }
    }
}
