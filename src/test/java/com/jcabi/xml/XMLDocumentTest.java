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

import com.rexsl.test.XhtmlMatchers;
import java.io.File;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.CharEncoding;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.w3c.dom.Node;

/**
 * Test case for {@link XMLDocument}.
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class XMLDocumentTest {

    /**
     * Temporary folder.
     * @checkstyle VisibilityModifier (3 lines)
     */
    @Rule
    public transient TemporaryFolder temp = new TemporaryFolder();

    /**
     * XMLDocument can find nodes with XPath.
     * @throws Exception If something goes wrong inside
     */
    @Test
    public void findsDocumentNodesWithXpath() throws Exception {
        final XML doc = new XMLDocument(
            "<r><a>\u0443\u0440\u0430!</a><a>B</a></r>"
        );
        MatcherAssert.assertThat(
            doc.xpath("//a/text()"),
            Matchers.hasSize(2)
        );
        MatcherAssert.assertThat(
            doc.xpath("/r/a/text()"),
            Matchers.hasItem("\u0443\u0440\u0430!")
        );
    }

    /**
     * XMLDocument can find with XPath and namespaces.
     * @throws Exception If something goes wrong inside
     */
    @Test
    public void findsWithXpathAndNamespaces() throws Exception {
        final XML doc = new XMLDocument(
            // @checkstyle LineLength (1 line)
            "<html xmlns='http://www.w3.org/1999/xhtml'><div>\u0443\u0440\u0430!</div></html>"
        );
        MatcherAssert.assertThat(
            doc.nodes("/xhtml:html/xhtml:div"),
            Matchers.hasSize(1)
        );
        MatcherAssert.assertThat(
            doc.nodes("//xhtml:div[.='\u0443\u0440\u0430!']"),
            Matchers.hasSize(1)
        );
    }

    /**
     * XMLDocument can find with XPath with custom namespaces.
     * @throws Exception If something goes wrong inside
     */
    @Test
    public void findsWithXpathWithCustomNamespace() throws Exception {
        final File file = this.temp.newFile("temp-1.xml");
        FileUtils.writeStringToFile(
            file,
            "<a xmlns='urn:foo'><b>\u0433!</b></a>",
            CharEncoding.UTF_8
        );
        final XML doc = new XMLDocument(file).registerNs("f", "urn:foo");
        MatcherAssert.assertThat(
            doc.nodes("/f:a/f:b[.='\u0433!']"),
            Matchers.hasSize(1)
        );
        MatcherAssert.assertThat(
            doc.xpath("//f:b/text()").get(0),
            Matchers.equalTo("\u0433!")
        );
    }

    /**
     * XMLDocument can find and return nodes with XPath.
     * @throws Exception If something goes wrong inside
     */
    @Test
    public void findsDocumentNodesWithXpathAndReturnsThem() throws Exception {
        final XML doc = new XMLDocument(
            IOUtils.toInputStream("<root><a><x>1</x></a><a><x>2</x></a></root>")
        );
        MatcherAssert.assertThat(
            doc.nodes("//a"),
            Matchers.hasSize(2)
        );
        MatcherAssert.assertThat(
            doc.nodes("/root/a").get(0).xpath("x/text()").get(0),
            Matchers.equalTo("1")
        );
    }

    /**
     * XMLDocument can convert itself back to XML.
     * @throws Exception If something goes wrong inside
     */
    @Test
    public void convertsItselfToXml() throws Exception {
        final XML doc = new XMLDocument("<hello><a/></hello>");
        MatcherAssert.assertThat(
            doc,
            Matchers.hasToString(XhtmlMatchers.hasXPath("/hello/a"))
        );
    }

    /**
     * XMLDocument can retrieve DOM node.
     * @throws Exception If something goes wrong inside
     */
    @Test
    public void retrievesDomNode() throws Exception {
        final XML doc = new XMLDocument(
            this.getClass().getResource("simple.xml")
        );
        MatcherAssert.assertThat(
            doc.nodes("/root/simple").get(0).node().getNodeName(),
            Matchers.equalTo("simple")
        );
        MatcherAssert.assertThat(
            doc.nodes("//simple").get(0).node().getNodeType(),
            Matchers.equalTo(Node.ELEMENT_NODE)
        );
    }

    /**
     * XMLDocument can throw custom exception when XPath not found.
     * @throws Exception If something goes wrong inside
     */
    @Test
    public void throwsCustomExceptionWhenXpathNotFound() throws Exception {
        try {
            new XMLDocument("<root/>").xpath("/absent-node/text()").get(0);
            MatcherAssert.assertThat("exception expected here", false);
        } catch (IndexOutOfBoundsException ex) {
            MatcherAssert.assertThat(
                ex.getMessage(),
                Matchers.allOf(
                    Matchers.containsString("/absent-node/text("),
                    Matchers.containsString("<root/")
                )
            );
        }
    }

    /**
     * XMLDocument can throw when invalid XPath.
     * @throws Exception If something goes wrong inside
     */
    @Test(expected = IllegalArgumentException.class)
    public void throwsWhenXpathQueryIsBroken() throws Exception {
        new XMLDocument("<root-99/>").xpath("invalid xpath query");
    }

    /**
     * XMLDocument can preserve processing instructions.
     * @throws Exception If something goes wrong inside
     */
    @Test
    public void preservesProcessingInstructions() throws Exception {
        MatcherAssert.assertThat(
            new XMLDocument("<?xml version='1.0'?><?x test?><a/>"),
            Matchers.hasToString(Matchers.containsString("<?x test?>"))
        );
    }

}
