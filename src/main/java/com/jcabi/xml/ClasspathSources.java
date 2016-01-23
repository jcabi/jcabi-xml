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

import java.io.InputStream;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import lombok.EqualsAndHashCode;

/**
 * Sources in classpath.
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @checkstyle StringLiteralsConcatenationCheck (110 lines)
 * @since 0.9
 */
@EqualsAndHashCode(of = "prefix")
public final class ClasspathSources implements Sources {

    /**
     * Pattern to compose resource.
     */
    private static final String PATTERN = "%s%s";

    /**
     * Prefix.
     */
    private final transient String prefix;

    /**
     * Public ctor.
     */
    public ClasspathSources() {
        this("");
    }

    /**
     * Public ctor.
     * @param type Start with this type
     */
    public ClasspathSources(final Class<?> type) {
        this(
            String.format(
                "/%s/",
                type.getPackage().getName().replace(".", "/")
            )
        );
    }

    /**
     * Public ctor.
     * @param pfx Classpath prefix
     */
    public ClasspathSources(final String pfx) {
        this.prefix = pfx;
    }

    @Override
    public Source resolve(final String href, final String base)
        throws TransformerException {
        InputStream stream = this.getClass().getResourceAsStream(
            String.format(PATTERN, this.prefix, href)
        );
        if (stream == null) {
            stream = this.getClass().getResourceAsStream(
                String.format(PATTERN, base, href)
            );
            if (stream == null) {
                throw new TransformerException(
                        String.format(
                                "resource \"%s\" not found in classpath"
                                    + " with prefix \"%s\" and base \"%s\"",
                                href, this.prefix, base
                        )
                );
            }
        }
        return new StreamSource(stream);
    }
}
