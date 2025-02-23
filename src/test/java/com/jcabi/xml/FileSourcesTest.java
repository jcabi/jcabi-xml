/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.xml;

import java.io.File;
import java.nio.file.Files;
import org.cactoos.io.TeeInput;
import org.cactoos.scalar.LengthOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test of FileSources.
 * @since 0.18
 */
final class FileSourcesTest {

    @Test
    void sourcesResolvedFromDir() throws Exception {
        final File file = Files.createTempDirectory("")
            .resolve("dummy.xml").toFile();
        new LengthOf(new TeeInput("test", file)).value();
        MatcherAssert.assertThat(
            new FileSources().resolve(file.getAbsolutePath(), null),
            Matchers.notNullValue()
        );
    }
}
