/**
 * Copyright (c) 2012-2019, jcabi.com
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

import com.jcabi.log.Logger;
import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import lombok.EqualsAndHashCode;
import org.w3c.dom.Node;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Strict {@link XML} that fails if encapsulated XML document
 * doesn't validate against externally provided XSD schema or internally
 * specified schema locations.
 *
 * <p>Objects of this class are immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.7
 * @checkstyle AbbreviationAsWordInNameCheck (5 lines)
 */
@EqualsAndHashCode(of = "origin")
public final class StrictXML implements XML {

    /**
     * Original XML document.
     */
    private final transient XML origin;

    /**
     * Public ctor.
     * @param xml XML document
     */
    public StrictXML(final XML xml) {
        this(xml, new ClasspathResolver());
    }

    /**
     * Public ctor.
     * @param xml XML document
     * @param resolver Custom resolver
     * @since 0.19
     */
    public StrictXML(final XML xml, final LSResourceResolver resolver) {
        this(xml, StrictXML.newValidator(resolver));
    }

    /**
     * Public ctor.
     * @param xml XML document
     * @param val Custom validator
     */
    public StrictXML(final XML xml, final Validator val) {
        this(xml, StrictXML.validate(xml, val));
    }

    /**
     * Public ctor.
     * @param xml XML document
     * @param schema XSD schema
     */
    public StrictXML(final XML xml, final XSD schema) {
        this(xml, schema.validate(new DOMSource(xml.node())));
    }

    /**
     * Private ctor.
     * @param xml XML Document
     * @param errors XML Document errors
     */
    @SuppressWarnings("PMD.ConstructorOnlyInitializesOrCallOtherConstructors")
    private StrictXML(final XML xml,
        final Collection<SAXParseException> errors) {
        if (!errors.isEmpty()) {
            Logger.warn(
                StrictXML.class,
                "%d XML validation error(s):\n  %s\n%s",
                errors.size(),
                StrictXML.join(StrictXML.print(errors), "\n  "),
                xml
            );
            throw new IllegalArgumentException(
                String.format(
                    "%d error(s) in XML document: %s",
                    errors.size(),
                    StrictXML.join(StrictXML.print(errors), ";")
                )
            );
        }
        this.origin = xml;
    }

    @Override
    public String toString() {
        return this.origin.toString();
    }

    @Override
    public List<String> xpath(final String query) {
        return this.origin.xpath(query);
    }

    @Override
    public List<XML> nodes(final String query) {
        return this.origin.nodes(query);
    }

    @Override
    public XML registerNs(final String prefix, final Object uri) {
        return this.origin.registerNs(prefix, uri);
    }

    @Override
    public XML merge(final NamespaceContext context) {
        return this.origin.merge(context);
    }

    @Override
    public Node node() {
        return this.origin.node();
    }

    /**
     * Convert errors to lines.
     * @param errors The errors
     * @return List of messages to print
     */
    private static Iterable<String> print(
        final Collection<SAXParseException> errors) {
        final Collection<String> lines = new ArrayList<>(errors.size());
        for (final SAXParseException error : errors) {
            lines.add(
                String.format(
                    "%d:%d: %s",
                    error.getLineNumber(),
                    error.getColumnNumber(),
                    error.getMessage()
                )
            );
        }
        return lines;
    }

    /**
     * Joins many objects' string representations with the given separator
     * string. The separator will not be appended to the beginning or the end.
     * @param iterable Iterable of objects.
     * @param sep Separator string.
     * @return Joined string.
     */
    private static String join(final Iterable<?> iterable, final String sep) {
        final Iterator<?> iterator = iterable.iterator();
        final Object first = iterator.next();
        // @checkstyle MagicNumber (1 line)
        final StringBuilder buf = new StringBuilder(256);
        if (first != null) {
            buf.append(first);
        }
        while (iterator.hasNext()) {
            buf.append(sep);
            final Object obj = iterator.next();
            if (obj != null) {
                buf.append(obj);
            }
        }
        return buf.toString();
    }

    /**
     * Validate XML without external schema.
     * @param xml XML Document
     * @param validator XML Validator
     * @return List of validation errors
     */
    private static Collection<SAXParseException> validate(
        final XML xml,
        final Validator validator) {
        final Collection<SAXParseException> errors =
            new CopyOnWriteArrayList<>();
        final int max = 3;
        try {
            validator.setErrorHandler(
                new XSDDocument.ValidationHandler(errors)
            );
            final DOMSource dom = new DOMSource(xml.node());
            for (int retry = 1; retry <= max; ++retry) {
                try {
                    validator.validate(dom);
                    break;
                } catch (final SocketException ex) {
                    Logger.error(
                        StrictXML.class,
                        "Try #%d of %d failed: %s: %s",
                        retry,
                        max,
                        ex.getClass().getName(),
                        ex.getMessage()
                    );
                    if (retry == max) {
                        throw new IllegalStateException(ex);
                    }
                }
            }
        } catch (final SAXException | IOException ex) {
            throw new IllegalStateException(ex);
        }
        return errors;
    }

    /**
     * Creates a new validator.
     * @param resolver The resolver for resources
     * @return A new validator
     */
    private static Validator newValidator(final LSResourceResolver resolver) {
        try {
            final Validator validator = SchemaFactory
                .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
                .newSchema()
                .newValidator();
            validator.setResourceResolver(resolver);
            return validator;
        } catch (final SAXException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
