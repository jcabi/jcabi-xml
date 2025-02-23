/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.xml;

import javax.xml.transform.URIResolver;

/**
 * Sources for XSLT.
 *
 * @since 0.9
 * @checkstyle InterfaceIsType (500 lines)
 */
public interface Sources extends URIResolver {

    /**
     * Dummy sources.
     */
    Sources DUMMY = (href, base) -> {
        throw new UnsupportedOperationException(
            String.format(
                "Sources.DUMMY#resolve(\"%s\", \"%s\"): URI resolving is not configured in XSLDocument, use #with(URIResolver) method",
                href, base
            )
        );
    };

}
