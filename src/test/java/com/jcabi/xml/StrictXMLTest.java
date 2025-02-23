/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.xml;

import com.yegor256.OnlineMeans;
import com.yegor256.Together;
import com.yegor256.WeAreOnline;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test case for {@link StrictXML}.
 * @since 0.1
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 * @checkstyle AbbreviationAsWordInNameCheck (5 lines)
 */
@SuppressWarnings({ "PMD.TooManyMethods", "PMD.AvoidDuplicateLiterals"})
final class StrictXMLTest {
    @BeforeEach
    void weAreOnline() throws IOException {
        Assumptions.assumeTrue(
            InetAddress.getByName("w3.org").isReachable(1000)
        );
    }

    @Test
    void passesValidXmlThrough() {
        Assertions.assertDoesNotThrow(
            new StrictXML(
                new XMLDocument("<root>RootXML</root>"),
                new XMLDocument(
                    StringUtils.join(
                        "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>",
                        "<xs:element name='root' type='xs:string'/>",
                        "</xs:schema>"
                    )
                )
            )::inner,
            "XML should be validated without errors"
        );
    }

    @Test
    void rejectsInvalidXmlThrough() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            new StrictXML(
                new XMLDocument("<root>string</root>"),
                new XMLDocument(
                    StringUtils.join(
                        "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>",
                        "<xs:element name='root' type='xs:integer'/>",
                        "</xs:schema>"
                    )
                )
            )::inner,
            "An exception should have been thrown if XML does not match to XSD schema"
        );
    }

    @Test
    @ExtendWith(WeAreOnline.class)
    @OnlineMeans(url = "http://maven.apache.org")
    void passesValidXmlUsingXsiSchemaLocation() throws Exception {
        Assertions.assertDoesNotThrow(
            new StrictXML(
                new XMLDocument(
                    this.getClass().getResource("xsi-schemalocation-valid.xml")
                )
            )::inner,
            "XML with path to schema should be validated without errors"
        );
    }

    @Test
    @ExtendWith(WeAreOnline.class)
    @OnlineMeans(url = "http://maven.apache.org")
    void rejectsInvalidXmlUsingXsiSchemaLocation() {
        Assertions.assertThrows(
            IllegalStateException.class,
            () -> new StrictXML(
                new XMLDocument(
                    this.getClass().getResource("xsi-schemalocation-invalid.xml")
                )
            ).inner(),
            "An exception should have been thrown if schema location is invalid"
        );
    }

    @Test
    void printsInnerXmlToString() {
        final String xml = "<root>just</root>";
        MatcherAssert.assertThat(
            "StrictXML must print inner XML",
            new StrictXML(
                new XMLDocument(xml),
                new XMLDocument(
                    StringUtils.join(
                        "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>",
                        "<xs:element name='root' type='xs:string'/>",
                        "</xs:schema>"
                    )
                )
            ).toString(),
            Matchers.containsString(xml)
        );
    }

    @RepeatedTest(60)
    void doesNotFailOnFetchingInMultipleThreadsFromTheSameDocument() {
        final XML xml = new StrictXML(
            new XMLDocument("<root>RootXML</root>"),
            new XMLDocument(
                StringUtils.join(
                    "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>",
                    "<xs:element name='root' type='xs:string'/>",
                    "</xs:schema>"
                )
            )
        );
        Assertions.assertDoesNotThrow(
            new Together<>(
                thread -> xml.nodes("/root")
            )::asList,
            "StrictXML must not fail on fetching in multiple threads from the same document"
        );
    }

    @RepeatedTest(60)
    void doesNotFailOnFetchingInMultipleThreadsFromDifferentDocuments() {
        Assertions.assertDoesNotThrow(
            new Together<>(
                thread -> {
                    final XML xml = new StrictXML(
                        new XMLDocument("<root>RootXML</root>"),
                        new XMLDocument(
                            StringUtils.join(
                                "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>",
                                "<xs:element name='root' type='xs:string'/>",
                                "</xs:schema>"
                            )
                        )
                    );
                    return xml.nodes("/root");
                }
            )::asList,
            "StrictXML must not fail on fetching in multiple threads from different document"
        );
    }

    @RepeatedTest(60)
    void doesNotFailOnFetchingInMultipleThreadsFromSameDocumentWithClasspathResolver() {
        final Path xsd = Paths.get(
            "src/test/resources/com/jcabi/xml/root.xsd"
        ).toAbsolutePath();
        final XML xml = new XMLDocument(
            StringUtils.join(
                "<root",
                " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"",
                " xsi:noNamespaceSchemaLocation=\"file:///",
                xsd.toString(),
                "\">just</root>"
            )
        );
        Assertions.assertDoesNotThrow(
            new Together<>(
                thread -> new StrictXML(xml).inner()
            )::asList
        );
    }

    @Test
    void lookupXsdsFromClasspath() {
        Assertions.assertDoesNotThrow(
            new StrictXML(
                new XMLDocument(
                    StringUtils.join(
                        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
                        "<payment xmlns=\"http://jcabi.com/schema/xml\" ",
                        "xmlns:xsi=\"",
                        "http://www.w3.org/2001/XMLSchema-instance",
                        "\" ",
                        "xsi:schemaLocation=\"",
                        "http://jcabi.com/schema/xml ",
                        "com/jcabi/xml/sample-namespaces.xsd",
                        "\">",
                        "<id>333</id>",
                        "<date>1-Jan-2013</date>",
                        "<debit>test-1</debit>",
                        "<credit>test-2</credit>",
                        "</payment>"
                    )
                )
            )::inner,
            "StrictXML should not have failed on validation via classpath"
        );
    }

    @Test
    void rejectXmlWhenXsdIsNotAvailableOnClasspath() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            new StrictXML(
                new XMLDocument(
                    StringUtils.join(
                        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
                        "<payment xmlns=\"http://jcabi.com/schema/xml\" ",
                        "xmlns:xsi=\"",
                        "http://www.w3.org/2001/XMLSchema-instance",
                        "\" ",
                        "xsi:schemaLocation=\"",
                        "http://jcabi.com/schema/xml ",
                        "sample-non-existing.xsd",
                        "\">",
                        "<id>333</id>",
                        "<date>1-Jan-2013</date>",
                        "<debit>test-1</debit>",
                        "<credit>test-2</credit>",
                        "</payment>"
                    )
                )
            )::inner,
            "An exception should have been thrown if XSD is not available on classpath"
        );
    }

    @Test
    void handlesXmlWithoutSchemaLocation() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            new StrictXML(
                new XMLDocument("<a></a>")
            )::inner,
            "An exception should have been thrown if XML does not contain a schema location"
        );
    }
}
