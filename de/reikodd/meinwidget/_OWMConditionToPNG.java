package de.reikodd.meinwidget;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import java.text.SimpleDateFormat;
import java.util.regex.*;

public class _OWMConditionToPNG {

    public static String getPNGFileString(String conditions, int temp, int wind) {
        String PNGFileString = "";
        String patternConditions = "rain|snow|drizzle|thunderstorm|fog|sleet|haze|mist|dust";

        try {
            Matcher mConditions = Pattern.compile(patternConditions).matcher(conditions);
            if (mConditions.find()) {
                switch (mConditions.group()) {

                    case "rain":
                        PNGFileString = "drain";
                        break;

                    case "drizzle":
                        PNGFileString = "drain";
                        break;

                    case "snow":
                        PNGFileString = "dsnow";
                        break;

                    case "sleet":
                        PNGFileString = "dsnow";
                        break;

                    case "thunderstorm":
                        PNGFileString = "dthunderstorm";
                        break;

                    case "fog":
                        PNGFileString = "dfog";
                        break;

                    case "mist":
                        PNGFileString = "dfog";
                        break;

                    case "haze":
                        PNGFileString = "dfog";
                        break;

                    case "dust":
                        PNGFileString = "dfog";
                        break;

                    default:
                        PNGFileString = "dquestion";
                        break;
                }
            } else {
                switch (conditions) {

                    case "clear sky":
                        PNGFileString = "dclear";
                        break;

                    // wolkig
                    case "few clouds":
                        PNGFileString = "dmpcloudy";
                        break;
                    case "broken clouds":
                        PNGFileString = "dcloudy";
                        break;
                    case "overcast clouds":
                        PNGFileString = "dcloudy";
                        break;
                    case "scattered clouds":
                        PNGFileString = "dcloudy";
                        break;

                    case "squalls":
                        PNGFileString = "dstorm";
                        break;

                    case "tornado":
                        PNGFileString = "dstorm";
                        break;

                    case "smoke":
                        PNGFileString = "dfog";
                        break;

                    default:
                        PNGFileString = "dquestion";
                        break;
                }
            }
        } catch (IllegalStateException e) {
            PNGFileString = "dquestion";
        }

        if (temp >= 25 && conditions.contains("Clear")) {
            PNGFileString = "dhotsun";
        }

        if (wind >= 12 && !conditions.contains("Clear")) {
            PNGFileString = "dstorm";
        }

        return PNGFileString;
    }

    public static String getPNGFileOfDate(Context context, String PNGFileString, long ts) {

        SharedPreferences settings;
        settings = PreferenceManager.getDefaultSharedPreferences(context);
        SimpleDateFormat sdfdate = new SimpleDateFormat("dd.MM.");
        sdfdate.format(ts);
        String PNGFileStringNew=PNGFileString;

        String patternHDay = "01.01.|14.02.|29.07.|13.08.|24.12.|25.12.|26.12.|31.12.";
        String patternEastern = settings.getString("patternEastern","--");
        String patternBirthday = settings.getString("patternBirthday","--");

        try {
            Matcher mpatternHDay = Pattern.compile(patternHDay).matcher(
                    sdfdate.format(ts));
            if (mpatternHDay.find()) {
                switch (mpatternHDay.group()) {

                    case "01.01.":
                        DateEastern.getEasterDate(context,ts);
                        break;

                    case "14.02.":
                        PNGFileStringNew = "h" + PNGFileString;
                        break;

                    case "29.07.":
                        PNGFileStringNew = "h" + PNGFileString;
                        break;

                    case "13.08.":
                        PNGFileStringNew = "h" + PNGFileString;
                        break;

                    case "24.12.":
                        PNGFileStringNew = "w" + PNGFileString;
                        break;

                    case "25.12.":
                        PNGFileStringNew = "w" + PNGFileString;
                        break;

                    case "26.12.":
                        PNGFileStringNew = "w" + PNGFileString;
                        break;

                    case "31.12.":
                        PNGFileStringNew = "s" + PNGFileString;
                        break;
                }
            }
        } catch (IllegalStateException e) {}

        try {
            Matcher mEastern = Pattern.compile(patternEastern).matcher(
                    sdfdate.format(ts));
            if (mEastern.find()) {
                PNGFileStringNew = "e" + PNGFileString;
            }
        } catch (IllegalStateException e) {}


        try {
            Matcher mBirthday = Pattern.compile(patternBirthday).matcher(
                    sdfdate.format(ts));
            if (mBirthday.find()) {
                PNGFileStringNew = "b" + PNGFileString;
            }
        } catch (IllegalStateException e) {}

        return PNGFileStringNew;
    }
}