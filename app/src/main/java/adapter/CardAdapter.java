package adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.luan.activity.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

import entity.Card;

/**
 * Created by Luan on 5/2/2016.
 */
public class CardAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Card> cards;
    public CardAdapter(Context context) {
        this.context = context;
        this.cards = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return cards.size();
    }

    @Override
    public Card getItem(int position) {
        return cards.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            // inflate the GridView item layout
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.card_item, parent, false);
        }

        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView creator = (TextView) convertView.findViewById(R.id.creator);
        TextView description = (TextView) convertView.findViewById(R.id.description);
        TextView star = (TextView) convertView.findViewById(R.id.star);
        TextView vote = (TextView) convertView.findViewById(R.id.vote);
        Card card = cards.get(position);

        title.setText(card.getTitle());
        creator.setText(card.getCreate_by().getBio().getFirstName());
        description.setText(card.getDescription());
        star.setText(String.valueOf(card.getRating()));
        vote.setText(String.valueOf(card.getUpvotes()));

        return convertView;
    }

    // Update new data in adapter
    public void setListCard(ArrayList<Card> cards) {
        this.cards.clear();
        this.cards.addAll(cards);
        // Update adapter's data
        notifyDataSetChanged();
    }
}
