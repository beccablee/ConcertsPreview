package com.example.jinjinz.concertprev.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jinjinz.concertprev.MainActivity;
import com.example.jinjinz.concertprev.R;
import com.example.jinjinz.concertprev.adapters.LikedConcertsAdapter;
import com.example.jinjinz.concertprev.models.Concert;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LikedConcertsFragmentListener} interface
 * to handle interaction events.
 * Use the {@link LikedConcertsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LikedConcertsFragment extends Fragment implements LikedConcertsAdapter.LikedConcertsAdapterListener {


    public interface LikedConcertsFragmentListener {
        void unlikeConcert(Concert concert);
        void onConcertTap(Concert concert);

    }

    private static final String ARG_LIKED_CONCERTS = "concerts";

    private ArrayList<Concert> myConcerts;
    private LikedConcertsAdapter userLikedConcertsRecyclerAdapter;
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
        userLikedConcertsRecyclerAdapter = new LikedConcertsAdapter(getActivity(), myConcerts, this);
        ArrayList<Concert> concerts = MainActivity.getLikedConcerts();
        myConcerts.addAll(concerts);
        userLikedConcertsRecyclerAdapter.notifyDataSetChanged();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_liked_concerts, container, false);
        RecyclerView rvMyConcerts = (RecyclerView) view.findViewById(R.id.rvLikedConcerts);
        rvMyConcerts.setAdapter(userLikedConcertsRecyclerAdapter);
        rvMyConcerts.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LikedConcertsFragmentListener) {
            mLikedConcertsFragmentListener = (LikedConcertsFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement LikedConcertsFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mLikedConcertsFragmentListener = null;
    }

    public void addConcerts(ArrayList<Concert> concerts) {
        myConcerts.addAll(concerts);
        userLikedConcertsRecyclerAdapter.notifyDataSetChanged();
    }


    @Override
    public void onConcertTap(Concert concert) {
        mLikedConcertsFragmentListener.onConcertTap(concert);
    }

    @Override
    public void unlikeConcert(Concert concert) {
        mLikedConcertsFragmentListener.unlikeConcert(concert);
    }



}
