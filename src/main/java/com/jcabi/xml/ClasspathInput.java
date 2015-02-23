package com.jcabi.xml;

import org.w3c.dom.ls.LSInput;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * {@link org.w3c.dom.ls.LSInput} implementation
 * used by {@link com.jcabi.xml.ClasspathResolver}.
 *
 * @author Adam Siemion (adam.siemion.null@lemonsoftware.pl)
 */
class ClasspathInput implements LSInput {

    private BufferedInputStream input;
    private String publicId;
    private String systemId;

    public ClasspathInput(final String publicId, final String systemId, final InputStream input) {
        this.publicId = publicId;
        this.systemId = systemId;
        this.input = new BufferedInputStream(input);
    }

    @Override
    public String getPublicId() {
        return publicId;
    }

    @Override
    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    @Override
    public String getSystemId() {
        return systemId;
    }

    @Override
    public void setSystemId(String systemId) {
        this.systemId = systemId;
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
    public void setBaseURI(String baseURI) {
    }

    @Override
    public void setByteStream(InputStream byteStream) {
    }

    @Override
    public void setCertifiedText(boolean certifiedText) {
    }

    @Override
    public void setCharacterStream(Reader characterStream) {
    }

    @Override
    public void setEncoding(String encoding) {
    }

    @Override
    public void setStringData(String stringData) {
    }

}
