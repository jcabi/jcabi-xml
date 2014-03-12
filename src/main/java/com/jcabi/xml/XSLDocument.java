/**
 * Copyright (c) 2012-2013, JCabi.com
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

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.log.Logger;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import javax.validation.constraints.NotNull;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import lombok.EqualsAndHashCode;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;

/**
 * Implementation of {@link XSL}.
 *
 * <p>Objects of this class are immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.4
 */
@Immutable
@EqualsAndHashCode(of = "xsl")
@Loggable(Loggable.DEBUG)
public final class XSLDocument implements XSL {

    /**
     * Transformer factory.
     */
    private static final TransformerFactory TFACTORY =
        TransformerFactory.newInstance();

    /**
     * DOM document builder factory.
     */
    private static final DocumentBuilderFactory DFACTORY =
        DocumentBuilderFactory.newInstance();

    /**
     * Encoding.
     */
    private static final String ENCODING = "UTF-8";

    /**
     * XSL document.
     */
    private final transient String xsl;

    /**
     * Public ctor, from XML as a source.
     * @param src XSL document body
     */
    public XSLDocument(@NotNull(message = "XML can't be NULL") final XML src) {
        this(src.toString());
    }

    /**
     * Public ctor, from XSL as a string.
     * @param src XML document body
     */
    public XSLDocument(@NotNull(message = "XSL can't be NULL")
        final String src) {
        this.xsl = src;
    }

    /**
     * Public ctor, from URL.
     * @param url Location of document
     * @throws IOException If fails to read
     * @since 0.7.4
     */
    public XSLDocument(@NotNull(message = "URL can't be NULL")
    final URL url) throws IOException {
        this(IOUtils.toString(url, ENCODING));
    }

    /**
     * Public ctor, from XSL as an input stream.
     * @param stream XSL input stream
     * @throws IOException If fails to read
     */
    public XSLDocument(@NotNull(message = "XSL input stream can't be NULL")
        final InputStream stream) throws IOException {
        this(IOUtils.toString(stream, ENCODING));
    }

    /**
     * Make an instance of XSL stylesheet without I/O exceptions.
     *
     * <p>This factory method is useful when you need to create
     * an instance of XSL stylesheet as a static final variable. In this
     * case you can't catch an exception but this method can help, for example:
     *
     * <pre> class Foo {
     *   private static final XSL STYLESHEET = XSLDocument.make(
     *     Foo.class.getResourceAsStream("my-stylesheet.xsl")
     *   );
     * }</pre>
     *
     * @param stream Input stream
     * @return XSL stylesheet
     */
    public static XSL make(@NotNull(message = "XSL input stream can't be NULL")
        final InputStream stream) {
        try {
            return new XSLDocument(stream);
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Make an instance of XSL stylesheet without I/O exceptions.
     * @param url URL with content
     * @return XSL stylesheet
     * @see #make(InputStream)
     * @since 0.7.4
     */
    public static XSL make(@NotNull(message = "URL can't be NULL")
        final URL url) {
        try {
            return new XSLDocument(url);
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public String toString() {
        return new XMLDocument(this.xsl).toString();
    }

    @Override
    @NotNull(message = "XML is never NULL")
    public XML transform(@NotNull(message = "XML can't be NULL")
        final XML xml) {
        final Transformer trans;
        try {
            synchronized (XSLDocument.class) {
                trans = XSLDocument.TFACTORY.newTransformer(
                    new StreamSource(new StringReader(this.xsl))
                );
            }
        } catch (final TransformerConfigurationException ex) {
            throw new IllegalStateException(ex);
        }
        final Document target;
        try {
            synchronized (XSLDocument.class) {
                target = XSLDocument.DFACTORY.newDocumentBuilder()
                    .newDocument();
            }
        } catch (final ParserConfigurationException ex) {
            throw new IllegalStateException(ex);
        }
        try {
            trans.transform(
                new DOMSource(xml.node()), new DOMResult(target)
            );
        } catch (final TransformerException ex) {
            throw new IllegalStateException(ex);
        }
        Logger.debug(
            this, "%s transformed XML",
            trans.getClass().getName()
        );
        return new XMLDocument(target);
    }

}
