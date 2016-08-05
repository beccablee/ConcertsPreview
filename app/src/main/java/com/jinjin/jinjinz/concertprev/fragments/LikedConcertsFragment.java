package com.jinjin.jinjinz.concertprev.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jinjin.jinjinz.concertprev.MainActivity;
import com.jinjin.jinjinz.concertprev.R;
import com.jinjin.jinjinz.concertprev.adapters.SearchRecyclerAdapter;
import com.jinjin.jinjinz.concertprev.models.Concert;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LikedConcertsFragmentListener} interface
 * to handle interaction events.
 * Use the {@link LikedConcertsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LikedConcertsFragment extends Fragment implements SearchRecyclerAdapter.SearchRecyclerAdapterListener {


    public interface LikedConcertsFragmentListener {
        void onConcertTap(Concert concert);

    }

    private ArrayList<Concert> myConcerts;
    private SearchRecyclerAdapter mSearchRecyclerAdapter;
    private LikedConcertsFragmentListener mLikedConcertsFragmentListener;

    public LikedConcertsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LikedConcertsFragment.
     */
    public static LikedConcertsFragment newInstance() {
        LikedConcertsFragment fragment = new LikedConcertsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myConcerts = new ArrayList<>();
        mSearchRecyclerAdapter = new SearchRecyclerAdapter(getActivity(), myConcerts, this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_liked_concerts, container, false);
        ArrayList<Concert> concerts = MainActivity.getLikedConcerts();
        myConcerts.clear();
        myConcerts.addAll(concerts);
        mSearchRecyclerAdapter.notifyDataSetChanged();
        RecyclerView rvMyConcerts = (RecyclerView) view.findViewById(R.id.rvLikedConcerts);
        TextView tvNoConcerts = (TextView) view.findViewById(R.id.tvNoConcerts);
        if(myConcerts.size() == 0) {
            rvMyConcerts.setVisibility(View.GONE);
            tvNoConcerts.setVisibility(View.VISIBLE);
        } else {
            rvMyConcerts.setVisibility(View.VISIBLE);
            tvNoConcerts.setVisibility(View.GONE);
        }

        rvMyConcerts.setAdapter(mSearchRecyclerAdapter);
        rvMyConcerts.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mLikedConcertsFragmentListener = (LikedConcertsFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement LikedConcertsFragmentListener");
        }
    }

    @Override
    public void onConcertTap(Concert concert) {
        mLikedConcertsFragmentListener.onConcertTap(concert);
    }


}
