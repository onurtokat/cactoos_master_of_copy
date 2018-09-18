/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017-2018 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.cactoos.collection;

import java.util.Collection;
import org.cactoos.list.ListOf;
import org.hamcrest.Description;
import org.hamcrest.MatcherAssert;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.collection.IsCollectionWithSize;
import org.hamcrest.collection.IsEmptyCollection;
import org.hamcrest.core.IsCollectionContaining;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNot;
import org.llorllale.cactoos.matchers.MatcherOf;

/**
 * Matcher for collection.
 * @param <E> Type of source item
 * @since 0.23
 * @checkstyle JavadocMethodCheck (500 lines)
 */
public final class BehavesAsCollection<E> extends
    TypeSafeMatcher<Collection<E>>  {

    /**
     * Sample item.
     */
    private final E sample;

    /**
     * Ctor.
     * @param item Sample item
     */
    public BehavesAsCollection(final E item) {
        super();
        this.sample = item;
    }

    @Override
    @SuppressWarnings({ "unchecked", "PMD.ClassCastExceptionWithToArray" })
    public boolean matchesSafely(final Collection<E> col) {
        MatcherAssert.assertThat(
            col, new IsCollectionContaining<>(new IsEqual<>(this.sample))
        );
        MatcherAssert.assertThat(
            col, new IsNot<>(new IsEmptyCollection<>())
        );
        MatcherAssert.assertThat(
            col, new IsCollectionWithSize<>(new MatcherOf<>(s -> s > 0))
        );
        MatcherAssert.assertThat(
            new ListOf<>((E[]) col.toArray()),
            new IsCollectionContaining<>(new IsEqual<>(this.sample))
        );
        final E[] array = (E[]) new Object[col.size()];
        col.toArray(array);
        MatcherAssert.assertThat(
            new ListOf<>(array),
            new IsCollectionContaining<>(new IsEqual<>(this.sample))
        );
        MatcherAssert.assertThat(
            col.containsAll(new ListOf<>(this.sample)), new IsEqual<>(true)
        );
        return true;
    }

    @Override
    public void describeTo(final Description desc) {
        desc.appendText("not a valid collection");
    }
}
