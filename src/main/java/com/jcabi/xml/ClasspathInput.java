/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.xml;

import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
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
     * Base URI.
     */
    private transient String baseuri;

    /**
     * Encoding to use when reading the resource.
     */
    private transient String encoding;

    /**
     * Whether the input source is certified.
     */
    private transient boolean certified;

    /**
     * Constructor.
     * @param pubid Public id
     * @param sysid System id
     */
    ClasspathInput(final String pubid, final String sysid) {
        this(pubid, sysid, null, null);
    }

    /**
     * Constructor.
     * @param pubid Public id
     * @param sysid System id
     * @param enc Encoding to use when reading the resource
     * @param base Base URI
     * @checkstyle ParameterNumberCheck (3 lines)
     */
    ClasspathInput(final String pubid, final String sysid,
        final String enc, final String base) {
        this.publicid = pubid;
        this.systemid = sysid;
        this.encoding = enc;
        this.baseuri = base;
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
        return this.baseuri;
    }

    @Override
    public InputStream getByteStream() {
        return null;
    }

    @Override
    @SuppressWarnings("PMD.BooleanGetMethodName")
    public boolean getCertifiedText() {
        return this.certified;
    }

    @Override
    public Reader getCharacterStream() {
        return null;
    }

    @Override
    public String getEncoding() {
        return this.encoding;
    }

    @SuppressWarnings("PMD.AvoidCatchingGenericException")
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
                this.charset()
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
    public void setBaseURI(final String base) {
        this.baseuri = base;
    }

    @Override
    public void setByteStream(final InputStream bytestream) {
        throw new UnsupportedOperationException(
            "ClasspathInput resolves data from the classpath; setByteStream() is not supported"
        );
    }

    @Override
    public void setCertifiedText(final boolean text) {
        this.certified = text;
    }

    @Override
    public void setCharacterStream(final Reader stream) {
        throw new UnsupportedOperationException(
            "ClasspathInput resolves data from the classpath; setCharacterStream() is not supported"
        );
    }

    @Override
    public void setEncoding(final String enc) {
        this.encoding = enc;
    }

    @Override
    public void setStringData(final String data) {
        throw new UnsupportedOperationException(
            "ClasspathInput resolves data from the classpath; setStringData() is not supported"
        );
    }

    /**
     * Charset to use when reading the resource.
     * @return Charset, or UTF-8 if no encoding was set
     */
    private Charset charset() {
        final Charset cset;
        if (this.encoding == null) {
            cset = StandardCharsets.UTF_8;
        } else {
            cset = Charset.forName(this.encoding);
        }
        return cset;
    }
}
