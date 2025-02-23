/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.xml;

/**
 * XSL stylesheet.
 *
 * <p>Implementation of this interface must be immutable and thread-safe.
 *
 * @see XSLDocument
 * @since 0.4
 * @checkstyle AbbreviationAsWordInNameCheck (5 lines)
 */
public interface XSL {

    /**
     * Transform XML to another one.
     * @param xml Source XML document
     * @return Result document
     */
    XML transform(XML xml);

    /**
     * Transform XML into text.
     * @param xml Source XML document
     * @return Result text
     * @since 0.11
     */
    String applyTo(XML xml);

    /**
     * With this sources.
     * @param src Sources
     * @return New XSL document
     */
    XSL with(Sources src);

    /**
     * With this parameter.
     * @param name Name of XSL parameter
     * @param value Value of XSL parameter
     * @return New XSL document
     * @since 0.16
     */
    XSL with(String name, Object value);

}
