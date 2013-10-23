/**
 * Copyright (c) 2012-2013, JCabi.com
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

import com.jcabi.log.Logger;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.StringEscapeUtils;
import org.w3c.dom.Node;

/**
 * Wrapper of {@link List}.
 *
 * <p>This wrapper is our internal implementation of a {@link List}. The only
 * purpose of this wrapper is to throw our own custom exception when the client
 * is trying to access an element that is absent in the list. Such a custom
 * exception ({@link ListWrapper.NodeNotFoundException})
 * includes detailed information about
 * the original document. Thus, such an incorrect list-access operation will
 * lead to an exception that contains all the details inside (not just a simple
 * error message). For example:
 *
 * <pre> String name = new XMLDocument("...")
 *   .xpath("/document/name/text()")
 *   .get(0)</pre>
 *
 * <p>This snippet is trying to get a text value from XML element, and there
 * is no guarantee that such an element exists in the document. In order to give
 * a detailed report to the user of the problem (including a full content of
 * the XML document) we're returning {@link ListWrapper} from
 * {@link XMLDocument#xpath(String)}. The only method that we implement
 * for this purpose is {@link #get(int)}.
 *
 * <p>{@link ListWrapper} is an unmodifiable list, that's why
 * the majority of inherited method are not implemented and
 * thow runtime exceptions if being called.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.1
 */
@SuppressWarnings("PMD.TooManyMethods")
@ToString
@EqualsAndHashCode(of = { "original", "dom", "xpath" })
final class ListWrapper<T> implements List<T> {

    /**
     * The original list.
     */
    private final transient List<T> original;

    /**
     * The XML where this list came from.
     */
    private final transient Node dom;

    /**
     * XPath.
     */
    private final transient String xpath;

    /**
     * Public ctor.
     * @param list Original list
     * @param node The XML
     * @param addr Address
     */
    ListWrapper(@NotNull final List<T> list, @NotNull final Node node,
        @NotNull final String addr) {
        this.original = list;
        this.dom = node;
        this.xpath = addr;
    }

    @Override
    public boolean add(@NotNull final T element) {
        throw new UnsupportedOperationException("#add(T)");
    }

    @Override
    public void add(final int index, @NotNull final T element) {
        throw new UnsupportedOperationException("#add(int, T)");
    }

    @Override
    public boolean addAll(@NotNull final Collection<? extends T> elements) {
        throw new UnsupportedOperationException("#addAll(Collection)");
    }

    @Override
    public boolean addAll(final int index,
        @NotNull final Collection<? extends T> elements) {
        throw new UnsupportedOperationException("#add(int, Collection)");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("#clear()");
    }

    @Override
    public boolean contains(@NotNull final Object element) {
        return this.original.contains(element);
    }

    @Override
    public boolean containsAll(@NotNull final Collection<?> elements) {
        return this.original.containsAll(elements);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The method throws {@link ListWrapper.NodeNotFoundException}
     * if such an element doesn't exist in the list.
     */
    @Override
    @NotNull
    public T get(final int index) {
        if (index >= this.size()) {
            throw new ListWrapper.NodeNotFoundException(
                String.format(
                    "Index (%d) is out of bounds (size=%d)",
                    index, this.size()
                ),
                this.dom,
                this.xpath
            );
        }
        return this.original.get(index);
    }

    @Override
    public int indexOf(@NotNull final Object element) {
        return this.original.indexOf(element);
    }

    @Override
    public boolean isEmpty() {
        return this.original.isEmpty();
    }

    @Override
    @NotNull
    public Iterator<T> iterator() {
        return this.original.iterator();
    }

    @Override
    public int lastIndexOf(@NotNull final Object element) {
        return this.original.lastIndexOf(element);
    }

    @Override
    @NotNull
    public ListIterator<T> listIterator() {
        return this.original.listIterator();
    }

    @Override
    @NotNull
    public ListIterator<T> listIterator(final int index) {
        return this.original.listIterator(index);
    }

    @Override
    public T remove(final int index) {
        throw new UnsupportedOperationException("#remove(int)");
    }

    @Override
    public boolean remove(@NotNull final Object element) {
        throw new UnsupportedOperationException("#remove(Object)");
    }

    @Override
    public boolean removeAll(@NotNull final Collection<?> elements) {
        throw new UnsupportedOperationException("#removeAll(Collection)");
    }

    @Override
    public boolean retainAll(@NotNull final Collection<?> elements) {
        throw new UnsupportedOperationException("#retainAll(Collection)");
    }

    @Override
    public T set(final int index, @NotNull final T element) {
        throw new UnsupportedOperationException("#set(int, T)");
    }

    @Override
    public int size() {
        return this.original.size();
    }

    /**
     * {@inheritDoc}
     *
     * <p>The method throws {@link ListWrapper.NodeNotFoundException}
     * when either
     * {@code start} or {@code end} is bigger than the size of the list. In all
     * other cases of illegal method call (start is less than zero, end is
     * less than zero, or start is bigger than end) a standard
     * {@link IndexOutOfBoundException} is thrown (by the encapsulated
     * implementation of {@Link List}).
     */
    @Override
    @NotNull
    public List<T> subList(final int start, final int end) {
        if (start >= this.size()) {
            throw new ListWrapper.NodeNotFoundException(
                String.format(
                    "Start of subList (%d) is out of bounds (size=%d)",
                    start, this.size()
                ),
                this.dom,
                this.xpath
            );
        }
        if (end >= this.size()) {
            throw new ListWrapper.NodeNotFoundException(
                String.format(
                    "End of subList (%d) is out of bounds (size=%d)",
                    end, this.size()
                ),
                this.dom,
                this.xpath
            );
        }
        return this.original.subList(start, end);
    }

    @Override
    public Object[] toArray() {
        return this.original.toArray();
    }

    @Override
    public <E> E[] toArray(@NotNull final E[] array) {
        return this.original.toArray(array);
    }

    /**
     * Node not found in XmlDocument.
     */
    private static final class NodeNotFoundException
        extends IndexOutOfBoundsException {
        /**
         * Serialization marker.
         */
        private static final long serialVersionUID = 0x7526FA78EEDAC470L;
        /**
         * Public ctor.
         * @param message Error message
         * @param node The XML with error
         * @param xpath The address
         */
        NodeNotFoundException(@NotNull final String message,
            @NotNull final Node node, @NotNull final String xpath) {
            super(
                Logger.format(
                    "XPath '%s' not found in '%s': %s",
                    StringEscapeUtils.escapeJava(xpath),
                    // @checkstyle LineLength (1 line)
                    StringEscapeUtils.escapeJava(new DomPrinter(node).toString()),
                    message
            )
            );
        }
    }

}
