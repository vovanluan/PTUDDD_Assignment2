package Fragment;

/**
 * Created by Luan on 5/2/2016.
 */
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.Toast;

import com.example.luan.activity.HomeActivity;
import com.example.luan.activity.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import adapter.CardAdapter;
import entity.Card;
import support.Support;

public class CardFragment extends Fragment implements AdapterView.OnItemClickListener, SearchView.OnQueryTextListener{
    private ArrayList<Card> cardList;
    private CardAdapter adapter;
    private GridView gridView;
    private View view;
    public CardFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initialize card list
        cardList = new ArrayList<>();
        String getCardListURL = Support.HOST +"mobile/cards";
        new GetCardRequest().execute(getCardListURL);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        HomeActivity activity = (HomeActivity) getActivity();

        view = inflater.inflate(R.layout.card_fragment, container, false);

        // initialize adapter
        adapter = new CardAdapter(getActivity());

        // initialize gridview
        gridView = (GridView) view.findViewById(R.id.gridview);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.action_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView =
                (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Clicked", "");
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Show all cards after collapsing
                adapter.setListCard(cardList);
                return true;  // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;  // Return true to expand action view
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    // Hande event submit query on search
    @Override
    public boolean onQueryTextSubmit(String query) {
        // Handle search card
        ArrayList<Card> resultCards = new ArrayList<>();
        for (Card card : cardList) {
            // search by title, creator or description
            if(card.getTitle().contains(query) || card.getCreate_by().getBio().getFirstName().contains(query)
                    || card.getDescription().contains(query)) {
                resultCards.add(card);
            }
        }
        if(resultCards.isEmpty()) {
            Toast.makeText(getActivity(),"Can't find this course", Toast.LENGTH_LONG).show();
        }
        else {
            adapter.setListCard(resultCards);
        }
        return true;
    }


    // Handle event query change on search
    @Override
    public boolean onQueryTextChange(String newText) {
        ArrayList<Card> resultCards = new ArrayList<>();
        for (Card card : cardList) {
            // search by title, creator or description
            if(card.getTitle().contains(newText) || card.getCreate_by().getBio().getFirstName().contains(newText)
                    || card.getDescription().contains(newText)) {
                resultCards.add(card);
            }
        }
        adapter.setListCard(resultCards);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()){
            case R.id.action_settings:
                // handle click Setting event
                Toast.makeText(getActivity(),"Action settings", Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_search:
                // handle click Search button
                Toast.makeText(getActivity(),"Action search", Toast.LENGTH_LONG).show();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                Toast.makeText(getActivity(),"Something else", Toast.LENGTH_LONG).show();
                return super.onOptionsItemSelected(item);
        }
    }

    // Handle click an card in fragment
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        // retrieve the GridView item
        Card card = cardList.get(position);

        // do something
        Toast.makeText(getActivity(), card.getTitle(), Toast.LENGTH_SHORT).show();
    }
    private class GetCardRequest extends AsyncTask<String, Void, Integer> {

        private String jsonResponse;
        private final ProgressDialog dialog = new ProgressDialog(getActivity());
        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Load cards...");
            this.dialog.setCancelable(false);
            this.dialog.show();
        }

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

                // Step 2: wait for incoming RESPONSE stream, place data in a buffer
                InputStream isResponse = urlConnection.getInputStream();
                BufferedReader responseBuffer = new BufferedReader(new InputStreamReader(isResponse));

                // Step 3: Arriving JSON fragments are concatenate into a StringBuilder
                String line = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ((line = responseBuffer.readLine()) != null){
                    stringBuilder.append(line);
                }
                jsonResponse = stringBuilder.toString();
                Log.e("Json", jsonResponse);
                Log.e("message", urlConnection.getResponseMessage());
                return urlConnection.getResponseCode();

            }
            catch (Exception e) {

            }
            return 0;
        }
        @Override
        protected void onPostExecute(Integer responseCode) {
            try {
                if (this.dialog.isShowing()) {
                    this.dialog.dismiss();
                }
                Log.e("CODE", String.valueOf(responseCode));
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Step 4 : Convert JSON string to User object
                    Gson gson = new Gson();
                    Type type = new TypeToken<ArrayList<Card>>(){}.getType();
                    cardList = gson.fromJson(jsonResponse, type);
                    Log.e("Size", String.valueOf(cardList.size()));
                    Log.e("Card list:", jsonResponse);
                }
                else {
                    Toast.makeText(getActivity(), "Error while getting card list", Toast.LENGTH_LONG).show();
                }
                adapter.setListCard(cardList);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
