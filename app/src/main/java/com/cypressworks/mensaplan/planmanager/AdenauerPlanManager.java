package com.cypressworks.mensaplan.planmanager;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

public class AdenauerPlanManager extends AkkStudentenWerkPlanManager {
    private final static String providerName = "adenauer";
    public final static String fullProviderName = "Mensa am Adenauer Ring";
    private final static String akkKey = "uni";
    private final static String studentenWerkKey = "adenauerring";

    private static final Map<String, String> lineNames = new HashMap<>();

    static {
        lineNames.put("l1", "Linie 1");
        lineNames.put("l2", "Linie 2");
        lineNames.put("l3", "Linie 3");
        lineNames.put("l45", "Linie 4/5");
        lineNames.put("schnitzelbar", "Schnitzelbar");
        lineNames.put("update", "L6 Update");
        lineNames.put("abend", "Abend");
        lineNames.put("aktion", "Curry Queen");
        lineNames.put("heisstheke", "Cafeteria Heiße Theke");
        lineNames.put("nmtisch", "Cafeteria ab 14:30");
    }

    public AdenauerPlanManager(final Context c) {
        super(c);
    }

    @Override
    protected String getAkkKey() {
        return akkKey;
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
