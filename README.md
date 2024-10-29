# Simple XML Parsing and Traversing, in Java

[![EO principles respected here](https://www.elegantobjects.org/badge.svg)](https://www.elegantobjects.org)
[![Managed by Zerocracy](https://www.0crat.com/badge/C3RUBL5H9.svg)](https://www.0crat.com/p/C3RUBL5H9)
[![DevOps By Rultor.com](http://www.rultor.com/b/jcabi/jcabi-xml)](http://www.rultor.com/p/jcabi/jcabi-xml)

[![mvn](https://github.com/jcabi/jcabi-xml/actions/workflows/mvn.yml/badge.svg)](https://github.com/jcabi/jcabi-xml/actions/workflows/mvn.yml)
[![PDD status](http://www.0pdd.com/svg?name=jcabi/jcabi-xml)](http://www.0pdd.com/p?name=jcabi/jcabi-xml)
[![codecov](https://codecov.io/gh/jcabi/jcabi-xml/branch/master/graph/badge.svg)](https://codecov.io/gh/jcabi/jcabi-xml)
[![Javadoc](https://javadoc.io/badge/com.jcabi/jcabi-xml.svg)](http://www.javadoc.io/doc/com.jcabi/jcabi-xml)
[![jpeek report](https://i.jpeek.org/com.jcabi/jcabi-xml/badge.svg)](https://i.jpeek.org/com.jcabi/jcabi-xml/)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.jcabi/jcabi-xml/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.jcabi/jcabi-xml)

More details are here: [xml.jcabi.com](http://xml.jcabi.com/index.html).
Also, read this blog post: [Java XML Parsing Made Easy][blog].

It's a simple wrapper around DOM that makes XML parsing and printing
easy and simple, for example:

```java
XML xml = new XMLDocument(
  "<cart><item id="4">Coffee to go</item></cart>"
);
XML item = xml.nodes("//item[@id=4]").get(0);
String name = item.xpath("text()").get(0);
System.out.println(xml.toString());
```

## How to contribute?

Fork the repository, make changes, submit a pull request.
We promise to review your changes same day and apply to
the `master` branch, if they look correct.

Please run Maven build before submitting a pull request:

```bash
mvn clean install -Pqulice
```

[blog]: http://www.yegor256.com/2014/04/24/java-xml-parsing-and-traversing.html
