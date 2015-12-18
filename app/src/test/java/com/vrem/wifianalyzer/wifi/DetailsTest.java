/*
 *    Copyright (C) 2010 - 2015 VREM Software Development <VREMSoftwareDevelopment@gmail.com>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.vrem.wifianalyzer.wifi;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest(WifiManager.class)
public class DetailsTest {
    @Mock private ScanResult scanResult;

    private Details fixture;

    @Before
    public void setUp() throws Exception {
        fixture = Details.make(scanResult);
    }

    @Test
    public void testGetFrequency() throws Exception {
        // setup
        Frequency expected = Frequency.TWO_POINT_FOUR;
        scanResult.frequency = 2470;
        // execute
        Frequency actual = fixture.getFrequency();
        // validate
        assertEquals(expected, actual);
    }

    @Test
    public void testGetChannel() throws Exception {
        // setup
        int expected = 5;
        scanResult.frequency = 2435;
        // execute
        int actual = fixture.getChannel();
        // validate
        assertEquals(expected, actual);
    }

    @Test
    public void testGetSecurity() throws Exception {
        // setup
        Security expected = Security.WPA;
        scanResult.capabilities = "WPA";
        // execute
        Security actual = fixture.getSecurity();
        // validate
        assertEquals(expected, actual);
    }

    @Test
    public void testGetWifiLevel() throws Exception {
        // setup
        PowerMockito.mockStatic(WifiManager.class);
        Strength expected = Strength.TWO;
        scanResult.level = -86;
        // expected
        Mockito.when(WifiManager.calculateSignalLevel(scanResult.level, Strength.values().length)).thenReturn(expected.ordinal());
        // execute
        Strength actual = fixture.getStrength();
        // validate
        assertEquals(expected, actual);
    }

    @Test
    public void testGetSSID() throws Exception {
        // setup
        scanResult.SSID = "xyzSSID";
        // execute
        String actual = fixture.getSSID();
        // validate
        assertEquals(scanResult.SSID, actual);
    }

    @Test
    public void testGetBSSID() throws Exception {
        // setup
        scanResult.BSSID = "xyzBSSID";
        // execute
        String actual = fixture.getBSSID();
        // validate
        assertEquals(scanResult.BSSID, actual);
    }

    @Test
    public void testGetLevel() throws Exception {
        // setup
        scanResult.level = 3;
        // execute
        int actual = fixture.getLevel();
        // validate
        assertEquals(scanResult.level, actual);
    }

    @Test
    public void testGetCapabilities() throws Exception {
        // setup
        scanResult.capabilities = "xyzCapabilities";
        // execute
        String actual = fixture.getCapabilities();
        // validate
        assertEquals(scanResult.capabilities, actual);
    }
}