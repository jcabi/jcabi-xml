/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.xml;

import com.jcabi.matchers.XhtmlMatchers;
import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link XSLChain}.
 * @since 0.12
 * @checkstyle AbbreviationAsWordInNameCheck (5 lines)
 */
final class XSLChainTest {

    @Test
    void makesXslTransformations() {
        final XSL first = new XSLDocument(
            StringUtils.join(
                "<xsl:stylesheet",
                " xmlns:xsl='http://www.w3.org/1999/XSL/Transform'",
                " version='2.0'>",
                "<xsl:template match='/'><done/>",
                "</xsl:template></xsl:stylesheet>"
            )
        );
        final XSL second = new XSLDocument(
            StringUtils.join(
                "<xsl:stylesheet ",
                " xmlns:xsl='http://www.w3.org/1999/XSL/Transform' ",
                " version='2.0' >",
                "<xsl:template match='/done'><twice/>",
                "</xsl:template> </xsl:stylesheet>"
            )
        );
        MatcherAssert.assertThat(
            new XSLChain(Arrays.asList(first, second)).transform(
                new XMLDocument("<a/>")
            ),
            XhtmlMatchers.hasXPath("/twice")
        );
    }

}
