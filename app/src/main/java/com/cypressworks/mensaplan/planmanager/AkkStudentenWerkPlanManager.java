package com.cypressworks.mensaplan.planmanager;

import android.content.Context;

import com.cypressworks.mensaplan.MensaWidgetProvider;
import com.cypressworks.mensaplan.MyApplication;
import com.cypressworks.mensaplan.food.Line;
import com.cypressworks.mensaplan.food.Meal;
import com.cypressworks.mensaplan.food.Plan;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Kirill Rakhman
 */
public abstract class AkkStudentenWerkPlanManager extends PlanManager {
    protected static final String blankAKKURL = "http://mensa.akk.uni-karlsruhe.de/?DATUM=%DATE%&%KEY%=1";
    private static final String blankStudentenwerkURL = "https://www.sw-ka.de/de/essen/?view=ok&STYLE=popup_plain&c=%KEY%&p=1&kw=%KW%";
    private static final String studentenwerkRestURL = "https://www.sw-ka.de/json_interface/canteen/?mensa[]=%KEY%";

    private static final SimpleDateFormat dayMonthFormat = new SimpleDateFormat("dd.MM",
                                                                                Locale.GERMANY);
    private final static DecimalFormat priceFormat = new DecimalFormat("0.00 €");

    AkkStudentenWerkPlanManager(final Context c) {
        super(c);
    }

    abstract protected String getAkkKey();

    abstract protected String getStudentenwerkKey();

    abstract protected String getLineName(String key);

    @Override
    protected final Plan downloadPlan(final Calendar date) {

        Plan plan = null;

        try {
            plan = getFromRestApi(date);
        } catch (final IOException | ParseException e) {
            e.printStackTrace();
        }

        if (plan == null && getRelativeDate(date) != RelativeDate.PAST) {
            try {
                plan = parseFromStudentenwerkWeb(date);
            } catch (final IOException e) {
                plan = new Plan();
                e.printStackTrace();
            }
        }

        if (plan == null) {
            plan = cachePlan(new Plan(), date);
        }

        return plan;

    }

    @SuppressWarnings("unchecked")
    private Plan getFromRestApi(final Calendar getDate) throws IOException, ParseException {
        Plan returnPlan = null;

        final String key = getStudentenwerkKey();
        final URL url = new URL(studentenwerkRestURL.replace("%KEY%", key));
        final String jsonString = downloadString(url);

        final JSONParser parser = new JSONParser();
        final JSONObject json = (JSONObject) ((JSONObject) parser.parse(jsonString)).get(key);

        for (final Map.Entry<String, JSONObject> entry : (Iterable<Map.Entry<String, JSONObject>>) json.entrySet()) {
            final Calendar date = new GregorianCalendar();
            date.setTimeInMillis(Long.parseLong(entry.getKey()) * 1000L);

            final List<Line> lines = new ArrayList<>();

            final JSONObject dayObject = entry.getValue();
            for (final String lineKey : (Iterable<String>) dayObject.keySet()) {
                final String lineName = getLineName(lineKey);
                final Line line = new Line(lineName);

                final JSONArray mealArray = (JSONArray) dayObject.get(lineKey);
                for (Object aMealArray : mealArray) {
                    final JSONObject mealObject = (JSONObject) aMealArray;

                    String price = (String) mealObject.get("info");
                    final Object price_1 = mealObject.get("price_1");
                    if (price_1 != null) {
                        price += " " + priceFormat.format(price_1).replace(".", ",").trim();
                    }
                    final Meal meal = new Meal(mealObject.optString("meal"),
                                               mealObject.optString("dish"), price);

                    meal.setBio(mealObject.optBoolean("bio"));
                    meal.setFish(mealObject.optBoolean("fish"));
                    meal.setPork(mealObject.optBoolean("pork"));
                    meal.setCow(mealObject.optBoolean("cow"));
                    meal.setCow_aw(mealObject.optBoolean("cow_aw"));
                    meal.setVegan(mealObject.optBoolean("vegan"));
                    meal.setVeg(mealObject.optBoolean("veg"));

                    line.addMeal(meal);
                }
                lines.add(line);

            }

            final Plan cachedPlan = cachePlan(new Plan(lines), date);

            if (getDate.get(Calendar.ERA) == date.get(Calendar.ERA) //
                    && getDate.get(Calendar.YEAR) == date.get(Calendar.YEAR) //
                    && getDate.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR)) {
                returnPlan = cachedPlan;
            }

        }

        MensaWidgetProvider.updateWidgets(MyApplication.instance);

        return returnPlan;
    }

    private Plan parseFromStudentenwerkWeb(final Calendar date) throws IOException {

        final String weekString = String.valueOf(date.get(Calendar.WEEK_OF_YEAR));

        final URL url = new URL(blankStudentenwerkURL.replace("%KW%", weekString).replace("%KEY%",
                                                                                          getStudentenwerkKey()));

        final Document doc = Jsoup.parse(downloadString(url));

        // gesuchter Tag
        final String dateHeading = dayMonthFormat.format(date.getTime());
        final Elements dayElements = doc.select("h1:contains(" + dateHeading + ") + table");
        if (dayElements.isEmpty()) {
            return null;
        }

        final Element dayElement = dayElements.get(0);

        // alle Linien
        final Elements lineElements = dayElement.select("td[width=20%] + td");

        final List<Line> lines = new ArrayList<>();

        for (final Element lineElement : lineElements) {
            final String lineName = lineElement.previousElementSibling().ownText();
            final Line line = new Line(lineName);

            // alle Gerichte
            final Elements mealElements = lineElement.select("tr");

            for (final Element mealElement : mealElements) {
                final String mealName = mealElement.select("span.bg").text();
                final String mealPrice = mealElement.select("span.price_1").text();

                final Meal meal = new Meal(mealName, null, mealPrice);
                line.addMeal(meal);
            }

            lines.add(line);
        }

        return cachePlan(new Plan(lines), date);
    }

    private static String downloadString(final URL url) throws IOException {
        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("jsonapi", "AhVai6OoCh3Quoo6ji".toCharArray());
            }
        });

        final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("User-Agent",
                                "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:13.0) Gecko/20100101 Firefox/13.0");
        // conn.setRequestProperty("Content-Type", "utf-8");
        // final String contentEncoding = conn.getContentEncoding();

        StringBuilder html = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()))) {
            String s;

            while ((s = reader.readLine()) != null) {
                html.append(s);
            }

        }

        return html.toString();
    }
}
