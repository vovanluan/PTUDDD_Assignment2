package fragment;

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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.yuyo.hikaru.activity.CourseActivity;
import com.yuyo.hikaru.activity.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;

import entity.Course;
import entity.DataHolder;
import entity.Review;
import support.Support;
// ...

public class ReviewDialogFragment extends DialogFragment {

    private EditText title;
    private EditText description;
    private TextView submit, rating;
    private RatingBar ratingBar;
    private Review review;
    private static final ReviewDialogFragment frag = new ReviewDialogFragment();
    public Course course;

    private static final String[] SATISFACTION = {"Bad", "Not bad", "Good", "Great", "Excellent"};
    public ReviewDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
        setStyle(STYLE_NO_TITLE, R.style.Theme_AppCompat_Light_Dialog);
    }

    public static ReviewDialogFragment getInstance() {
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.rating_dialog, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        // Fetch arguments from bundle and set title
        getDialog().setTitle("Send feed back for YuYo");

        title = (EditText) view.findViewById(R.id.title);
        description = (EditText) view.findViewById(R.id.description);
        submit = (TextView) view.findViewById(R.id.submit);
        rating = (TextView) view.findViewById(R.id.ratingText);
        ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
        // Show soft keyboard automatically and request focus to field
        title.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validation
                if(title.getText().toString().isEmpty()) {
                    showError(title);
                    return;
                }
                if(description.getText().toString().isEmpty()) {
                    showError(description);
                    return;
                }

                review = new Review();
                review.setTitle(title.getText().toString());
                review.setDescription(description.getText().toString());
                review.setCreated_by(DataHolder.getInstance().getUser().get_id());
                review.setFor_card(course.get_id());
                review.setRating(((int) ratingBar.getRating()));

                String sendReviewURL = Support.HOST + "users/review";
                new FeedbackRequest().execute(sendReviewURL);
            }
        });

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener(){
            @Override
            public void onRatingChanged(RatingBar ratingBar, float ratingNumber,
                                        boolean fromUser) {
                String satisfaction = "";
                switch ((int) ratingNumber){
                    case 1:
                        satisfaction = SATISFACTION[0];
                        break;
                    case 2:
                        satisfaction = SATISFACTION[1];
                        break;
                    case 3:
                        satisfaction = SATISFACTION[2];
                        break;
                    case 4:
                        satisfaction = SATISFACTION[3];
                        break;
                    case 5:
                        satisfaction = SATISFACTION[4];
                        break;
                    default:
                        satisfaction = SATISFACTION[1];
                        break;
                }
                rating.setText(satisfaction);
                ratingBar.setRating(ratingNumber);
            }});
    }

    private void showError(EditText editText) {
        Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
        editText.startAnimation(shake);
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

                Gson gson = new Gson();
                Type type = new TypeToken<Review>(){}.getType();
                String json = gson.toJson(review, type);
                Log.e("Review", json);
                OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());
                wr.write(json);
                wr.flush();
                wr.close();

                InputStream isResponse = urlConnection.getInputStream();
                BufferedReader responseBuffer = new BufferedReader(new InputStreamReader(isResponse));
                String line = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ((line = responseBuffer.readLine()) != null) {
                    stringBuilder.append(line);
                }
                jsonResponse = stringBuilder.toString();
                Log.e("Update course", jsonResponse);
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
                    Gson gson = new Gson();
                    Type type = new TypeToken<Course>() {
                    }.getType();
                    course = gson.fromJson(jsonResponse, type);
                    //((CourseActivity)getActivity()).star.setText(String.valueOf(course.getRating()));
                    Toast.makeText(getActivity(), "Thanks for your review!", Toast.LENGTH_LONG).show();

                    DataHolder.getInstance().removeCourse(course.get_id());
                    //Update new course
                    DataHolder.getInstance().getCourseList().add(course);

                    //Broadcast update card list
                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction("UPDATE_CARD_LIST");
                    getActivity().sendBroadcast(broadcastIntent);
                    //TODO: Update review in course activity
                }
                else {
                    Toast.makeText(getActivity(), "Sending review failed", Toast.LENGTH_LONG).show();
                }
                ReviewDialogFragment.getInstance().title.setText("");
                ReviewDialogFragment.getInstance().description.setText("");
                ReviewDialogFragment.getInstance().title.requestFocus();
                ReviewDialogFragment.getInstance().dismiss();
                ratingBar.setRating(2);
                rating.setText(SATISFACTION[1]);

            } catch (Exception e) {

            }
        }
    }
}