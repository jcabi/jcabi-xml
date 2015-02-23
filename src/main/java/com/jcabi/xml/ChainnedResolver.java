package com.jcabi.xml;

import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * TODO add javadoc
 */
public class ChainnedResolver implements LSResourceResolver {

    private List<LSResourceResolver> resolvers = new ArrayList<LSResourceResolver>();

    public ChainnedResolver(LSResourceResolver... resolvers) {
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
