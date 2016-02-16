/**
 * Copyright (c) 2012-2015, jcabi.com
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

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.ls.LSInput;

/**
 * {@link org.w3c.dom.ls.LSInput} implementation
 * used by {@link com.jcabi.xml.ClasspathResolver}.
 *
 * @author Adam Siemion (adam.siemion.null@lemonsoftware.pl)
 * @version $Id$
 */
class ClasspathInput implements LSInput {

    /**
     * Public Id.
     */
    private transient String publicid;
    /**
     * System Id.
     */
    private transient String systemid;

    /**
     * Constructor.
     * @param pubid Public id
     * @param sysid System id
     */
    ClasspathInput(final String pubid, final String sysid) {
        this.publicid = pubid;
        this.systemid = sysid;
    }

    @Override
    public String getPublicId() {
        return this.publicid;
    }

    @Override
    public void setPublicId(final String pubid) {
        this.publicid = pubid;
    }

    @Override
    public String getSystemId() {
        return this.systemid;
    }

    @Override
    public void setSystemId(final String sysid) {
        this.systemid = sysid;
    }

    @Override
    public String getBaseURI() {
        return null;
    }

    @Override
    public InputStream getByteStream() {
        return null;
    }

    @Override
    @SuppressWarnings("PMD.BooleanGetMethodName")
    public boolean getCertifiedText() {
        return false;
    }

    @Override
    public Reader getCharacterStream() {
        return null;
    }

    @Override
    public String getEncoding() {
        return null;
    }

    @Override
    @SuppressWarnings("PMD.DoNotThrowExceptionInFinally")
    public String getStringData() {
        final InputStream stream = getClass()
                                 .getResourceAsStream(this.systemid);
        if (stream == null) {
            throw new IllegalArgumentException(
                String.format(
                    "Systemid %s resource does not exist or can't be opened.",
                    this.systemid
                )
            );
        }
        try {
            final String data = IOUtils.toString(
                stream,
                Charset.forName("UTF-8")
            );
            return data;
        } catch (final IOException exception) {
            throw new IllegalArgumentException(
                String.format(
                    "Unable to read input stream of systemid %s", this.systemid
                ),
                exception
            );
        } finally {
            try {
                stream.close();
            } catch (final IOException ex) {
                throw new IllegalArgumentException(
                    String.format(
                        "Unable to close input stream of systemid %s",
                        this.systemid
                    ),
                    ex
                );
            }
        }
    }

    @Override
    public void setBaseURI(final String baseuri) {
        // intentionally empty
    }

    @Override
    public void setByteStream(final InputStream bytestream) {
        // intentionally empty
    }

    @Override
    public void setCertifiedText(final boolean certifiedtext) {
        // intentionally empty
    }

    @Override
    public void setCharacterStream(final Reader characterstream) {
        // intentionally empty
    }

    @Override
    public void setEncoding(final String encoding) {
        // intentionally empty
    }

    @Override
    public void setStringData(final String stringdata) {
        // intentionally empty
    }
}
