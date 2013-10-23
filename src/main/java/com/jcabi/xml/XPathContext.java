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

import com.jcabi.log.Logger;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.validation.constraints.NotNull;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Convenient internal implementation of {@link NamespaceContext}.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.1
 */
@ToString
@EqualsAndHashCode(of = { "map", "contexts" })
public final class XPathContext implements NamespaceContext {

    /**
     * Map of prefixes and URIs.
     */
    private final transient ConcurrentMap<String, String> map =
        new ConcurrentHashMap<String, String>(0);

    /**
     * List of contexts to use.
     */
    private final transient List<NamespaceContext> contexts =
        new CopyOnWriteArrayList<NamespaceContext>();

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
        this.map.put("xhtml", "http://www.w3.org/1999/xhtml");
        this.map.put("xs", "http://www.w3.org/2001/XMLSchema");
        this.map.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        this.map.put("xsl", "http://www.w3.org/1999/XSL/Transform");
        this.map.put("svg", "http://www.w3.org/2000/svg");
    }

    /**
     * Public ctor with custom namespaces.
     * @param namespaces List of namespaces
     */
    public XPathContext(final Object... namespaces) {
        this();
        for (int pos = 0; pos < namespaces.length; ++pos) {
            this.map.put(
                Logger.format("ns%d", pos + 1),
                namespaces[pos].toString()
            );
        }
    }

    /**
     * Public ctor.
     * @param old Old map of prefixes and namespaces
     * @param prefix The prefix
     * @param namespace The namespace
     */
    private XPathContext(final ConcurrentMap<String, String> old,
        final String prefix, final Object namespace) {
        this();
        this.map.putAll(old);
        if (this.map.containsKey(prefix)) {
            throw new IllegalArgumentException(
                String.format(
                    "prefix '%s' already registered for namespace '%s'",
                    prefix,
                    this.map.get(prefix)
                )
            );
        }
        this.map.put(prefix, namespace.toString());
    }

    @Override
    public String getNamespaceURI(@NotNull final String prefix) {
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
    public String getPrefix(@NotNull final String namespace) {
        final Iterator<String> prefixes = this.getPrefixes(namespace);
        String prefix = null;
        if (prefixes.hasNext()) {
            prefix = prefixes.next();
        }
        return prefix;
    }

    @Override
    public Iterator<String> getPrefixes(@NotNull final String namespace) {
        final List<String> prefixes = new LinkedList<String>();
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
    public XPathContext add(@NotNull final String prefix,
        @NotNull final Object namespace) {
        return new XPathContext(this.map, prefix, namespace);
    }

    /**
     * Add new context.
     * @param context The context to merge into this one
     * @return New context
     */
    public XPathContext merge(@NotNull final NamespaceContext context) {
        final XPathContext ctx = new XPathContext();
        ctx.map.putAll(this.map);
        ctx.contexts.addAll(this.contexts);
        ctx.contexts.add(context);
        return ctx;
    }

}
