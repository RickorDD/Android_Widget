package de.reikodd.meinwidget;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class _OWMJsonParser {


    public static int getWindSpeed(String content) {
        int intWindSpeed=0;

        try {
            JSONObject jsonObject = new JSONObject(content);
            JSONObject location = jsonObject
                    .getJSONObject("wind");
            double dbWind = location.getDouble("speed");
            final Long longWind = Math.round(dbWind);
            intWindSpeed = longWind.intValue();


        } catch (JSONException e) {
        }
        return intWindSpeed;
    }


    public static String getCloud(String content) {

        String cloud = "";

        try {
            JSONObject jsonObject = new JSONObject(content);
            JSONArray location = jsonObject.getJSONArray("weather");

            for (int i = 0; i < location.length(); i++) {
                JSONObject jb = location.getJSONObject(i);
                cloud = jb.getString("description");
            }
        } catch (JSONException e) {
        }
        return cloud;
    }

    public static int getIntTemp(String content) {

        int intTemp=0;
        double Kelvin = 273.15;

        try {
            JSONObject jsonObject = new JSONObject(content);
            JSONObject location = jsonObject
                    .getJSONObject("main");
            double dbTemp = location.getDouble("temp")-Kelvin;

            final Long longTemp = Math.round(dbTemp);
            intTemp = longTemp.intValue();



        } catch (JSONException e) {
        }
        return intTemp;
    }

    public static String getTempTextView(String content) {

        String textview="";
        int intTempTxt=0;
        double Kelvin = 273.15;

        try {
            JSONObject jsonObject = new JSONObject(content);
            JSONObject location = jsonObject
                    .getJSONObject("main");
            double dbTemp = location.getDouble("temp")-Kelvin;

            final Long longTemp = Math.round(dbTemp);
            intTempTxt = longTemp.intValue();

            if (intTempTxt <= 9 && intTempTxt >= 0) {
                textview = "  " + intTempTxt + "°";
            } else if (intTempTxt >= -9 && intTempTxt <= -1) {
                textview = " " + intTempTxt + "°";
            } else if (intTempTxt >= 10) {
                textview = " " + intTempTxt + "°";
            } else if (intTempTxt <= -10) {
                textview = intTempTxt + "°";
            }
        } catch (JSONException e) {
            textview = "---";
        }

        return textview;
    }

    public static long getSunriseTimestamp(String content) {
        long sunriseTimestamp = 0;

        try {
            JSONObject jsonObject = new JSONObject(content);
            JSONObject sunPhase = jsonObject.getJSONObject("sys");
            sunriseTimestamp = sunPhase.getInt("sunrise");

        } catch (JSONException e) {
        }

        return sunriseTimestamp*1000;
    }

    public static long getSunsetTimestamp(String content) {
        long sunsetTimestamp = 0;

        try {
            JSONObject jsonObject = new JSONObject(content);
            JSONObject sunPhase = jsonObject.getJSONObject("sys");
            sunsetTimestamp = sunPhase.getInt("sunset");


        } catch (JSONException e) {
        }

        return sunsetTimestamp*1000;
    }

}