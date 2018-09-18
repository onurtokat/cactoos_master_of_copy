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

import org.cactoos.Scalar;

/**
 * Double Scalar which sums up the values of other Scalars of the same type.
 *
 * <p>Here is how you can use it to summarize double numbers:</p>
 *
 * <pre>{@code
 * double sum = new SumOfDoubleScalar(() -> 1.1,() -> 2.1, () -> 3.1).value();
 * }</pre>
 *
 * <p>This class implements {@link Scalar}, which throws a checked
 * {@link Exception}. Despite that this class does NOT throw a checked
 * exception.</p>
 *
 * <p>There is no thread-safety guarantee.
 *
 * @since 0.30
 */
public final class SumOfDoubleScalar implements Scalar<Double> {

    /**
     * Varargs of Scalar to sum up values from.
     */
    private final Scalar<Double>[] scalars;

    /**
     * Ctor.
     * @param src Varargs of Scalar to sum up values from
     * @since 0.30
     */
    @SafeVarargs
    public SumOfDoubleScalar(final Scalar<Double>... src) {
        this.scalars = src;
    }

    @Override
    public Double value() {
        return new SumOfScalar(this.scalars).value().doubleValue();
    }
}
