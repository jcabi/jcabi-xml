/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.xml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.cactoos.io.TeeInput;
import org.cactoos.scalar.LengthOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link TextResource}.
 *
 * @since 0.1
 */
final class TextResourceTest {

    @Test
    void readsStreamAsText() {
        final String text = "Blah!\u20ac\u2122";
        final InputStream stream = new ByteArrayInputStream(
            text.getBytes(StandardCharsets.UTF_8)
        );
        MatcherAssert.assertThat(
            new TextResource(stream).toString(),
            Matchers.is(text)
        );
    }

    @Test
    void readsFileAsText() throws Exception {
        final String text = "<a xmlns='urn:foo'><b>\u0433!</b></a>";
        final File file = Files.createTempDirectory("")
            .resolve("dummy.xml").toFile();
        new LengthOf(new TeeInput(text, file)).value();
        MatcherAssert.assertThat(
            new TextResource(file).toString(),
            Matchers.is(text)
        );
    }

}
