package com.example.jinjinz.concertprev.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.jinjinz.concertprev.R;
import com.example.jinjinz.concertprev.models.Concert;
import com.squareup.picasso.Picasso;

import java.text.MessageFormat;
import java.util.ArrayList;

public class UserLikedConcertsRecyclerAdapter extends RecyclerView.Adapter<UserLikedConcertsRecyclerAdapter.ViewHolder> implements View.OnClickListener {

    @Override
    public void onClick(View view) {
        ImageView ivBackgroundImage = (ImageView) view.findViewById(R.id.ivBackgroundImage);
        Concert concert = (Concert) ivBackgroundImage.getTag();
        mUserLikedConcertsRecyclerAdapterListener.onConcertTap(concert);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final Context context;
        public Button btnUnlike;
        public TextView tvEventName;
        public TextView tvEventLocation;
        public RelativeLayout rlConcert;
        public ImageView ivBackgroundImage;


        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            context = itemView.getContext();
            btnUnlike = (Button) itemView.findViewById(R.id.btnUnlikeConcert);
            tvEventName = (TextView) itemView.findViewById(R.id.tvEventName);
            tvEventLocation = (TextView) itemView.findViewById(R.id.tvEventLocation);
            rlConcert = (RelativeLayout) itemView.findViewById(R.id.rlConcert);
            ivBackgroundImage = (ImageView) itemView.findViewById(R.id.ivBackgroundImage);
        }

    }

    public interface UserLikedConcertsRecyclerAdapterListener {
        void onConcertTap(Concert concert);
        void unlikeConcert(Concert concert);
    }

    UserLikedConcertsRecyclerAdapterListener mUserLikedConcertsRecyclerAdapterListener;

    // Store a member variable for the concerts
    private ArrayList<Concert> mConcerts;
    // Store the context for easy access
    private Context mContext;

    // Pass the concert array into the recycler constructor
    public UserLikedConcertsRecyclerAdapter(Context context, ArrayList<Concert> concerts, UserLikedConcertsRecyclerAdapterListener userLikedConcertsRecyclerAdapterListener) {
        mConcerts = concerts;
        mContext = context;
        mUserLikedConcertsRecyclerAdapterListener = userLikedConcertsRecyclerAdapterListener;
    }

    private Context getContext() {
        return mContext;
    }

    // Implement the three recyclerview methods

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public UserLikedConcertsRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View concertView = layoutInflater.inflate(R.layout.item_liked_concert, parent, false);

        //Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(concertView);
        concertView.setOnClickListener(this);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        //Get concert based on position
        final Concert concert = mConcerts.get(position);

        // Set item views based on your views and data model
        TextView eventName = viewHolder.tvEventName;
        eventName.setText(concert.getEventName());
        TextView eventLoc = viewHolder.tvEventLocation;
        if (concert.getStateCode() != null) {
            eventLoc.setText(MessageFormat.format("{0}, {1}", concert.getCity(), concert.getStateCode()));
        } else {
            eventLoc.setText(MessageFormat.format("{0}, {1}", concert.getCity(), concert.getCountryCode()));

        }
        Button unlike = viewHolder.btnUnlike;
        unlike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUserLikedConcertsRecyclerAdapterListener.unlikeConcert(concert);
            }
        });
        // set background programatically with image
        ImageView backgroundImg = viewHolder.ivBackgroundImage;
        Picasso.with(getContext()).load(concert.getBackdropImage()).placeholder(R.drawable.concert_placeholder).into(backgroundImg);
        viewHolder.ivBackgroundImage.setTag(concert);
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mConcerts.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        mConcerts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items
    public void addAll(ArrayList<Concert> concerts) {
        mConcerts.addAll(concerts);
        notifyDataSetChanged();
    }

}
