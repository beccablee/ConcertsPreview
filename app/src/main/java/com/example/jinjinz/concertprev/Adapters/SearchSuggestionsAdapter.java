/*
package com.example.jinjinz.concertprev.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.widget.SimpleCursorAdapter;

*/
/**
 * Created by noradiegwu on 7/14/16.
 *//*

public class SearchSuggestionsAdapter extends SimpleCursorAdapter {

    public interface SearchSuggestionsAdapterListener {
        Cursor runQueryOnBackgroundThread(CharSequence constraint);
    }

    SearchSuggestionsAdapterListener searchSuggestionsAdapterListener;

    public static final String[] mFields  = { "_id", "result" };
    private static final String[] mVisible = { "result" };
    private static final int[]    mViewIds = { android.R.id.text1 };

    public SearchSuggestionsAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_1, null, mVisible, mViewIds, 0);
    }

    @Override
    public Cursor runQueryOnBackgroundThread(CharSequence constraint)
    {
        searchSuggestionsAdapterListener.runQueryOnBackgroundThread(constraint);
//        return new SuggestionsCursor(constraint);
        return null;
    }




}
*/
