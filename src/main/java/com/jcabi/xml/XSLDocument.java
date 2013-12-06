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
import java.io.StringReader;
import javax.validation.constraints.NotNull;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import lombok.EqualsAndHashCode;
import org.w3c.dom.Document;

/**
 * Implementation of {@link XSL}.
 *
 * <p>Objects of this class are immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.4
 */
@EqualsAndHashCode(of = "xsl")
@Loggable(Loggable.DEBUG)
public final class XSLDocument implements XSL {

    /**
     * Transformer factory.
     */
    private static final TransformerFactory TFACTORY =
        TransformerFactory.newInstance();

    /**
     * DOM document builder factory.
     */
    private static final DocumentBuilderFactory DFACTORY =
        DocumentBuilderFactory.newInstance();

    /**
     * XSL document.
     */
    private final transient Source xsl;

    /**
     * Public ctor, from XML as a source.
     * @param src XSL document body
     */
    public XSLDocument(@NotNull(message = "XML can't be NULL") final XML src) {
        this(new DOMSource(src.node()));
    }

    /**
     * Public ctor, from XSL as a string.
     * @param src XML document body
     */
    public XSLDocument(@NotNull(message = "XSL can't be NULL")
        final String src) {
        this(new StreamSource(new StringReader(src)));
    }

    /**
     * Public ctor, from XML as a source.
     * @param src XML document body
     */
    public XSLDocument(@NotNull(message = "source can't be NULL")
        final Source src) {
        this.xsl = src;
    }

    @Override
    public String toString() {
        return this.xsl.toString();
    }

    @Override
    @NotNull(message = "XML is never NULL")
    public XML transform(@NotNull(message = "XML can't be NULL")
        final XML xml) {
        final Transformer trans;
        try {
            trans = XSLDocument.TFACTORY.newTransformer(this.xsl);
        } catch (TransformerConfigurationException ex) {
            throw new IllegalStateException(ex);
        }
        final Document target;
        try {
            target = XSLDocument.DFACTORY.newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException ex) {
            throw new IllegalStateException(ex);
        }
        try {
            trans.transform(
                new DOMSource(xml.node()), new DOMResult(target)
            );
        } catch (TransformerException ex) {
            throw new IllegalStateException(ex);
        }
        return new XMLDocument(target);
    }

}
