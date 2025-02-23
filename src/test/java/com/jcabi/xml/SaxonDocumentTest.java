/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.xml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test case for {@link SaxonDocument}.
 * @since 0.28
 */
final class SaxonDocumentTest {

    /**
     * Default XML content for tests.
     */
    private static final String DEFAULT_XML = "<o><o base='a'/></o>";

    /**
     * Default assertion message that will be used in many test assertions.
     */
    private static final String ASSERTION_MSG = "Can't create Saxon XML document from '%s'";

    @Test
    void createsFromFile(@TempDir final Path temp) throws IOException {
        final Path xml = SaxonDocumentTest.xmlFile(temp);
        final File file = xml.toFile();
        MatcherAssert.assertThat(
            String.format(SaxonDocumentTest.ASSERTION_MSG, file),
            new SaxonDocument(file).xpath("//o[@base]"),
            Matchers.hasSize(1)
        );
    }

    @Test
    void createsFromPath(@TempDir final Path temp) throws IOException {
        final Path xml = SaxonDocumentTest.xmlFile(temp);
        MatcherAssert.assertThat(
            String.format(SaxonDocumentTest.ASSERTION_MSG, xml),
            new SaxonDocument(xml).xpath("//o[@base]"),
            Matchers.hasSize(1)
        );
    }

    @Test
    void createsFromByteArray() {
        final Charset utf = StandardCharsets.UTF_8;
        final byte[] bytes = SaxonDocumentTest.DEFAULT_XML.getBytes(utf);
        MatcherAssert.assertThat(
            String.format(SaxonDocumentTest.ASSERTION_MSG, new String(bytes, utf)),
            new SaxonDocument(bytes).xpath("//o[@base]"),
            Matchers.hasSize(1)
        );
    }

    @Test
    void createsFromUrl() throws IOException {
        final URL resource = this.getClass().getResource("simple.xml");
        MatcherAssert.assertThat(
            String.format(SaxonDocumentTest.ASSERTION_MSG, resource),
            new SaxonDocument(resource).xpath("//simple"),
            Matchers.hasSize(1)
        );
    }

    @Test
    void createsFromUri() throws URISyntaxException, IOException {
        final URI resource = this.getClass().getResource("simple.xml").toURI();
        MatcherAssert.assertThat(
            String.format(SaxonDocumentTest.ASSERTION_MSG, resource),
            new SaxonDocument(resource).xpath("//simple"),
            Matchers.hasSize(1)
        );
    }

    @Test
    void createsFromStream() {
        MatcherAssert.assertThat(
            String.format(SaxonDocumentTest.ASSERTION_MSG, SaxonDocumentTest.DEFAULT_XML),
            new SaxonDocument(
                new ByteArrayInputStream(
                    SaxonDocumentTest.DEFAULT_XML.getBytes(StandardCharsets.UTF_8)
                )
            ).xpath("//o[@base]"),
            Matchers.hasSize(1)
        );
    }

    @Test
    void findsXpathWithConcatFunctionThatReturnsSeveralItems() {
        MatcherAssert.assertThat(
            "SaxonDocument can handle XPath 2.0 feature - XPath evaluation of concat method, but it can't",
            new SaxonDocument(
                "<o><o base='a' ver='1'/><o base='b' ver='2'/></o>"
            ).xpath("//o[@base and @ver]/concat(@base,'|',@ver)"),
            Matchers.hasItems("a|1", "b|2")
        );
    }

    @Test
    void findsXpathWithStringJoinFunctionThatReturnsSeveralItems() {
        MatcherAssert.assertThat(
            "SaxonDocument can handle XPath 2.0 feature - XPath evaluation of string-join method, but it can't",
            new SaxonDocument(
                "<o><o base='a'/><o base='b' ver='2'/><o base='c'/></o>"
            ).xpath("//o[@base]/string-join((@base,@ver),'|')"),
            Matchers.hasItems("a", "b|2", "c")
        );
    }

    /**
     * Creates XML file.
     * @param temp Temporary directory where file will be created
     * @return Path to created file
     * @throws IOException If something goes wrong
     */
    private static Path xmlFile(final Path temp) throws IOException {
        final Path xml = temp.resolve("test.xml");
        Files.write(xml, SaxonDocumentTest.DEFAULT_XML.getBytes(StandardCharsets.UTF_8));
        return xml;
    }
}
