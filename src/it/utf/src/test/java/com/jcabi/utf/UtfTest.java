/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.utf;

import com.jcabi.xml.XMLDocument;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Test of XML features.
 * @since 0.1
 */
public final class UtfTest {

    @Test
    public void printsDocumentWithUtf() throws Exception {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder = factory.newDocumentBuilder();
        final Document doc = builder.newDocument();
        final Element root = doc.createElement("foo");
        doc.appendChild(root);
        root.appendChild(doc.createTextNode("привет"));
        Assertions.assertTrue(new XMLDocument(doc).toString().contains("привет"));
    }
}
