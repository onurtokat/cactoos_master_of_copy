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
 * Scalar that is thread-safe.
 *
 * <p>This class implements {@link Scalar}, which throws a checked
 * {@link Exception}. This may not be convenient in many cases. To make
 * it more convenient and get rid of the checked exception you can
 * use the {@link UncheckedScalar} decorator. Or you may use
 * {@link IoCheckedScalar} to wrap it in an IOException.</p>
 *
 * <pre>{@code
 * final List<Integer> list = new LinkedList<>();
 * final int threads = 100;
 * new RunsInThreads<>(
 *     new SyncScalar<>(() -> list.add(1)), threads
 * ); // list.size() will be equal to threads value
 * }</pre>
 *
 * <p>Objects of this class are thread-safe.</p>
 *
 * @param <T> Type of result
 * @since 0.3
 */
public final class SyncScalar<T> implements Scalar<T> {

    /**
     * The scalar to cache.
     */
    private final Scalar<T> origin;

    /**
     * Sync lock.
     */
    private final Object mutex;

    /**
     * Ctor.
     * @param src The Scalar to cache
     */
    public SyncScalar(final Scalar<T> src) {
        this(src, src);
    }

    /**
     * Ctor.
     * @param scalar The Scalar to cache
     * @param lock Sync lock
     */
    public SyncScalar(final Scalar<T> scalar, final Object lock) {
        this.origin = scalar;
        this.mutex = lock;
    }

    @Override
    public T value() throws Exception {
        synchronized (this.mutex) {
            return this.origin.value();
        }
    }
}
