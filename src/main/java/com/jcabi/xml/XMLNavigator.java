package com.jcabi.xml;

import java.util.Optional;

public final class XMLNavigator implements Navigator{
    @Override
    public Navigator child(final String element) {
        return null;
    }

    @Override
    public Navigator attribute(final String name) {
        return null;
    }

    @Override
    public Optional<String> text() {
        return Optional.empty();
    }
}
