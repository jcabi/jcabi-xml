/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.xml;

import com.jcabi.log.Logger;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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
import net.sf.saxon.Version;
import net.sf.saxon.jaxp.TransformerImpl;
import net.sf.saxon.serialize.MessageWarner;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;
import org.w3c.dom.Document;

/**
 * Implementation of {@link XSL}.
 *
 * <p>Objects of this class are immutable and thread-safe.
 *
 * @since 0.4
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 * @checkstyle AbbreviationAsWordInNameCheck (5 lines)
 * @checkstyle ClassFanOutComplexityCheck (500 lines)
 */
@EqualsAndHashCode(of = "xsl")
@SuppressWarnings({"PMD.TooManyMethods", "PMD.ExcessiveImports"})
public final class XSLDocument implements XSL {

    /**
     * Strips spaces of whitespace-only text nodes.
     *
     * <p>This will NOT remove
     * existing indentation between Element nodes currently introduced by the
     * constructor of {@link XMLDocument}. For example:
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
    private final transient Map<String, Object> params;

    /**
     * System ID (base).
     * @since 0.20
     */
    private final transient String sid;

    /**
     * Public ctor, from XML as a source.
     * @param src XSL document body
     */
    public XSLDocument(final XML src) {
        this(src, "/");
    }

    /**
     * Public ctor, from XML as a source.
     * @param src XSL document body
     * @param base SystemId/Base
     * @since 0.20
     */
    public XSLDocument(final XML src, final String base) {
        this(src.toString(), base);
    }

    /**
     * Public ctor, from URL.
     * @param url Location of document
     * @throws IOException If fails to read
     * @since 0.7.4
     */
    public XSLDocument(final URL url) throws IOException {
        this(url, url.toString());
    }

    /**
     * Public ctor, from URL with alternative SystemId.
     * @param url Location of document
     * @param base SystemId/Base
     * @throws IOException If fails to read
     * @since 0.26.0
     */
    public XSLDocument(final URL url, final String base) throws IOException {
        this(new TextResource(url).toString(), base);
    }

    /**
     * Public ctor, from file.
     * @param file Location of document
     * @throws FileNotFoundException If fails to read
     * @since 0.21
     */
    public XSLDocument(final File file) throws FileNotFoundException {
        this(file, file.getAbsolutePath());
    }

    /**
     * Public ctor, from file  with alternative SystemId.
     * @param file Location of document
     * @param base SystemId/Base
     * @throws FileNotFoundException If fails to read
     * @since 0.26.0
     */
    public XSLDocument(final File file, final String base)
        throws FileNotFoundException {
        this(new TextResource(file).toString(), base);
    }

    /**
     * Public ctor, from file.
     * @param file Location of document
     * @throws FileNotFoundException If fails to read
     * @since 0.21
     */
    public XSLDocument(final Path file) throws FileNotFoundException {
        this(file.toFile());
    }

    /**
     * Public ctor, from file with custom SystemId.
     * @param file Location of document
     * @param base SystemId/Base
     * @throws FileNotFoundException If fails to read
     * @since 0.26.0
     */
    public XSLDocument(final Path file, final String base)
        throws FileNotFoundException {
        this(file.toFile(), base);
    }

    /**
     * Public ctor, from URI.
     * @param uri Location of document
     * @throws IOException If fails to read
     * @since 0.15
     */
    public XSLDocument(final URI uri) throws IOException {
        this(uri, uri.toString());
    }

    /**
     * Public ctor, from URI.
     * @param uri Location of document
     * @param base SystemId/Base
     * @throws IOException If fails to read
     * @since 0.26.0
     */
    public XSLDocument(final URI uri, final String base) throws IOException {
        this(new TextResource(uri).toString(), base);
    }

    /**
     * Public ctor, from XSL as an input stream.
     * @param stream XSL input stream
     */
    public XSLDocument(final InputStream stream) {
        this(new TextResource(stream).toString());
    }

    /**
     * Public ctor, from XSL as an input stream.
     * @param stream XSL input stream
     * @param base SystemId/Base
     * @since 0.20
     */
    public XSLDocument(final InputStream stream, final String base) {
        this(new TextResource(stream).toString(), base);
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
     * @param base SystemId/Base
     * @since 0.20
     */
    public XSLDocument(final String src, final String base) {
        this(src, Sources.DUMMY, base);
    }

    /**
     * Public ctor, from XSL as a string.
     * @param src XML document body
     * @param srcs Sources
     * @since 0.9
     */
    public XSLDocument(final String src, final Sources srcs) {
        this(src, srcs, new HashMap<>(0));
    }

    /**
     * Public ctor, from XSL as a string.
     * @param src XML document body
     * @param srcs Sources
     * @param base SystemId/Base
     * @since 0.20
     */
    public XSLDocument(final String src, final Sources srcs,
        final String base) {
        this(src, srcs, new HashMap<>(0), base);
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
        this(src, srcs, map, "/");
    }

    /**
     * Public ctor, from XSL as a string.
     * @param src XML document body
     * @param srcs Sources
     * @param map Map of XSL params
     * @param base SystemId/Base
     * @since 0.20
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public XSLDocument(final String src, final Sources srcs,
        final Map<String, Object> map, final String base) {
        this.xsl = src;
        this.sources = srcs;
        this.params = new HashMap<>(map);
        this.sid = base;
    }

    @Override
    public XSL with(final Sources src) {
        return new XSLDocument(this.xsl, src, this.params, this.sid);
    }

    @Override
    public XSL with(final String name, final Object value) {
        return new XSLDocument(
            this.xsl, this.sources,
            new MapOf<String, Object>(this.params, new MapEntry<>(name, value)),
            this.sid
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
    @SuppressWarnings("PMD.ProhibitPublicStaticMethods")
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
    @SuppressWarnings("PMD.ProhibitPublicStaticMethods")
    public static XSL make(final URL url) {
        try {
            return new XSLDocument(url, url.toString());
        } catch (final IOException ex) {
            throw new IllegalStateException(
                String.format(
                    "Failed to read from URL '%s'",
                    url
                ),
                ex
            );
        }
    }

    @Override
    public String toString() {
        return new XMLDocument(this.xsl).toString();
    }

    @Override
    public XML transform(final XML xml) {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
        } catch (final ParserConfigurationException ex) {
            throw new IllegalArgumentException(
                String.format(
                    "Failed to create new XML document by %s",
                    factory.getClass().getName()
                ),
                ex
            );
        }
        final Document target = builder.newDocument();
        this.transformInto(xml, new DOMResult(target));
        return new XMLDocument(target);
    }

    @Override
    public String applyTo(final XML xml) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        this.transformInto(xml, new StreamResult(baos));
        try {
            return baos.toString(StandardCharsets.UTF_8.name());
        } catch (final UnsupportedEncodingException ex) {
            throw new IllegalArgumentException(
                "Failed to convert bytes into UTF-8 string",
                ex
            );
        }
    }

    /**
     * Transform XML into result.
     *
     * We create {@link TransformerFactory} here on every transformation
     * because {@link javax.xml.transform.URIResolver} must be set into
     * it before making an instance of a transformer. Otherwise, it won't
     * understand "xsl:import" statements.
     *
     * @param xml XML
     * @param result Result
     * @since 0.11
     * @link <a href="https://stackoverflow.com/questions/4695489">Relevant SO question</a>
     */
    private void transformInto(final XML xml, final Result result) {
        final Transformer trans = this.transformer();
        final ConsoleErrorListener errors = new ConsoleErrorListener();
        trans.setErrorListener(errors);
        final long start = System.nanoTime();
        try {
            trans.transform(new DOMSource(xml.inner()), result);
        } catch (final TransformerException ex) {
            final StringBuilder summary = new StringBuilder(
                String.join("; ", errors.summary())
            );
            if (!summary.toString().equals(ex.getMessageAndLocation())) {
                summary.append("; ").append(ex.getMessageAndLocation());
            }
            throw new IllegalArgumentException(
                String.format(
                    "Failed to transform by %s: %s",
                    trans.getClass().getName(),
                    summary
                ),
                ex
            );
        }
        if (Logger.isTraceEnabled(this)) {
            Logger.trace(
                this,
                "%s transformed XML in %[nano]s",
                trans.getClass().getName(),
                System.nanoTime() - start
            );
        }
    }

    /**
     * Make a transformer.
     * @return The transformer
     */
    private Transformer transformer() {
        final TransformerFactory factory = TransformerFactory.newInstance();
        factory.setURIResolver(this.sources);
        final Transformer trans;
        try {
            trans = factory.newTransformer(
                new StreamSource(new StringReader(this.xsl), this.sid)
            );
        } catch (final TransformerConfigurationException ex) {
            throw new IllegalArgumentException(
                String.format(
                    "Failed to create transformer by %s",
                    factory.getClass().getName()
                ),
                ex
            );
        }
        for (final Map.Entry<String, Object> ent : this.params.entrySet()) {
            trans.setParameter(ent.getKey(), ent.getValue());
        }
        return trans;
    }

    /**
     * Prepare it for Saxon.
     * @param trans The transformer
     * @return The same
     * @checkstyle ReturnCountCheck (5 lines)
     */
    @SuppressWarnings("deprecation")
    private static Transformer forSaxon(final Transformer trans) {
        final String type = trans.getClass().getCanonicalName();
        if (!"net.sf.saxon.jaxp.TransformerImpl".equals(type)) {
            return trans;
        }
        if (Version.getStructuredVersionNumber()[0] < 11) {
            ((TransformerImpl) trans)
                .getUnderlyingController()
                .setMessageEmitter(new MessageWarner());
        }
        if (Version.getStructuredVersionNumber()[0] >= 11) {
            ((TransformerImpl) trans)
                .getUnderlyingController()
                .setMessageHandler(
                    message -> Logger.error(
                        XSLDocument.class,
                        "%s: %s",
                        message.getLocation(),
                        message.toString()
                    )
                );
        }
        return trans;
    }

}
