/*
package com.example.jinjinz.concertprev;

import android.database.AbstractCursor;
import android.text.TextUtils;

import com.example.jinjinz.concertprev.Adapters.SearchSuggestionsAdapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

*/
/**
 * Created by noradiegwu on 7/14/16.
 *//*

public class SuggestionsCursor extends AbstractCursor
{
    private ArrayList<String> mResults;

    public SuggestionsCursor(CharSequence constraint)
    {
        final int count = 100;
        mResults = new ArrayList<String>(count);
        for(int i = 0; i < count; i++){
            mResults.add("Result " + (i + 1));
        }
        if(!TextUtils.isEmpty(constraint)){
            String constraintString = constraint.toString().toLowerCase(Locale.ROOT);
            Iterator<String> iter = mResults.iterator();
            while(iter.hasNext()){
                if(!iter.next().toLowerCase(Locale.ROOT).startsWith(constraintString))
                {
                    iter.remove();
                }
            }
        }
    }

    @Override
    public int getCount()
    {
        return mResults.size();
    }

    @Override
    public String[] getColumnNames()
    {
        return SearchSuggestionsAdapter.mFields;
    }

    @Override
    public long getLong(int column)
    {
        if(column == 0){
            return getPosition();
        }
        throw new UnsupportedOperationException("unimplemented");
    }

    @Override
    public String getString(int column)
    {
        if(column == 1){
            return mResults.get(getPosition());
        }
        throw new UnsupportedOperationException("unimplemented");
    }

    @Override
    public short getShort(int column)
    {
        throw new UnsupportedOperationException("unimplemented");
    }

    @Override
    public int getInt(int column)
    {
        throw new UnsupportedOperationException("unimplemented");
    }

    @Override
    public float getFloat(int column)
    {
        throw new UnsupportedOperationException("unimplemented");
    }

    @Override
    public double getDouble(int column)
    {
        throw new UnsupportedOperationException("unimplemented");
    }

    @Override
    public boolean isNull(int column)
    {
        return false;
    }
}
*/
