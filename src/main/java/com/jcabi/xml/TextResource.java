/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.xml;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Scanner;
import lombok.EqualsAndHashCode;

/**
 * Represent a given resource (InputStream, URL/URI location content, File)
 * as a string. UTF-8 encoding is used.
 *
 * <p>Objects of this class are immutable and thread-safe.
 *
 * @since 0.1
 */
@EqualsAndHashCode(of = "content")
final class TextResource {

    /**
     * The text representation.
     */
    private final transient String content;

    /**
     * Private constructor, used for initializing the field text content.
     * @param text The text content
     */
    private TextResource(final String text) {
        this.content = text;
    }

    /**
     * Public constructor, represent an InputStream as a text resource.
     *
     * <p>The provided input stream will be closed automatically after
     * getting data from it.
     * @param stream Stream to represent as text.
     */
    TextResource(final InputStream stream) {
        this(TextResource.readAsString(stream));
    }

    /**
     * Public constructor, represent a File as a text resource.
     * @param file File to represent as text.
     * @throws FileNotFoundException If file not found
     */
    TextResource(final File file) throws IOException {
        this(
            TextResource.readAsString(
                new BufferedInputStream(Files.newInputStream(file.toPath()))
            )
        );
    }

    /**
     * Public constructor, represent a URL location as a text resource.
     * @param url URL to represent as text.
     * @throws IOException If an IO problem occurs.
     */
    TextResource(final URL url) throws IOException {
        this(TextResource.readAsString(url));
    }

    /**
     * Public constructor, represent a URI location as a text resource.
     * @param uri URI to represent as text.
     * @throws IOException If an IO problem occurs.
     */
    TextResource(final URI uri) throws IOException {
        this(TextResource.readAsString(uri.toURL()));
    }

    @Override
    public String toString() {
        return this.content;
    }

    /**
     * Reads an entire stream's contents into a string.
     * @param stream The stream to read
     * @return The stream content, in String form
     */
    private static String readAsString(final InputStream stream) {
        final String result;
        try (Scanner scanner = new Scanner(
            stream, StandardCharsets.UTF_8.name()
        ).useDelimiter("\\A")) {
            if (scanner.hasNext()) {
                result = scanner.next();
            } else {
                result = "";
            }
        }
        return result;
    }

    /**
     * Reads URI contents into a string.
     * @param url The URL to read
     * @return The stream content, in String form
     * @throws IOException if an IO exception occurs
     */
    private static String readAsString(final URL url) throws IOException {
        return TextResource.readAsString(
            new BufferedInputStream(url.openStream())
        );
    }
}
