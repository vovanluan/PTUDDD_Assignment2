package services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.IBinder;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yuyo.hikaru.activity.HomeActivity;
import com.yuyo.hikaru.activity.R;
import com.yuyo.hikaru.activity.RegisterActivity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import entity.DataHolder;
import entity.Notification;
import support.Support;

/**
 * Created by Bach Do on 7/28/2016.
 */
public class NotificationService extends Service {

    boolean firstRequest = false;
    int unreadNoti = 0;
    int INTERVAL = 5 * 1000;
    Timer timer;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.


        // test1@gmail.com id: 5736a4a0a2f2e811007d1ad3
        // nguyenbach2810@gmail.com id: 572ec9cb464db7041be753f0
        final String URL = Support.HOST + "users/" +
                DataHolder.getInstance().getUser().get_id() +
                "/notification";
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                // Get notification list every 5s
                new NotificationRequest().execute(URL);
            }

        }, 0, INTERVAL);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        timer.cancel();
        super.onDestroy();
    }

    private void createNotification() {
        int notiId = 10285;
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.putExtra("selectTab", 2);
        PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

        Bitmap iconBitmap = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.yuyo_logo);

        android.app.Notification noti = new android.app.Notification.Builder(getApplicationContext())
                .setContentTitle("New partner found")
                .setContentText("Someone joint your course, check it out!!")
                .setSmallIcon(R.drawable.ic_favorite_black)
                .setLargeIcon(iconBitmap)
                .setContentIntent(pIntent)
                .setVibrate(new long[]{500, 500})
                .setAutoCancel(true).build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notiId, noti);

    }

    private class NotificationRequest extends AsyncTask<String, Void, Integer> {

        private String jsonResponse;

        @Override
        protected Integer doInBackground(String... urls) {
            try {
                // Create connection
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoInput(true);
                urlConnection.setRequestProperty("Accept-Charset", "UTF-8");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.connect();

                InputStream isResponse = urlConnection.getInputStream();
                BufferedReader responseBuffer = new BufferedReader(new InputStreamReader(isResponse));

                String line = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ((line = responseBuffer.readLine()) != null) {
                    stringBuilder.append(line);
                }
                jsonResponse = stringBuilder.toString();
                return urlConnection.getResponseCode();

            } catch (Exception e) {

            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer responseCode) {
            try {
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Gson gson = new Gson();
                    Type type = new TypeToken<ArrayList<Notification>>() {
                    }.getType();
                    ArrayList<Notification> notificationList = gson.fromJson(jsonResponse, type);
                    //Toast.makeText(getApplicationContext(), "Notification size: " + notificationList.size(), Toast.LENGTH_SHORT).show();
                    if (!firstRequest) {
                        firstRequest = true;
                        unreadNoti = notificationList.size();
                        if (unreadNoti > 0) {
                            createNotification();
                        }
                    } else {
                        if (notificationList.size() > unreadNoti) {
                            createNotification();
                        }
                        unreadNoti = notificationList.size();
                    }
                }
            } catch (Exception e) {

            }
        }
    }
}
