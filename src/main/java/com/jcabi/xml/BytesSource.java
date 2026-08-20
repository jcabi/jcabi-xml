/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Bytes source of XML.
 * @since 0.32
 */
class BytesSource implements DocSource {

    /**
     * Bytes of the XML.
     */
    private final byte[] xml;

    /**
     * Public ctor.
     * @param xml Bytes of the XML
     */
    BytesSource(final String xml) {
        this(xml.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Public ctor.
     * @param xml Bytes of the XML
     */
    @SuppressWarnings("PMD.ArrayIsStoredDirectly")
    BytesSource(final byte[] xml) {
        this.xml = xml;
    }

    @Override
    public Document apply(final DocumentBuilder builder) throws IOException, SAXException {
        return builder.parse(new ByteArrayInputStream(this.xml));
    }

    @Override
    public long length() {
        return this.xml.length;
    }
}
