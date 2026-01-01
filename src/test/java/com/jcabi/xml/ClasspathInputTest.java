/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.xml;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.w3c.dom.ls.LSInput;

/**
 * Test case for {@link ClasspathInput}.
 * @since 0.17.3
 */
final class ClasspathInputTest {
    @Test
    void readsStringFromResourceSuccessfully() {
        final LSInput input = new ClasspathInput(
            "Id", "com/jcabi/xml/simple.xml"
        );
        MatcherAssert.assertThat(
            "Input XML does not contains expected string, but it should",
            input.getStringData(),
            Matchers.containsString("<root>")
        );
    }
}
