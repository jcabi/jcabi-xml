/*
 * Copyright (c) 2012-2022, jcabi.com
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

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXParseException;

/**
 * Test case for {@link XSDDocument}.
 * @since 0.1
 * @checkstyle AbbreviationAsWordInNameCheck (5 lines)
 */
final class XSDDocumentTest {

    @Test
    void validatesXml() {
        final XSD xsd = new XSDDocument(
            new ByteArrayInputStream(
                StringUtils.join(
                    "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema' >",
                    "<xs:element name='test'/>",
                    " </xs:schema>"
                ).getBytes()
            )
        );
        MatcherAssert.assertThat(
            xsd.validate(new DOMSource(new XMLDocument("<test/>").node())),
            Matchers.empty()
        );
        MatcherAssert.assertThat(
            xsd.validate(
                new DOMSource(new XMLDocument("<test></test>").node())
            ),
            Matchers.empty()
        );
    }

    @Test
    void detectsSchemaViolations() {
        final String xsd = StringUtils.join(
            "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>",
            "<xs:element name='first'/></xs:schema>"
        );
        final Collection<SAXParseException> errors =
            new XSDDocument(xsd).validate(
                new StreamSource(
                    new ByteArrayInputStream("<second/>".getBytes())
                )
            );
        MatcherAssert.assertThat(
            errors,
            Matchers.iterableWithSize(1)
        );
        MatcherAssert.assertThat(
            errors.iterator().next().getLineNumber(),
            Matchers.greaterThan(0)
        );
    }

    @Test
    @SuppressWarnings({
        "PMD.AvoidInstantiatingObjectsInLoops",
        "PMD.InsufficientStringBufferDeclaration"
        })
    void validatesComplexXml() throws Exception {
        final int loopp = 5;
        final int size = 10_000;
        final int loop = 100;
        final int random = 10;
        final String xsd = StringUtils.join(
            "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'  >",
            "<xs:element name='root'>",
            "<xs:complexType><xs:sequence>",
            "<xs:element name='a' type='xs:string' maxOccurs='unbounded' />",
            "</xs:sequence></xs:complexType>",
            "</xs:element></xs:schema>"
        );
        final StringBuilder text = new StringBuilder(size)
            .append("<root>");
        for (int idx = 0; idx < loop; ++idx) {
            text.append("\n<a>\t&lt;&gt;&amp;&quot;&#09;&#x0A;")
                .append(RandomStringUtils.randomAlphanumeric(random))
                .append("</a>\n\r \t    ");
        }
        text.append("</root>");
        for (int idx = 0; idx < loopp; ++idx) {
            MatcherAssert.assertThat(
                new XSDDocument(xsd).validate(
                    new StreamSource(
                        new ByteArrayInputStream(
                            text.toString().getBytes(StandardCharsets.UTF_8)
                        )
                    )
                ),
                Matchers.empty()
            );
        }
    }

    @Test
    void validatesLongXml() throws Exception {
        final XSD xsd = new XSDDocument(
            this.getClass().getResource("sample.xsd")
        );
        MatcherAssert.assertThat(
            xsd.validate(
                new DOMSource(
                    new XMLDocument(
                        StringUtils.join(
                            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
                            "<payment><id>333</id>",
                            "<date>1-Jan-2013</date>",
                            "<debit>test-1</debit>",
                            "<credit>test-2</credit>",
                            "</payment>"
                        )
                    ).node()
                )
            ),
            Matchers.empty()
        );
    }

    @Test
    void validatesMultipleXmlsInThreads() throws Exception {
        final int random = 100;
        final int loop = 10;
        final int timeout = 30;
        final Random rand = new SecureRandom();
        final XSD xsd = new XSDDocument(
            StringUtils.join(
                "<xs:schema xmlns:xs ='http://www.w3.org/2001/XMLSchema' >",
                "<xs:element name='r'><xs:complexType>",
                "<xs:sequence>",
                "<xs:element name='x' type='xs:integer'",
                " minOccurs='0' maxOccurs='unbounded'/>",
                "</xs:sequence></xs:complexType></xs:element>",
                "</xs:schema>"
            )
        );
        // @checkstyle AnonInnerLengthCheck (50 lines)
        final Callable<Void> callable = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                final int cnt = rand.nextInt(random);
                MatcherAssert.assertThat(
                    xsd.validate(
                        new DOMSource(
                            new XMLDocument(
                                StringUtils.join(
                                    "<r>",
                                    StringUtils.repeat("<x>hey</x>", cnt),
                                    "</r>"
                                )
                            ).node()
                        )
                    ),
                    Matchers.hasSize(cnt << 1)
                );
                return null;
            }
        };
        final ExecutorService service = Executors.newFixedThreadPool(5);
        for (int count = 0; count < loop; count += 1) {
            service.submit(callable);
        }
        service.shutdown();
        MatcherAssert.assertThat(
            service.awaitTermination(timeout, TimeUnit.SECONDS),
            Matchers.is(true)
        );
        service.shutdownNow();
    }

}
