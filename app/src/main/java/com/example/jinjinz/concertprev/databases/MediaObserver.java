package com.example.jinjinz.concertprev.databases;

import android.database.ContentObserver;
import android.net.Uri;

/**
 * Created by jinjinz on 7/28/16.
 */
public class MediaObserver extends ContentObserver {
    /**
     * interface callback
     */
    public interface ContentObserverCallback {
        void updateObserver();
    }
    //variable
    private ContentObserverCallback contentObserverCallback;

    /**
     * constructor
     * @param contentObserverCallback Activity which implements this
     */
    public MediaObserver(ContentObserverCallback contentObserverCallback) {
        super(null);
        this.contentObserverCallback = contentObserverCallback;
    }

    /**
     * calls newer method
     * @param selfChange same as super
     */
    @Override
    public void onChange(boolean selfChange) {
        contentObserverCallback.updateObserver();
        this.onChange(selfChange, null);
    }

    /**
     * called when there is change
     * @param uri uri changed
     * @param selfChange same as super
     */
    @Override
    public void onChange(boolean selfChange, Uri uri) {
        contentObserverCallback.updateObserver();
    }

}
