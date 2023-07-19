/*
 * Copyright (c) 2012-2022, jcabi.com
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

import java.io.StringReader;
import java.util.List;
import java.util.stream.Collectors;
import javax.xml.namespace.NamespaceContext;
import javax.xml.transform.stream.StreamSource;
import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XPathCompiler;
import net.sf.saxon.s9api.XPathExecutable;
import net.sf.saxon.s9api.XPathSelector;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmValue;
import org.w3c.dom.Node;

public class SaxonDocument implements XML {

    private final String text;

    /**
     * Public constructor from XML as string text.
     *
     * @param text XML document body.
     */
    public SaxonDocument(final String text) {
        this.text = text;
    }

    @Override
    public List<String> xpath(final String query) {
        Processor processor = new Processor(false);
        try {
            DocumentBuilder builder = processor.newDocumentBuilder();
            XdmNode doc = builder.build(new StreamSource(new StringReader(text)));
            XPathCompiler xpathCompiler = processor.newXPathCompiler();
            XPathExecutable xpathExec = xpathCompiler.compile(query);
            XPathSelector selector = xpathExec.load();
            selector.setContextItem(doc);
            XdmValue result = selector.evaluate();
            return result.stream().map(XdmItem::getStringValue).collect(Collectors.toList());
        } catch (SaxonApiException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<XML> nodes(final String query) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XML registerNs(final String prefix, final Object uri) {
        throw new UnsupportedOperationException();
    }

    @Override
    public XML merge(final NamespaceContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Node node() {
        throw new UnsupportedOperationException();
    }
}
