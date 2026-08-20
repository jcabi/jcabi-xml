/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.xml;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * File source of XML from a file.
 * @since 0.32
 */
class FileSource implements DocSource {

    /**
     * The file.
     */
    private final File file;

    /**
     * Public ctor.
     * @param file The file
     */
    FileSource(final File file) {
        this.file = file;
    }

    @Override
    public Document apply(final DocumentBuilder builder) throws IOException, SAXException {
        return builder.parse(this.file);
    }

    @Override
    public long length() {
        return this.file.length();
    }
}
