package com.jcabi.xml;

import java.util.Optional;

public interface Navigator {

    /**
     * Get a child node by its name.
     * @param element Element name.
     * @return Navigator for the child.
     */
    Navigator child(String element);

    /**
     * Get an attribute by its name.
     * @param name Attribute name.
     * @return Navigator for the attribute.
     */
    Navigator attribute(String name);

    /**
     * Get the text of the current node.
     * @return Text of the node.
     */
    Optional<String> text();
}
