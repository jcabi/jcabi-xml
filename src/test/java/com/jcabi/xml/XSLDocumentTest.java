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

import com.jcabi.aspects.Parallel;
import com.jcabi.aspects.Tv;
import com.jcabi.matchers.XhtmlMatchers;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link XSLDocument}.
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 */
public final class XSLDocumentTest {

    /**
     * XSLDocument can make XSL transformations.
     * @throws Exception If something goes wrong inside
     */
    @Test
    public void makesXslTransformations() throws Exception {
        final XSL xsl = new XSLDocument(
            StringUtils.join(
                "<xsl:stylesheet",
                " xmlns:xsl='http://www.w3.org/1999/XSL/Transform'",
                " version='2.0'>",
                "<xsl:template match='/'><done/>",
                "</xsl:template></xsl:stylesheet>"
            )
        );
        MatcherAssert.assertThat(
            xsl.transform(new XMLDocument("<a/>")),
            XhtmlMatchers.hasXPath("/done")
        );
        MatcherAssert.assertThat(
            xsl.transform(new XMLDocument("<a></a>")),
            XhtmlMatchers.hasXPath("/done ")
        );
    }

    /**
     * XSLDocument can make XSL transformations in multiple threads.
     * @throws Exception If something goes wrong inside
     */
    @Test
    @SuppressWarnings("PMD.DoNotUseThreads")
    public void makesXslTransformationsInThreads() throws Exception {
        final XSL xsl = new XSLDocument(
            StringUtils.join(
                "<xsl:stylesheet  ",
                " xmlns:xsl='http://www.w3.org/1999/XSL/Transform' ",
                " version='2.0' >",
                "<xsl:template match='/'><works/>",
                "</xsl:template> </xsl:stylesheet>"
            )
        );
        new Runnable() {
            @Override
            @Parallel(threads = Tv.FIFTY)
            public void run() {
                MatcherAssert.assertThat(
                    xsl.transform(new XMLDocument("<test/>")),
                    XhtmlMatchers.hasXPath("/works")
                );
            }
        } .run();
    }

    /**
     * XSLDocument can transform with IMPORTs.
     * @throws Exception If something goes wrong inside
     */
    @Test
    public void transformsWithImports() throws Exception {
        final XSL xsl = new XSLDocument(
            this.getClass().getResourceAsStream("first.xsl")
        ).with(new ClasspathSources(this.getClass()));
        MatcherAssert.assertThat(
            xsl.transform(new XMLDocument("<simple-test/>")),
            XhtmlMatchers.hasXPath("/result[.=6]")
        );
    }

    /**
     * XSLDocument can transform into text.
     * @throws Exception If something goes wrong inside
     * @since 0.11
     */
    @Test
    public void transformsIntoText() throws Exception {
        final XSL xsl = new XSLDocument(
            StringUtils.join(
                "<xsl:stylesheet ",
                " xmlns:xsl='http://www.w3.org/1999/XSL/Transform'  ",
                " version='2.0'><xsl:output method='text'/>",
                "<xsl:template match='/'>hello</xsl:template></xsl:stylesheet>"
            )
        );
        MatcherAssert.assertThat(
            xsl.applyTo(new XMLDocument("<something/>")),
            Matchers.equalTo("hello")
        );
    }

    /**
     * XSL.STRIP can strip XML.
     * @throws Exception If something goes wrong inside
     */
    @Test
    public void stripsXml() throws Exception {
        MatcherAssert.assertThat(
            XSLDocument.STRIP.transform(
                new XMLDocument("<a>   <b/>  </a>")
            ).toString(),
            Matchers.containsString(
                new StringBuilder()
                    .append("<a>")
                    .append(System.lineSeparator())
                    .append("<b/>")
                    .append(System.lineSeparator())
                    .append("</a>")
                    .toString()
            )
        );
    }

}
