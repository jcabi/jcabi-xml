/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.xml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import lombok.EqualsAndHashCode;

/**
 * Chain of {@link XSL} stylesheets.
 *
 * @since 0.12
 * @checkstyle AbbreviationAsWordInNameCheck (5 lines)
 */
@EqualsAndHashCode(of = "sheets")
public final class XSLChain implements XSL {

    /**
     * XSL sheets.
     */
    private final transient XSL[] sheets;

    /**
     * Public ctor.
     * @param shts Sheets
     * @since 0.22
     */
    public XSLChain(final XSL... shts) {
        this(Arrays.asList(shts));
    }

    /**
     * Public ctor.
     * @param shts Sheets
     */
    public XSLChain(final Collection<XSL> shts) {
        this.sheets = shts.toArray(new XSL[shts.size()]);
    }

    @Override
    public XML transform(final XML xml) {
        XML output = xml;
        for (final XSL sheet : this.sheets) {
            output = sheet.transform(output);
        }
        return output;
    }

    @Override
    public String applyTo(final XML xml) {
        throw new UnsupportedOperationException("#applyTo()");
    }

    @Override
    public XSL with(final Sources src) {
        final Collection<XSL> list = new ArrayList<>(this.sheets.length);
        for (final XSL sheet : this.sheets) {
            list.add(sheet.with(src));
        }
        return new XSLChain(list);
    }

    @Override
    public XSL with(final String name, final Object value) {
        final Collection<XSL> list = new ArrayList<>(this.sheets.length);
        for (final XSL sheet : this.sheets) {
            list.add(sheet.with(name, value));
        }
        return new XSLChain(list);
    }
}
