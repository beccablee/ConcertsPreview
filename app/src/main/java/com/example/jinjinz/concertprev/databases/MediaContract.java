package com.example.jinjinz.concertprev.databases;

import android.net.Uri;

/**
 * Created by jinjinz on 7/25/16.
 */
public class MediaContract {
    /**
     * The Content Authority is a name for the entire content provider, similar to the relationship
     * between a domain name and its website. A convenient string to use for content authority is
     * the package name for the app, since it is guaranteed to be unique on the device.
     */

    public static final String CONTENT_AUTHORITY = "com.example.jinjinz.concertprev.databases";

    /**
     * The content authority is used to create the base of all URIs which apps will use to
     * contact this content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * A list of possible paths that will be appended to the base URI for each of the different
     * tables.
     */
    public static final String PATH_PLAYLIST = "playlist";
    public static final String PATH_NOW = "current";
    public static final String PATH_CONCERT = "thisConcert";

}
