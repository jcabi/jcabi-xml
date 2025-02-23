/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.xml;

import com.jcabi.matchers.XhtmlMatchers;
import com.yegor256.Together;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.cactoos.io.ResourceOf;
import org.cactoos.io.TeeInput;
import org.cactoos.scalar.LengthOf;
import org.cactoos.text.FormattedText;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXParseException;

/**
 * Test case for {@link XMLDocument}.
 *
 * @since 0.1
 * @checkstyle AbbreviationAsWordInNameCheck (20 lines)
 */
@SuppressWarnings({"PMD.TooManyMethods", "PMD.DoNotUseThreads"})
final class XMLDocumentTest {
    /**
     * Root XSD.
     */
    private static final String XSD = StringUtils.join(
        "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>",
        "<xs:element name='root' type='xs:string'/>",
        "</xs:schema>"
    );

    @Test
    void findsDocumentNodesWithXpath() {
        final XML doc = new XMLDocument(
            "<r><a>\u0443\u0440\u0430!</a><a>B</a></r>"
        );
        MatcherAssert.assertThat(
            doc.xpath("//a/text()"),
            Matchers.hasSize(2)
        );
        MatcherAssert.assertThat(
            doc.xpath("/r/a/text()"),
            Matchers.hasItem("\u0443\u0440\u0430!")
        );
    }

    @Test
    void findWithXpathListEqualsToJavaUtilList() {
        MatcherAssert.assertThat(
            new XMLDocument(
                "<does><not><matter/></not></does>"
            ).xpath("//missing/text()"),
            new IsEqual<>(
                Collections.<String>emptyList()
            )
        );
        MatcherAssert.assertThat(
            new XMLDocument(
                "<root><item>first</item><item>second</item></root>"
            ).xpath("//root/item[1]/text()"),
            new IsEqual<>(
                Collections.singletonList("first")
            )
        );
        MatcherAssert.assertThat(
            new XMLDocument(
                "<root><item>abc</item><item>def</item></root>"
            ).xpath("/root/item/text()"),
            new IsEqual<>(
                Arrays.asList("abc", "def")
            )
        );
    }

    @RepeatedTest(60)
    void doesNotFailOnFetchingInMultipleThreadsFromTheSameDocument() {
        final XML xml = new XMLDocument("<a></a>");
        Assertions.assertDoesNotThrow(
            new Together<>(
                thread -> xml.nodes("/a")
            )::asList,
            "XMLDocument must not fail on fetching in multiple threads from the same document"
        );
    }

    @RepeatedTest(60)
    void doesNotFailOnFetchingInMultipleThreadsFromDifferentDocuments() {
        Assertions.assertDoesNotThrow(
            new Together<>(
                thread -> {
                    final XML xmir = new XMLDocument("<a></a>");
                    return xmir.nodes("/a");
                }
            )::asList,
            "XMLDocument must not fail on fetching in multiple threads from different documents"
        );
    }

    @RepeatedTest(60)
    void doesNotFailOnXsdValidationInMultipleThreadsWithTheSameDocumentAndXsd() {
        final XML xml = new XMLDocument("<root>passesValidXmlThrough</root>");
        final XML xsd = new XMLDocument(XMLDocumentTest.XSD);
        Assertions.assertDoesNotThrow(
            new Together<>(
                thread -> xml.validate(xsd)
            )::asList,
            "XMLDocument should not fail on validation in multiple threads with the same document and XSD"
        );
    }

    @RepeatedTest(60)
    void doesNotFailOnXsdValidationInMultipleThreadsWithTheSameDocumentAndDifferentXsd() {
        final XML xml = new XMLDocument("<root>passesValidXmlThrough</root>");
        Assertions.assertDoesNotThrow(
            new Together<>(
                thread -> xml.validate(new XMLDocument(XMLDocumentTest.XSD))
            )::asList,
            "XMLDocument should not fail on validation in multiple threads with the same document and XSD"
        );
    }

    @RepeatedTest(60)
    void doesNotFailOnXsdValidationInMultipleThreadsWithDifferentDocumentAndXsd() {
        Assertions.assertDoesNotThrow(
            new Together<>(
                thread -> {
                    final XML xml = new XMLDocument("<root>passesValidXmlThrough</root>");
                    final XML xsd = new XMLDocument(XMLDocumentTest.XSD);
                    return xml.validate(xsd);
                }
            )::asList,
            "XMLDocument should not fail on validation in multiple threads with different document and XSD"
        );
    }

    @RepeatedTest(60)
    void doesNotFailOnXsdValidationInMultipleThreadsWithDifferentDocumentAndTheSameXsd() {
        final XML xsd = new XMLDocument(XMLDocumentTest.XSD);
        Assertions.assertDoesNotThrow(
            new Together<>(
                thread -> {
                    final XML xml = new XMLDocument("<root>passesValidXmlThrough</root>");
                    return xml.validate(xsd);
                }
            )::asList,
            "XMLDocument should not fail on validation in multiple threads with different document and the same XSD"
        );
    }

    @Test
    void findsWithXpathAndNamespaces() {
        final XML doc = new XMLDocument(
            "<html xmlns='http://www.w3.org/1999/xhtml'><div>\u0443\u0440\u0430!</div></html>"
        );
        MatcherAssert.assertThat(
            doc.nodes("/xhtml:html/xhtml:div"),
            Matchers.hasSize(1)
        );
        MatcherAssert.assertThat(
            doc.nodes("//xhtml:div[.='\u0443\u0440\u0430!']"),
            Matchers.hasSize(1)
        );
    }

    @Test
    void findsWithXpathWithCustomNamespace() throws Exception {
        final File file = Files.createTempDirectory("")
            .resolve("x.xml").toFile();
        new LengthOf(
            new TeeInput(
                "<a xmlns='urn:foo'><b>\u0433!</b></a>",
                file
            )
        ).value();
        final XML doc = new XMLDocument(file).registerNs("f", "urn:foo");
        MatcherAssert.assertThat(
            doc.nodes("/f:a/f:b[.='\u0433!']"),
            Matchers.hasSize(1)
        );
        MatcherAssert.assertThat(
            doc.xpath("//f:b/text()").get(0),
            Matchers.equalTo("\u0433!")
        );
    }

    @Test
    void findsDocumentNodesWithXpathAndReturnsThem() throws Exception {
        final XML doc = new XMLDocument(
            new ByteArrayInputStream(
                "<root><a><x>1</x></a><a><x>2</x></a></root>".getBytes()
            )
        );
        MatcherAssert.assertThat(
            doc.nodes("//a"),
            Matchers.hasSize(2)
        );
        MatcherAssert.assertThat(
            doc.nodes("/root/a").get(0).xpath("x/text()").get(0),
            Matchers.equalTo("1")
        );
    }

    @Test
    void convertsItselfToXml() {
        final XML doc = new XMLDocument("<hello><a/></hello>");
        MatcherAssert.assertThat(
            doc.toString(),
            Matchers.hasToString(XhtmlMatchers.hasXPath("/hello/a"))
        );
    }

    @Test
    void printsWithNamespace() {
        MatcherAssert.assertThat(
            new XMLDocument(
                new XMLDocument(
                    "<z xmlns:a='hey'><f a:boom='test'/></z>"
                ).inner()
            ).toString(),
            Matchers.containsString("a:boom")
        );
    }

    @Test
    void retrievesDomNode() throws Exception {
        final XML doc = new XMLDocument(
            this.getClass().getResource("simple.xml")
        );
        MatcherAssert.assertThat(
            doc.nodes("/root/simple").get(0).inner().getNodeName(),
            Matchers.equalTo("simple")
        );
        MatcherAssert.assertThat(
            doc.nodes("//simple").get(0).inner().getNodeType(),
            Matchers.equalTo(Node.ELEMENT_NODE)
        );
    }

    @Test
    void throwsCustomExceptionWhenXpathNotFound() {
        try {
            new XMLDocument("<root/>").xpath("/absent-node/text()").get(0);
            MatcherAssert.assertThat("exception expected here", false);
        } catch (final IndexOutOfBoundsException ex) {
            MatcherAssert.assertThat(
                ex.getMessage(),
                Matchers.allOf(
                    Matchers.containsString("/absent-node/text("),
                    Matchers.containsString("<root/")
                )
            );
        }
    }

    @Test
    void throwsWhenXpathQueryIsBroken() {
        try {
            new XMLDocument("<root-99/>").xpath("/*/hello()");
            MatcherAssert.assertThat("exception expected", false);
        } catch (final IllegalArgumentException ex) {
            MatcherAssert.assertThat(
                ex.getMessage(),
                Matchers.containsString("XPathFactoryImpl")
            );
        }
    }

    @Test
    void preservesProcessingInstructions() {
        MatcherAssert.assertThat(
            new XMLDocument("<?xml version='1.0'?><?x test?><a/>"),
            Matchers.hasToString(Matchers.containsString("<?x test?>"))
        );
    }

    @Test
    void preservesDomStructureWhenXpath() {
        final XML doc = new XMLDocument(
            "<root><item1/><item2/><item3/></root>"
        );
        final XML item = doc.nodes("//root/item2").get(0);
        MatcherAssert.assertThat(
            item.nodes("..").get(0).xpath("name()").get(0),
            Matchers.equalTo("root")
        );
    }

    @Test
    void printsWithAndWithoutXmlHeader() {
        final XML doc = new XMLDocument("<hey/>");
        MatcherAssert.assertThat(
            doc,
            Matchers.hasToString(Matchers.startsWith("<?xml "))
        );
        MatcherAssert.assertThat(
            doc.nodes("/*").get(0),
            Matchers.hasToString(Matchers.startsWith("<hey"))
        );
    }

    @Test
    void parsesInMultipleThreads() throws Exception {
        final int timeout = 10;
        final int loop = 100;
        final Runnable runnable = () -> MatcherAssert.assertThat(
            new XMLDocument("<root><hey/></root>"),
            XhtmlMatchers.hasXPath("/root/hey")
        );
        final ExecutorService service = Executors.newFixedThreadPool(5);
        for (int count = 0; count < loop; count += 1) {
            service.submit(runnable);
        }
        service.shutdown();
        MatcherAssert.assertThat(
            service.awaitTermination(timeout, TimeUnit.SECONDS),
            Matchers.is(true)
        );
        service.shutdownNow();
    }

    @Test
    void xpathInMultipleThreads() throws Exception {
        final int timeout = 30;
        final int repeat = 1000;
        final int loop = 50;
        final XML xml = new XMLDocument(
            String.format(
                "<a><b>test text</b><c>%s</c></a>",
                StringUtils.repeat(
                    "<beta>some text \u20ac</beta> ",
                    repeat
                )
            )
        );
        final Runnable runnable = () -> {
            MatcherAssert.assertThat(
                xml.xpath("/a/b/text()").get(0),
                Matchers.equalTo("test text")
            );
            MatcherAssert.assertThat(
                xml.nodes("/a").get(0).nodes("c"),
                Matchers.iterableWithSize(1)
            );
        };
        final ExecutorService service = Executors.newFixedThreadPool(5);
        for (int count = 0; count < loop; count += 1) {
            service.submit(runnable);
        }
        service.shutdown();
        MatcherAssert.assertThat(
            service.awaitTermination(timeout, TimeUnit.SECONDS),
            Matchers.is(true)
        );
        service.shutdownNow();
    }

    @Test
    void printsInMultipleThreads() throws Exception {
        final int repeat = 1000;
        final int loop = 50;
        final XML xml = new XMLDocument(
            String.format(
                "<root><data>%s</data></root>",
                StringUtils.repeat(
                    "<alpha>some text \u20ac</alpha> ",
                    repeat
                )
            )
        );
        final AtomicInteger done = new AtomicInteger();
        final Runnable runnable = () -> {
            MatcherAssert.assertThat(
                xml.toString(),
                XhtmlMatchers.hasXPath("/root/data/alpha")
            );
            done.incrementAndGet();
        };
        final ExecutorService service = Executors.newFixedThreadPool(5);
        for (int count = 0; count < loop; count += 1) {
            service.submit(runnable);
        }
        service.shutdown();
        while (true) {
            if (done.get() == loop) {
                break;
            }
            if (service.awaitTermination(1L, TimeUnit.MILLISECONDS)) {
                break;
            }
        }
        MatcherAssert.assertThat(
            service.awaitTermination(1L, TimeUnit.SECONDS),
            Matchers.is(true)
        );
        service.shutdownNow();
    }

    @Test
    void performsXpathCalculations() {
        final XML xml = new XMLDocument("<x><a/><a/><a/></x>");
        MatcherAssert.assertThat(
            xml.xpath("count(//x/a)"),
            Matchers.iterableWithSize(1)
        );
        MatcherAssert.assertThat(
            xml.xpath("count(//a)").get(0),
            Matchers.equalTo("3")
        );
    }

    @Test
    void buildsDomNode() {
        final XML doc = new XMLDocument("<?xml version='1.0'?><f/>");
        MatcherAssert.assertThat(
            doc.inner(),
            Matchers.instanceOf(Document.class)
        );
        MatcherAssert.assertThat(
            doc.nodes("/f").get(0).inner(),
            Matchers.instanceOf(Element.class)
        );
    }

    @Test
    void comparesToAnotherDocument() {
        MatcherAssert.assertThat(
            new XMLDocument("<hi>\n<dude>  </dude></hi>"),
            Matchers.equalTo(new XMLDocument("<hi><dude>  </dude></hi>"))
        );
        MatcherAssert.assertThat(
            new XMLDocument("<hi><man></man></hi>"),
            Matchers.not(
                Matchers.equalTo(new XMLDocument("<hi><man>  </man></hi>"))
            )
        );
    }

    @Test
    @Disabled
    void comparesDocumentsWithDifferentIndentations() {
        // @checkstyle MethodBodyCommentsCheck (4 lines)
        // @todo #1:90min Implement comparison of XML documents with different indentations.
        //  The current implementation of XMLDocument does not ignore different indentations
        //  when comparing two XML documents. We need to implement a comparison that ignores
        //  different indentations. Don't forget to remove the @Disabled annotation from this test.
        MatcherAssert.assertThat(
            "Different indentations should be ignored",
            new XMLDocument("<program>\n <indentation/>\n</program>"),
            Matchers.equalTo(
                new XMLDocument("<program>\n  <indentation/>\n</program>\n")
            )
        );
    }

    @Test
    void preservesXmlNamespaces() {
        final String xml = "<a xmlns='http://www.w3.org/1999/xhtml'><b/></a>";
        MatcherAssert.assertThat(
            new XMLDocument(xml),
            XhtmlMatchers.hasXPath("/xhtml:a/xhtml:b")
        );
    }

    @Test
    void preservesImmutability() {
        final XML xml = new XMLDocument("<r1><a/></r1>");
        final Node node = xml.nodes("/r1/a").get(0).deepCopy();
        node.appendChild(node.getOwnerDocument().createElement("h9"));
        MatcherAssert.assertThat(
            xml,
            XhtmlMatchers.hasXPath("/r1/a[not(h9)]")
        );
    }

    @Test
    void appliesXpathToClonedNode() {
        final XML xml = new XMLDocument("<t6><z9 a='433'/></t6>");
        final XML root = xml.nodes("/t6").get(0);
        MatcherAssert.assertThat(
            root.xpath("//z9/@a").get(0),
            Matchers.equalTo("433")
        );
    }

    @Test
    void extractsNodesFromPom() throws Exception {
        final XML xml = new XMLDocument(new ResourceOf("com/jcabi/xml/small-pom.xml").stream());
        final List<XML> properties = xml
            .registerNs("ns1", "http://maven.apache.org/POM/4.0.0")
            .nodes("/ns1:project/ns1:properties/*");
        MatcherAssert.assertThat(
            new FormattedText(
                "%s should contain 2 property nodes\n but was %s\n in %s",
                xml,
                properties.size(),
                properties
            ).toString(),
            properties,
            Matchers.hasSize(2)
        );
    }

    @Test
    void stripsUnnecessaryWhiteSpacesWhileParsing() {
        MatcherAssert.assertThat(
            "Two XML documents are equal to each other",
            new XMLDocument("<x><y>hello</y></x>"),
            Matchers.equalTo(
                new XMLDocument(
                    "<x>  \n\n\n      <y>hello</y  >  \n    </x >"
                )
            )
        );
    }

    @Test
    void validatesXml() {
        final XML xsd = new XMLDocument(XMLDocumentTest.XSD);
        MatcherAssert.assertThat(
            new XMLDocument("<root/>").validate(xsd),
            Matchers.empty()
        );
        MatcherAssert.assertThat(
            new XMLDocument("<root></root>").validate(xsd),
            Matchers.empty()
        );
    }

    @Test
    void detectsSchemaViolations() {
        final Collection<SAXParseException> errors =
            new XMLDocument("<second/>").validate(new XMLDocument(XMLDocumentTest.XSD));
        MatcherAssert.assertThat(
            errors,
            Matchers.iterableWithSize(1)
        );
    }

    @Test
    void validatesAndDetectsTwice() {
        final XML xml = new XMLDocument("<second/>");
        final XML xsd = new XMLDocument(XMLDocumentTest.XSD);
        MatcherAssert.assertThat(
            xml.validate(xsd),
            Matchers.iterableWithSize(1)
        );
        MatcherAssert.assertThat(
            xml.validate(xsd),
            Matchers.iterableWithSize(1)
        );
    }

    @Test
    @SuppressWarnings({
        "PMD.AvoidInstantiatingObjectsInLoops",
        "PMD.InsufficientStringBufferDeclaration"
    })
    void validatesComplexXml() throws Exception {
        final int loopp = 5;
        final int size = 10_000;
        final int loop = 100;
        final int random = 10;
        final String xsd = StringUtils.join(
            "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'  >",
            "<xs:element name='root'>",
            "<xs:complexType><xs:sequence>",
            "<xs:element name='a' type='xs:string' maxOccurs='unbounded' />",
            "</xs:sequence></xs:complexType>",
            "</xs:element></xs:schema>"
        );
        final StringBuilder text = new StringBuilder(size)
            .append("<root>");
        for (int idx = 0; idx < loop; ++idx) {
            text.append("\n<a>\t&lt;&gt;&amp;&quot;&#09;&#x0A;")
                .append(RandomStringUtils.secure().nextAlphanumeric(random))
                .append("</a>\n\r \t    ");
        }
        text.append("</root>");
        final XML xml = new XMLDocument(text.toString());
        for (int idx = 0; idx < loopp; ++idx) {
            MatcherAssert.assertThat(
                xml.validate(new XMLDocument(xsd)),
                Matchers.empty()
            );
        }
    }

    @Test
    void validatesLongXml() throws Exception {
        final XML xsd = new XMLDocument(
            this.getClass().getResource("sample.xsd")
        );
        MatcherAssert.assertThat(
            new XMLDocument(
                StringUtils.join(
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
                    "<payment><id>333</id>",
                    "<date>1-Jan-2013</date>",
                    "<debit>test-1</debit>",
                    "<credit>test-2</credit>",
                    "</payment>"
                )
            ).validate(xsd),
            Matchers.empty()
        );
    }

    @Test
    void validatesMultipleXmlsInThreads() throws Exception {
        final int random = 100;
        final int loop = 10;
        final int timeout = 30;
        final Random rand = new SecureRandom();
        final XML xsd = new XMLDocument(
            StringUtils.join(
                "<xs:schema xmlns:xs ='http://www.w3.org/2001/XMLSchema' >",
                "<xs:element name='r'><xs:complexType>",
                "<xs:sequence>",
                "<xs:element name='x' type='xs:integer'",
                " minOccurs='0' maxOccurs='unbounded'/>",
                "</xs:sequence></xs:complexType></xs:element>",
                "</xs:schema>"
            )
        );
        // @checkstyle AnonInnerLengthCheck (50 lines)
        final Callable<Void> callable = () -> {
            final int cnt = rand.nextInt(random);
            MatcherAssert.assertThat(
                new XMLDocument(
                    StringUtils.join(
                        "<r>",
                        StringUtils.repeat("<x>hey</x>", cnt),
                        "</r>"
                    )
                ).validate(xsd),
                Matchers.hasSize(cnt << 1)
            );
            return null;
        };
        final ExecutorService service = Executors.newFixedThreadPool(5);
        for (int count = 0; count < loop; count += 1) {
            service.submit(callable);
        }
        service.shutdown();
        MatcherAssert.assertThat(
            service.awaitTermination(timeout, TimeUnit.SECONDS),
            Matchers.is(true)
        );
        service.shutdownNow();
    }

    /**
     * This test is disabled because it is a performance test that might be flaky.
     * @param temp Temporary directory.
     * @throws IOException If something goes wrong.
     */
    @RepeatedTest(10)
    @Disabled
    void createsXmlFromFile(@TempDir final Path temp) throws IOException {
        final Path xml = temp.resolve("test.xml");
        Files.write(xml, XMLDocumentTest.large().getBytes(StandardCharsets.UTF_8));
        final long clear = XMLDocumentTest.measure(
            () -> DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(xml.toFile())
                .getFirstChild()
                .getNodeName()
        );
        final long wrapped = XMLDocumentTest.measure(
            () -> new XMLDocument(xml.toFile()).inner().getFirstChild().getNodeName()
        );
        MatcherAssert.assertThat(
            String.format(
                "We expect that jcabi-xml is at max 2 times slower than default approach, time spend on jcabi-xml: %d ms, time spend on default approach: %d ms",
                wrapped,
                clear
            ),
            wrapped / clear,
            Matchers.lessThan(2L)
        );
    }

    /**
     * Measure the time of execution.
     * @param run The callable to run.
     * @return Time in milliseconds.
     * @checkstyle IllegalCatchCheck (20 lines)
     */
    @SuppressWarnings({"PMD.AvoidCatchingGenericException", "PMD.PrematureDeclaration"})
    private static long measure(final Callable<String> run) {
        final long start = System.nanoTime();
        if (!IntStream.range(0, 1000).mapToObj(
            each -> {
                try {
                    return run.call();
                } catch (final Exception exception) {
                    throw new IllegalStateException(
                        String.format("Failed to run %s", run), exception
                    );
                }
            }
        ).allMatch("root"::equals)) {
            throw new IllegalStateException("Invalid result");
        }
        return System.nanoTime() - start / 1_000_000;
    }

    /**
     * Generate large XML for tests.
     * @return Large XML string.
     */
    private static String large() {
        return IntStream.range(0, 100)
            .mapToObj(
                i -> StringUtils.join(
                    "<payment><id>333</id>",
                    "<date>1-Jan-2013</date>",
                    "<debit>test-1</debit>",
                    "<credit>test-2</credit>",
                    "</payment>"
                )
            ).collect(
                Collectors.joining(
                    "", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root>", "</root>"
                )
            );
    }
}
