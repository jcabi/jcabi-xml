/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.xml;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test of ClasspathSources.
 * @since 0.18
 */
final class ClasspathSourcesTest {

    @Test
    void sourcesResolvedFromBase() throws Exception {
        MatcherAssert.assertThat(
            new ClasspathSources().resolve("simple.xml", "com.jcabi.xml."),
            Matchers.notNullValue()
        );
    }
}
