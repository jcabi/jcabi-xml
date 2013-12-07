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

import com.jcabi.aspects.Loggable;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.validation.constraints.NotNull;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode(of = "xsd")
@Loggable(Loggable.DEBUG)
public final class XSDDocument implements XSD {

    /**
     * XSD document.
     */
    private final transient Source xsd;

    /**
     * Public ctor, from XML as a source.
     * @param src XSL document body
     */
    public XSDDocument(@NotNull(message = "XML can't be NULL")
        final XML src) {
        this(new DOMSource(src.node()));
    }

    /**
     * Public ctor, from XSL as a string.
     * @param src XML document body
     */
    public XSDDocument(@NotNull(message = "XSD text can't be NULL")
        final String src) {
        this(new StreamSource(new StringReader(src)));
    }

    /**
     * Public ctor, from XML as a source.
     * @param src XML document body
     */
    public XSDDocument(@NotNull(message = "source can't be NULL")
        final Source src) {
        this.xsd = src;
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
            schema = SchemaFactory
                .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
                .newSchema(this.xsd);
        } catch (SAXException ex) {
            throw new IllegalStateException(ex);
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
            validator.validate(xml);
        } catch (SAXException ex) {
            throw new IllegalStateException(ex);
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
        return errors;
    }

}
