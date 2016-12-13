package vu.group6.amsterquest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ChatMessageAdapter extends ArrayAdapter<ChatMessage> {

    private static final int MY_MESSAGE = 0, OTHER_MESSAGE = 1, MY_IMAGE = 2, OTHER_IMAGE = 3;
    private static final int MAX_IMAGE_HEIGHT_DP = 120;
    private final ArrayList<ChatMessage> items;

    public ChatMessageAdapter(Context context, ArrayList<ChatMessage> items) {
        super(context, R.layout.item_mine_message, items);
        this.items = items;
    }

    @Override
    public int getViewTypeCount() {
        return 4;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage item = getItem(position);
        if (item.mine && item.image == null) return MY_MESSAGE;
        else if (!item.mine && item.image == null) return OTHER_MESSAGE;
        else if (item.mine) return MY_IMAGE;
        else return OTHER_IMAGE;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int viewType = getItemViewType(position);
        if (viewType == MY_MESSAGE) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_mine_message, parent, false);
            TextView textView = (TextView) convertView.findViewById(R.id.text);
            textView.setText(getItem(position).content);

        } else if (viewType == OTHER_MESSAGE) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_other_message, parent, false);
            TextView textView = (TextView) convertView.findViewById(R.id.text);
            textView.setText(getItem(position).content);

        } else if (viewType == MY_IMAGE) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_mine_image, parent, false);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.image);
            imageView.setImageURI(getItem(position).image);
            imageView.setMaxHeight((int) Utils.dpToPx(getContext().getResources(), MAX_IMAGE_HEIGHT_DP));

        } else {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_other_image, parent, false);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.image);
            imageView.setImageURI(getItem(position).image);
            imageView.setMaxHeight((int) Utils.dpToPx(getContext().getResources(), MAX_IMAGE_HEIGHT_DP));

        }

        convertView.findViewById(R.id.chatMessageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getContext(), "onClick", Toast.LENGTH_LONG).show();
            }
        });

        return convertView;
    }

    public ArrayList<ChatMessage> getItems() {
        return items;
    }
}
