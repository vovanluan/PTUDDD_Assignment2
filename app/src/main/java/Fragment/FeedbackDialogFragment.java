package Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.luan.activity.HomeActivity;
import com.example.luan.activity.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;

import entity.DataHolder;
import entity.Feedback;
import entity.Local;
import entity.User;
import support.Support;
// ...

public class FeedbackDialogFragment extends DialogFragment {

    public EditText title;
    public EditText description;
    public TextView email;
    public TextView submit;
    public Feedback feedBack;
    private static final FeedbackDialogFragment frag = new FeedbackDialogFragment();
    public FeedbackDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static FeedbackDialogFragment getInstance() {
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.feedback, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        // Fetch arguments from bundle and set title
        //String title = getArguments().getString("title", "Enter Name");
        //getDialog().setTitle(title);

        title = (EditText) view.findViewById(R.id.title);
        description = (EditText) view.findViewById(R.id.description);
        email = (TextView) view.findViewById(R.id.email);
        email.setText(DataHolder.getInstance().getData().getLocal().getEmail());
        submit = (TextView) view.findViewById(R.id.submit);

        // Show soft keyboard automatically and request focus to field
        title.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedBack = new Feedback();
                feedBack.setTitle(title.getText().toString());
                feedBack.setDescription(description.getText().toString());
                feedBack.setCreated_by(DataHolder.getInstance().getData().get_id());
                String sendFeedBackURL = Support.HOST + "feedbacks";
                new FeedbackRequest().execute(sendFeedBackURL);
            }
        });
    }

    private class FeedbackRequest extends AsyncTask<String, Void, Integer> {
        private String jsonResponse;
        private final ProgressDialog dialog = new ProgressDialog(getActivity());
        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Sending...");
            this.dialog.setCancelable(false);
            this.dialog.show();
        }

        @Override
        protected Integer doInBackground(String... urls) {
            try {
                // Step 1 : Create a HttpURLConnection object send REQUEST to server
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.setRequestProperty("Accept-Charset", "UTF-8");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestMethod("POST");
                urlConnection.connect();

                // Convert this object to json string using gson
                Gson gson = new Gson();
                Type type = new TypeToken<Feedback>(){}.getType();
                String json = gson.toJson(feedBack, type);

                OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());
                wr.write(json);
                wr.flush();
                wr.close();

                return urlConnection.getResponseCode();

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("error", e.toString());
            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer responseCode) {
            try {
                if (this.dialog.isShowing()) {
                    this.dialog.dismiss();
                }
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Toast.makeText(getActivity(), "Awesome!!! We really appreciate your feed back!", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getActivity(), "Sending feedback failed", Toast.LENGTH_LONG).show();
                }
                FeedbackDialogFragment.getInstance().title.setText("");
                FeedbackDialogFragment.getInstance().description.setText("");
                FeedbackDialogFragment.getInstance().title.requestFocus();
                FeedbackDialogFragment.getInstance().dismiss();
            } catch (Exception e) {

            }
        }
    }
}