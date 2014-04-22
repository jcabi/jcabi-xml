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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import lombok.EqualsAndHashCode;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Implementation of {@link XML}.
 *
 * <p>Objects of this class are immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.1
 * @checkstyle ClassDataAbstractionCoupling (500 lines)
 */
@Immutable
@EqualsAndHashCode(of = "xml")
@Loggable(Loggable.DEBUG)
@SuppressWarnings("PMD.ExcessiveImports")
public final class XMLDocument implements XML {

    /**
     * XPath factory.
     */
    private static final XPathFactory XFACTORY =
        XPathFactory.newInstance();

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
     * Namespace context to use for {@link #xpath(String)}
     * and {@link #nodes(String)} methods.
     */
    private final transient XPathContext context;

    /**
     * Encapsulated String representation of this XML document.
     */
    private final transient String xml;

    static {
        if (XMLDocument.DFACTORY.getClass().getName().contains("xerces")) {
            try {
                XMLDocument.DFACTORY.setFeature(
                    // @checkstyle LineLength (1 line)
                    "http://apache.org/xml/features/nonvalidating/load-external-dtd",
                    false
                );
            } catch (final ParserConfigurationException ex) {
                throw new IllegalStateException(ex);
            }
        }
        XMLDocument.DFACTORY.setNamespaceAware(true);
    }

    /**
     * Public ctor, from XML as a text.
     *
     * <p>The object is created with a default implementation of
     * {@link NamespaceContext}, which already defines a
     * number of namespaces, for convenience, including:
     *
     * <pre> xhtml: http://www.w3.org/1999/xhtml
     * xs: http://www.w3.org/2001/XMLSchema
     * xsi: http://www.w3.org/2001/XMLSchema-instance
     * xsl: http://www.w3.org/1999/XSL/Transform
     * svg: http://www.w3.org/2000/svg</pre>
     *
     * <p>In future versions we will add more namespaces (submit a ticket if
     * you need more of them defined here).
     *
     * <p>An {@link IllegalArgumentException} is thrown if the parameter
     * passed is not in XML format.
     *
     * @param text XML document body
     */
    public XMLDocument(@NotNull(message = "XML text can't be NULL")
        final String text) {
        this(
            new DomParser(XMLDocument.DFACTORY, text).document(),
            new XPathContext()
        );
    }

    /**
     * Public ctor, from a DOM node.
     *
     * <p>The object is created with a default implementation of
     * {@link NamespaceContext}, which already defines a
     * number of namespaces, see {@link XMLDocument#XMLDocument(String)}.
     *
     * @param node DOM source
     * @since 0.2
     */
    public XMLDocument(@NotNull(message = "node can't be NULL")
        final Node node) {
        this(node, new XPathContext());
    }

    /**
     * Public ctor, from a source.
     *
     * <p>The object is created with a default implementation of
     * {@link NamespaceContext}, which already defines a
     * number of namespaces, see {@link XMLDocument#XMLDocument(String)}.
     *
     * <p>An {@link IllegalArgumentException} is thrown if the parameter
     * passed is not in XML format.
     *
     * @param source Source of XML document
     */
    public XMLDocument(@NotNull(message = "source can't be NULL")
        final Source source) {
        this(XMLDocument.transform(source), new XPathContext());
    }

    /**
     * Public ctor, from XML in a file.
     *
     * <p>The object is created with a default implementation of
     * {@link NamespaceContext}, which already defines a
     * number of namespaces, see {@link XMLDocument#XMLDocument(String)}.
     *
     * <p>An {@link IllegalArgumentException} is thrown if the parameter
     * passed is not in XML format.
     *
     * @param file XML file
     * @throws IOException In case of I/O problems
     */
    public XMLDocument(@NotNull(message = "file can't be NULL")
        final File file) throws IOException {
        this(new TextResource(file).toString());
    }

    /**
     * Public ctor, from XML in the URL.
     *
     * <p>The object is created with a default implementation of
     * {@link NamespaceContext}, which already defines a
     * number of namespaces, see {@link XMLDocument#XMLDocument(String)}.
     *
     * <p>An {@link IllegalArgumentException} is thrown if the parameter
     * passed is not in XML format.
     *
     * @param url The URL to load from
     * @throws IOException In case of I/O problems
     */
    public XMLDocument(@NotNull(message = "URL can't be NULL")
        final URL url) throws IOException {
        this(new TextResource(url).toString());
    }

    /**
     * Public ctor, from XML in the URI.
     *
     * <p>The object is created with a default implementation of
     * {@link NamespaceContext}, which already defines a
     * number of namespaces, see {@link XMLDocument#XMLDocument(String)}.
     *
     * <p>An {@link IllegalArgumentException} is thrown if the parameter
     * passed is not in XML format.
     *
     * @param uri The URI to load from
     * @throws IOException In case of I/O problems
     */
    public XMLDocument(@NotNull(message = "URI can't be NULL")
        final URI uri) throws IOException {
        this(new TextResource(uri.toURL()).toString());
    }

    /**
     * Public ctor, from input stream.
     *
     * <p>The object is created with a default implementation of
     * {@link NamespaceContext}, which already defines a
     * number of namespaces, see {@link XMLDocument#XMLDocument(String)}.
     *
     * <p>An {@link IllegalArgumentException} is thrown if the parameter
     * passed is not in XML format.
     *
     * <p>The provided input stream will be closed automatically after
     * getting data from it.
     *
     * @param stream The input stream, which will be closed automatically
     * @throws IOException In case of I/O problem
     */
    public XMLDocument(@NotNull(message = "input stream can't be NULL")
        final InputStream stream) throws IOException {
        this(new TextResource(stream).toString());
        stream.close();
    }

    /**
     * Private ctor.
     * @param node The source
     * @param ctx Namespace context
     */
    private XMLDocument(final Node node, final XPathContext ctx) {
        this.xml = XMLDocument.asString(node);
        this.context = ctx;
    }

    @Override
    public String toString() {
        return this.xml;
    }

    @Override
    @NotNull(message = "node is never NULL")
    public Node node() {
        return new DomParser(XMLDocument.DFACTORY, this.xml).document()
            .getDocumentElement();
    }

    @Override
    @NotNull(message = "list of texts is never NULL")
    public List<String> xpath(@NotNull final String query) {
        final NodeList nodes = this.nodelist(query);
        final List<String> items = new ArrayList<String>(nodes.getLength());
        for (int idx = 0; idx < nodes.getLength(); ++idx) {
            final int type = nodes.item(idx).getNodeType();
            if (type != Node.TEXT_NODE && type != Node.ATTRIBUTE_NODE
                && type != Node.CDATA_SECTION_NODE) {
                throw new IllegalArgumentException(
                    String.format(
                        // @checkstyle LineLength (1 line)
                        "Only text() nodes or attributes are retrievable with xpath() '%s': %d",
                        query, type
                    )
                );
            }
            items.add(nodes.item(idx).getNodeValue());
        }
        return new ListWrapper<String>(items, this.node(), query);
    }

    @Override
    @NotNull(message = "XML is never NULL")
    public XML registerNs(@NotNull final String prefix,
        @NotNull final Object uri) {
        return new XMLDocument(this.node(), this.context.add(prefix, uri));
    }

    @Override
    @NotNull(message = "XML is never NULL")
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public List<XML> nodes(@NotNull final String query) {
        final NodeList nodes = this.nodelist(query);
        final List<XML> items = new ArrayList<XML>(nodes.getLength());
        for (int idx = 0; idx < nodes.getLength(); ++idx) {
            items.add(new XMLDocument(nodes.item(idx), this.context));
        }
        return new ListWrapper<XML>(items, this.node(), query);
    }

    @Override
    @NotNull(message = "XML is never NULL")
    public XML merge(@NotNull(message = "context can't be NULL")
        final NamespaceContext ctx) {
        return new XMLDocument(this.node(), this.context.merge(ctx));
    }

    /**
     * Retrieve and return a nodelist for XPath query.
     *
     * <p>An {@link IllegalArgumentException} is thrown if the parameter
     * passed is not a valid XPath expression.
     *
     * @param query XPath query
     * @return List of DOM nodes
     */
    private NodeList nodelist(final String query) {
        final NodeList nodes;
        try {
            final XPath xpath;
            synchronized (XMLDocument.class) {
                xpath = XMLDocument.XFACTORY.newXPath();
            }
            xpath.setNamespaceContext(this.context);
            nodes = (NodeList) xpath.evaluate(
                query, this.node(), XPathConstants.NODESET
            );
        } catch (final XPathExpressionException ex) {
            throw new IllegalArgumentException(
                String.format("invalid XPath query '%s'", query), ex
            );
        }
        return nodes;
    }

    /**
     * Transform node to String.
     *
     * @param node The DOM node.
     * @return String representation
     */
    private static String asString(final Node node) {
        final StringWriter writer = new StringWriter();
        try {
            final Transformer trans;
            synchronized (XMLDocument.class) {
                trans = XMLDocument.TFACTORY.newTransformer();
            }
            // @checkstyle MultipleStringLiterals (1 line)
            trans.setOutputProperty(OutputKeys.INDENT, "yes");
            trans.setOutputProperty(OutputKeys.VERSION, "1.0");
            if (!(node instanceof Document)) {
                trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            }
            synchronized (node) {
                trans.transform(
                    new DOMSource(node),
                    new StreamResult(writer)
                );
            }
        } catch (final TransformerConfigurationException ex) {
            throw new IllegalStateException(ex);
        } catch (final TransformerException ex) {
            throw new IllegalArgumentException(ex);
        }
        return writer.toString();
    }

    /**
     * Transform source to DOM node.
     * @param source The source
     * @return The node
     */
    private static Node transform(final Source source) {
        final DOMResult result = new DOMResult();
        try {
            final Transformer trans;
            synchronized (XMLDocument.class) {
                trans = XMLDocument.TFACTORY.newTransformer();
            }
            trans.transform(source, result);
        } catch (final TransformerConfigurationException ex) {
            throw new IllegalStateException(ex);
        } catch (final TransformerException ex) {
            throw new IllegalStateException(ex);
        }
        return result.getNode();
    }

}
