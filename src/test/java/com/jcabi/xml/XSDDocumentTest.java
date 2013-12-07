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

import com.jcabi.aspects.Tv;
import java.io.InputStream;
import java.util.Collection;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.xml.sax.SAXParseException;

/**
 * Test case for {@link XSDDocument}.
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 */
public final class XSDDocumentTest {

    /**
     * XSDDocument can validate XML.
     * @throws Exception If something goes wrong inside
     */
    @Test
    public void validatesXml() throws Exception {
        final InputStream xsd = IOUtils.toInputStream(
            StringUtils.join(
                "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema' >",
                "<xs:element name='test'/>",
                " </xs:schema>"
            )
        );
        MatcherAssert.assertThat(
            new XSDDocument(xsd).validate(
                new DOMSource(new XMLDocument("<test/>").node())
            ),
            Matchers.empty()
        );
    }

    /**
     * XSDDocument can detect schema violations.
     * @throws Exception If something goes wrong inside
     */
    @Test
    public void detectsSchemaViolations() throws Exception {
        final String xsd = StringUtils.join(
            "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>",
            "<xs:element name='first'/>",
            "</xs:schema>"
        );
        final Collection<SAXParseException> errors = new XSDDocument(xsd)
            .validate(new StreamSource(IOUtils.toInputStream("<second/>")));
        MatcherAssert.assertThat(
            errors,
            Matchers.<SAXParseException>iterableWithSize(1)
        );
        MatcherAssert.assertThat(
            errors.iterator().next().getLineNumber(),
            Matchers.greaterThan(0)
        );
    }

    /**
     * XSDDocument can validate complex XML.
     * @throws Exception If something goes wrong inside
     */
    @Test
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public void validatesComplexXml() throws Exception {
        final String xsd = StringUtils.join(
            "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'  >",
            "<xs:element name='root'>",
            "<xs:complexType><xs:sequence>",
            "<xs:element name='a' type='xs:string' maxOccurs='unbounded' />",
            "</xs:sequence></xs:complexType>",
            "</xs:element></xs:schema>"
        );
        final StringBuilder text = new StringBuilder("<root>");
        for (int idx = 0; idx < Tv.HUNDRED; ++idx) {
            text.append("\n<a>\t&lt;&gt;&amp;&quot;&#09;&#x0A;")
                .append(RandomStringUtils.randomAlphanumeric(Tv.TEN))
                .append("</a>\n\r \t    ");
        }
        text.append("</root>");
        for (int idx = 0; idx < Tv.FIVE; ++idx) {
            MatcherAssert.assertThat(
                new XSDDocument(xsd).validate(
                    new StreamSource(
                        IOUtils.toInputStream(
                            text.toString(), CharEncoding.UTF_8
                        )
                    )
                ),
                Matchers.empty()
            );
        }
    }

}
