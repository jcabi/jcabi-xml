/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.xml;

import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import org.cactoos.io.ResourceOf;
import org.cactoos.text.TextOf;
import org.w3c.dom.ls.LSInput;

/**
 * {@link LSInput} implementation used by {@link ClasspathResolver}.
 *
 * @since 0.1
 */
final class ClasspathInput implements LSInput {

    /**
     * Public Id.
     */
    private transient String publicid;

    /**
     * System Id.
     */
    private transient String systemid;

    /**
     * Constructor.
     * @param pubid Public id
     * @param sysid System id
     */
    ClasspathInput(final String pubid, final String sysid) {
        this.publicid = pubid;
        this.systemid = sysid;
    }

    @Override
    public String getPublicId() {
        return this.publicid;
    }

    @Override
    public void setPublicId(final String pubid) {
        this.publicid = pubid;
    }

    @Override
    public String getSystemId() {
        return this.systemid;
    }

    @Override
    public void setSystemId(final String sysid) {
        this.systemid = sysid;
    }

    @Override
    public String getBaseURI() {
        return null;
    }

    @Override
    public InputStream getByteStream() {
        return null;
    }

    @Override
    @SuppressWarnings("PMD.BooleanGetMethodName")
    public boolean getCertifiedText() {
        return false;
    }

    @Override
    public Reader getCharacterStream() {
        return null;
    }

    @Override
    public String getEncoding() {
        return null;
    }

    @Override
    public String getStringData() {
        try {
            return new TextOf(
                new ResourceOf(
                    this.systemid,
                    path -> {
                        throw new IllegalArgumentException(
                            String.format(
                                "SystemID \"%s\" resource does not exist or can't be opened.",
                                path
                            )
                        );
                    }
                ),
                StandardCharsets.UTF_8
            ).asString();
            // @checkstyle IllegalCatchCheck (1 line)
        } catch (final Exception ex) {
            throw new IllegalArgumentException(
                String.format(
                    "Unable to read input stream of SystemID \"%s\"",
                    this.systemid
                ),
                ex
            );
        }
    }

    @Override
    public void setBaseURI(final String baseuri) {
        // intentionally empty
    }

    @Override
    public void setByteStream(final InputStream bytestream) {
        // intentionally empty
    }

    @Override
    public void setCertifiedText(final boolean certifiedtext) {
        // intentionally empty
    }

    @Override
    public void setCharacterStream(final Reader characterstream) {
        // intentionally empty
    }

    @Override
    public void setEncoding(final String encoding) {
        // intentionally empty
    }

    @Override
    public void setStringData(final String stringdata) {
        // intentionally empty
    }
}
