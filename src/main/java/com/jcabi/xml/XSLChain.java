/**
 * Copyright (c) 2012-2017, jcabi.com
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

import java.util.Collection;
import lombok.EqualsAndHashCode;

/**
 * Chain of {@link XSL} stylesheets.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.12
 */
@EqualsAndHashCode(of = "sheets")
public final class XSLChain implements XSL {

    /**
     * XSL sheets.
     */
    private final transient XSL[] sheets;

    /**
     * Public ctor.
     * @param shts Sheets
     */
    public XSLChain(final Collection<XSL> shts) {
        this.sheets = shts.toArray(new XSL[shts.size()]);
    }

    @Override
    public XML transform(final XML xml) {
        XML output = xml;
        for (final XSL sheet : this.sheets) {
            output = sheet.transform(output);
        }
        return output;
    }

    @Override
    public String applyTo(final XML xml) {
        throw new UnsupportedOperationException("#applyTo()");
    }

    @Override
    public XSL with(final Sources src) {
        throw new UnsupportedOperationException("#with()");
    }

    @Override
    public XSL with(final String name, final Object value) {
        throw new UnsupportedOperationException("#with(name, value)");
    }
}
