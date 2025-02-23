/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import lombok.EqualsAndHashCode;

/**
 * Sources in file system.
 *
 * If you have some resources in files, just configure your
 * XSL with this "sources":
 *
 * <pre> XSL xsl = new XSLDocument(input).with(
 *   new FileSources("/tmp/my-resources")
 * );</pre>
 *
 * @since 0.18
 */
@EqualsAndHashCode(of = "path")
public final class FileSources implements Sources {

    /**
     * Directory.
     */
    private final transient File path;

    /**
     * Public ctor.
     */
    public FileSources() {
        this("");
    }

    /**
     * Public ctor.
     * @param dir Directory
     */
    public FileSources(final String dir) {
        this(new File(dir));
    }

    /**
     * Public ctor.
     * @param dir Directory
     */
    public FileSources(final File dir) {
        this.path = dir;
    }

    @Override
    public Source resolve(final String href, final String base)
        throws TransformerException {
        File file = new File(this.path, href);
        if (!file.exists()) {
            if (base == null) {
                file = new File(href);
            } else {
                file = new File(new File(base), href);
            }
            if (!file.exists()) {
                throw new TransformerException(
                    String.format(
                        "File \"%s\" not found in \"%s\" and in base \"%s\"",
                        href, this.path, base
                    )
                );
            }
        }
        try {
            return new StreamSource(Files.newInputStream(file.toPath()));
        } catch (final IOException ex) {
            throw new TransformerException(
                String.format("Can't read from file '%s'", file),
                ex
            );
        }
    }
}
