/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.xml;

import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

/**
 * JMH benchmark for {@link XSLDocument#transform(XML)}.
 *
 * <p>Three scenarios:
 * <ul>
 *   <li>{@link #reuseInstance} — same {@link XSLDocument} reused every call</li>
 *   <li>{@link #withParamEachCall} — new instance via {@code .with()} each call</li>
 *   <li>{@link #freshInstanceEachCall} — brand-new {@link XSLDocument} every call</li>
 * </ul>
 *
 * @since 0.35.0
 * @checkstyle AbbreviationAsWordInNameCheck (15 lines)
 * @checkstyle NonStaticMethodCheck (100 lines)
 */
@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@State(Scope.Benchmark)
public class XSLDocumentBenchmark {

    /**
     * XSL stylesheet with a {@code step} parameter.
     */
    private static final String STYLESHEET = String.join(
        "",
        "<xsl:stylesheet version='2.0'",
        " xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>",
        "<xsl:param name='step' select='0'/>",
        "<xsl:template match='/'>",
        "<result step='{$step}'>",
        "<xsl:copy-of select='node()'/>",
        "</result>",
        "</xsl:template>",
        "</xsl:stylesheet>"
    );

    /**
     * Input XML document.
     */
    private static final XML INPUT = new XMLDocument(
        "<root><item>hello</item></root>"
    );

    /**
     * Reused XSL instance.
     */
    private static final XSL XSL = new XSLDocument(
        XSLDocumentBenchmark.STYLESHEET
    );

    /**
     * Same {@link XSLDocument} instance reused on every call.
     * @return Transformed XML
     */
    @Benchmark
    public final XML reuseInstance() {
        return XSLDocumentBenchmark.XSL.transform(XSLDocumentBenchmark.INPUT);
    }

    /**
     * New {@link XSLDocument} via {@code .with("step", n)} on every call.
     * @return Transformed XML
     */
    @Benchmark
    public final XML withParamEachCall() {
        return XSLDocumentBenchmark.XSL
            .with("step", 1)
            .transform(XSLDocumentBenchmark.INPUT);
    }

    /**
     * Brand-new {@link XSLDocument} constructed on every call.
     * @return Transformed XML
     */
    @Benchmark
    public final XML freshInstanceEachCall() {
        return new XSLDocument(XSLDocumentBenchmark.STYLESHEET)
            .transform(XSLDocumentBenchmark.INPUT);
    }

}
