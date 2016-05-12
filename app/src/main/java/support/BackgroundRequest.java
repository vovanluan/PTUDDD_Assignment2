package support;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import entity.Course;
import entity.DataHolder;
import entity.User;

/**
 * Created by Admin on 5/12/2016.
 */
public class BackgroundRequest {
    private Context context;
    public BackgroundRequest(Context context){
        this.context = context;
    }
    private class GetCardListRequest extends AsyncTask<String, Void, Integer> {

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
                Log.e("CourseList", jsonResponse);
                Log.e("message", urlConnection.getResponseMessage());
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
                    Type type = new TypeToken<ArrayList<Course>>() {
                    }.getType();
                    DataHolder.getInstance().setCourseList((ArrayList<Course>) gson.fromJson(jsonResponse, type));
                    Log.e("Size course", String.valueOf(DataHolder.getInstance().getCourseList().size()));
                }
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction("GOT_CARD_LIST");
                BackgroundRequest.this.context.sendBroadcast(broadcastIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void getCardListRequest() {
        new GetCardListRequest().execute(Support.HOST + "cards");
    }

    private class GetUserListRequest extends AsyncTask<String, Void, Integer> {

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
                Log.e("Json User", jsonResponse);
                Log.e("message USer", urlConnection.getResponseMessage());
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
                    Type type = new TypeToken<ArrayList<User>>() {
                    }.getType();
                    DataHolder.getInstance().setUserList((ArrayList<User>) gson.fromJson(jsonResponse, type));
                    Log.e("Size User", String.valueOf(DataHolder.getInstance().getUserList().size()));
                }
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction("GOT_USER_LIST");
                BackgroundRequest.this.context.sendBroadcast(broadcastIntent);
            } catch (Exception e) {

            }
        }
    }

    public void getUserListRequest() {
        new GetUserListRequest().execute(Support.HOST + "users");
    }
}
