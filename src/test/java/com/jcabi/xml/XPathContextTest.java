/**
 * Copyright (c) 2012-2015, jcabi.com
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

import com.google.common.collect.Lists;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link XPathContext}.
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 */
public final class XPathContextTest {

    /**
     * XPathContext can find namespaces by prefix.
     * @throws Exception If something goes wrong inside
     */
    @Test
    public void findsNamespaceByPrefix() throws Exception {
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

    /**
     * XPathContext can find prefix by namespace.
     * @throws Exception If something goes wrong inside
     */
    @Test
    public void findsPrefixByNamespace() throws Exception {
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

    /**
     * XPathContext can find prefixes by namespace.
     * @throws Exception If something goes wrong inside
     */
    @Test
    @SuppressWarnings("unchecked")
    public void findsPrefixesByNamespace() throws Exception {
        final String namespace = "simple-short-namespace";
        final NamespaceContext ctx = new XPathContext(namespace, namespace);
        MatcherAssert.assertThat(
            Lists.newArrayList(ctx.getPrefixes(namespace)),
            Matchers.allOf(
                (Matcher) Matchers.iterableWithSize(2),
                Matchers.hasItem("ns1"),
                Matchers.hasItem("ns2")
            )
        );
    }

    /**
     * XPathContext can find default namespaces.
     * @throws Exception If something goes wrong inside
     */
    @Test
    public void findsDefaultNamespaces() throws Exception {
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

    /**
     * XPathContext can find non-bound namespaces.
     * @throws Exception If something goes wrong inside
     */
    @Test
    public void findsNonBoundNamespaces() throws Exception {
        final NamespaceContext ctx = new XPathContext();
        MatcherAssert.assertThat(
            ctx.getNamespaceURI("some-other-unbound-prefix"),
            Matchers.equalTo(XMLConstants.NULL_NS_URI)
        );
    }

}
