/**
 * Copyright (c) 2012-2019, jcabi.com
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

import java.util.List;
import javax.xml.namespace.NamespaceContext;
import org.w3c.dom.Node;

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
 * <p>You can always get DOM node out of this abstraction using {@link #node()}
 * method.
 *
 * <p>{@code toString()} must produce a full XML.
 *
 * <p>Implementation of this interface must be immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
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
     * will be merged together. The existing context will be have higher
     * priority.
     *
     * @param context The context to append
     * @return A new XML document, with a merged context on board
     */
    XML merge(NamespaceContext context);

    /**
     * Retrieve DOM node, represented by this wrapper.
     * @return DOM node
     */
    Node node();

}
