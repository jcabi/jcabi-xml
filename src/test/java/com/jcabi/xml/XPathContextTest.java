/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.xml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link XPathContext}.
 * @since 0.1
 */
final class XPathContextTest {

    @Test
    void findsNamespaceByPrefix() {
        final String prefix = "ns1-foo";
        final String namespace = "hey-it-is-a-namespace";
        final NamespaceContext ctx = new XPathContext()
            .add(prefix, namespace)
            .add("ns-something-else", "boom");
        MatcherAssert.assertThat(
            ctx.getNamespaceURI(prefix),
            Matchers.equalTo(namespace)
        );
    }

    @Test
    void findsPrefixByNamespace() {
        final String prefix = "ns2-foo";
        final String namespace = "hey-it-is-a-new-namespace";
        final NamespaceContext ctx = new XPathContext()
            .add(prefix, namespace)
            .add("other-prefix", "boom-boom");
        MatcherAssert.assertThat(
            ctx.getPrefix(namespace),
            Matchers.equalTo(prefix)
        );
    }

    @Test
    void findsPrefixesByNamespace() {
        final String namespace = "simple-short-namespace";
        final NamespaceContext ctx = new XPathContext(namespace, namespace);
        final List<String> prefixes = new ArrayList<>(0);
        final Iterator<?> iter = ctx.getPrefixes(namespace);
        while (iter.hasNext()) {
            prefixes.add((String) iter.next());
        }
        MatcherAssert.assertThat(
            prefixes,
            Matchers.allOf(
                Matchers.iterableWithSize(2),
                Matchers.hasItem("ns1"),
                Matchers.hasItem("ns2")
            )
        );
    }

    @Test
    void findsDefaultNamespaces() {
        final NamespaceContext ctx = new XPathContext();
        MatcherAssert.assertThat(
            ctx.getNamespaceURI("xhtml"),
            Matchers.equalTo("http://www.w3.org/1999/xhtml")
        );
        MatcherAssert.assertThat(
            ctx.getNamespaceURI("xsl"),
            Matchers.equalTo("http://www.w3.org/1999/XSL/Transform")
        );
        MatcherAssert.assertThat(
            ctx.getNamespaceURI("xsi"),
            Matchers.equalTo("http://www.w3.org/2001/XMLSchema-instance")
        );
        MatcherAssert.assertThat(
            ctx.getNamespaceURI("xs"),
            Matchers.equalTo("http://www.w3.org/2001/XMLSchema")
        );
        MatcherAssert.assertThat(
            ctx.getNamespaceURI(XMLConstants.XML_NS_PREFIX),
            Matchers.equalTo(XMLConstants.XML_NS_URI)
        );
        MatcherAssert.assertThat(
            ctx.getNamespaceURI(XMLConstants.XMLNS_ATTRIBUTE),
            Matchers.equalTo(XMLConstants.XMLNS_ATTRIBUTE_NS_URI)
        );
    }

    @Test
    void findsNonBoundNamespaces() {
        final NamespaceContext ctx = new XPathContext();
        MatcherAssert.assertThat(
            ctx.getNamespaceURI("some-other-unbound-prefix"),
            Matchers.equalTo(XMLConstants.NULL_NS_URI)
        );
    }

}
