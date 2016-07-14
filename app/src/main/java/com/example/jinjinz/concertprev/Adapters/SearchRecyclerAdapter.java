package com.example.jinjinz.concertprev.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.jinjinz.concertprev.R;
import com.example.jinjinz.concertprev.models.Concert;
import com.squareup.picasso.Picasso;

import java.text.MessageFormat;
import java.util.ArrayList;

/**
 * Created by noradiegwu on 7/12/16.
 */
public class SearchRecyclerAdapter extends RecyclerView.Adapter<SearchRecyclerAdapter.ViewHolder> implements View.OnClickListener {
    @Override
    public void onClick(View view) {
        ImageView ivBackgroundImage = (ImageView) view.findViewById(R.id.ivBackgroundImage);
        Concert concert = (Concert) ivBackgroundImage.getTag();
        mSearchRecyclerAdapterListener.onConcertTap(concert);

    }

// interface

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final Context context;
        public TextView tvEventName;
        public TextView tvEventLocation;
        public RelativeLayout rlConcert;
        public ImageView ivBackgroundImage;


        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            context = itemView.getContext();
            tvEventName = (TextView) itemView.findViewById(R.id.tvEventName);
            tvEventLocation = (TextView) itemView.findViewById(R.id.tvEventLocation);
            rlConcert = (RelativeLayout) itemView.findViewById(R.id.rlConcert);
            ivBackgroundImage = (ImageView) itemView.findViewById(R.id.ivBackgroundImage);
        }

    }

    SearchRecyclerAdapterListener mSearchRecyclerAdapterListener;

    public interface SearchRecyclerAdapterListener {
        void onConcertTap(Concert concert);
        // pass to fragment
        // open concert details(concert)
    }

    // Store a member variable for the contacts
    private ArrayList<Concert> mConcerts;
    // Store the context for easy access
    private Context mContext;

    // Pass in the contact array into the constructor
    public SearchRecyclerAdapter(Context context, ArrayList<Concert> concerts, SearchRecyclerAdapterListener searchRecyclerAdapterListener) {
        mConcerts = concerts;
        mContext = context;
        mSearchRecyclerAdapterListener = searchRecyclerAdapterListener;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    // Implement the three recyclerview methods

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public SearchRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View concertView = layoutInflater.inflate(R.layout.item_concert, parent, false);

        //Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(concertView);
        concertView.setOnClickListener(this);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Concert concert = mConcerts.get(position);

        // Set item views based on your views and data model
        TextView eventName = viewHolder.tvEventName;
        eventName.setText(concert.getEventName());
        //viewHolder.tvEventName.setTag(concert);
        TextView eventLoc = viewHolder.tvEventLocation;
        if (concert.getStateCode() != null) {
            eventLoc.setText(MessageFormat.format("{0}, {1}, {2}", concert.getCity(), concert.getStateCode(), concert.getCountryCode()));
        } else {
            eventLoc.setText(MessageFormat.format("{0}, {1}", concert.getCity(), concert.getCountryCode()));

        }
        //viewHolder.tvEventLocation.setTag(concert);
        // set background
        RelativeLayout myRL = viewHolder.rlConcert;
        //myRL.setBackground();
        // set background programatically with image
        ImageView backgroundImg = viewHolder.ivBackgroundImage;
        Picasso.with(getContext()).load(concert.getBackdropImage()).placeholder(R.drawable.concert_placeholder).into(backgroundImg);
        viewHolder.ivBackgroundImage.setTag(concert);
        //Picasso.with(getContext()).load(concert.getBackdropImage()).into(backgroundImg);

        // everywhere may not have a state code
        //TODO: consider adding logic to decide to use state or country code



    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mConcerts.size();
    }

}
