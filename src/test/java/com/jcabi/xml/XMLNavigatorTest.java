/*
 * Copyright (c) 2012-2025, jcabi.com
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

import java.util.stream.Stream;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Test case for {@link XMLNavigator}.
 * @since 0.33
 */
final class XMLNavigatorTest {

    @ParameterizedTest
    @MethodSource({"elementPaths", "attributePaths"})
    void retrievesTextFromElements(final Navigator navigator, final String expected) {
        MatcherAssert.assertThat(
            "We expect the text to be retrieved correctly",
            navigator.text().orElseThrow(
                () -> new IllegalStateException(
                    String.format("Text not found in navigator %s", navigator)
                )
            ).replaceAll(" ", "").trim(),
            Matchers.equalTo(expected)
        );
    }

    /**
     * Provide navigators to test.
     * This method provides a stream of arguments to the test method:
     * {@link #retrievesTextFromElements(Navigator, String)}.
     * @return Stream of arguments.
     */
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private static Stream<Arguments> elementPaths() {
        final XML xml = new XMLDocument(
            String.join(
                "\n",
                "<program><metas>",
                "  <meta>",
                "    <head>version</head>",
                "    <tail>1.2.3</tail>",
                "  </meta>",
                "</metas></program>"
            )
        );
        return Stream.of(
            Arguments.of(
                xml.navigate(),
                ""
            ),
            Arguments.of(
                xml.navigate()
                    .child("program")
                    .child("metas"),
                "version\n1.2.3"
            ),
            Arguments.of(
                xml.navigate()
                    .child("program")
                    .child("metas")
                    .child("meta"),
                "version\n1.2.3"
            ),
            Arguments.of(
                xml.navigate()
                    .child("program")
                    .child("metas")
                    .child("meta")
                    .child("head"),
                "version"
            ),
            Arguments.of(
                xml.navigate()
                    .child("program")
                    .child("metas")
                    .child("meta")
                    .child("tail"),
                "1.2.3"
            )
        );
    }

    /**
     * Provide navigators to test.
     * This method provides a stream of arguments to the test method:
     * {@link #retrievesTextFromElements(Navigator, String)}.
     * @return Stream of arguments.
     */
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private static Stream<Arguments> attributePaths() {
        final XML xml = new XMLDocument(
            String.join(
                "\n",
                "<program progattr='1'><metas>",
                "  <meta metarg='2'>",
                "    <head harg='3'>version</head>",
                "    <tail targ='4'>1.2.3</tail>",
                "  </meta></metas></program>"
            )
        );
        return Stream.of(
            Arguments.of(
                xml.navigate()
                    .child("program")
                    .attribute("progattr"),
                "1"
            ),
            Arguments.of(
                xml.navigate()
                    .child("program")
                    .child("metas")
                    .child("meta")
                    .attribute("metarg"),
                "2"
            ),
            Arguments.of(
                xml.navigate()
                    .child("program")
                    .child("metas")
                    .child("meta")
                    .child("head")
                    .attribute("harg"),
                "3"
            ),
            Arguments.of(
                xml.navigate()
                    .child("program")
                    .child("metas")
                    .child("meta")
                    .child("tail")
                    .attribute("targ"),
                "4"
            )
        );
    }
}
