/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.xml;

import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Source of XML.
 * @since 0.32
 */
interface DocSource {

    /**
     * Parse XML by the builder.
     * @param builder The builder to use during parsing
     * @return The document
     * @throws IOException If fails.
     * @throws SAXException If fails.
     */
    Document apply(DocumentBuilder builder) throws IOException, SAXException;

    /**
     * The length of the source.
     * @return The length
     */
    long length();
}
