package de.reikodd.meinwidget;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.RemoteViews;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;
import java.util.TimeZone;


public class MainActivity extends AppWidgetProvider {


    static private Context ctx;


    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Intent intent = new Intent(UpdateTimeService.UPDATE);
        intent.setPackage("de.reikodd.meinwidget");

        context.startForegroundService(intent);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        context.stopService(new Intent(context, UpdateTimeService.class));
    }

    public static final class UpdateTimeService extends Service implements AsyncInterface {

        static boolean LogState;
        static String textTempString = "---°";
        static String weatherCondition = "NV";
        static String strDSKIcon = "clear-day";
        static String weatherIcon = "dquestion";
        static String moonIcon = "nclear";
        static String AWSURL = BuildConfig.AWS_URL;
        static boolean stateDay = true;
        static int LogCount = 0;
        static long tsSunrise = 0;
        static long tsSunset = 0;
        static long tsUpdateConditions = 0;
        static long tsStartWidget = 0;
        static int intTemp = 0;
        static int intWind = 0;
        static int intCloudCover=0;
        static int minuteLastWeatherUpdate = 0;
        static final String UPDATE = "de.reikodd.meinwidget.action.UPDATE";
        static boolean STARTWIDGET = false;
        static int timeOffset = 0;
        static int timeActAllMinute = 0;

        final String KeyValueTimeOffset = "10";
        final String KeyValueActHour = "3";

        static String KeyValueWeatherPlace = "51.05,13.73";

        static int LogCountValue=30;


        static String URLWeather ="https://api.darksky.net/forecast/"
                + BuildConfig.API_KEY
                + "/"
                + KeyValueWeatherPlace
                + "?exclude=alerts,flags,minutely,hourly&units=si";

        final int minuteRefreshWeatherAtLock = 30; //Refresh wenn Wetterupdate x min her

        SharedPreferences settings;
        SharedPreferences.Editor editor;

        SimpleDateFormat sdftime = new SimpleDateFormat("HH:mm");
        SimpleDateFormat sdfdate = new SimpleDateFormat("EEEE, dd. MMMM");
        SimpleDateFormat sdfdateact = new SimpleDateFormat("dd.MM.");

        private final static IntentFilter mIntentFilter = new IntentFilter();

        //Intent für Minutentakt, Änderung Uhrzeit und Entsperren
        static {
            mIntentFilter.addAction(Intent.ACTION_TIME_TICK);
            mIntentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
            mIntentFilter.addAction(Intent.ACTION_USER_PRESENT);
            mIntentFilter.addAction("de.reikodd.meinwidget.TimeDiffAct");
            mIntentFilter.addAction("de.reikodd.meinwidget.WeatherAct");
            mIntentFilter.addAction("de.reikodd.meinwidget.SyncBirthday");
        }

        /**
         * Interface Rückgabefunktion aus URLConnection
         */
        @Override
        public void receivedContent(String content) {

            Calendar c = Calendar.getInstance();

            if (content.contains("currently")) {
                intTemp = DSKJsonParser.getIntTemp(content);
                intWind = DSKJsonParser.getWindSpeed(content);
                intCloudCover = DSKJsonParser.getcloudCover(content);
                textTempString = DSKJsonParser.getTempTextView(content);
                weatherCondition = DSKJsonParser.getCloud(content);
                strDSKIcon = DSKJsonParser.getIcon(content);
                tsUpdateConditions = c.getTimeInMillis();
                tsSunrise = DSKJsonParser.getSunriseTimestamp(content);
                tsSunset = DSKJsonParser.getSunsetTimestamp(content);

                weatherIcon = DSKConditionToPNG.getPNGFileOfDate(ctx, DSKConditionToPNG.getPNGFileString
                        (strDSKIcon, intTemp, intWind, intCloudCover), c.getTimeInMillis());
                moonIcon = DSKConditionToPNG.getPNGFileOfDate(ctx,"nclear", c.getTimeInMillis());

                int intStateDay = stateDay ? 1:0;

                if(LogState==true) {


                    StringBuilder AWSLogString1 = new StringBuilder();
                    AWSLogString1.append("{EMU,");
                    AWSLogString1.append(intTemp);
                    AWSLogString1.append("C,");
                    AWSLogString1.append(intWind);
                    AWSLogString1.append("m/s,");
                    AWSLogString1.append(intCloudCover);
                    AWSLogString1.append("%,");
                    AWSLogString1.append(weatherIcon);
                    AWSLogString1.append(",");
                    AWSLogString1.append(sdftime.format(tsSunrise));
                    AWSLogString1.append("-");
                    AWSLogString1.append(sdftime.format(tsSunset));
                    AWSLogString1.append(",");
                    AWSLogString1.append(intStateDay);
                    AWSLogString1.append("//");
                    AWSLogString1.append(sdfdateact.format(tsStartWidget));
                    AWSLogString1.append(sdftime.format(tsStartWidget));
                    AWSLogString1.append(",");
                    AWSLogString1.append(weatherCondition);
                    AWSLogString1.append(",");
                    AWSLogString1.append(strDSKIcon);
                    AWSLogString1.append(",");
                    AWSLogString1.append(moonIcon);
                    AWSLogString1.append(",");
                    AWSLogString1.append(tsSunrise);
                    AWSLogString1.append(",");
                    AWSLogString1.append(tsSunset);
                    AWSLogString1.append("}");


                    AsyncURLPost task = new AsyncURLPost();

                    if (task.getStatus() != AsyncTask.Status.RUNNING) {
                        new AsyncURLPost().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                                AWSURL, AWSLogString1.toString());
                    }
                }
                LogCount++;
            }
        }


        @Override
        public void onCreate() {
            super.onCreate();
            ctx=getBaseContext();
            ctx.registerReceiver(mTimeChangedReceiver, mIntentFilter);


            //Service wird zum Foreground Services mit Notification

            String NOTIFICATION_CHANNEL_ID = "de.reikodd.meinwidget";
            String channelName = "Background Service";
            NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);

            Notification.Builder notificationBuilder = new Notification.Builder(this, NOTIFICATION_CHANNEL_ID);
            Notification notification = notificationBuilder.setOngoing(true)
                    .setSmallIcon(R.drawable.ic_action_time)
                    .setContentTitle("Widget running in Background")
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .build();

            Random rn = new Random();
            int nid = rn.nextInt(9999 - 3333 + 1) + 3333;

            startForeground(nid, notification);

            WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
            final DisplayMetrics displayMetrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(displayMetrics);
            sdftime.setTimeZone(TimeZone.getDefault());
            sdfdate.setTimeZone(TimeZone.getDefault());
            sdfdateact.setTimeZone(TimeZone.getDefault());

            /*
            String TAG = "Reiko";
            Log.i(TAG, "CPU_ABI : " + Build.CPU_ABI);
            Log.i(TAG, "CPU_ABI2 : " + Build.CPU_ABI2);
            Log.i(TAG, "OS.ARCH : " + System.getProperty("os.arch"));

            Log.i(TAG, "SUPPORTED_ABIS : " + Arrays.toString(Build.SUPPORTED_ABIS));
            Log.i(TAG, "SUPPORTED_32_BIT_ABIS : " + Arrays.toString(Build.SUPPORTED_32_BIT_ABIS));
            Log.i(TAG, "SUPPORTED_64_BIT_ABIS : " + Arrays.toString(Build.SUPPORTED_64_BIT_ABIS));


            float height = displayMetrics.heightPixels;
            float width = displayMetrics.widthPixels;
            float densityDpi = displayMetrics.densityDpi;
            float wdp = (width/densityDpi)*160;
            float hdp = (height/densityDpi)*160;
            Log.i(TAG,""+ height+"px"+ " x " +width+"px "+ densityDpi + "dpi " + hdp + "dp x " + wdp + "dp" );
            Log.i(TAG,""+getResources().getConfiguration().fontScale);
            */

        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            unregisterReceiver(mTimeChangedReceiver);
            stopForeground(true);
        }


        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            Calendar c = Calendar.getInstance();
            tsStartWidget = c.getTimeInMillis();
            if (STARTWIDGET == false) {
                STARTWIDGET = true;

                BirthAdressbook.getBirthPattern(getApplicationContext());
                DateEastern.getEasterDate(getApplicationContext(),c.getTimeInMillis());

            }


            return START_STICKY; //Restart wenn Kill Process
        }

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        private final BroadcastReceiver mTimeChangedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
                    updateView();
                }

                if (intent.getAction().equals(Intent.ACTION_TIMEZONE_CHANGED)) {
                    sdftime.setTimeZone(TimeZone.getDefault());
                    sdfdate.setTimeZone(TimeZone.getDefault());
                    sdfdateact.setTimeZone(TimeZone.getDefault());
                    updateView();
                }

                //Wenn entsperrt und Wetter vor x min aktualisiert dann Wetterupdate
                if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
                    if (minuteLastWeatherUpdate > minuteRefreshWeatherAtLock) {
                        updateWeather();
                    }

                    new Handler().postDelayed(() -> updateView(), 1500);
                }

                if (intent.getAction().equals("de.reikodd.meinwidget.TimeDiffAct")) {
                    updateView();
                }

                if (intent.getAction().equals("de.reikodd.meinwidget.WeatherAct")) {
                    updateView();
                    updateWeather();

                    new Handler().postDelayed(() -> updateView(), 1500);
                }

                if (intent.getAction().equals("de.reikodd.meinwidget.SyncBirthday"))
                {
                    BirthAdressbook.getBirthPattern(getApplicationContext());
                }
            }
        };


        /**
         * updatet den Textview
         *
         */
        private void updateView() {

            settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            updateSharedPreference(settings);

            Calendar c = Calendar.getInstance();
            Intent intent = new Intent(this, SettingActivity.class);
            PendingIntent pending = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            RemoteViews views = new RemoteViews(getPackageName(), R.layout.activity_main);
            views.setOnClickPendingIntent(R.id.textclock, pending);

            long timeDiff = c.getTimeInMillis() + timeOffset * 60 * 1000;

            views.setTextViewText(R.id.textclock, sdftime.format(timeDiff));
            views.setTextViewText(R.id.textdate, sdfdate.format(timeDiff).replace("Samstag", "Sonnabend"));
            views.setTextViewText(R.id.texttemp, textTempString);

            //views.setFloat(R.id.textclock,"setTextSize",0);

            weatherIcon = DSKConditionToPNG.getPNGFileOfDate(ctx, DSKConditionToPNG.getPNGFileString
                    (strDSKIcon, intTemp, intWind, intCloudCover), c.getTimeInMillis());

            moonIcon = DSKConditionToPNG.getPNGFileOfDate(ctx,"nclear", c.getTimeInMillis());

            //Tag und Nachtanzeige
            if (c.getTimeInMillis() >= tsSunrise && c.getTimeInMillis() <= tsSunset) {
                views.setImageViewResource
                        (R.id.imageview, getResources().getIdentifier
                                (weatherIcon, "drawable", getPackageName()));
                stateDay = true;
            } else {
                views.setImageViewResource
                        (R.id.imageview, getResources().getIdentifier
                                (moonIcon, "drawable", getPackageName()));

                stateDay = false;
            }

            minuteLastWeatherUpdate = (int) (long) ((c.getTimeInMillis() - tsUpdateConditions) / 1000 / 60);
            if (minuteLastWeatherUpdate >= timeActAllMinute || minuteLastWeatherUpdate < 0) {

                updateWeather();

            }

            postSettings();

            ComponentName widget = new ComponentName(this, MainActivity.class);
            AppWidgetManager manager = AppWidgetManager.getInstance(this);
            manager.updateAppWidget(widget, views);

        }


        /**
         * ruft die SharedPreferences auf und setzt die Werte
         *
         * @param settings Übergabeparameter aus updateView()
         */
        private void updateSharedPreference(SharedPreferences settings) {

            URLWeather ="https://api.darksky.net/forecast/"
                    + BuildConfig.API_KEY
                    + "/"
                    + settings.getString("WeatherPlace", KeyValueWeatherPlace)
                    + "?exclude=alerts,flags,minutely,hourly&units=si";

            editor = settings.edit();
            editor.putString("WeatherActString", sdfdateact.format(tsUpdateConditions) + "  " +
                    sdftime.format(tsUpdateConditions) + "   Co: " + weatherCondition);
            editor.putString("SunUpdateStart", sdfdateact.format(tsStartWidget));
            editor.apply();

            timeOffset = Integer.parseInt(settings.getString("TimeOffset", KeyValueTimeOffset));
            timeActAllMinute = Integer.parseInt(settings.getString("WeatherTimeAct", KeyValueActHour))*60;

            if (getResources().getString(R.string.strLogState).equals("false")) {
                LogState = settings.getBoolean("LogState", false);
            }

            if (getResources().getString(R.string.strLogState).equals("true")) {
                LogState = settings.getBoolean("LogState", true);
            }
        }


        private void updateWeather() {

            AsyncURLGet task;
            task = new AsyncURLGet(new UpdateTimeService());

            if (task.getStatus() != AsyncTask.Status.RUNNING  && isOnline() == true)

            {
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,URLWeather);
            }

        }




        private boolean isOnline() {
            ConnectivityManager cm =
                    (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();
        }


        private void postSettings() {

                if (isOnline()==true) {
                    if (LogState==true && LogCount>=LogCountValue) {

                        StringBuilder AWSLogString2 = new StringBuilder();
                        AWSLogString2.append("EMU,");
                        AWSLogString2.append(settings.getString("WeatherPlace", "--"));
                        AWSLogString2.append(",");
                        AWSLogString2.append(Build.VERSION.SDK_INT);
                        AWSLogString2.append(",");
                        AWSLogString2.append(settings.getString("patternBirthday", "--"));
                        AWSLogString2.append("-,");
                        AWSLogString2.append(settings.getString("patternEastern", "--"));

                        new AsyncURLPost().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                                AWSURL,AWSLogString2.toString());
                        LogCount=0;
                    }
                }
        }

    }
}