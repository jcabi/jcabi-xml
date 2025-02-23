/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.xml;

import java.io.InputStream;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import lombok.EqualsAndHashCode;

/**
 * Sources in classpath.
 * @since 0.9
 */
@EqualsAndHashCode(of = "prefix")
public final class ClasspathSources implements Sources {

    /**
     * Pattern to compose resource.
     */
    private static final String PATTERN = "%s%s";

    /**
     * Prefix.
     */
    private final transient String prefix;

    /**
     * Public ctor.
     */
    public ClasspathSources() {
        this("");
    }

    /**
     * Public ctor.
     * @param type Start with this type
     */
    public ClasspathSources(final Class<?> type) {
        this(
            String.format(
                "/%s/",
                type.getPackage().getName().replace(".", "/")
            )
        );
    }

    /**
     * Public ctor.
     * @param pfx Classpath prefix
     */
    public ClasspathSources(final String pfx) {
        this.prefix = pfx;
    }

    @Override
    public Source resolve(final String href, final String base)
        throws TransformerException {
        InputStream stream = this.getClass().getResourceAsStream(
            String.format(ClasspathSources.PATTERN, this.prefix, href)
        );
        if (stream == null) {
            stream = this.getClass().getResourceAsStream(
                String.format(ClasspathSources.PATTERN, base, href)
            );
            if (stream == null) {
                throw new TransformerException(
                    String.format(
                        "Resource \"%s\" not found in classpath with prefix \"%s\" and base \"%s\"",
                        href, this.prefix, base
                    )
                );
            }
        }
        return new StreamSource(stream);
    }
}
