/*
 * Copyright (c) 2012-2025, jcabi.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the jcabi.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jcabi.xml;

import com.jcabi.log.Logger;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Convenient parser of XML to DOM.
 *
 * <p>Objects of this class are immutable and thread-safe.
 *
 * @since 0.1
 */
@ToString
@EqualsAndHashCode
final class DomParser {

    /**
     * Document builder factory to use for parsing.
     */
    private final transient DocumentBuilderFactory factory;

    /**
     * Source of XML.
     */
    private final DocSource source;

    /**
     * Public ctor.
     *
     * <p>An {@link IllegalArgumentException} may be thrown if the parameter
     * passed is not in XML format. It doesn't perform a strict validation
     * and is not guaranteed that an exception will be thrown whenever
     * the parameter is not XML.
     *
     * <p>It is assumed that the text is in UTF-8.
     *
     * @param fct Document builder factory to use
     * @param txt The XML in text (in UTF-8)
     */
    DomParser(final DocumentBuilderFactory fct, final String txt) {
        this(fct, new BytesSource(txt));
    }

    /**
     * Public ctor.
     *
     * <p>An {@link IllegalArgumentException} may be thrown if the parameter
     * passed is not in XML format. It doesn't perform a strict validation
     * and is not guaranteed that an exception will be thrown whenever
     * the parameter is not XML.
     *
     * @param fct Document builder factory to use
     * @param bytes The XML in bytes
     */
    @SuppressWarnings("PMD.ArrayIsStoredDirectly")
    DomParser(final DocumentBuilderFactory fct, final byte[] bytes) {
        this(fct, new BytesSource(bytes));
    }

    /**
     * Public ctor.
     *
     * <p>An {@link IllegalArgumentException} may be thrown if the parameter
     * passed is not in XML format. It doesn't perform a strict validation
     * and is not guaranteed that an exception will be thrown whenever
     * the parameter is not XML.
     *
     * @param fct Document builder factory to use
     * @param file The XML as a file
     */
    DomParser(final DocumentBuilderFactory fct, final File file) {
        this(fct, new FileSource(file));
    }

    /**
     * Private ctor.
     * @param factory Document builder factory to use
     * @param source Source of XML
     */
    private DomParser(final DocumentBuilderFactory factory, final DocSource source) {
        this.factory = factory;
        this.source = source;
    }

    /**
     * Get the document body.
     * @return The document
     */
    public Document document() {
        final DocumentBuilder builder;
        try {
            builder = this.factory.newDocumentBuilder();
        } catch (final ParserConfigurationException ex) {
            throw new IllegalArgumentException(
                String.format(
                    "Failed to create document builder by %s",
                    this.factory.getClass().getName()
                ),
                ex
            );
        }
        final long start = System.nanoTime();
        final Document doc;
        try {
            doc = this.source.apply(builder);
        } catch (final IOException | SAXException ex) {
            throw new IllegalArgumentException(
                String.format(
                    "Can't parse by %s, most probably the XML is invalid",
                    builder.getClass().getName()
                ),
                ex
            );
        }
        if (Logger.isTraceEnabled(this)) {
            Logger.trace(
                this,
                "%s parsed %d bytes of XML in %[nano]s",
                builder.getClass().getName(),
                this.source.length(),
                System.nanoTime() - start
            );
        }
        return doc;
    }

    /**
     * Source of XML.
     * @since 0.32
     */
    private interface DocSource {

        /**
         * Parse XML by the builder.
         * @param builder The builder to use during parsing.
         * @return The document.
         * @throws IOException If fails.
         * @throws SAXException If fails.
         */
        Document apply(DocumentBuilder builder) throws IOException, SAXException;

        /**
         * The length of the source.
         * @return The length.
         */
        long length();
    }

    /**
     * File source of XML from a file.
     * @since 0.32
     */
    private static class FileSource implements DocSource {

        /**
         * The file.
         */
        private final File file;

        /**
         * Public ctor.
         * @param file The file.
         */
        FileSource(final File file) {
            this.file = file;
        }

        @Override
        public Document apply(final DocumentBuilder builder) throws IOException, SAXException {
            return builder.parse(this.file);
        }

        @Override
        public long length() {
            return this.file.length();
        }
    }

    /**
     * Bytes source of XML.
     * @since 0.32
     */
    private static class BytesSource implements DocSource {

        /**
         * Bytes of the XML.
         */
        private final byte[] xml;

        /**
         * Public ctor.
         * @param xml Bytes of the XML.
         */
        BytesSource(final String xml) {
            this(xml.getBytes(StandardCharsets.UTF_8));
        }

        /**
         * Public ctor.
         * @param xml Bytes of the XML.
         */
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
}
