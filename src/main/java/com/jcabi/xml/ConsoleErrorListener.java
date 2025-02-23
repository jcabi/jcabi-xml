/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.xml;

import com.jcabi.log.Logger;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

/**
 * Error listener to console.
 *
 * @since 0.22
 */
final class ConsoleErrorListener implements ErrorListener {

    /**
     * Errors.
     */
    private final Collection<String> errors =
        new CopyOnWriteArrayList<>();

    @Override
    public void warning(final TransformerException warning) {
        Logger.warn(
            this, "#warning(): %s",
            warning.getMessageAndLocation()
        );
        this.errors.add(warning.getMessageAndLocation());
    }

    @Override
    public void error(final TransformerException error)
        throws TransformerException {
        Logger.error(
            this, "#error(): %s",
            error.getMessageAndLocation()
        );
        this.errors.add(error.getMessageAndLocation());
        throw error;
    }

    @Override
    public void fatalError(final TransformerException error)
        throws TransformerException {
        Logger.error(
            this, "#fatalError(): %s",
            error.getMessageAndLocation()
        );
        this.errors.add(error.getMessageAndLocation());
        throw error;
    }

    /**
     * Return all errors.
     * @return List of errors accumulated
     */
    public Collection<String> summary() {
        return Collections.unmodifiableCollection(this.errors);
    }

}
