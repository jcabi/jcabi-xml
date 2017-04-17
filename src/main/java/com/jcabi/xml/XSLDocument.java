/**
 * Copyright (c) 2012-2017, jcabi.com
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

import com.jcabi.immutable.ArrayMap;
import com.jcabi.log.Logger;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import lombok.EqualsAndHashCode;
import org.w3c.dom.Document;

/**
 * Implementation of {@link XSL}.
 *
 * <p>Objects of this class are immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.4
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
@EqualsAndHashCode(of = "xsl")
@SuppressWarnings("PMD.TooManyMethods")
public final class XSLDocument implements XSL {

    /**
     * Strips spaces of whitespace-only text nodes.
     *
     * <p>This will NOT remove
     * existing indentation between Element nodes currently introduced by the
     * constructor of {@link com.jcabi.xml.XMLDocument}. For example:
     *
     * <pre>
     * {@code
     * &lt;a&gt;
     *           &lt;b> TXT &lt;/b>
     *    &lt;/a>}
     * </pre>
     *
     * becomes
     *
     * <pre>
     * {@code
     * &lt;a>
     *     &lt;b> TXT &lt;/b>
     * &lt;/a>}
     * </pre>
     *
     * @since 0.14
     */
    public static final XSL STRIP = XSLDocument.make(
        XSL.class.getResourceAsStream("strip.xsl")
    );

    /**
     * DOM document builder factory.
     */
    private static final DocumentBuilderFactory DFACTORY =
        DocumentBuilderFactory.newInstance();

    /**
     * Error listener.
     */
    private static ErrorListener ERRORS = new ErrorListener() {
        @Override
        public void warning(final TransformerException exception) {
            Logger.warn(this, exception.getMessageAndLocation());
        }
        @Override
        public void error(final TransformerException exception) {
            Logger.error(this, exception.getMessageAndLocation());
        }
        @Override
        public void fatalError(final TransformerException exception) {
            this.error(exception);
        }
    };

    /**
     * Setting up Custom Error Listener
     */
    public void SetErrorListener(ErrorListener errorListener){
        this.ERRORS = errorListener;
    }
    
    
    /**
     * XSL document.
     */
    private final transient String xsl;

    /**
     * Sources.
     */
    private final transient Sources sources;

    /**
     * Parameters.
     */
    private final transient ArrayMap<String, Object> params;

    /**
     * Public ctor, from XML as a source.
     * @param src XSL document body
     */
    public XSLDocument(final XML src) {
        this(src.toString());
    }

    /**
     * Public ctor, from URL.
     * @param url Location of document
     * @throws IOException If fails to read
     * @since 0.7.4
     */
    public XSLDocument(final URL url) throws IOException {
        this(new TextResource(url).toString());
    }

    /**
     * Public ctor, from URI.
     * @param uri Location of document
     * @throws IOException If fails to read
     * @since 0.15
     */
    public XSLDocument(final URI uri) throws IOException {
        this(new TextResource(uri).toString());
    }

    /**
     * Public ctor, from XSL as an input stream.
     * @param stream XSL input stream
     */
    public XSLDocument(final InputStream stream) {
        this(new TextResource(stream).toString());
    }

    /**
     * Public ctor, from XSL as a string.
     * @param src XML document body
     */
    public XSLDocument(final String src) {
        this(src, Sources.DUMMY);
    }

    /**
     * Public ctor, from XSL as a string.
     * @param src XML document body
     * @param srcs Sources
     * @since 0.9
     */
    public XSLDocument(final String src, final Sources srcs) {
        this(src, srcs, new ArrayMap<String, Object>());
    }

    /**
     * Public ctor, from XSL as a string.
     * @param src XML document body
     * @param srcs Sources
     * @param map Map of XSL params
     * @since 0.16
     */
    public XSLDocument(final String src, final Sources srcs,
        final Map<String, Object> map) {
        this.xsl = src;
        this.sources = srcs;
        this.params = new ArrayMap<String, Object>(map);
    }

    @Override
    public XSL with(final Sources src) {
        return new XSLDocument(this.xsl, src, this.params);
    }

    @Override
    public XSL with(final String name, final Object value) {
        return new XSLDocument(
            this.xsl, this.sources, this.params.with(name, value)
        );
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
    public static XSL make(final InputStream stream) {
        return new XSLDocument(stream);
    }

    /**
     * Make an instance of XSL stylesheet without I/O exceptions.
     * @param url URL with content
     * @return XSL stylesheet
     * @see #make(InputStream)
     * @since 0.7.4
     */
    public static XSL make(final URL url) {
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
    public XML transform(final XML xml) {
        final Document target;
        try {
            target = XSLDocument.DFACTORY.newDocumentBuilder()
                .newDocument();
            this.transformInto(
                xml, new DOMResult(target)
            );
        } catch (final ParserConfigurationException ex) {
            throw new IllegalStateException(ex);
        }
        return new XMLDocument(target);
    }

    @Override
    public String applyTo(final XML xml) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        this.transformInto(xml, new StreamResult(baos));
        try {
            return baos.toString("UTF-8");
        } catch (final UnsupportedEncodingException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Tranform XML into result.
     * @param xml XML
     * @param result Result
     * @since 0.11
     */
    private void transformInto(final XML xml, final Result result) {
        final Transformer trans;
        synchronized (XSLDocument.class) {
            final TransformerFactory factory =
                TransformerFactory.newInstance();
            try {
                factory.setErrorListener(XSLDocument.ERRORS);
                factory.setURIResolver(this.sources);
                trans = factory.newTransformer(
                    new StreamSource(new StringReader(this.xsl))
                );
                trans.setURIResolver(this.sources);
                for (final Map.Entry<String, Object> ent
                    : this.params.entrySet()) {
                    trans.setParameter(ent.getKey(), ent.getValue());
                }
                trans.transform(new DOMSource(xml.node()), result);
            } catch (final TransformerConfigurationException ex) {
                throw new IllegalStateException(
                    String.format(
                        "failed to configure transformer by %s",
                        factory.getClass().getName()
                    ),
                    ex
                );
            } catch (final TransformerException ex) {
                throw new IllegalStateException(
                    String.format(
                        "failed to transform by %s",
                        factory.getClass().getName()
                    ),
                    ex
                );
            }
        }
        Logger.debug(this, "%s transformed XML", trans.getClass().getName());
    }

}
