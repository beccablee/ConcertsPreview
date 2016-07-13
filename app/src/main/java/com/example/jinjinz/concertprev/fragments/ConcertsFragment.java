package com.example.jinjinz.concertprev.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jinjinz.concertprev.Adapters.SearchRecyclerAdapter;
import com.example.jinjinz.concertprev.R;
import com.example.jinjinz.concertprev.models.Concert;
import com.loopj.android.http.AsyncHttpClient;

import java.util.ArrayList;


public class ConcertsFragment extends Fragment implements SearchRecyclerAdapter.SearchRecyclerAdapterListener {

    @Override
    public void onConcertTap(Concert concert) {
        concertsFragmentListener.onConcertTap(concert);
    }

    public interface ConcertsFragmentListener {
        void populateConcertsNearYou(ConcertsFragment fragment);
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
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConcertsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ConcertsFragment newInstance(String param1, String param2) { // Don't really need yet
        ConcertsFragment fragment = new ConcertsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
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
        concertsFragmentListener.populateConcertsNearYou(this);
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

        return view;
    }


//
//    protected void populateConcertsNearYou() {
//        // url: includes api key and music classification
//        String eventsURL = "https://app.ticketmaster.com/discovery/v2/events.json?apikey=7elxdku9GGG5k8j0Xm8KWdANDgecHMV0&classificationName=Music";
//        // the parameter(s)
//        RequestParams params = new RequestParams();
//        //params.put("latlong", dummyLatlong);
//        params.put("latlong", "29.563034,-95.262090"); // must be N, E (in the us the last should def be -) that num + is W
//        params.put("radius", "50");
//        params.put("size", "200");
//
//        // call client
//        client = new AsyncHttpClient();
//        client.get(eventsURL, params, new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) { // on success I will get back the large json obj: { _embedded: { events: [ {0, 1, 2, ..} ] } }
//                // DESERIALIZE JSON
//                // CREATE MODELS AND ADD TO ADAPTER
//                // LOAD MODEL INTO LIST VIEW
//                JSONArray eventsArray = null;
//                try {
//                    eventsArray = jsonObject.getJSONObject("_embedded").getJSONArray("events"); // size = 0 rn
//                    Log.d("populateFragment", String.valueOf(eventsArray.length()));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                addConcerts(Concert.concertsFromJsonArray(eventsArray)); //
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                Log.d("populateFragment", "failure");
//            }
//        });
//    }

    public void addConcerts(ArrayList<Concert> concertArrayList) {
        concerts.addAll(concertArrayList);
        searchAdapter.notifyDataSetChanged();
    }




}
