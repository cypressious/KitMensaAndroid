package com.cypressworks.mensaplan.planmanager;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

public class ErzbergerPlanManager extends AkkStudentenWerkPlanManager {
    private final static String providerName = "erzberger";
    public final static String fullProviderName = "Mensa Erzbergerstraße";
    private final static String studentenWerkKey = "erzberger";

    private static final Map<String, String> lineNames = new HashMap<>();

    static {
        lineNames.put("wahl1", "Wahlessen 1");
        lineNames.put("wahl2", "Wahlessen 2");
        lineNames.put("wahl3", "Wahlessen 3");
    }

    @Override
    protected String getLineName(final String key) {
        return lineNames.get(key);
    }

    public ErzbergerPlanManager(final Context c) {
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

}
