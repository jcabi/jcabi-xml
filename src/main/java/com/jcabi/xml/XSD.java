/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.xml;

import java.util.Collection;
import javax.xml.transform.Source;
import org.xml.sax.SAXParseException;

/**
 * XSD schema.
 *
 * <p>Implementation of this interface must be immutable and thread-safe.
 *
 * @see XSDDocument
 * @since 0.5
 * @deprecated This class is deprecated since 0.31.0. Instead, you can
 *  use {@link StrictXML} with a schema provided in the constructor. Otherwise,
 *  you can use {@link XMLDocument} and validate the XML against the schema
 *  via the {@link XMLDocument#validate(XML)} method.
 * @checkstyle AbbreviationAsWordInNameCheck (5 lines)
 */
@Deprecated
public interface XSD {

    /**
     * Validate XML.
     *
     * @param xml Source XML document
     * @return Collection of problems found (empty if no problems)
     */
    Collection<SAXParseException> validate(Source xml);

}
