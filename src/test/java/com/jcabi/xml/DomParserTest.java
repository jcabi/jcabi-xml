/*
 * Copyright (c) 2012-2022, jcabi.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the jcabi.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
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
