/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.xml;

import java.util.Arrays;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link ListWrapper}.
 *
 * @since 0.1
 */
final class ListWrapperTest {

    @Test
    void slicesWholeListUpToItsSize() {
        MatcherAssert.assertThat(
            "Whole range is not returned by subList",
            new XMLDocument("<r><v>one</v><v>two</v></r>")
                .xpath("/r/v/text()").subList(0, 2),
            Matchers.equalTo(Arrays.asList("one", "two"))
        );
    }

    @Test
    void slicesEmptyRangeAtTheEnd() {
        MatcherAssert.assertThat(
            "Empty range at the end is not returned by subList",
            new XMLDocument("<r><v>one</v><v>two</v></r>")
                .xpath("/r/v/text()").subList(2, 2),
            Matchers.empty()
        );
    }

    @Test
    void slicesEmptyListToEmptyRange() {
        MatcherAssert.assertThat(
            "Empty range of an empty list is not returned by subList",
            new XMLDocument("<r/>").xpath("/r/v/text()").subList(0, 0),
            Matchers.equalTo(Collections.emptyList())
        );
    }

    @Test
    void rejectsStartBeyondSize() {
        MatcherAssert.assertThat(
            "Start beyond the size is accepted by subList",
            Assertions.assertThrows(
                NodeNotFoundException.class,
                () -> new XMLDocument("<r><v>one</v></r>")
                    .xpath("/r/v/text()").subList(2, 2)
            ).getMessage(),
            Matchers.containsString("Start of subList")
        );
    }

    @Test
    void rejectsEndBeyondSize() {
        MatcherAssert.assertThat(
            "End beyond the size is accepted by subList",
            Assertions.assertThrows(
                NodeNotFoundException.class,
                () -> new XMLDocument("<r><v>one</v></r>")
                    .xpath("/r/v/text()").subList(0, 2)
            ).getMessage(),
            Matchers.containsString("End of subList")
        );
    }
}
