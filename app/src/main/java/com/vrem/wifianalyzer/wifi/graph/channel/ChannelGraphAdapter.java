/*
 *    Copyright (C) 2015 - 2016 VREM Software Development <VREMSoftwareDevelopment@gmail.com>
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
package com.vrem.wifianalyzer.wifi.graph.channel;

import android.support.annotation.NonNull;
import android.view.View;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.vrem.wifianalyzer.MainContext;
import com.vrem.wifianalyzer.wifi.UpdateNotifier;
import com.vrem.wifianalyzer.wifi.WiFiConstants;
import com.vrem.wifianalyzer.wifi.graph.Colors;
import com.vrem.wifianalyzer.wifi.graph.GraphAdapterUtils;
import com.vrem.wifianalyzer.wifi.model.Frequency;
import com.vrem.wifianalyzer.wifi.model.WiFiData;
import com.vrem.wifianalyzer.wifi.model.WiFiDetails;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

class ChannelGraphAdapter implements UpdateNotifier {
    private final MainContext mainContext = MainContext.INSTANCE;
    private final GraphView graphView;
    private final Constraints constraints;
    private final Map<String, LineGraphSeries<DataPoint>> seriesMap;

    ChannelGraphAdapter(@NonNull GraphView graphView, @NonNull Constraints constraints) {
        this.graphView = graphView;
        this.constraints = constraints;
        this.seriesMap = new TreeMap<>();
        this.mainContext.getScanner().addUpdateNotifier(this);
        this.graphView.addSeries(defaultsSeries());
    }

    @Override
    public void update(@NonNull WiFiData wifiData) {
        Set<String> newSeries = new TreeSet<>();
        for (WiFiDetails wifiDetails : wifiData.getWiFiList(constraints.getWiFiBand())) {
            String key = wifiDetails.getTitle();
            newSeries.add(key);
            LineGraphSeries<DataPoint> series = seriesMap.get(key);
            if (series == null) {
                series = new LineGraphSeries<>(createDataPoints(wifiDetails));
                setSeriesOptions(series, wifiDetails);
                graphView.addSeries(series);
                seriesMap.put(key, series);
            } else {
                series.resetData(createDataPoints(wifiDetails));
            }
        }
        GraphAdapterUtils.removeSeries(graphView, seriesMap, newSeries);
        GraphAdapterUtils.updateLegendRenderer(graphView);

        graphView.setVisibility(constraints.getWiFiBand().equals(mainContext.getSettings().getWiFiBand()) ? View.VISIBLE : View.GONE);
    }

    private void setSeriesOptions(@NonNull LineGraphSeries<DataPoint> series, @NonNull WiFiDetails wifiDetails) {
        if (wifiDetails.isConnected()) {
            series.setColor(Colors.BLUE.getPrimary());
            series.setBackgroundColor(Colors.BLUE.getBackground());
            series.setThickness(6);
        } else {
            Colors colors = Colors.findRandomColor();
            series.setColor(colors.getPrimary());
            series.setBackgroundColor(colors.getBackground());
            series.setThickness(2);
        }
        series.setDrawBackground(true);
        series.setTitle(wifiDetails.getTitle() + " " + wifiDetails.getChannel());
    }

    private DataPoint[] createDataPoints(@NonNull WiFiDetails wifiDetails) {
        int channel = wifiDetails.getChannel();
        int level = wifiDetails.getLevel();
        return new DataPoint[]{
                new DataPoint(channel - Frequency.CHANNEL_SPREAD, WiFiConstants.MIN_Y),
                new DataPoint(channel - Frequency.CHANNEL_SPREAD / 2, level),
                new DataPoint(channel, level),
                new DataPoint(channel + Frequency.CHANNEL_SPREAD / 2, level),
                new DataPoint(channel + Frequency.CHANNEL_SPREAD, WiFiConstants.MIN_Y)
        };
    }

    private LineGraphSeries<DataPoint> defaultsSeries() {
        int minValue = constraints.channelFirst() - Frequency.CHANNEL_SPREAD;
        int maxValue = constraints.channelLast() + Frequency.CHANNEL_SPREAD;
        if (maxValue % 2 != 0) {
            maxValue++;
        }
        DataPoint[] dataPoints = new DataPoint[]{
                new DataPoint(minValue, WiFiConstants.MIN_Y),
                new DataPoint(maxValue, WiFiConstants.MIN_Y)
        };

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints);
        series.setColor(Colors.TRANSPARENT.getPrimary());
        series.setDrawBackground(false);
        series.setThickness(0);
        series.setTitle("");
        return series;
    }
}
