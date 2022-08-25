/*
 * Copyright (c) 2012-2022, jcabi.com
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
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
 * @since 0.1
 * @checkstyle ClassFanOutComplexity (500 lines)
 * @checkstyle AbbreviationAsWordInNameCheck (10 lines)
 */
@EqualsAndHashCode(of = { "xml", "leaf" })
@SuppressWarnings
    (
        {
            "PMD.ExcessiveImports",
            "PMD.OnlyOneConstructorShouldDoInitialization"
        }
    )
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

    /**
     * Is it a leaf node (Element, not a Document)?
     */
    private final transient boolean leaf;

    /**
     * Actual XML document node.
     */
    private final transient Object cache;

    static {
        if (XMLDocument.DFACTORY.getClass().getName().contains("xerces")) {
            try {
                XMLDocument.DFACTORY.setFeature(
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
     * @param text XML document body
     */
    public XMLDocument(final String text) {
        this(
            new DomParser(XMLDocument.DFACTORY, text).document(),
            new XPathContext(),
            false
        );
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
     * @param data The XML body
     */
    public XMLDocument(final byte[] data) {
        this(
            new DomParser(XMLDocument.DFACTORY, data).document(),
            new XPathContext(),
            false
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
    public XMLDocument(final Node node) {
        this(node, new XPathContext(), !(node instanceof Document));
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
    public XMLDocument(final Source source) {
        this(XMLDocument.transform(source), new XPathContext(), false);
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
     * @throws FileNotFoundException In case of I/O problems
     */
    public XMLDocument(final File file) throws FileNotFoundException {
        this(new TextResource(file).toString());
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
     * @throws FileNotFoundException In case of I/O problems
     */
    public XMLDocument(final Path file) throws FileNotFoundException {
        this(file.toFile());
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
    public XMLDocument(final URL url) throws IOException {
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
    public XMLDocument(final URI uri) throws IOException {
        this(new TextResource(uri).toString());
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
    @SuppressWarnings("PMD.ConstructorOnlyInitializesOrCallOtherConstructors")
    public XMLDocument(final InputStream stream) throws IOException {
        this(new TextResource(stream).toString());
        stream.close();
    }

    /**
     * Private ctor.
     * @param node The source
     * @param ctx Namespace context
     * @param lfe Is it a leaf node?
     */
    private XMLDocument(final Node node, final XPathContext ctx,
        final boolean lfe) {
        this.xml = XMLDocument.asString(node);
        this.context = ctx;
        this.leaf = lfe;
        this.cache = node;
    }

    @Override
    public String toString() {
        return this.xml;
    }

    @Override
    public Node node() {
        final Node casted = Node.class.cast(this.cache);
        final Node answer;
        if (casted instanceof Document) {
            answer = casted.cloneNode(true);
        } else {
            answer = XMLDocument.createImportedNode(casted);
        }
        return answer;
    }

    @Override
    @SuppressWarnings
        (
            {
                "PMD.ExceptionAsFlowControl",
                "PMD.PreserveStackTrace"
            }
        )
    public List<String> xpath(final String query) {
        // @checkstyle FinalLocalVariableCheck (1 line)
        List<String> items;
        try {
            final NodeList nodes = this.fetch(query, NodeList.class);
            items = new ArrayList<>(nodes.getLength());
            for (int idx = 0; idx < nodes.getLength(); ++idx) {
                final int type = (int) nodes.item(idx).getNodeType();
                if (type != (int) Node.TEXT_NODE
                    && type != (int) Node.ATTRIBUTE_NODE
                    && type != (int) Node.CDATA_SECTION_NODE) {
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
        } catch (final XPathExpressionException ex) {
            try {
                items = Collections.singletonList(
                    this.fetch(query, String.class)
                );
            } catch (final XPathExpressionException exp) {
                throw new IllegalArgumentException(
                    String.format(
                        "Invalid XPath query '%s' at %s: %s",
                        query, XMLDocument.XFACTORY.getClass().getName(),
                        ex.getLocalizedMessage()
                    ),
                    exp
                );
            }
        }
        return new ListWrapper<>(items, Node.class.cast(this.cache), query);
    }

    @Override
    public XML registerNs(final String prefix, final Object uri) {
        return new XMLDocument(
            Node.class.cast(this.cache),
            this.context.add(prefix, uri),
            this.leaf
        );
    }

    @Override
    public List<XML> nodes(final String query) {
        final List<XML> items;
        try {
            final NodeList nodes = this.fetch(query, NodeList.class);
            items = new ArrayList<>(nodes.getLength());
            for (int idx = 0; idx < nodes.getLength(); ++idx) {
                items.add(
                    new XMLDocument(
                        nodes.item(idx),
                        this.context, true
                    )
                );
            }
        } catch (final XPathExpressionException ex) {
            throw new IllegalArgumentException(
                String.format(
                    "Invalid XPath query '%s' by %s",
                    query, XMLDocument.XFACTORY.getClass().getName()
                ), ex
            );
        }
        return new ListWrapper<>(items, Node.class.cast(this.cache), query);
    }

    @Override
    public XML merge(final NamespaceContext ctx) {
        return new XMLDocument(
            Node.class.cast(this.cache),
            this.context.merge(ctx),
            this.leaf
        );
    }

    /**
     * Clones a node and imports it in a new document.
     * @param node A node to clone.
     * @return A cloned node imported in a dedicated document.
     */
    private static Node createImportedNode(final Node node) {
        final Document document;
        try {
            document = XMLDocument.DFACTORY.newDocumentBuilder().newDocument();
        } catch (final ParserConfigurationException ex) {
            throw new IllegalStateException(ex);
        }
        final Node imported = document.importNode(node, true);
        document.appendChild(imported);
        return imported;
    }

    /**
     * Retrieve XPath query result. Supports returning {@link NodeList} and
     * {@link String} types.
     *
     * <p>An {@link IllegalArgumentException} is thrown if the parameter
     * passed is not a valid XPath expression or an unsupported type is
     * specified.
     *
     * @param query XPath query
     * @param type The return type
     * @param <T> The type to return
     * @return Result of XPath query
     * @throws XPathExpressionException If an error occurs when evaluating XPath
     */
    @SuppressWarnings("unchecked")
    private <T> T fetch(final String query, final Class<T> type)
        throws XPathExpressionException {
        final XPath xpath;
        synchronized (XMLDocument.class) {
            xpath = XMLDocument.XFACTORY.newXPath();
        }
        xpath.setNamespaceContext(this.context);
        final QName qname;
        if (type.equals(String.class)) {
            qname = XPathConstants.STRING;
        } else if (type.equals(NodeList.class)) {
            qname = XPathConstants.NODESET;
        } else {
            throw new IllegalArgumentException(
                String.format(
                    "Unsupported type: %s",
                    type.getName()
                )
            );
        }
        return (T) xpath.evaluate(query, Node.class.cast(this.cache), qname);
    }

    /**
     * Transform node to String.
     *
     * @param node The DOM node.
     * @return String representation
     */
    private static String asString(final Node node) {
        final StringWriter writer = new StringWriter();
        final Transformer trans;
        try {
            synchronized (XMLDocument.class) {
                trans = XMLDocument.TFACTORY.newTransformer();
            }
        } catch (final TransformerConfigurationException ex) {
            throw new IllegalArgumentException(
                String.format(
                    "Failed to create transformer by %s",
                    XMLDocument.TFACTORY.getClass().getName()
                ),
                ex
            );
        }
        trans.setOutputProperty(OutputKeys.INDENT, "yes");
        trans.setOutputProperty(OutputKeys.VERSION, "1.0");
        if (!(node instanceof Document)) {
            trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        }
        final Source source = new DOMSource(node);
        final Result result = new StreamResult(writer);
        try {
            synchronized (node) {
                trans.transform(source, result);
            }
        } catch (final TransformerException ex) {
            throw new IllegalArgumentException(
                String.format(
                    "Failed to transform %s to %s",
                    source.getClass().getName(),
                    result.getClass().getName()
                ),
                ex
            );
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
        } catch (final TransformerException ex) {
            throw new IllegalArgumentException(
                String.format(
                    "Failed to transform %s to %s",
                    source.getClass().getName(),
                    result.getClass().getName()
                ),
                ex
            );
        }
        return result.getNode();
    }

}
