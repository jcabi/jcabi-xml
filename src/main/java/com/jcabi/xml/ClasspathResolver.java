package com.jcabi.xml;

import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

import java.io.InputStream;

/**
 * {@link org.w3c.dom.ls.LSResourceResolver} implementation
 * supporting classpath lookups.
 *
 * @author Adam Siemion (adam.siemion.null@lemonsoftware.pl)
 */
class ClasspathResolver implements LSResourceResolver {
    @Override
    public LSInput resolveResource(
        final String type,
        final String namespaceURI,
        final String publicId,
        final String systemId,
        final String baseURI
    ) {
        final InputStream stream = getClass().getResourceAsStream(systemId);
        if (stream != null) {
            return new ClasspathInput(publicId, systemId, stream);
        }
        return null;
    }
}
