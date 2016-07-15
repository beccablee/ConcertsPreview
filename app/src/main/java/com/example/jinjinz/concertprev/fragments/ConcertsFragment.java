package com.example.jinjinz.concertprev.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.jinjinz.concertprev.Adapters.SearchRecyclerAdapter;
import com.example.jinjinz.concertprev.R;
import com.example.jinjinz.concertprev.models.Concert;
import com.loopj.android.http.AsyncHttpClient;

import java.util.ArrayList;


public class ConcertsFragment extends Fragment implements SearchRecyclerAdapter.SearchRecyclerAdapterListener{
    // , SearchSuggestionsAdapter.SearchSuggestionsAdapterListener
    /*  @Override
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        return new SuggestionsCursor(constraint);
    }
*/

    @Override
    public void onConcertTap(Concert concert) {
        concertsFragmentListener.onConcertTap(concert);
    }


    public interface ConcertsFragmentListener {
        void populateConcerts(ConcertsFragment fragment, String query);
        void onConcertTap(Concert concert);

    }




    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public String dummyLatlong = "34.0928090,-118.3286610"; // need to get from user (may extract from activity.. not sure how yet)
    private String queryText;
    AsyncHttpClient client;
    ArrayList<Concert> concerts;
    SearchRecyclerAdapter searchAdapter;
    ConcertsFragmentListener concertsFragmentListener;

   // private OnFragmentInteractionListener mListener;

    public ConcertsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ConcertsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ConcertsFragment newInstance() { // Don't really need yet
        ConcertsFragment fragment = new ConcertsFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            concertsFragmentListener = (ConcertsFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnArticleSelectedListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
/*
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
*/

        concerts = new ArrayList<>();
        // Create adapter passing in activity context and concerts list
        searchAdapter = new SearchRecyclerAdapter(getActivity(), concerts, this);
        // populate view
        concertsFragmentListener.populateConcerts(this, queryText);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //TODO: get user location
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_concerts, container, false);
        // Lookup the recyclerview in the fragment and
        RecyclerView rvConcerts = (RecyclerView) view.findViewById(R.id.rvConcerts);
        // / connect adapter to recyclerview
        rvConcerts.setAdapter(searchAdapter);
        // Set layout manager to position the items
        rvConcerts.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Find the toolbar view inside the activity layout
        setHasOptionsMenu(true);

        Toolbar tbSearch = (Toolbar) view.findViewById(R.id.tbSearch);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        ((AppCompatActivity) getActivity()).setSupportActionBar(tbSearch);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_search, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = new SearchView(getActivity());
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        MenuItemCompat.setActionView(item, searchView);
        searchView.setBackgroundColor(Color.WHITE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                concertsFragmentListener.populateConcerts(ConcertsFragment.this, query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }


    public void addConcerts(ArrayList<Concert> concertArrayList) {
        concerts.clear();
        searchAdapter.notifyDataSetChanged();
        concerts.addAll(concertArrayList);
        searchAdapter.notifyDataSetChanged();
    }

}
