/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.xml;

import javax.xml.transform.TransformerException;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.trans.XPathException;

/**
 * Message content attached to an optional Saxon transformation error.
 *
 * @since 1.0
 */
final class SaxonError {

    /**
     * Original transformation failure.
     */
    private final TransformerException error;

    /**
     * New error.
     *
     * @param failure Transformation failure
     */
    SaxonError(final TransformerException failure) {
        this.error = failure;
    }

    /**
     * Read the message document without requiring Saxon for other providers.
     *
     * @return Message text, or an empty string when no message document exists
     */
    String message() {
        Class<?> type = this.error.getClass();
        while (type != null && !"net.sf.saxon.trans.XPathException".equals(type.getName())) {
            type = type.getSuperclass();
        }
        String text = "";
        if (type != null) {
            final Sequence content = ((XPathException) this.error).getErrorObject();
            if (content instanceof NodeInfo) {
                text = ((NodeInfo) content).getStringValue();
            }
        }
        return text;
    }
}
