package com.jcabi.xml;

import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * {@link org.w3c.dom.ls.LSResourceResolver} implementation based
 * the chain of responsibility design pattern.
 * The resolveResource method is called on the provided instances
 * until a non null value is returned.
 *
 * @author Adam Siemion (adam.siemion.null@lemonsoftware.pl)
 */
class ChainedResourceResolver implements LSResourceResolver {

    private List<LSResourceResolver> resolvers = new ArrayList<LSResourceResolver>();

    public ChainedResourceResolver(LSResourceResolver... resolvers) {
        for(final LSResourceResolver resolver : resolvers) {
            this.resolvers.add(resolver);
        }
    }

    @Override
    public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
        for(final LSResourceResolver resolver : this.resolvers) {
            final LSInput input = resolver.resolveResource(type, namespaceURI, publicId, systemId, baseURI);
            if (input != null) {
                return input;
            }
        }
        return null;
    }
}
