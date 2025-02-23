/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.xml;

import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

/**
 * {@link LSResourceResolver} implementation
 * supporting classpath lookups.
 *
 * @since 0.1
 */
public final class ClasspathResolver implements LSResourceResolver {

    @Override
    @SuppressWarnings("PMD.UseObjectForClearerAPI")
    // @checkstyle ParameterNumber (10 lines)
    public LSInput resolveResource(
        final String type,
        final String nspace,
        final String pid,
        final String sid,
        final String base
    ) {
        LSInput input = null;
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (sid != null && loader.getResource(sid) != null) {
            input = new ClasspathInput(pid, sid);
        }
        return input;
    }
}
