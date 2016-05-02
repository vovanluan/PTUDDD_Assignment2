package Fragment;

/**
 * Created by Luan on 5/2/2016.
 */
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.luan.activity.R;

public class CardFragment extends Fragment{

    public CardFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        String cards = this.getArguments().getString("cards");
 //       Log.e("CARDS: ", cards);
        return inflater.inflate(R.layout.card_fragment, container, false);
    }

}
