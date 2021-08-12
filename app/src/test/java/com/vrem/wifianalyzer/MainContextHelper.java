package com.vrem.wifianalyzer;

import com.vrem.wifianalyzer.settings.Settings;
import com.vrem.wifianalyzer.vendor.model.VendorService;
import com.vrem.wifianalyzer.wifi.filter.adapter.FilterAdapter;
import com.vrem.wifianalyzer.wifi.scanner.ScannerService;

import org.apache.commons.collections4.Closure;
import org.apache.commons.collections4.IterableUtils;

import java.util.HashMap;
import java.util.Map;

import static org.powermock.api.mockito.PowerMockito.mock;

public enum MainContextHelper {
    INSTANCE;

    private final Map<Class<?>, Object> saved;
    private final MainContext mainContext;

    MainContextHelper() {
        mainContext = MainContext.INSTANCE;
        saved = new HashMap<>();
    }

    private Object save(Class<?> clazz, Object object) {
        saved.put(clazz, object);
        return mock(clazz);
    }

    public Settings getSettings() {
        Settings result = (Settings) save(Settings.class, mainContext.getSettings());
        mainContext.setSettings(result);
        return result;
    }

    public VendorService getVendorService() {
        VendorService result = (VendorService) save(VendorService.class, mainContext.getVendorService());
        mainContext.setVendorService(result);
        return result;
    }

    public ScannerService getScannerService() {
        ScannerService result = (ScannerService) save(ScannerService.class, mainContext.getScannerService());
        mainContext.setScannerService(result);
        return result;
    }

    public MainActivity getMainActivity() {
        MainActivity result = (MainActivity) save(MainActivity.class, mainContext.getMainActivity());
        mainContext.setMainActivity(result);
        return result;
    }

    public Configuration getConfiguration() {
        Configuration result = (Configuration) save(Configuration.class, mainContext.getConfiguration());
        mainContext.setConfiguration(result);
        return result;
    }

    public FilterAdapter getFilterAdapter() {
        FilterAdapter result = (FilterAdapter) save(FilterAdapter.class, mainContext.getFilterAdapter());
        mainContext.setFilterAdapter(result);
        return result;
    }

    public void restore() {
        IterableUtils.forEach(saved.keySet(), new RestoreClosure());
        saved.clear();
    }

    private class RestoreClosure implements Closure<Class<?>> {
        @Override
        public void execute(Class<?> input) {
            Object result = saved.get(input);
            if (input.equals(Settings.class)) {
                mainContext.setSettings((Settings) result);
            } else if (input.equals(VendorService.class)) {
                mainContext.setVendorService((VendorService) result);
            } else if (input.equals(ScannerService.class)) {
                mainContext.setScannerService((ScannerService) result);
            } else if (input.equals(MainActivity.class)) {
                mainContext.setMainActivity((MainActivity) result);
            } else if (input.equals(Configuration.class)) {
                mainContext.setConfiguration((Configuration) result);
            } else if (input.equals(FilterAdapter.class)) {
                mainContext.setFilterAdapter((FilterAdapter) result);
            } else {
                throw new IllegalArgumentException(input.getName());
            }
        }
    }
}
