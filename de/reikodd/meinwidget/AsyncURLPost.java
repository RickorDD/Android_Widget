package de.reikodd.meinwidget;

import android.os.AsyncTask;

import java.io.OutputStream;
import javax.net.ssl.HttpsURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class AsyncURLPost extends AsyncTask<String, String, String> {

    @Override
    protected String doInBackground(String... urls) {

        try {
            URL url = new URL(urls[0]);
            String input = urls[1];
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("connection", "close");
            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();
            int resp = conn.getResponseCode();

            conn.disconnect();

            return null;

        } catch (MalformedURLException rat) {
            rat.printStackTrace();
            return null;
        } catch (Exception rat) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {

    }
}