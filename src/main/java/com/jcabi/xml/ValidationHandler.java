/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.xml;

import java.util.Collection;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

/**
 * Validation error handler.
 * @since 0.1
 */
final class ValidationHandler implements ErrorHandler {

    /**
     * Errors.
     */
    private final transient Collection<SAXParseException> errors;

    /**
     * Constructor.
     * @param errs Collection of errors
     */
    ValidationHandler(final Collection<SAXParseException> errs) {
        this.errors = errs;
    }

    @Override
    public void warning(final SAXParseException error) {
        this.errors.add(error);
    }

    @Override
    public void error(final SAXParseException error) {
        this.errors.add(error);
    }

    @Override
    public void fatalError(final SAXParseException error) {
        this.errors.add(error);
    }
}
