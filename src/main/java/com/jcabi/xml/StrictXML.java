/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.xml;

import com.jcabi.log.Logger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.NamespaceContext;
import lombok.EqualsAndHashCode;
import org.cactoos.Scalar;
import org.cactoos.scalar.Sticky;
import org.cactoos.scalar.Unchecked;
import org.w3c.dom.Node;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.SAXParseException;

/**
 * Strict {@link XML} that fails if encapsulated XML document
 * doesn't validate against externally provided XSD schema or internally
 * specified schema locations.
 *
 * <p>Objects of this class are immutable and thread-safe.
 *
 * @since 0.7
 * @checkstyle AbbreviationAsWordInNameCheck (5 lines)
 */
@EqualsAndHashCode(of = "origin")
public final class StrictXML implements XML {

    /**
     * Original XML document.
     */
    private final transient Unchecked<XML> origin;

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
        this(xml, () -> xml.validate(resolver));
    }

    /**
     * Public ctor.
     * @param xml XML document
     * @param schema XSD schema
     */
    public StrictXML(final XML xml, final XML schema) {
        this(xml, () -> xml.validate(schema));
    }

    /**
     * Private ctor.
     * @param xml XML Document
     * @param errs XML Document errors function
     */
    private StrictXML(
        final XML xml,
        final Scalar<Collection<SAXParseException>> errs
    ) {
        this(
            () -> {
                final Collection<SAXParseException> errors = errs.value();
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
                return xml;
            }
        );
    }

    /**
     * Default ctor.
     * @param xml XML supplier
     */
    private StrictXML(final Scalar<XML> xml) {
        this.origin = new Unchecked<>(new Sticky<>(xml));
    }

    @Override
    public String toString() {
        return this.origin.value().toString();
    }

    @Override
    public List<String> xpath(final String query) {
        return this.origin.value().xpath(query);
    }

    @Override
    public List<XML> nodes(final String query) {
        return this.origin.value().nodes(query);
    }

    @Override
    public XML registerNs(final String prefix, final Object uri) {
        return this.origin.value().registerNs(prefix, uri);
    }

    @Override
    public XML merge(final NamespaceContext context) {
        return this.origin.value().merge(context);
    }

    /**
     * Retrieve DOM node, represented by this wrapper.
     * This method works exactly the same as {@link #deepCopy()}.
     * @deprecated Use {@link #inner()} or {@link #deepCopy()} instead.
     * @return Deep copy of the inner DOM node.
     */
    @Deprecated
    public Node node() {
        return this.origin.value().deepCopy();
    }

    @Override
    public Node inner() {
        return this.origin.value().inner();
    }

    @Override
    public Node deepCopy() {
        return this.origin.value().deepCopy();
    }

    @Override
    public Collection<SAXParseException> validate(final LSResourceResolver resolver) {
        return this.origin.value().validate(resolver);
    }

    @Override
    public Collection<SAXParseException> validate(final XML xsd) {
        return this.origin.value().validate(xsd);
    }

    /**
     * Convert errors to lines.
     * @param errors The errors
     * @return List of messages to print
     */
    private static Iterable<String> print(
        final Collection<SAXParseException> errors
    ) {
        final Collection<String> lines = new ArrayList<>(errors.size());
        for (final SAXParseException error : errors) {
            lines.add(StrictXML.asMessage(error));
        }
        return lines;
    }

    /**
     * Turn violation into a message.
     * @param violation The violation
     * @return The message
     */
    private static String asMessage(final SAXParseException violation) {
        final StringBuilder msg = new StringBuilder(100);
        if (violation.getLineNumber() >= 0) {
            msg.append('#').append(violation.getLineNumber());
            if (violation.getColumnNumber() >= 0) {
                msg.append(':').append(violation.getColumnNumber());
            }
            msg.append(' ');
        }
        msg.append(violation.getLocalizedMessage());
        if (violation.getException() != null) {
            msg.append(" (")
                .append(violation.getException().getClass().getSimpleName())
                .append(')');
        }
        return msg.toString();
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
}
