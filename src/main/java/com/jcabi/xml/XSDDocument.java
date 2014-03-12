/**
 * Copyright (c) 2012-2013, JCabi.com
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
import com.jcabi.aspects.Loggable;
import com.jcabi.log.Logger;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.validation.constraints.NotNull;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import lombok.EqualsAndHashCode;
import org.apache.commons.io.IOUtils;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Implementation of {@link XSD}.
 *
 * <p>Objects of this class are immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.5
 */
@Immutable
@EqualsAndHashCode(of = "xsd")
@Loggable(Loggable.DEBUG)
public final class XSDDocument implements XSD {

    /**
     * Encoding.
     */
    private static final String ENCODING = "UTF-8";

    /**
     * XSD document.
     */
    private final transient String xsd;

    /**
     * Public ctor, from XSD as a source.
     * @param src XSD document body
     */
    public XSDDocument(@NotNull(message = "XML can't be NULL")
        final XML src) {
        this(src.toString());
    }

    /**
     * Public ctor, from XSD as a string.
     * @param src XSD document body
     */
    public XSDDocument(@NotNull(message = "XSD text can't be NULL")
        final String src) {
        this.xsd = src;
    }

    /**
     * Public ctor, from URL.
     * @param url Location of document
     * @throws IOException If fails to read
     * @since 0.7.4
     */
    public XSDDocument(@NotNull(message = "URL can't be NULL")
        final URL url) throws IOException {
        this(IOUtils.toString(url, ENCODING));
    }

    /**
     * Public ctor, from XSD as an input stream.
     * @param stream XSD input stream
     * @throws IOException If fails to read
     */
    public XSDDocument(@NotNull(message = "XSD input stream can't be NULL")
        final InputStream stream) throws IOException {
        this(IOUtils.toString(stream, ENCODING));
    }

    /**
     * Make an instance of XSD schema without I/O exceptions.
     *
     * <p>This factory method is useful when you need to create
     * an instance of XSD schema as a static final variable. In this
     * case you can't catch an exception but this method can help, for example:
     *
     * <pre> class Foo {
     *   private static final XSD SCHEMA = XSDDocument.make(
     *     Foo.class.getResourceAsStream("my-schema.xsd")
     *   );
     * }</pre>
     *
     * @param stream Input stream
     * @return XSD schema
     */
    public static XSD make(@NotNull(message = "XSD input stream can't be NULL")
        final InputStream stream) {
        try {
            return new XSDDocument(stream);
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Make an instance of XSD schema without I/O exceptions.
     * @param url URL with content
     * @return XSD schema
     * @see #make(InputStream)
     * @since 0.7.4
     */
    public static XSD make(@NotNull(message = "URL can't be NULL")
        final URL url) {
        try {
            return new XSDDocument(url);
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public String toString() {
        return new XMLDocument(this.xsd).toString();
    }

    @Override
    @NotNull(message = "list of exceptions is never NULL")
    public Collection<SAXParseException> validate(
        @NotNull(message = "XML can't be NULL") final Source xml) {
        final Schema schema;
        try {
            synchronized (XSDDocument.class) {
                schema = SchemaFactory
                    .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
                    .newSchema(new StreamSource(new StringReader(this.xsd)));
            }
        } catch (final SAXException ex) {
            throw new IllegalStateException(
                String.format("failed to create XSD schema from %s", this.xsd),
                ex
            );
        }
        final Collection<SAXParseException> errors =
            new CopyOnWriteArrayList<SAXParseException>();
        final Validator validator = schema.newValidator();
        validator.setErrorHandler(
            new ErrorHandler() {
                @Override
                public void warning(final SAXParseException error) {
                    errors.add(error);
                }
                @Override
                public void error(final SAXParseException error) {
                    errors.add(error);
                }
                @Override
                public void fatalError(final SAXParseException error) {
                    errors.add(error);
                }
            }
        );
        try {
            synchronized (XSDDocument.class) {
                validator.validate(xml);
            }
        } catch (final SAXException ex) {
            throw new IllegalStateException(ex);
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
        Logger.debug(
            this, "%s detected %d error(s)",
            schema.getClass().getName(), errors.size()
        );
        return errors;
    }

}
