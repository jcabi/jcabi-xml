package com.jcabi.xml;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

class SaxonDocumentTest {

    @Test
    void findsXpathWithFunctionThatReturnsSeveralItems() {
        MatcherAssert.assertThat(
            "XMLDocument can handle XPath 2.0 feature - XPath evaluation of concat method, but it can't",
            new SaxonDocument(
                "<o><o base='a' ver='1'/><o base='b' ver='2'/></o>"
            ).xpath("//o[@base and @ver]/concat(@base,'|',@ver)"),
            Matchers.hasItems("a|1", "b|2")
        );
    }
}