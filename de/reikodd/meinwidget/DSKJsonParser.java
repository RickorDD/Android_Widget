package de.reikodd.meinwidget;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DSKJsonParser {

    public static int getWindSpeed(String content) {
        int intWindSpeed=0;

        try {

            JSONObject jsonObject = new JSONObject(content);
            JSONObject currently = jsonObject.getJSONObject("currently");
            double windspeed = currently.getDouble("windSpeed");
            Long longWindSpeed = Math.round(windspeed);
            intWindSpeed = longWindSpeed.intValue();

        } catch (JSONException e) {
        }
        return intWindSpeed;
    }

    public static int getcloudCover(String content) {
        int intcloudCover=0;

        try {

            JSONObject jsonObject = new JSONObject(content);
            JSONObject currently = jsonObject.getJSONObject("currently");
            double cloudCover = currently.getDouble("cloudCover")*100; //
            Long longcloudCover = Math.round(cloudCover);
            intcloudCover = longcloudCover.intValue();

        } catch (JSONException e) {
        }
        return intcloudCover;
    }

    public static String getCloud(String content) {

        String cloud = "";

        try {

            JSONObject jsonObject = new JSONObject(content);
            JSONObject currently = jsonObject.getJSONObject("currently");
            cloud = currently.getString("summary");


        } catch (JSONException e) {}

        return cloud;
    }

    public static String getIcon(String content) {

        String icon = "";

        try {

            JSONObject jsonObject = new JSONObject(content);
            JSONObject currently = jsonObject.getJSONObject("currently");
            icon = currently.getString("icon");


        } catch (JSONException e) {}

        return icon;
    }


    public static int getIntTemp(String content) {

        int intTemp=0;

        try {

            JSONObject jsonObject = new JSONObject(content);
            JSONObject currently = jsonObject.getJSONObject("currently");
            double temperature = currently.getDouble("temperature");
            Long longTemp = Math.round(temperature);
            intTemp = longTemp.intValue();


        } catch (JSONException e) {
        }
        return intTemp;
    }

    public static String getTempTextView(String content) {

        String textview="";

        try {

            JSONObject jsonObject = new JSONObject(content);
            JSONObject currently = jsonObject.getJSONObject("currently");
            double temperature = currently.getDouble("temperature");
            Long longTemp = Math.round(temperature);
            int intTempTxt = longTemp.intValue();
            textview = intTempTxt + "°";

        } catch (JSONException e) {
            textview = "---°";
        }

        return textview;
    }

    public static long getSunriseTimestamp(String content) {

        long sunriseTimestamp = 0;

        try {

            JSONObject jsonObject = new JSONObject(content);
            JSONObject json_daily = jsonObject.getJSONObject("daily");
            JSONArray json_data = json_daily.getJSONArray("data");
            JSONObject json_sunriseTime = json_data.getJSONObject(0);
            sunriseTimestamp = json_sunriseTime.getLong("sunriseTime")*1000;

        } catch (JSONException e) {
        }

        return sunriseTimestamp;
    }

    public static long getSunsetTimestamp(String content) {
        long sunsetTimestamp = 0;

        try {

            JSONObject jsonObject = new JSONObject(content);
            JSONObject json_daily = jsonObject.getJSONObject("daily");
            JSONArray json_data = json_daily.getJSONArray("data");
            JSONObject json_sunriseTime = json_data.getJSONObject(0);
            sunsetTimestamp = json_sunriseTime.getLong("sunsetTime")*1000;


        } catch (JSONException e) {
        }

        return sunsetTimestamp;
    }

}