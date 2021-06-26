package com.cypressworks.mensaplan.planmanager;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

public class CafeteriaMoltkePlanManager extends AkkStudentenWerkPlanManager {
    private final static String providerName = "x1moltkestrasse";
    public final static String fullProviderName = "Caféteria Moltkestraße 30";
    private final static String studentenWerkKey = "x1moltkestrasse";

    private static final Map<String, String> lineNames = new HashMap<>();

    static {
        lineNames.put("gut", "Gut & Günstig");
    }

    public CafeteriaMoltkePlanManager(final Context c) {
        super(c);
    }

    @Override
    protected String getStudentenwerkKey() {
        return studentenWerkKey;
    }

    @Override
    public String getFullProviderName() {
        return fullProviderName;
    }

    @Override
    public String getProviderName() {
        return providerName;
    }

    @Override
    protected String getLineName(final String key) {
        return lineNames.get(key);
    }

}
