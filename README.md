[![Build Status](https://travis-ci.org/breun/lastfm-friend-ranker.png?branch=master)](https://travis-ci.org/breun/lastfm-friend-ranker)

Last.fm Friend Ranker
=====================

Last.fm Friend Ranker ranks a [Last.fm](http://www.last.fm/) user's friends by the compatibility score. Who could really
be friends with someone who doesn't share their taste in music?

Requirements
------------

You need Java Development Kit 8 or higher and Maven to build it.

Building
--------

```
$ mvn package
```

Configuration
-------------

You'll need to have a Last.fm API key for this application. Get one [here](http://www.last.fm/api/account/create) if you
don't have one yet!

Then copy the file called `configuration.properties.dist` to `configuration.properties` and put in your Last.fm API key.

That's it, you're ready to go!

Running
-------

After the WAR file has been built you can run the application locally on Tomcat 7 via the Maven plugin:

```
$ mvn tomcat7:run
```

Once the application has started you can access it on [http://localhost:8080/](http://localhost:8080/).

Of course you can always deploy the WAR file (you should find it in the `target` directory after building) to your
favorite Java Servlet container, but the configuration loading code might need some love before that will work
properly.