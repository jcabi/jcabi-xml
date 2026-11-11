/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.xml;

import javax.xml.transform.TransformerException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test of ClasspathSources.
 *
 * @since 0.18
 */
final class ClasspathSourcesTest {

    @Test
    void sourcesResolvedFromBase() throws Exception {
        MatcherAssert.assertThat(
            "Resolved classpath source was not present, but it should be",
            new ClasspathSources().resolve("simple.xml", "com.jcabi.xml."),
            Matchers.notNullValue()
        );
    }

    @Test
    void resolvesIncludeBesideParentStylesheet() throws Exception {
        MatcherAssert.assertThat(
            "Include beside the parent stylesheet was not resolved, but it should be",
            new ClasspathSources("/absent/").resolve(
                "simple.xml", "/com/jcabi/xml/first.xsl"
            ),
            Matchers.notNullValue()
        );
    }

    @Test
    void failsWithoutBase() {
        Assertions.assertThrows(
            TransformerException.class,
            () -> new ClasspathSources("/absent/").resolve("simple.xml", null),
            "Null base was accepted, but it should not be"
        );
    }
}
