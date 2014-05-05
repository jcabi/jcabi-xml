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
package com.jcabi.saxon;

import com.jcabi.aspects.Parallel;
import com.jcabi.aspects.Tv;
import com.jcabi.xml.XMLDocument;
import com.jcabi.xml.XSD;
import com.jcabi.xml.XSDDocument;
import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.Callable;
import javax.xml.transform.dom.DOMSource;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test of XML features with Saxon.
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 */
public final class SaxonSampleTest {

    /**
     * XSDDocument can validate XML against its schema, in multiple threads.
     * @throws Exception If fails
     */
    @Test
    public void validatesInMultipleThreads() throws Exception {
        final Random rand = new SecureRandom();
        // @checkstyle AnonInnerLengthCheck (100 lines)
        new Callable<Void>() {
            @Override
            @Parallel(threads = Tv.TEN)
            public Void call() throws Exception {
                final int cnt = rand.nextInt(Tv.HUNDRED);
                final XSD xsd = new XSDDocument(
                    StringUtils.join(
                        "<xs:schema ",
                        "xmlns:xs='http://www.w3.org/2001/XMLSchema' >",
                        "<xs:element name='root'><xs:complexType>",
                        "<xs:sequence>",
                        "<xs:element name='a' type='xs:integer'",
                        " minOccurs='0' maxOccurs='unbounded'/>",
                        "</xs:sequence></xs:complexType></xs:element>",
                        "</xs:schema>"
                    )
                );
                MatcherAssert.assertThat(
                    xsd.validate(
                        new DOMSource(
                            new XMLDocument(
                                StringUtils.join(
                                    "<root>",
                                    StringUtils.repeat("<a>hey you</a>", cnt),
                                    "</root>"
                                )
                            ).node()
                        )
                    ),
                    Matchers.hasSize(cnt << 1)
                );
                return null;
            }
        } .call();
    }

    /**
     * XSDDocument can validate XML against its schema, in multiple threads.
     * @throws Exception If fails
     */
    @Test
    public void validatesInMultipleThreadsAgain() throws Exception {
        final Random rand = new SecureRandom();
        final XSD xsd = new XSDDocument(
            StringUtils.join(
                "<xs:schema xmlns:xs ='http://www.w3.org/2001/XMLSchema' >",
                "<xs:element name='r'><xs:complexType><xs:sequence>",
                "<xs:element name='x' type='xs:integer'",
                " minOccurs='0'  maxOccurs='unbounded'/>",
                "</xs:sequence></xs:complexType></xs:element></xs:schema>"
            )
        );
        new Callable<Void>() {
            @Override
            @Parallel(threads = Tv.TEN)
            public Void call() throws Exception {
                final int cnt = rand.nextInt(Tv.HUNDRED);
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
        } .call();
    }

}
