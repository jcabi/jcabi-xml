/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.xml;

import javax.xml.transform.ErrorListener;
import javax.xml.xpath.XPathFactory;
import net.sf.saxon.lib.ErrorReporterToListener;
import net.sf.saxon.xpath.XPathFactoryImpl;

/**
 * Route of XPath compilation warnings from a Saxon factory into a listener.
 *
 * <p>JAXP gives no way to observe warnings raised while an XPath
 * expression compiles, so Saxon prints them to the console and the
 * caller never sees them. This class plugs a listener into the Saxon
 * configuration behind the factory instead. It touches classes that
 * only Saxon 10 and later provide, so it must be loaded only when
 * they are present on the classpath.</p>
 *
 * @since 0.38
 */
final class SaxonWarnings {

    /**
     * Listener that receives the warnings.
     */
    private final ErrorListener listener;

    /**
     * Ctor.
     *
     * @param lstnr Listener that receives the warnings
     */
    SaxonWarnings(final ErrorListener lstnr) {
        this.listener = lstnr;
    }

    /**
     * Plug the listener into the factory, if it is a Saxon one.
     *
     * @param factory The factory to plug the listener into
     */
    void attach(final XPathFactory factory) {
        if (factory instanceof XPathFactoryImpl) {
            ((XPathFactoryImpl) factory).getConfiguration().setErrorReporterFactory(
                config -> new ErrorReporterToListener(this.listener)
            );
        }
    }
}
