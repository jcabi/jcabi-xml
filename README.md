<img src="http://img.jcabi.com/logo-square.png" width="64px" height="64px" />

[![Made By Teamed.io](http://img.teamed.io/btn.svg)](http://www.teamed.io)
[![DevOps By Rultor.com](http://www.rultor.com/b/jcabi/jcabi-xml)](http://www.rultor.com/p/jcabi/jcabi-xml)

[![Build Status](https://travis-ci.org/jcabi/jcabi-xml.svg?branch=master)](https://travis-ci.org/jcabi/jcabi-xml)
[![PDD status](http://www.0pdd.com/svg?name=jcabi/jcabi-xml)](http://www.0pdd.com/p?name=teamed/jcabi/jcabi-xml)
[![Build status](https://ci.appveyor.com/api/projects/status/323ak1323abk3x30/branch/master?svg=true)](https://ci.appveyor.com/project/yegor256/jcabi-xml/branch/master)
[![Coverage Status](https://coveralls.io/repos/jcabi/jcabi-xml/badge.svg?branch=__rultor&service=github)](https://coveralls.io/github/jcabi/jcabi-xml?branch=__rultor)
[![Javadoc](https://javadoc-emblem.rhcloud.com/doc/com.jcabi/jcabi-xml/badge.svg)](http://www.javadoc.io/doc/com.jcabi/jcabi-xml)

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.jcabi/jcabi-xml/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.jcabi/jcabi-xml)
[![Dependencies](https://www.versioneye.com/user/projects/561a9e86a193340f2f00115e/badge.svg?style=flat)](https://www.versioneye.com/user/projects/561a9e86a193340f2f00115e)

More details are here: [xml.jcabi.com](http://xml.jcabi.com/index.html).
Also, read this blog post: [Java XML Parsing Made Easy](http://www.yegor256.com/2014/04/24/java-xml-parsing-and-traversing.html).

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
