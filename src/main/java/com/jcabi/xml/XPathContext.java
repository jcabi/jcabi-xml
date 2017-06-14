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

import com.jcabi.immutable.Array;
import com.jcabi.immutable.ArrayMap;
import com.jcabi.log.Logger;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import lombok.EqualsAndHashCode;

/**
 * Convenient internal implementation of {@link NamespaceContext}.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.1
 */
@EqualsAndHashCode(of = { "map", "contexts" })
public final class XPathContext implements NamespaceContext {

    /**
     * Map of prefixes and URIs.
     */
    private final transient ArrayMap<String, String> map;

    /**
     * List of contexts to use.
     */
    private final transient Array<NamespaceContext> contexts;

    /**
     * Public ctor.
     *
     * <p>Since this class is private in the package users won't be able
     * to see this code and its documentation. That's why all these prefixes
     * and namespaces should be documented in
     * {@link XMLDocument#XMLDocument(String)} ctor. When adding/changing this
     * list - don't forget to document it there.
     */
    public XPathContext() {
        this(
            new ArrayMap<String, String>()
                .with("xhtml", "http://www.w3.org/1999/xhtml")
                .with("xs", "http://www.w3.org/2001/XMLSchema")
                .with("xsi", "http://www.w3.org/2001/XMLSchema-instance")
                .with("xsl", "http://www.w3.org/1999/XSL/Transform")
                .with("svg", "http://www.w3.org/2000/svg"),
            new Array<NamespaceContext>()
        );
    }

    /**
     * Public ctor with custom namespaces.
     * @param namespaces List of namespaces
     */
    public XPathContext(final Object... namespaces) {
        this(
            XPathContext.namespacesAsMap(namespaces),
            new Array<NamespaceContext>()
        );
    }

    /**
     * Public ctor.
     * @param old Old map of prefixes and namespaces
     * @param prefix The prefix
     * @param namespace The namespace
     */
    private XPathContext(final ArrayMap<String, String> old,
        final String prefix, final Object namespace) {
        this(
            old.with(prefix, namespace.toString()),
            new Array<NamespaceContext>()
        );
    }

    /**
     * Private ctor.
     * @param mapping Mapping to set.
     * @param ctxs Context to set.
     */
    private XPathContext(final ArrayMap<String, String> mapping,
        final Array<NamespaceContext> ctxs) {
        this.map = mapping;
        this.contexts = ctxs;
    }

    @Override
    public String toString() {
        return this.map.keySet().toString();
    }

    @Override
    public String getNamespaceURI(final String prefix) {
        String namespace = this.map.get(prefix);
        if (namespace == null) {
            for (final NamespaceContext ctx : this.contexts) {
                namespace = ctx.getNamespaceURI(prefix);
                if (namespace != null) {
                    break;
                }
            }
        }
        if (namespace == null) {
            if (prefix.equals(XMLConstants.XML_NS_PREFIX)) {
                namespace = XMLConstants.XML_NS_URI;
            } else if (prefix.equals(XMLConstants.XMLNS_ATTRIBUTE)) {
                namespace = XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
            } else {
                namespace = XMLConstants.NULL_NS_URI;
            }
        }
        return namespace;
    }

    @Override
    public String getPrefix(final String namespace) {
        final Iterator<String> prefixes = this.getPrefixes(namespace);
        String prefix = null;
        if (prefixes.hasNext()) {
            prefix = prefixes.next();
        }
        return prefix;
    }

    @Override
    public Iterator<String> getPrefixes(final String namespace) {
        final List<String> prefixes = new LinkedList<>();
        for (final ConcurrentMap.Entry<String, String> entry
            : this.map.entrySet()) {
            if (entry.getValue().equals(namespace)) {
                prefixes.add(entry.getKey());
            }
        }
        for (final NamespaceContext ctx : this.contexts) {
            final Iterator<?> iterator = ctx.getPrefixes(namespace);
            while (iterator.hasNext()) {
                prefixes.add(iterator.next().toString());
            }
        }
        if (namespace.equals(XMLConstants.XML_NS_URI)) {
            prefixes.add(XMLConstants.XML_NS_PREFIX);
        }
        if (namespace.equals(XMLConstants.XMLNS_ATTRIBUTE_NS_URI)) {
            prefixes.add(XMLConstants.XMLNS_ATTRIBUTE);
        }
        return Collections.unmodifiableList(prefixes).iterator();
    }

    /**
     * Add new prefix and namespace.
     * @param prefix The prefix
     * @param namespace The namespace
     * @return New context
     */
    public XPathContext add(final String prefix, final Object namespace) {
        if (this.map.containsKey(prefix)) {
            throw new IllegalArgumentException(
                String.format(
                    "prefix '%s' already registered for namespace '%s'",
                    prefix,
                    this.map.get(prefix)
                )
            );
        }
        return new XPathContext(this.map, prefix, namespace);
    }

    /**
     * Add new context.
     * @param context The context to merge into this one
     * @return New context
     */
    public XPathContext merge(final NamespaceContext context) {
        return new XPathContext(
            this.map, this.contexts.with(context)
        );
    }

    /**
     * Get namespaces as map.
     * @param namespaces The namespaces
     * @return Namespaces as map
     */
    private static ArrayMap<String, String> namespacesAsMap(
        final Object...namespaces) {
        final ConcurrentMap<String, String> map =
            new ConcurrentHashMap<>(namespaces.length);
        for (int pos = 0; pos < namespaces.length; ++pos) {
            map.put(
                Logger.format("ns%d", pos + 1),
                namespaces[pos].toString()
            );
        }
        return new ArrayMap<>(map);
    }

}
