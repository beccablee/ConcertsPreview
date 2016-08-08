# **Vibe** - *feel the vibe of the concert*

**Vibe** helps users make the most of their concert experience by exposing them to all the artists in a concert. Vibe auto-populates playlists with 30-second song previews of the concert's artists and makes it easy to purchase concert tickets.

Time spent: **5** weeks in total

Team members: Rebecca Lee, Nneoma Oradiegwu, Jinjin Zhao

## Features

The following **features** were implemented:

- User can search through concerts in their area using the Ticketmaster API.
- For each concert displayed, the user can see the following details:
  - Details include: event name, location, backdrop image
- When the user clicks on a concert, the concert details and playlist are shown
  - Details include: event name, artists, location, backdrop image, date
- Each concert's playlist contains 30-second song previews of every artist's top tracks from the Spotify API.
- The user can purchase concert tickets from the concert details page.
- When the user taps on a song, the app's media player opens up, including navigation controls.
- The user can like songs and concerts, which are stored in the database.
- The user can view liked songs and concerts from the user page.

## Video Walkthrough

Here's a walkthrough of implemented user stories:

<img src='http://g.recordit.co/tGD1RVmEGC.gif' title='Vertical' width='' alt='Vertical' />

GIF created with [Recordit](http://recordit.co/).

Video Version: http://recordit.co/tGD1RVmEGC

## Notes

Main challenges faced:

## Open-source libraries used

- [Android Async HTTP](https://github.com/loopj/android-async-http) - Simple asynchronous HTTP requests with JSON parsing
- [Picasso](http://square.github.io/picasso/) - Image loading and caching library for Android
- [PermissionsDispatcher] (https://github.com/hotchemi/PermissionsDispatcher) - Simple annotation-based API to handle runtime permissions
- [Parceler] (https://parceler.org/) - Library that generates the Android Parcelable boilerplate source code
- [Google Location and Activity Recognition] (https://developers.google.com/android/guides/setup) - User location and current activity location services from Google Play
- [PagerSlidingTabStrip] (https://github.com/astuetz/PagerSlidingTabStrip) - Interactive indicator to navigate between the different pages of a ViewPager

## Sources

- [Concert Loading Image] http://www.paperhi.com/Toplist_Best_23527/download_2560x1440
- [Ticketmaster API] http://developer.ticketmaster.com/products-and-docs/apis/discovery/v2/
- [Spotify API] https://developer.spotify.com/web-api/endpoint-reference/
- [Spotify Android SDK] https://developer.spotify.com/technologies/spotify-android-sdk/

## License

    Copyright [2016] [Vibe]

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
