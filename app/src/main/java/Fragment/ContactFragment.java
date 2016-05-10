package fragment;

/**
 * Created by Luan on 5/10/2016.
 */
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.luan.activity.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import entity.User;

public class ContactFragment extends Fragment{
    private User user;
    private TextView email;
    public ContactFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        String jsonUser = getArguments().getString("User");
        Gson gson = new Gson();
        Type type = new TypeToken<User>() {
        }.getType();
        user = gson.fromJson(jsonUser, type);
        View view = inflater.inflate(R.layout.contact_fragment, container, false);
        email = (TextView) view.findViewById(R.id.email);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        email.setText(user.getLocal().getEmail());
    }
}
