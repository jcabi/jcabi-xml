<img src="http://img.jcabi.com/logo-square.png" width="64px" height="64px" />

[![Made By Teamed.io](http://img.teamed.io/btn.svg)](http://www.teamed.io)
[![DevOps By Rultor.com](http://www.rultor.com/b/jcabi/jcabi-xml)](http://www.rultor.com/p/jcabi/jcabi-xml)

[![Build Status](https://travis-ci.org/jcabi/jcabi-xml.svg?branch=master)](https://travis-ci.org/jcabi/jcabi-xml)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.jcabi/jcabi-xml/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.jcabi/jcabi-xml)

More details are here: [xml.jcabi.com](http://xml.jcabi.com/index.html)

It's a simple wrapper around DOM that makes XML parsing and printing
easy and simple, for example:

```java
XML xml = new XMLDocument("<orders><order id="4">Coffee to go</order></orders>");
String id = xml.xpath("//order/@id").get(0);
String name = xml.xpath("//order[@id=4]/text()").get(0);
System.out.println(xml.toString());
```

## Questions?

If you have any questions about the framework, or something doesn't work as expected,
please [submit an issue here](https://github.com/jcabi/jcabi-xml/issues/new).

## How to contribute?

Fork the repository, make changes, submit a pull request.
We promise to review your changes same day and apply to
the `master` branch, if they look correct.

Please run Maven build before submitting a pull request:

```
$ mvn clean install -Pqulice
```
