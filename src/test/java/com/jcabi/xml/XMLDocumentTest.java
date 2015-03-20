/**
 * Copyright (c) 2012-2014, jcabi.com
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

import com.google.common.io.Files;
import com.jcabi.aspects.Parallel;
import com.jcabi.aspects.Tv;
import com.jcabi.matchers.XhtmlMatchers;
import java.io.File;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Test case for {@link XMLDocument}.
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 */
@SuppressWarnings({ "PMD.TooManyMethods", "PMD.DoNotUseThreads" })
public final class XMLDocumentTest {

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
        final File file = new File(Files.createTempDir(), "x.xml");
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
            doc.toString(),
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
        } catch (final IndexOutOfBoundsException ex) {
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
    @Test
    public void throwsWhenXpathQueryIsBroken() throws Exception {
        try {
            new XMLDocument("<root-99/>").xpath("/*/hello()");
            MatcherAssert.assertThat("exception expected", false);
        } catch (final IllegalArgumentException ex) {
            MatcherAssert.assertThat(
                ex.getMessage(),
                Matchers.containsString("XPathFactoryImpl")
            );
        }
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

    /**
     * XMLDocument can print with and without XML header.
     * @throws Exception If something goes wrong inside
     * @since 0.2
     */
    @Test
    public void printsWithAndWithoutXmlHeader() throws Exception {
        final XML doc = new XMLDocument("<hey/>");
        MatcherAssert.assertThat(
            doc,
            Matchers.hasToString(Matchers.startsWith("<?xml "))
        );
        MatcherAssert.assertThat(
            doc.nodes("/*").get(0),
            Matchers.hasToString(Matchers.startsWith("<hey"))
        );
    }

    /**
     * XMLDocument can parse in multiple threads.
     * @throws Exception If something goes wrong inside
     */
    @Test
    public void parsesInMultipleThreads() throws Exception {
        new Runnable() {
            @Override
            @Parallel(threads = Tv.HUNDRED)
            public void run() {
                MatcherAssert.assertThat(
                    new XMLDocument("<root><hey/></root>"),
                    XhtmlMatchers.hasXPath("/root/hey")
                );
            }
        } .run();
    }

    /**
     * XMLDocument can get XPath in multiple threads.
     * @throws Exception If something goes wrong inside
     */
    @Test
    public void xpathInMultipleThreads() throws Exception {
        final XML xml = new XMLDocument(
            String.format(
                "<a><b>test text</b><c>%s</c></a>",
                StringUtils.repeat(
                    "<beta>some text \u20ac</beta> ",
                    Tv.THOUSAND
                )
            )
        );
        new Runnable() {
            @Override
            @Parallel(threads = Tv.FIFTY)
            public void run() {
                MatcherAssert.assertThat(
                    xml.xpath("/a/b/text()").get(0),
                    Matchers.equalTo("test text")
                );
                MatcherAssert.assertThat(
                    xml.nodes("/a").get(0).nodes("c"),
                    Matchers.<XML>iterableWithSize(1)
                );
            }
        } .run();
    }

    /**
     * XMLDocument can print in multiple threads.
     * @throws Exception If something goes wrong inside
     */
    @Test
    public void printsInMultipleThreads() throws Exception {
        final XML xml = new XMLDocument(
            String.format(
                "<root><data>%s</data></root>",
                StringUtils.repeat(
                    "<alpha>some text \u20ac</alpha> ",
                    Tv.THOUSAND
                )
            )
        );
        new Runnable() {
            @Override
            @Parallel(threads = Tv.FIFTY)
            public void run() {
                MatcherAssert.assertThat(
                    xml.toString(),
                    XhtmlMatchers.hasXPath("/root/data/alpha")
                );
            }
        } .run();
    }

    /**
     * XMLDocument can calculate using XPath functions.
     * @throws Exception If something goes wrong inside
     */
    @Test
    public void performsXpathCalculations() throws Exception {
        final XML xml = new XMLDocument("<x><a/><a/><a/></x>");
        MatcherAssert.assertThat(
            xml.xpath("count(//x/a)"),
            Matchers.<String>iterableWithSize(1)
        );
        MatcherAssert.assertThat(
            xml.xpath("count(//a)").get(0),
            Matchers.equalTo("3")
        );
    }

    /**
     * XMLDocument can build a DOM node (Document or Element).
     * @throws Exception If something goes wrong inside
     */
    @Test
    public void buildsDomNode() throws Exception {
        final XML doc = new XMLDocument("<?xml version='1.0'?><f/>");
        MatcherAssert.assertThat(
            doc.node(),
            Matchers.instanceOf(Document.class)
        );
        MatcherAssert.assertThat(
            doc.nodes("/f").get(0).node(),
            Matchers.instanceOf(Element.class)
        );
    }

    /**
     * XMLDocument can compare to itself.
     * @throws Exception If something goes wrong inside
     */
    @Test
    public void comparesToAnotherDocument() throws Exception {
        MatcherAssert.assertThat(
            new XMLDocument("<hi>\n<dude>  </dude></hi>"),
            Matchers.equalTo(new XMLDocument("<hi><dude>  </dude></hi>"))
        );
        MatcherAssert.assertThat(
            new XMLDocument("<hi><man></man></hi>"),
            Matchers.not(
                Matchers.equalTo(new XMLDocument("<hi><man>  </man></hi>"))
            )
        );
    }

    /**
     * XMLDocument can preserve xml namespaces.
     * @throws Exception If something goes wrong inside
     */
    @Test
    public void preservesXmlNamespaces() throws Exception {
        final String xml = "<a xmlns='http://www.w3.org/1999/xhtml'><b/></a>";
        MatcherAssert.assertThat(
            new XMLDocument(xml),
            XhtmlMatchers.hasXPath("/xhtml:a/xhtml:b")
        );
    }

    /**
     * XMLDocument can preserve immutability.
     * @throws Exception If something goes wrong inside
     */
    @Test
    public void preservesImmutability() throws Exception {
        final XML xml = new XMLDocument("<r1><a/></r1>");
        final Node node = xml.nodes("/r1/a").get(0).node();
        node.appendChild(node.getOwnerDocument().createElement("h9"));
        MatcherAssert.assertThat(
            xml,
            XhtmlMatchers.hasXPath("/r1/a[not(hey-you)]")
        );
    }

}
