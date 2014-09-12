/**
 * Copyright (c) 2012-2014, jcabi.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the jcabi.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jcabi.xml;

import com.jcabi.aspects.Immutable;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Scanner;
import lombok.EqualsAndHashCode;

/**
 * Represent a given resource (InputStream, URL/URI location content, File)
 * as a string. UTF-8 encoding is used.
 *
 * <p>Objects of this class are immutable and thread-safe.
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 */
@Immutable
@EqualsAndHashCode(of = "content")
final class TextResource {
    /**
     * Encoding.
     */
    public static final String ENCODING = "UTF-8";

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
     * @throws IOException If an IO problem occurs.
     */
    public TextResource(final InputStream stream) throws IOException {
        this(readAsString(stream));
    }

    /**
     * Public constructor, represent a File as a text resource.
     * @param file File to represent as text.
     * @throws IOException If an IO problem occurs.
     */
    public TextResource(final File file) throws IOException {
        this(readAsString(new BufferedInputStream(new FileInputStream(file))));
    }

    /**
     * Public constructor, represent a URL location as a text resource.
     * @param url URL to represent as text.
     * @throws IOException If an IO problem occurs.
     */
    public TextResource(final URL url) throws IOException {
        this(readAsString(url));
    }

    /**
     * Public constructor, represent a URI location as a text resource.
     * @param uri URI to represent as text.
     * @throws IOException If an IO problem occurs.
     */
    public TextResource(final URI uri) throws IOException {
        this(readAsString(uri.toURL()));
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
        @SuppressWarnings("resource")
        final Scanner scanner =
            new Scanner(stream, ENCODING).useDelimiter("\\A");
        final String result;
        try {
            if (scanner.hasNext()) {
                result = scanner.next();
            } else {
                result = "";
            }
        } finally {
            scanner.close();
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
        return readAsString(new BufferedInputStream(url.openStream()));
    }
}
