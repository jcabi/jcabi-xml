/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.xml;

import com.jcabi.log.Logger;
import org.w3c.dom.Node;

/**
 * Node not found in XmlDocument.
 * @since 0.1
 */
final class NodeNotFoundException
    extends IndexOutOfBoundsException {

    /**
     * Serialization marker.
     */
    private static final long serialVersionUID = 0x7526FA78EEDAC470L;

    /**
     * Public ctor.
     * @param message Error message
     * @param node The XML with error
     * @param query The query in XPath
     */
    NodeNotFoundException(final String message, final Node node,
        final CharSequence query) {
        super(
            Logger.format(
                "XPath '%s' not found in '%[text]s': %s",
                NodeNotFoundException.escapeUnicode(query),
                NodeNotFoundException.escapeUnicode(
                    new XMLDocument(node).toString()
                ),
                message
            )
        );
    }

    private static String escapeUnicode(final CharSequence input) {
        final int length = input.length();
        final StringBuilder output = new StringBuilder(length);
        for (int index = 0; index < length; index += 1) {
            final char character = input.charAt(index);
            if (character < 32 || character > 0x7F) {
                output.append(String.format("\\u%X", (int) character));
            } else {
                output.append(character);
            }
        }
        return output.toString();
    }
}
