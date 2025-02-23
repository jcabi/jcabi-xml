/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.saxon;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.xml.transform.dom.DOMSource;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test of XML features with Saxon.
 * @since 0.1
 */
public final class SaxonSampleTest {

    /**
     * XSDDocument can validate XML against its schema, in multiple threads.
     * @throws Exception If fails
     */
    @Test
    public void validatesInMultipleThreads() throws Exception {
        final int timeout = 10;
        final int random = 100;
        final int loop = 50;
        final Random rand = new SecureRandom();
        // @checkstyle AnonInnerLengthCheck (100 lines)
        final Callable<Void> callable = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                final int cnt = rand.nextInt(random);
                final XML xsd = new XMLDocument(
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
                    new XMLDocument(
                        StringUtils.join(
                            "<root>",
                            StringUtils.repeat("<a>hey you</a>", cnt),
                            "</root>"
                        )
                    ).validate(xsd),
                    Matchers.hasSize(cnt << 1)
                );
                return null;
            }
        };
        final ExecutorService service = Executors.newFixedThreadPool(5);
        for (int count = 0; count < loop; count = count + 1) {
            service.submit(callable);
        }
        service.shutdown();
        MatcherAssert.assertThat(
            service.awaitTermination(timeout, TimeUnit.SECONDS),
            Matchers.is(true)
        );
        service.shutdownNow();
    }

    /**
     * XSDDocument can validate XML against its schema, in multiple threads.
     * @throws Exception If fails
     */
    @Test
    public void validatesInMultipleThreadsAgain() throws Exception {
        final int timeout = 10;
        final int random = 100;
        final int loop = 50;
        final Random rand = new SecureRandom();
        final XML xsd = new XMLDocument(
            StringUtils.join(
                "<xs:schema xmlns:xs ='http://www.w3.org/2001/XMLSchema' >",
                "<xs:element name='r'><xs:complexType><xs:sequence>",
                "<xs:element name='x' type='xs:integer'",
                " minOccurs='0'  maxOccurs='unbounded'/>",
                "</xs:sequence></xs:complexType></xs:element></xs:schema>"
            )
        );
        final Callable<Void> callable = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                final int cnt = rand.nextInt(random);
                MatcherAssert.assertThat(
                    new XMLDocument(
                        StringUtils.join(
                            "<r>",
                            StringUtils.repeat("<x>hey</x>", cnt),
                            "</r>"
                        )
                    ).validate(xsd),
                    Matchers.hasSize(cnt << 1)
                );
                return null;
            }
        };
        final ExecutorService service = Executors.newFixedThreadPool(5);
        for (int count = 0; count < loop; count = count + 1) {
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
