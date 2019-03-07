package de.reikodd.meinwidget;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

public class AsyncURLGet extends AsyncTask<String, String, String> {

    private AsyncInterface urlInterface;

    public AsyncURLGet(AsyncInterface activityContext) {
        this.urlInterface = activityContext;
    }



    @Override
    protected String doInBackground(String... urls) {
        String contents = "";
        String line;

        try {
            URL url = new URL(urls[0]);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestProperty("connection", "close");

            InputStream inputStream = connection.getInputStream();

            BufferedReader buffer = new BufferedReader(new InputStreamReader(
                    inputStream));

            while ((line = buffer.readLine()) != null) {
                contents += line;
            }

            return contents;

        } catch (MalformedURLException rat) {
            rat.printStackTrace();
            return " ";
        } catch (Exception rat) {
            rat.printStackTrace();
            return " ";
        }
    }

    @Override
    protected void onPostExecute(String contents) {
        urlInterface.receivedContent(contents);
    }
}