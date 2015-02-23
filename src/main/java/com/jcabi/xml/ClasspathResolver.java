package com.jcabi.xml;

import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

import java.io.InputStream;

/**
 * TODO add javadoc
 * TODO fix formatting
 */
public class ClasspathResolver implements LSResourceResolver {
    @Override
    public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
        final InputStream stream = getClass().getResourceAsStream(systemId);
        if (stream != null) {
            return new ClasspathInput(publicId, systemId, stream);
        }
        return null;
    }
}
