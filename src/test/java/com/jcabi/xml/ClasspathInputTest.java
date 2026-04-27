/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.xml;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.w3c.dom.ls.LSInput;

/**
 * Test case for {@link ClasspathInput}.
 * @since 0.17.3
 */
final class ClasspathInputTest {
    /**
     * Path of an XML resource available on the classpath in tests.
     */
    private static final String RESOURCE = "com/jcabi/xml/simple.xml";

    @Test
    void readsStringFromResourceSuccessfully() {
        final LSInput input = new ClasspathInput(
            "Id", ClasspathInputTest.RESOURCE
        );
        MatcherAssert.assertThat(
            "Input XML does not contains expected string, but it should",
            input.getStringData(),
            Matchers.containsString("<root>")
        );
    }

    @Test
    void roundTripsBaseUri() {
        final LSInput input = new ClasspathInput(
            "Id", ClasspathInputTest.RESOURCE
        );
        input.setBaseURI("http://example.com/base");
        MatcherAssert.assertThat(
            "BaseURI set on the input should be returned by getBaseURI()",
            input.getBaseURI(),
            Matchers.equalTo("http://example.com/base")
        );
    }

    @Test
    void roundTripsEncoding() {
        final LSInput input = new ClasspathInput(
            "Id", ClasspathInputTest.RESOURCE
        );
        input.setEncoding("ISO-8859-1");
        MatcherAssert.assertThat(
            "Encoding set on the input should be returned by getEncoding()",
            input.getEncoding(),
            Matchers.equalTo("ISO-8859-1")
        );
    }

    @Test
    void roundTripsCertifiedText() {
        final LSInput input = new ClasspathInput(
            "Id", ClasspathInputTest.RESOURCE
        );
        input.setCertifiedText(true);
        MatcherAssert.assertThat(
            "CertifiedText set to true should be returned by getCertifiedText()",
            input.getCertifiedText(),
            Matchers.is(true)
        );
    }

    @Test
    void rejectsByteStreamMutation() {
        final LSInput input = new ClasspathInput(
            "Id", ClasspathInputTest.RESOURCE
        );
        Assertions.assertThrows(
            UnsupportedOperationException.class,
            () -> input.setByteStream(null),
            "Setting a byte stream on a classpath-resolved input is meaningless"
        );
    }

    @Test
    void rejectsCharacterStreamMutation() {
        final LSInput input = new ClasspathInput(
            "Id", ClasspathInputTest.RESOURCE
        );
        Assertions.assertThrows(
            UnsupportedOperationException.class,
            () -> input.setCharacterStream(null),
            "Setting a character stream on a classpath-resolved input is meaningless"
        );
    }

    @Test
    void rejectsStringDataMutation() {
        final LSInput input = new ClasspathInput(
            "Id", ClasspathInputTest.RESOURCE
        );
        Assertions.assertThrows(
            UnsupportedOperationException.class,
            () -> input.setStringData("anything"),
            "Setting string data on a classpath-resolved input is meaningless"
        );
    }
}
