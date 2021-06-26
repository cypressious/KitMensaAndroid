package com.cypressworks.mensaplan.planmanager;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

public class HolzgartenstrPlanManager extends AkkStudentenWerkPlanManager {
    private final static String providerName = "holzgarten";
    public final static String fullProviderName = "Mensa I Holzgartenstraße (Stuttgart-Mitte) ";
    private final static String studentenWerkKey = "holzgarten";

    private static final Map<String, String> lineNames = new HashMap<>();

    static {
        lineNames.put("gut", "Gut & Günstig 1");
        lineNames.put("gut1", "Gut & Günstig 1");
        lineNames.put("gut2", "Gut & Günstig 2");
    }

    public HolzgartenstrPlanManager(final Context c) {
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
