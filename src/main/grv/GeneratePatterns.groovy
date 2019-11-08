/**
 * Copyright (c) 2012-2019, jcabi.com
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
package com.jcabi.xml.scripts

import java.util.regex.Pattern
import net.sourceforge.reb4j.Adopted
import net.sourceforge.reb4j.Entity
import net.sourceforge.reb4j.Group
import net.sourceforge.reb4j.Literal
import net.sourceforge.reb4j.Sequence
import net.sourceforge.reb4j.charclass.CharClass
import org.apache.commons.lang3.StringEscapeUtils

def start = CharClass.characters(':' as char, '_' as char)
    .union(CharClass.range('a' as char, 'z' as char))
    .union(CharClass.range('A' as char, 'Z' as char))
    .union(CharClass.range('\u00C0' as char, '\u00D6' as char))
    .union(CharClass.range('\u00D8' as char, '\u00F6' as char))
    .union(CharClass.range('\u00F8' as char, '\u02FF' as char))
    .union(CharClass.range('\u0370' as char, '\u037D' as char))
    .union(CharClass.range('\u037F' as char, '\u1FFF' as char))
    .union(CharClass.range('\u200C' as char, '\u200D' as char))
    .union(CharClass.range('\u2070' as char, '\u218F' as char))
    .union(CharClass.range('\u2C00' as char, '\u2FEF' as char))
    .union(CharClass.range('\u3001' as char, '\uD7FF' as char))
    .union(CharClass.range('\uF900' as char, '\uFDCF' as char))
    .union(CharClass.range('\uFDF0' as char, '\uFFFD' as char))
def letter = CharClass.characters('-' as char, '.' as char, '\u00B7' as char)
    .union(CharClass.range('0' as char, '9' as char))
    .union(CharClass.range('\u0300' as char, '\u036F' as char))
    .union(CharClass.range('\u203F' as char, '\u2040' as char))
def element = start.andThen(Group.nonCapturing(letter).anyTimes())
def xml = StringEscapeUtils.escapeJava(
    Sequence.sequence(
        Group.nonCapturing(
            Adopted.fromPattern(
                Pattern.compile('<\\?xml.*\\?>\\s*', Pattern.CASE_INSENSITIVE)
            )
        ).optional(),
        Group.nonCapturing(
            Adopted.fromPattern(Pattern.compile('<!DOCTYPE.*>'))
        ).optional(),
        Group.nonCapturing(
            Adopted.fromPattern(Pattern.compile('<!--.*-->'))
        ).optional(),
        Literal.literal('<'),
        element,
        Entity.ANY_CHAR.anyTimes(),
        Group.nonCapturing(element | CharClass.character('/' as char)),
        Literal.literal('>')
    ).toString()
)
def dest = new File(project.properties['destinationPath'])
dest.parentFile.mkdirs()
dest.text =
    new groovy.text.SimpleTemplateEngine()
        .createTemplate(new File(project.properties['sourcePath']).text)
        .make(['xmlPattern' : xml])
        .toString()
