package com.example.jinjinz.concertprev.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.EditText;
import android.widget.ImageView;


import com.example.jinjinz.concertprev.adapters.EndlessRecyclerViewScrollListener;
import com.example.jinjinz.concertprev.R;
import com.example.jinjinz.concertprev.adapters.SearchRecyclerAdapter;
import com.example.jinjinz.concertprev.models.Concert;

import java.util.ArrayList;


public class SearchFragment extends Fragment implements SearchRecyclerAdapter.SearchRecyclerAdapterListener, SwipeRefreshLayout.OnRefreshListener {

    public interface SearchFragmentListener {
        void populateConcerts(String query);
        void onConcertTap(Concert concert);
        void loadMoreConcerts(String query, int page);
    }

    public static SwipeRefreshLayout mSwipeRefreshLayout;
    public static SearchRecyclerAdapter searchAdapter;

    private String queryText;
    private ArrayList<Concert> concerts;
    private SearchFragmentListener searchFragmentListener;

    @Override
    public void onConcertTap(Concert concert) {
        searchFragmentListener.onConcertTap(concert);
    }

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * @return A new instance of fragment SearchFragment.
     */
    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            searchFragmentListener = (SearchFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnConcertsFragmentListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        concerts = new ArrayList<>();
        // Create adapter passing in activity context and concerts list
        searchAdapter = new SearchRecyclerAdapter(getActivity(), concerts, this);
        // populate view
        searchFragmentListener.populateConcerts(queryText);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_concerts, container, false);
        // Lookup the recyclerview in the fragment and
        RecyclerView rvConcerts = (RecyclerView) view.findViewById(R.id.rvConcerts);
        // connect adapter to recyclerview
        rvConcerts.setAdapter(searchAdapter);
        // Set layout manager to position the items
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rvConcerts.setLayoutManager(linearLayoutManager);
        // Find the toolbar view inside the activity layout
        setHasOptionsMenu(true);

        Toolbar tbSearch = (Toolbar) view.findViewById(R.id.tbSearch);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        ((AppCompatActivity) getActivity()).setSupportActionBar(tbSearch);
        // swipe refresh
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        // EndlessScroll
        rvConcerts.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                //TODO: call more concerts
                searchFragmentListener.loadMoreConcerts(queryText, page);
            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = new SearchView(getActivity());
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        MenuItemCompat.setActionView(item, searchView);

        //change searchview styling
        ((EditText)searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text)).setTextColor(Color.WHITE);
        ((ImageView)searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn)).setImageResource(R.drawable.ic_close);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                queryText = query;
                searchFragmentListener.populateConcerts(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    /** Adds an array list of Concert objects to the custom adapter */
    public void addConcerts(ArrayList<Concert> concertArrayList) {
        concerts.addAll(concertArrayList);
        searchAdapter.notifyDataSetChanged();
    }


    @Override
    public void onRefresh() {
        searchFragmentListener.populateConcerts(null);

    }


}
