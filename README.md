<img src="http://img.jcabi.com/logo-square.png" width="64px" height="64px" />
 
[![Build Status](https://travis-ci.org/jcabi/jcabi-xml.svg?branch=master)](https://travis-ci.org/jcabi/jcabi-xml)

More details are here: [xml.jcabi.com](http://xml.jcabi.com/index.html)

It's a simple wrapper around DOM that makes XML parsing and printing
easy and simple, for example:

```java
XML xml = new XMLDocument("<orders><order id="4">Coffee to go</order></orders>");
String id = xml.xpath("//order/@id").get(0);
String name = xml.xpath("//order[@id=4]/text()").get(0);
System.out.println(xml.toString());
```

You need just this dependency:

```xml
<dependency>
  <groupId>com.jcabi</groupId>
  <artifactId>jcabi-xml</artifactId>
  <version>0.7.8</version>
</dependency>
```

## Questions?

If you have any questions about the framework, or something doesn't work as expected,
please [submit an issue here](https://github.com/jcabi/jcabi-xml/issues/new).
If you want to discuss, please use our [Google Group](https://groups.google.com/forum/#!forum/jcabi).

## How to contribute?

Fork the repository, make changes, submit a pull request.
We promise to review your changes same day and apply to
the `master` branch, if they look correct.

Please run Maven build before submitting a pull request:

```
$ mvn clean install -Pqulice
```
