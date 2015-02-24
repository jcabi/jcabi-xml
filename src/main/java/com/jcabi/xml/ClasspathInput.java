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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import org.w3c.dom.ls.LSInput;

/**
 * {@link org.w3c.dom.ls.LSInput} implementation
 * used by {@link com.jcabi.xml.ClasspathResolver}.
 *
 * @author Adam Siemion (adam.siemion.null@lemonsoftware.pl)
 * @version $Id$
 */
class ClasspathInput implements LSInput {

    private BufferedInputStream input;
    private String publicid;
    private String systemid;

    public ClasspathInput(final String publicid, final String systemid, final InputStream input) {
        this.publicid = publicid;
        this.systemid = systemid;
        this.input = new BufferedInputStream(input);
    }

    @Override
    public String getPublicId() {
        return publicid;
    }

    @Override
    public void setPublicId(String publicid) {
        this.publicid = publicid;
    }

    @Override
    public String getSystemId() {
        return systemid;
    }

    @Override
    public void setSystemId(String systemid) {
        this.systemid = systemid;
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

    // @todo #31:30min replace below code with org.apache.commons.io.IOUtils.toString()
    @Override
    public String getStringData() {
        synchronized (input) {
            try {
                byte[] input = new byte[this.input.available()];
                this.input.read(input);
                String contents = new String(input);
                return contents;
            } catch (IOException e) {
                throw new RuntimeException("Unable to read input", e);
            }
        }
    }

    @Override
    public void setBaseURI(String baseuri) {
    }

    @Override
    public void setByteStream(InputStream bytestream) {
    }

    @Override
    public void setCertifiedText(boolean certifiedtext) {
    }

    @Override
    public void setCharacterStream(Reader characterstream) {
    }

    @Override
    public void setEncoding(String encoding) {
    }

    @Override
    public void setStringData(String stringdata) {
    }
}
