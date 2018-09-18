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
package org.cactoos.scalar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.cactoos.Proc;
import org.cactoos.Scalar;
import org.cactoos.collection.CollectionOf;
import org.cactoos.func.FuncOf;
import org.cactoos.iterable.IterableOf;
import org.cactoos.iterable.Mapped;
import org.cactoos.list.ListOf;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.junit.Test;
import org.llorllale.cactoos.matchers.MatcherOf;
import org.llorllale.cactoos.matchers.ScalarHasValue;

/**
 * Test case for {@link AndInThreads}.
 * @since 0.25
 * @todo #829:30min Remove the use of the static method
 *  `Collections.synchronizedList`. Replace by an object-oriented approach.
 *  Create a class similar to `SyncCollection` but mutable.
 * @checkstyle JavadocMethodCheck (500 lines)
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
@SuppressWarnings({"PMD.TooManyMethods", "PMD.AvoidDuplicateLiterals"})
public final class AndInThreadsTest {

    @Test
    public void allTrue() throws Exception {
        MatcherAssert.assertThat(
            new AndInThreads(
                new True(),
                new True(),
                new True()
            ),
            new ScalarHasValue<>(true)
        );
    }

    @Test
    public void oneFalse() throws Exception {
        MatcherAssert.assertThat(
            new AndInThreads(
                new True(),
                new False(),
                new True()
            ),
            new ScalarHasValue<>(false)
        );
    }

    @Test
    public void allFalse() throws Exception {
        MatcherAssert.assertThat(
            new AndInThreads(
                new IterableOf<Scalar<Boolean>>(
                    new False(),
                    new False(),
                    new False()
                )
            ),
            new ScalarHasValue<>(false)
        );
    }

    @Test
    public void emptyIterator() throws Exception {
        MatcherAssert.assertThat(
            new AndInThreads(new IterableOf<Scalar<Boolean>>()),
            new ScalarHasValue<>(true)
        );
    }

    @Test
    public void iteratesList() {
        final List<String> list = Collections.synchronizedList(
            new ArrayList<String>(2)
        );
        MatcherAssert.assertThat(
            "Can't iterate a list with a procedure",
            new AndInThreads(
                new Mapped<String, Scalar<Boolean>>(
                    new FuncOf<>(list::add, () -> true),
                    new IterableOf<>("hello", "world")
                )
            ),
            new ScalarHasValue<>(true)
        );
        MatcherAssert.assertThat(
            list,
            new IsIterableContainingInAnyOrder<String>(
                new CollectionOf<Matcher<? super String>>(
                    new MatcherOf<>(
                        text -> {
                            return "hello".equals(text);
                        }
                    ),
                    new MatcherOf<>(
                        text -> {
                            return "world".equals(text);
                        }
                    )
                )
            )
        );
    }

    @Test
    public void iteratesEmptyList() {
        final List<String> list = Collections.synchronizedList(
            new ArrayList<String>(2)
        );
        MatcherAssert.assertThat(
            "Can't iterate a list",
            new AndInThreads(
                new Mapped<String, Scalar<Boolean>>(
                    new FuncOf<>(list::add, () -> true), new IterableOf<>()
                )
            ),
            new ScalarHasValue<>(
                Matchers.allOf(
                    Matchers.equalTo(true),
                    new MatcherOf<>(
                        value -> {
                            return list.isEmpty();
                        }
                    )
                )
            )
        );
    }

    @Test
    public void worksWithProc() throws Exception {
        final List<Integer> list = Collections.synchronizedList(
            new ArrayList<Integer>(2)
        );
        new AndInThreads(
            (Proc<Integer>) list::add,
            1, 1
        ).value();
        MatcherAssert.assertThat(
            list,
            new IsIterableContainingInAnyOrder<Integer>(
                new CollectionOf<Matcher<? super Integer>>(
                    new MatcherOf<>(
                        value -> {
                            return value.equals(1);
                        }
                    ),
                    new MatcherOf<>(
                        value -> {
                            return value.equals(1);
                        }
                    )
                )
            )
        );
    }

    @Test
    public void worksWithFunc() throws Exception {
        MatcherAssert.assertThat(
            new AndInThreads(
                input -> input > 0,
                1, -1, 0
            ),
            new ScalarHasValue<>(false)
        );
    }

    @Test
    public void worksWithProcIterable() throws Exception {
        final List<Integer> list = Collections.synchronizedList(
            new ArrayList<Integer>(2)
        );
        new AndInThreads(
            new Proc.NoNulls<Integer>(list::add),
                new ListOf<>(1, 2)
        ).value();
        MatcherAssert.assertThat(
            list,
            new IsIterableContainingInAnyOrder<Integer>(
                new CollectionOf<Matcher<? super Integer>>(
                    new MatcherOf<>(
                        value -> {
                            return value.equals(1);
                        }
                    ),
                    new MatcherOf<>(
                        value -> {
                            return value.equals(2);
                        }
                    )
                )
            )
        );
    }

    @Test
    public void worksWithIterableScalarBoolean() throws Exception {
        MatcherAssert.assertThat(
            new AndInThreads(
                new ListOf<Scalar<Boolean>>(
                    new Constant<Boolean>(true),
                    new Constant<Boolean>(true)
                )
            ).value(),
            Matchers.equalTo(true)
        );
    }

    @Test
    public void worksWithExecServiceProcValues() throws Exception {
        final List<Integer> list = Collections.synchronizedList(
            new ArrayList<Integer>(2)
        );
        final ExecutorService service = Executors.newSingleThreadExecutor();
        new AndInThreads(
            service,
            new Proc.NoNulls<Integer>(list::add),
            1, 2
        ).value();
        MatcherAssert.assertThat(
            list,
            new IsIterableContainingInAnyOrder<Integer>(
                new CollectionOf<Matcher<? super Integer>>(
                    new MatcherOf<>(
                        value -> {
                            return value.equals(1);
                        }
                    ),
                    new MatcherOf<>(
                        value -> {
                            return value.equals(2);
                        }
                    )
                )
            )
        );
    }

    @Test
    public void worksWithExecServiceProcIterable() throws Exception {
        final List<Integer> list = Collections.synchronizedList(
            new ArrayList<Integer>(2)
        );
        final ExecutorService service = Executors.newSingleThreadExecutor();
        new AndInThreads(
            service,
            new Proc.NoNulls<Integer>(list::add),
                new ListOf<>(1, 2)
        ).value();
        MatcherAssert.assertThat(
            list,
            new IsIterableContainingInAnyOrder<Integer>(
                new CollectionOf<Matcher<? super Integer>>(
                    new MatcherOf<>(
                        value -> {
                            return value.equals(1);
                        }
                    ),
                    new MatcherOf<>(
                        value -> {
                            return value.equals(2);
                        }
                    )
                )
            )
        );
    }

    @Test
    public void worksWithExecServiceScalarBooleans() throws Exception {
        MatcherAssert.assertThat(
            new AndInThreads(
                Executors.newSingleThreadExecutor(),
                new Constant<Boolean>(false),
                new Constant<Boolean>(false)
            ).value(),
            Matchers.equalTo(false)
        );
    }

    @Test
    public void worksWithExecServiceIterableScalarBoolean() throws Exception {
        MatcherAssert.assertThat(
            new AndInThreads(
                Executors.newSingleThreadExecutor(),
                new ListOf<Scalar<Boolean>>(
                    new Constant<Boolean>(true),
                    new Constant<Boolean>(false)
                )
            ).value(),
            Matchers.equalTo(false)
        );
    }

    @Test
    public void worksWithEmptyIterableScalarBoolean() throws Exception {
        MatcherAssert.assertThat(
            new AndInThreads(
                new ListOf<Scalar<Boolean>>()
            ).value(),
            Matchers.equalTo(true)
        );
    }

}
