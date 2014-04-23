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

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.jcabi.aspects.Parallel;
import com.jcabi.aspects.Tv;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link StrictXML}.
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 */
public final class StrictXMLTest {

    /**
     * StrictXML can pass a valid document.
     * @throws Exception If something goes wrong inside
     */
    @Test
    public void passesValidXmlThrough() throws Exception {
        new StrictXML(
            new XMLDocument("<root>test</root>"),
            new XSDDocument(
                StringUtils.join(
                    "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>",
                    "<xs:element name='root' type='xs:string'/>",
                    "</xs:schema>"
                )
            )
        );
    }

    /**
     * StrictXML can reject an invalid document.
     * @throws Exception If something goes wrong inside
     */
    @Test(expected = IllegalArgumentException.class)
    public void rejectsInvalidXmlThrough() throws Exception {
        new StrictXML(
            new XMLDocument("<root>not an integer</root>"),
            new XSDDocument(
                StringUtils.join(
                    "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema' >",
                    "<xs:element name='root' type='xs:integer'/></xs:schema>"
                )
            )
        );
    }

    /**
     * StrictXML passes a valid document using xsi:schemaLocation.
     * @throws Exception If something goes wrong inside.
     */
    @Test
    public void passesValidXmlUsingXsiSchemaLocation() throws Exception {
        new StrictXML(
            new XMLDocument(
                this.getClass().getResource("xsi-schemalocation-valid.xml")
            )
        );
    }

    /**
     * StrictXML rejects an invalid document using xsi:schemaLocation.
     * @throws Exception If something goes wrong inside.
     */
    @Test(expected = IllegalArgumentException.class)
    public void rejectsInvalidXmlUsingXsiSchemaLocation() throws Exception {
        new StrictXML(
            new XMLDocument(
                this.getClass().getResource("xsi-schemalocation-invalid.xml")
            )
        );
    }

    /**
     * StrictXML can validate XML in multiple threads.
     * @throws Exception If something goes wrong inside
     */
    @Test
    public void validatesMultipleXmlsInThreads() throws Exception {
        final XSD xsd = new XSDDocument(
            StringUtils.join(
                "<xs:schema xmlns:xs ='http://www.w3.org/2001/XMLSchema' >",
                "<xs:element name='r'><xs:complexType><xs:sequence>",
                "<xs:element name='x' maxOccurs='unbounded'><xs:simpleType>",
                "<xs:restriction base='xs:integer'>",
                "<xs:maxInclusive value='100'/></xs:restriction>",
                "</xs:simpleType></xs:element>",
                "</xs:sequence></xs:complexType></xs:element></xs:schema>"
            )
        );
        final Random rnd = new SecureRandom();
        final XML xml = new XMLDocument(
            StringUtils.join(
                Iterables.concat(
                    Collections.singleton("<r>"),
                    Iterables.transform(
                        Collections.nCopies(Tv.TEN, 0),
                        new Function<Integer, String>() {
                            @Override
                            public String apply(final Integer pos) {
                                return String.format(
                                    "<x>%d</x>", rnd.nextInt(Tv.HUNDRED)
                                );
                            }
                        }
                    ),
                    Collections.singleton("<x>101</x></r>")
                ),
                " "
            )
        );
        final AtomicInteger done = new AtomicInteger();
        new Callable<Void>() {
            @Override
            @Parallel(threads = Tv.FIFTY)
            public Void call() throws Exception {
                try {
                    new StrictXML(xml, xsd);
                } catch (final IllegalArgumentException ex) {
                    done.incrementAndGet();
                }
                return null;
            }
        } .call();
        MatcherAssert.assertThat(done.get(), Matchers.equalTo(Tv.FIFTY));
    }

}
