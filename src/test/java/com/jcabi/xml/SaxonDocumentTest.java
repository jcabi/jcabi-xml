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

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link SaxonDocument}.
 * @since 0.28
 */
final class SaxonDocumentTest {

    @Test
    void createsFromFile(){

    }


    @Test
    void findsXpathWithConcatFunctionThatReturnsSeveralItems() {
        MatcherAssert.assertThat(
            "SaxonDocument can handle XPath 2.0 feature - XPath evaluation of concat method, but it can't",
            new SaxonDocument(
                "<o><o base='a' ver='1'/><o base='b' ver='2'/></o>"
            ).xpath("//o[@base and @ver]/concat(@base,'|',@ver)"),
            Matchers.hasItems("a|1", "b|2")
        );
    }

    @Test
    void findsXpathWithStringJoinFunctionThatReturnsSeveralItems() {
        MatcherAssert.assertThat(
            "SaxonDocument can handle XPath 2.0 feature - XPath evaluation of string-join method, but it can't",
            new SaxonDocument(
                "<o><o base='a'/><o base='b' ver='2'/><o base='c'/></o>"
            ).xpath("//o[@base]/string-join((@base,@ver),'|')"),
            Matchers.hasItems("a", "b|2", "c")
        );
    }
}
