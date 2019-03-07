package de.reikodd.meinwidget;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;


public class DateEastern {

    public static void getEasterDate(Context context, long ts) {

        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = PreferenceManager.getDefaultSharedPreferences(context);

        SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy");
        int jahr = Integer.parseInt(sdfdate.format(ts));
        int a, b, c, d, e, f, g, h, i, j, k, l;
        int x, mon, tag;

        a = jahr % 19;
        b = jahr / 100;
        c = jahr % 100;
        d = b / 4;
        e = b % 4;
        f = (b + 8) / 25;
        g = (b - f + 1) / 3;
        h = (19 * a + b - d - g + 15) % 30;
        i = c / 4;
        j = c % 4;
        k = (32 + 2 * e + 2 * i - h - j) % 7;
        l = (a + 11 * h + 22 * k) / 451;
        x = h + k - 7 * l + 114;
        mon = x / 31;
        tag = (x % 31) + 1;

        StringBuilder oSo = new StringBuilder();
        oSo.append(tag);
        oSo.append(".");
        oSo.append(mon);

        StringBuilder oDay = new StringBuilder();
        oDay.append(jahr);
        oDay.append("-");
        oDay.append("0");
        oDay.append(mon);
        oDay.append("-");
        if(tag<10) {
            oDay.append("0");
            oDay.append(tag);
        }
        else {
            oDay.append(tag);
        }

        oDay.append(" 00:00:00.000");

        Timestamp timestamp = Timestamp.valueOf(oDay.toString());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.");

        StringBuilder oPattern = new StringBuilder();

        oPattern.append(simpleDateFormat.format(timestamp.getTime()
                - (3 * 86400000)));
        oPattern.append("|");
        oPattern.append(simpleDateFormat.format(timestamp.getTime()
                - (2 * 86400000)));
        oPattern.append("|");
        oPattern.append(simpleDateFormat.format(timestamp.getTime() - 86400000));
        oPattern.append("|");
        oPattern.append(simpleDateFormat.format(timestamp.getTime()));
        oPattern.append("|");
        oPattern.append(simpleDateFormat.format(timestamp.getTime() + 86400000));

        editor = settings.edit();
        editor.putString("patternEastern",oPattern.toString());
        editor.apply();
    }
}