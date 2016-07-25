package com.example.jinjinz.concertprev;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.jinjinz.concertprev.adapters.UserLikedConcertsRecyclerAdapter;
import com.example.jinjinz.concertprev.fragments.ConcertDetailsFragment;
import com.example.jinjinz.concertprev.models.Concert;

import org.parceler.Parcels;

import java.util.ArrayList;

public class DBTestActivity extends AppCompatActivity implements UserLikedConcertsRecyclerAdapter.UserLikedConcertsRecyclerAdapterListener {
    public static UserLikedConcertsRecyclerAdapter likedConcertsRecyclerAdapter;
    ConcertDetailsFragment mConcertDetailsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbtest);

        RecyclerView rvLikedConcerts = (RecyclerView) findViewById(R.id.rvLikedConcerts);
        ArrayList<Concert> concerts = new ArrayList<>();
        ArrayList<Concert> myconcerts;
        myconcerts = Parcels.unwrap(getIntent().getParcelableExtra("concerts"));
        likedConcertsRecyclerAdapter = new UserLikedConcertsRecyclerAdapter(DBTestActivity.this, concerts, this);
        concerts.addAll(myconcerts); // array of liked concerts
        likedConcertsRecyclerAdapter.notifyDataSetChanged();
        rvLikedConcerts.setAdapter(likedConcertsRecyclerAdapter); // set adapter to custom recycler adapter
        rvLikedConcerts.setLayoutManager(new LinearLayoutManager(DBTestActivity.this));
    }

    @Override
    public void onConcertTap(Concert concert) {
/*        // open songs fragment --> needs more stuff from songsfrag
        mConcertDetailsFragment = ConcertDetailsFragment.newInstance(Parcels.wrap(concert)); // add params if needed
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainFragment, mConcertDetailsFragment);
        ft.addToBackStack("concerts");
        ft.commit();*/
    }

    @Override
    public void onUnlike(Concert concert) {
        MainActivity.userDataSource.deleteLikedConcert(concert);
    }
}
