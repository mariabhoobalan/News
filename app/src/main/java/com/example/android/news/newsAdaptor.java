package com.example.android.news;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class newsAdaptor extends ArrayAdapter<news> {

    public newsAdaptor(Activity context, ArrayList<news> x_news) {
        super(context, 0, x_news);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.news_list, parent, false);
        }

        //Retrieve the current instance of the list item by querying the position
        news currentNews = getItem(position);
        View listLayout = listItemView.findViewById(R.id.mainlist);

        //get the data to be populated in the list view
        TextView newsView = (TextView) listItemView.findViewById(R.id.webtitle);
        newsView.setText(currentNews.getmNews());
        TextView sectionView = (TextView) listItemView.findViewById(R.id.section);
        sectionView.setText(currentNews.getmSection());
        TextView dateView = (TextView) listItemView.findViewById(R.id.date);
        dateView.setText(currentNews.getmDate());

        //Alternate the list color for better readability
        if (position % 2 == 0) {
            listLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.EvenPanelColor));
        } else {
            listLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.OddPanelColor));
        }
        return listItemView;
    }
}
