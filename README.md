# Simple XML Parsing and Traversing, in Java

[![EO principles respected here](https://www.elegantobjects.org/badge.svg)](https://www.elegantobjects.org)
[![DevOps By Rultor.com](https://www.rultor.com/b/jcabi/jcabi-xml)](https://www.rultor.com/p/jcabi/jcabi-xml)

[![mvn](https://github.com/jcabi/jcabi-xml/actions/workflows/mvn.yml/badge.svg)](https://github.com/jcabi/jcabi-xml/actions/workflows/mvn.yml)
[![PDD status](https://www.0pdd.com/svg?name=jcabi/jcabi-xml)](https://www.0pdd.com/p?name=jcabi/jcabi-xml)
[![codecov](https://codecov.io/gh/jcabi/jcabi-xml/branch/master/graph/badge.svg)](https://codecov.io/gh/jcabi/jcabi-xml)
[![Javadoc](https://javadoc.io/badge/com.jcabi/jcabi-xml.svg)](https://www.javadoc.io/doc/com.jcabi/jcabi-xml)
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

## Architecture

The library defines three core interfaces: `XML`, `XSL`, and `Sources`.
All concrete classes implement one of these interfaces, and callers
  program against the interfaces exclusively.
This differs from typical Java XML code that couples callers directly
  to [JAXP][jaxp] types such as `Document`, `Transformer`, or `SAXParser`.
New contributors add implementations of these interfaces, not subclasses
  of existing concrete types.

All implementations are immutable and thread-safe.
Operations that would alter state — `registerNs()` on `XML` and `with()`
  on `XSL` — return new objects rather than modifying existing ones.
The [W3C DOM][dom] `Document` is mutable by design and requires external
  synchronization under concurrent access; this library avoids that
  structurally.

Two implementations of `XML` exist for different [XPath][xpath] needs.
[`XMLDocument`][xmldoc] wraps the JDK's [JAXP][jaxp] processor,
  which supports [XPath 1.0][xpath1] and is thread-safe.
[`SaxonDocument`][saxondoc] uses [Saxon-HE][saxon] and supports
  [XPath 2.0][xpath2] and above, but is not thread-safe.
Callers who need typed comparisons, sequences, or advanced `fn:` functions
  must use `SaxonDocument`; others should prefer `XMLDocument`.

[XSD][xsd] validation is enforced through the [`StrictXML`][strictxml]
  decorator, which wraps any `XML` and throws during construction
  if the document is invalid.
Libraries such as [dom4j][dom4j] or plain JAXP leave validation as an
  explicit separate call; here it becomes a structural guarantee at
  object-creation time.

[XSLT][xslt] stylesheets are composed with [`XSLChain`][xslchain],
  which accepts an ordered list of `XSL` instances and applies each
  in sequence, feeding the output of one as the input to the next.
Without this class, callers must manage intermediate `XML` objects
  between transformation steps manually.

[`XSLDocument`][xsldoc] uses [Saxon][saxon] as its [XSLT][xslt] engine
  instead of the JDK's built-in transformer.
Saxon supports [XSLT 2.0][xslt2] and [XSLT 3.0][xslt3]; the JDK bundled
  transformer supports only [XSLT 1.0][xslt1].
Saxon is therefore a required runtime dependency despite the library's
  narrow scope.

XSD schemas and XSLT imports (`xsl:include`, `xsl:import`) are resolved
  from the JVM classpath via [`ClasspathResolver`][cpresolver] and
  [`ClasspathSources`][cpsources].
This is necessary when schemas and stylesheets are packaged inside JAR
  archives, where absolute filesystem paths are unavailable at runtime.
For external files, [`FileSources`][filesources] is the alternative.

## How to contribute?

Fork the repository, make changes, submit a pull request.
We promise to review your changes same day and apply to
the `master` branch, if they look correct.

Please run Maven build before submitting a pull request:

```bash
mvn clean install -Pqulice
```

[blog]: http://www.yegor256.com/2014/04/24/java-xml-parsing-and-traversing.html
[jaxp]: https://docs.oracle.com/javase/tutorial/jaxp/
[dom]: https://www.w3.org/DOM/
[xpath]: https://www.w3.org/TR/xpath-datamodel/
[xpath1]: https://www.w3.org/TR/xpath-10/
[xpath2]: https://www.w3.org/TR/xpath20/
[saxon]: https://www.saxonica.com/welcome/welcome.xml
[xsd]: https://www.w3.org/XML/Schema
[xslt]: https://www.w3.org/TR/xslt-30/
[xslt1]: https://www.w3.org/TR/xslt-10/
[xslt2]: https://www.w3.org/TR/xslt20/
[xslt3]: https://www.w3.org/TR/xslt-30/
[dom4j]: https://dom4j.github.io/
[xmldoc]: src/main/java/com/jcabi/xml/XMLDocument.java
[saxondoc]: src/main/java/com/jcabi/xml/SaxonDocument.java
[xsldoc]: src/main/java/com/jcabi/xml/XSLDocument.java
[xslchain]: src/main/java/com/jcabi/xml/XSLChain.java
[strictxml]: src/main/java/com/jcabi/xml/StrictXML.java
[cpresolver]: src/main/java/com/jcabi/xml/ClasspathResolver.java
[cpsources]: src/main/java/com/jcabi/xml/ClasspathSources.java
[filesources]: src/main/java/com/jcabi/xml/FileSources.java
