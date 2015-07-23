LAE Live Demo project [![Build Status](https://travis-ci.org/UltimaPhoenix/lucene-appengine-examples.svg?branch=master)](https://travis-ci.org/UltimaPhoenix/lucene-appengine-examples)
===============

![https://lucene.apache.org/images/lucene_logo_green_300.png](https://lucene.apache.org/images/lucene_logo_green_300.png) ![https://www.google.com/accounts/ah/appengine.jpg](https://www.google.com/accounts/ah/appengine.jpg)

This project contains [live demo](http://bigtable-lucene.appspot.com) source code and examples and an how to for using [luceneappengine](https://github.com/UltimaPhoenix/luceneappengine) (LAE).

**Starting from LAE 2.0.0 release:**
To deploy this demo on your own Google App Engine application:
  * create your application in [Google App Engine](https://developers.google.com/appengine)
  * clone this repository
  * configure the [appengine-maven-plugin](https://developers.google.com/appengine/docs/java/tools/maven) and the your appengine-web.xml
  * and run the following commands
```
    mvn clean package
    mvn appengine:update
```

**Pre LAE 2.0.0 release:**
To deploy this demo on your own Google App Engine application:
  * create your application in [Google App Engine](https://developers.google.com/appengine)
  * clone this repository
  * configure [maven-gae-plugin](http://www.kindleit.net/maven_gae_plugin/usage.html) properties
  * and run the following commands
```
    mvn clean package
    mvn gae:unpack
    mvn gae:deploy
```

## Changelog ##
  * 05-07-2015: now build with the new released LAE 3.0.0 and LUCENE 5.0.0, this is a major release with breaking changes check official site for details
  * 17-09-2014: now build with the new released LAE 2.2.0 and LUCENE 4.10.0
  * 10-08-2014: now build with the new released LAE 2.1.0 and LUCENE 4.9.0
  * 23-07-2014: now build with the new released LAE 2.0.1 and LUCENE 4.8.1, added pagination of search results and other performance improvements.
  * **20-05-2014**: now build with the new released LAE 2.0.0 and LUCENE 4.8.1, Java 7 and appengine sdk 1.9.4 see the [new LAE project site for details](https://code.google.com/p/luceneappengine) now build with appengine-maven-plugin
  * 05-04-2014: now build with the new released LAE 1.2.0 and LUCENE 4.7.1 see the [new LAE project site for details](https://code.google.com/p/luceneappengine)
  * 05-04-2014: now build with the new released LAE 1.1.0 and LUCENE 4.6.0 see the [new LAE project site for details](https://code.google.com/p/luceneappengine)
  * 05-04-2014: now build with the new released LAE 1.0.3 and LUCENE 4.5.0 see the [new LAE project site for details](https://code.google.com/p/luceneappengine)
  * 17-08-2013: update information messages, enabled wildcard query
  * 06-06-2013: now build with LAE 4.3.0-SNAPSHOT and LUCENE 4.3.0
  * 05-05-2013: now build with LAE 4.2.1-SNAPSHOT and LUCENE 4.2.1
  * 21-04-2013: updated with latest gae-sdk library, updated look and feel, updated with new simplified configuration of LAE
  * 12-03-2013: now build with LAE 4.2.0-SNAPSHOT and LUCENE 4.2.0
  * 13-02-2013: now build with LAE 4.1.0-SNAPSHOT and LUCENE 4.1.0
  * 22-10-2012: now build with LAE 4.0.0-SNAPSHOT and LUCENE 4.0.0

That's all!
