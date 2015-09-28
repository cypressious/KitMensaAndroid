package com.cypressworks.mensaplan.planmanager;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

public class GottesauePlanManager extends AkkStudentenWerkPlanManager {
    private final static String providerName = "gottesaue";
    public final static String fullProviderName = "Mensa Schloss Gottesaue";
    private final static String akkKey = "mh";
    private final static String studentenWerkKey = "gottesaue";

    private static final Map<String, String> lineNames = new HashMap<>();

    static {
        lineNames.put("wahl1", "Wahlessen 1");
        lineNames.put("wahl2", "Wahlessen 2");
    }

    public GottesauePlanManager(final Context c) {
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
