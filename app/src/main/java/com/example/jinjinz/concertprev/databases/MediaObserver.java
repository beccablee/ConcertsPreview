package com.example.jinjinz.concertprev.databases;

import android.database.ContentObserver;
import android.net.Uri;

/**
 * Created by jinjinz on 7/28/16.
 */
public class MediaObserver extends ContentObserver {
    public interface ContentObserverCallback {
        void updateObserver();
    }

    private ContentObserverCallback contentObserverCallback;

    public MediaObserver(ContentObserverCallback contentObserverCallback) {
        super(null);
        this.contentObserverCallback = contentObserverCallback;
    }

    @Override
    public void onChange(boolean selfChange) {
        contentObserverCallback.updateObserver();
        this.onChange(selfChange, null);
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        contentObserverCallback.updateObserver();
    }

}
