/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.xml;

import com.jcabi.log.Logger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import lombok.EqualsAndHashCode;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;

/**
 * Convenient internal implementation of {@link NamespaceContext}.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@EqualsAndHashCode(of = { "map", "contexts" })
public final class XPathContext implements NamespaceContext {

    /**
     * Map of prefixes and URIs.
     */
    private final transient Map<String, String> map;

    /**
     * List of contexts to use.
     */
    private final transient List<NamespaceContext> contexts;

    /**
     * Public ctor.
     *
     * <p>Since this class is private in the package users won't be able
     * to see this code and its documentation. That's why all these prefixes
     * and namespaces should be documented in
     * {@link XMLDocument#XMLDocument(String)} ctor. When adding/changing this
     * list - don't forget to document it there.
     */
    @SuppressWarnings("unchecked")
    public XPathContext() {
        this(
            new MapOf<>(
                new MapEntry<>("xhtml", "http://www.w3.org/1999/xhtml"),
                new MapEntry<>("xs", "http://www.w3.org/2001/XMLSchema"),
                new MapEntry<>("xsi", "http://www.w3.org/2001/XMLSchema-instance"),
                new MapEntry<>("xsl", "http://www.w3.org/1999/XSL/Transform"),
                new MapEntry<>("svg", "http://www.w3.org/2000/svg")
            ),
            new ArrayList<>(0)
        );
    }

    /**
     * Public ctor with custom namespaces.
     * @param namespaces List of namespaces
     */
    public XPathContext(final Object... namespaces) {
        this(
            XPathContext.namespacesAsMap(namespaces),
            new ArrayList<>(0)
        );
    }

    /**
     * Public ctor.
     * @param old Old map of prefixes and namespaces
     * @param prefix The prefix
     * @param namespace The namespace
     */
    @SuppressWarnings("unchecked")
    private XPathContext(final Map<String, String> old,
        final String prefix, final Object namespace) {
        this(
            new MapOf<String, String>(old, new MapEntry<>(prefix, namespace.toString())),
            new ArrayList<>(0)
        );
    }

    /**
     * Private ctor.
     * @param mapping Mapping to set.
     * @param ctxs Context to set.
     */
    private XPathContext(final Map<String, String> mapping,
        final List<NamespaceContext> ctxs) {
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
                    "Prefix '%s' already registered for namespace '%s'",
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
        final List<NamespaceContext> list = new ArrayList<>(
            this.contexts.size() + 1
        );
        list.addAll(this.contexts);
        list.add(context);
        return new XPathContext(this.map, list);
    }

    /**
     * Get namespaces as map.
     * @param namespaces The namespaces
     * @return Namespaces as map
     */
    private static Map<String, String> namespacesAsMap(
        final Object...namespaces) {
        final ConcurrentMap<String, String> map =
            new ConcurrentHashMap<>(namespaces.length);
        for (int pos = 0; pos < namespaces.length; ++pos) {
            map.put(
                Logger.format("ns%d", pos + 1),
                namespaces[pos].toString()
            );
        }
        return new HashMap<>(map);
    }

}
