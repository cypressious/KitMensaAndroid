package com.cypressworks.mensaplan.planmanager;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

public class PforzheimPlanManager extends AkkStudentenWerkPlanManager {
    private final static String providerName = "tiefenbronner";
    public final static String fullProviderName = "Mensa Fachhochschule Pforzheim";
    private final static String akkKey = "fh-pf";
    private final static String studentenWerkKey = "tiefenbronner";

    private static final Map<String, String> lineNames = new HashMap<>();

    static {
        lineNames.put("wahl1", "Wahlessen 1");
        lineNames.put("wahl2", "Wahlessen 2");
        lineNames.put("gut", "Gut & Günstig");
        lineNames.put("buffet", "Buffet");
    }

    public PforzheimPlanManager(final Context c) {
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
