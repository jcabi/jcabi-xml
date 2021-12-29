/*
 * Copyright (c) 2012-2021, jcabi.com
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

import com.jcabi.matchers.XhtmlMatchers;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link XSLDocument}.
 * @since 0.1
 * @checkstyle AbbreviationAsWordInNameCheck (5 lines)
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class XSLDocumentTest {

    @Test
    public void makesXslTransformations() {
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

    @Test
    @SuppressWarnings("PMD.DoNotUseThreads")
    public void makesXslTransformationsInThreads() throws Exception {
        final int loop = 50;
        final int timeout = 30;
        final XSL xsl = new XSLDocument(
            StringUtils.join(
                "<xsl:stylesheet  ",
                " xmlns:xsl='http://www.w3.org/1999/XSL/Transform' ",
                " version='2.0' >",
                "<xsl:template match='/'><works/>",
                "</xsl:template> </xsl:stylesheet>"
            )
        );
        final Runnable runnable = () -> MatcherAssert.assertThat(
            xsl.transform(new XMLDocument("<test/>")),
            XhtmlMatchers.hasXPath("/works")
        );
        final ExecutorService service = Executors.newFixedThreadPool(5);
        for (int count = 0; count < loop; count += 1) {
            service.submit(runnable);
        }
        service.shutdown();
        MatcherAssert.assertThat(
            service.awaitTermination(timeout, TimeUnit.SECONDS),
            Matchers.is(true)
        );
        service.shutdownNow();
    }

    @Test
    public void transformsWithImports() {
        final XSL xsl = new XSLDocument(
            this.getClass().getResourceAsStream("first.xsl")
        ).with(new ClasspathSources(this.getClass()));
        MatcherAssert.assertThat(
            xsl.transform(new XMLDocument("<simple-test/>")),
            XhtmlMatchers.hasXPath("/result[.=6]")
        );
    }

    @Test
    public void transformsWithImportsFromUrl() {
        final XSL xsl = XSLDocument.make(
            this.getClass().getResource("first.xsl")
        ).with(new ClasspathSources(this.getClass()));
        MatcherAssert.assertThat(
            xsl.transform(new XMLDocument("<simple-test/>")),
            XhtmlMatchers.hasXPath("/result[.=6]")
        );
    }

    @Test
    public void transformsIntoText() {
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

    @Test
    public void stripsXml() {
        MatcherAssert.assertThat(
            XSLDocument.STRIP.transform(
                new XMLDocument("<a>   <b/>  </a>")
            ).toString(),
            Matchers.containsString("<a>\n")
        );
    }

    @Test
    public void transformsIntoTextWithParams() {
        final XSL xsl = new XSLDocument(
            StringUtils.join(
                "<xsl:stylesheet   ",
                " xmlns:xsl='http://www.w3.org/1999/XSL/Transform'    ",
                " xmlns:xs='http://www.w3.org/2001/XMLSchema'",
                " version='2.0'><xsl:output method='text'  />",
                "<xsl:param name='boom' />",
                "<xsl:template match='/'>[<xsl:value-of select='$boom'/>]",
                "</xsl:template>   </xsl:stylesheet>"
            )
        );
        MatcherAssert.assertThat(
            xsl.with("boom", "Donny").applyTo(new XMLDocument("<ehe/>")),
            Matchers.equalTo("[Donny]")
        );
    }

    @Test
    public void transformsIntoTextWithIntegerParams() {
        final XSL xsl = new XSLDocument(
            StringUtils.join(
                "<xsl:stylesheet     ",
                " xmlns:xsl='http://www.w3.org/1999/XSL/Transform'       ",
                " version='2.0'><xsl:output method='text'    />",
                "<xsl:param name='faa' select='5'/>",
                "<xsl:template match='/'>+<xsl:value-of select='$faa'/>+",
                "</xsl:template>   </xsl:stylesheet>  "
            )
        );
        MatcherAssert.assertThat(
            xsl.with("faa", 1).applyTo(new XMLDocument("<r0/>")),
            Matchers.equalTo("+1+")
        );
    }

    @Test
    public void catchesXslErrorMessages() {
        try {
            new XSLDocument(
                StringUtils.join(
                    " <xsl:stylesheet",
                    "  xmlns:xsl='http://www.w3.org/1999/XSL/Transform'",
                    "  version='2.0'><xsl:template match='/'>",
                    "<xsl:message terminate='yes'>",
                    "<xsl:text>oopsie...</xsl:text>",
                    " </xsl:message></xsl:template></xsl:stylesheet>"
                )
            ).transform(new XMLDocument("<zz1/>"));
            Assertions.fail("Exception expected here");
        } catch (final IllegalArgumentException ex) {
            MatcherAssert.assertThat(
                ex.getLocalizedMessage(),
                Matchers.containsString("oopsie...")
            );
        }
    }

    @Test
    public void catchesSaxonWarnings() {
        new XSLDocument(
            StringUtils.join(
                " <xsl:stylesheet",
                "  xmlns:xsl='http://www.w3.org/1999/XSL/Transform'",
                "  version='2.0'>",
                "<xsl:template match='a'></xsl:template>",
                "<xsl:template match='a'></xsl:template>",
                "</xsl:stylesheet>"
            ),
            "https://example.com/hello.xsl"
        ).transform(new XMLDocument("<x><a/></x>"));
    }

}
