/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.xml;

import java.util.Collection;
import java.util.List;
import javax.xml.namespace.NamespaceContext;
import org.w3c.dom.Node;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.SAXParseException;

/**
 * XML document.
 *
 * <p>Set of convenient XML manipulations:
 *
 * <pre> XML xml = new XMLDocument(content);
 * for (XML employee : xml.nodes("//Employee")) {
 *   String name = employee.xpath("name/text()").get(0);
 *   // ...
 * }</pre>
 *
 * <p>You can always get DOM node out of this abstraction using {@link #inner()}
 * or {@link #deepCopy()} methods.
 *
 * <p>{@code toString()} must produce a full XML.
 *
 * <p>Implementation of this interface must be immutable and thread-safe.
 *
 * <p> In most cases, you can use the {@link XMLDocument} implementation. It
 * implements all required features and will be sufficient for most practical tasks.
 * The only problem with that implementation is that it uses javax.xml classes under
 * the hood. The issue with the default java implementation is that it only supports
 * XPath 1.0. If you require XPath 2.0 support and beyond, you can use the Saxon
 * implementation of {@link XML} - {@link SaxonDocument}. It is based on the Saxon
 * library and supports XPath 2.0 and higher.
 * You can read more about Java XPath versioning problems in the following threads:
 * <ul>
 *   <li><a href="https://stackoverflow.com/questions/6624149/xpath-2-0-for-java-possible">xpath 2.0 for java possible</a></li>
 *   <li><a href="https://stackoverflow.com/questions/5802895/does-jdk-6-support-all-features-of-xpath-2-0/5803028#5803028">does JDK 6 support all features of XPath 2.0?</a></li>
 * </ul>
 *
 * @see XMLDocument
 * @since 0.1
 * @checkstyle AbbreviationAsWordInNameCheck (5 lines)
 */
public interface XML {

    /**
     * Find and return text elements or attributes matched by XPath address.
     *
     * <p>The XPath query should point to text elements or attributes in the
     * XML document. If any nodes of different types (elements, comments, etc.)
     * are found in result node list -
     * a {@link RuntimeException} will be thrown.
     *
     * <p>Alternatively, the XPath query can be a function or expression that
     * returns a single value instead of pointing to a set of nodes. In this
     * case, the result will be a List containing a single String, the content
     * of which is the result of the evaluation. If the expression result is not
     * a String, it will be converted to a String representation and returned as
     * such. For example, a document containing three &lt;a&gt; elements,
     * the input query "count(//a)", will return a singleton List with a single
     * string value "3".
     *
     * <p>This is a convenient method, which is used (according to our
     * experience) in 95% of all cases. Usually you don't need to get anything
     * else but a text value of some node or an attribute. And in most cases
     * you are interested to get just the first value
     * (use {@code xpath(..).get(0)}). But when/if you need to get more than
     * just a plain text - use {@link #nodes(String)}.
     *
     * <p>The {@link List} returned will throw {@link IndexOutOfBoundsException}
     * if you try to access a node which wasn't found by this XPath query.
     *
     * <p>An {@link IllegalArgumentException} is thrown if the parameter
     * passed is not a valid XPath expression.
     *
     * @param query The XPath query
     * @return The list of string values (texts) or single function result
     */
    List<String> xpath(String query);

    /**
     * Retrieve DOM nodes from the XML response.
     *
     * <p>The {@link List} returned will throw {@link IndexOutOfBoundsException}
     * if you try to access a node which wasn't found by this XPath query.
     *
     * <p>An {@link IllegalArgumentException} is thrown if the parameter
     * passed is not a valid XPath expression.
     *
     * @param query The XPath query
     * @return Collection of DOM nodes
     */
    List<XML> nodes(String query);

    /**
     * Register additional namespace prefix for XPath.
     *
     * <p>For example:
     *
     * <pre>
     * String name = new XMLDocument("...")
     *   .registerNs("ns1", "http://example.com")
     *   .registerNs("foo", "http://example.com/foo")
     *   .xpath("/ns1:root/foo:name/text()")
     *   .get(0);
     * </pre>
     *
     * <p>A number of standard namespaces are registered by default in
     * instances of XML. Their
     * full list is in {@link XMLDocument#XMLDocument(String)}.
     *
     * <p>If a namespace prefix is already registered an
     * {@link IllegalArgumentException} will be thrown.
     *
     * @param prefix The XPath prefix to register
     * @param uri Namespace URI
     * @return A new XML document, with this additional namespace registered
     */
    XML registerNs(String prefix, Object uri);

    /**
     * Append this namespace context to the existing one.
     *
     * <p>The existing context (inside this object) and the new one provided
     * will be merged together. The existing context will have higher
     * priority.
     *
     * @param context The context to append
     * @return A new XML document, with a merged context on board
     */
    XML merge(NamespaceContext context);

    /**
     * Retrieve DOM node, represented by this wrapper.
     * This method works exactly the same as {@link #deepCopy()}.
     * @deprecated Use {@link #inner()} or {@link #deepCopy()} instead.
     * @return Deep copy of the inner DOM node.
     */
    @Deprecated
    Node node();

    /**
     * Retrieve DOM node, represented by this wrapper.
     * Pay attention that this method returns inner node, not a deep copy.
     * It means that any changes to the returned node will affect the original XML.
     * @return Inner node.
     */
    Node inner();

    /**
     * Retrieve a deep copy of the DOM node, represented by this wrapper.
     * Might be expensive in terms of performance.
     * @return Deep copy of the node.
     */
    Node deepCopy();

    /**
     * Validate this XML against the XSD schema inside it.
     *
     * <p>If you don't have your own resolver, try using
     * {@link ClasspathResolver}.</p>
     *
     * @param resolver XSD schema resolver
     * @return List of errors found
     * @since 0.31.0
     */
    Collection<SAXParseException> validate(LSResourceResolver resolver);

    /**
     * Validate this XML against the provided XSD schema.
     * @param xsd The Schema
     * @return List of errors found
     * @since 0.31.0
     */
    Collection<SAXParseException> validate(XML xsd);
}
