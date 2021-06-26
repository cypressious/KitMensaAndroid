package com.cypressworks.mensaplan.planmanager;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

public class MoltkePlanManager extends AkkStudentenWerkPlanManager {
    private final static String providerName = "moltke";
    public final static String fullProviderName = "Mensa Moltkestraße";
    private final static String studentenWerkKey = "moltke";

    private static final Map<String, String> lineNames = new HashMap<>();

    static {
        lineNames.put("wahl1", "Wahlessen 1");
        lineNames.put("wahl2", "Wahlessen 2");
        lineNames.put("aktion", "Aktionstheke");
        lineNames.put("gut", "Gut & Günstig");
        lineNames.put("buffet", "Buffet");
        lineNames.put("schnitzelbar", "Schnitzelbar");
    }

    public MoltkePlanManager(final Context c) {
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
