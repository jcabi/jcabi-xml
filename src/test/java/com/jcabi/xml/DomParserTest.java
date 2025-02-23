/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.xml;

import com.jcabi.matchers.XhtmlMatchers;
import javax.xml.parsers.DocumentBuilderFactory;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link DomParser}.
 * @since 0.1
 */
final class DomParserTest {

    @Test
    void parsesIncomingXmlDocument() {
        final String xml = "<a><b>\u0443\u0440\u0430!</b></a>";
        final DomParser parser = new DomParser(
            DocumentBuilderFactory.newInstance(), xml
        );
        MatcherAssert.assertThat(
            parser.document(),
            XhtmlMatchers.hasXPath("/a/b")
        );
    }

    @Test
    void parsesIncomingXmlDocumentComment() {
        final String xml = "<?xml version='1.0'?><!-- test --><root/>";
        final DomParser parser = new DomParser(
            DocumentBuilderFactory.newInstance(), xml
        );
        MatcherAssert.assertThat(
            parser.document(),
            XhtmlMatchers.hasXPath("/root")
        );
    }

    @Test
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    void allowsValidXmlFormatting() {
        final String[] texts = {
            "<?xml version=\"1.0\" encoding='ISO-8895-1'?><a/>",
            "<:a/>",
            "<ns:a><ns2:test-me/></ns:a>",
            "<_a/>",
            "<\u00c0a/>",
            "<something>\uFFFD</something>",
        };
        for (final String text : texts) {
            new DomParser(
                DocumentBuilderFactory.newInstance(),
                text
            );
        }
    }

}
