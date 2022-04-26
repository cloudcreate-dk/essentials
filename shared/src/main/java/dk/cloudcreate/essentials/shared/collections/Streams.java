/*
 * Copyright 2021-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dk.cloudcreate.essentials.shared.collections;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import static dk.cloudcreate.essentials.shared.FailFast.requireNonNull;
import static dk.cloudcreate.essentials.shared.MessageFormatter.msg;

public class Streams {
    public static <R, T, U> Stream<R> zipOrderedAndEqualSizedStreams(Stream<T> streamT, Stream<U> streamU, BiFunction<T, U, R> zipFunction) {
        requireNonNull(streamT, "No streamT provided");
        requireNonNull(streamU, "No streamU provided");
        requireNonNull(zipFunction, "No zipFunction provided");
        var streamTSpliterator = streamT.spliterator();
        if (!streamTSpliterator.hasCharacteristics(Spliterator.SIZED) || !streamTSpliterator.hasCharacteristics(Spliterator.ORDERED)) {
            throw new IllegalArgumentException("streamT isn't a sized and ordered stream");
        }
        var streamUSpliterator = streamU.spliterator();
        if (!streamUSpliterator.hasCharacteristics(Spliterator.SIZED) || !streamUSpliterator.hasCharacteristics(Spliterator.ORDERED)) {
            throw new IllegalArgumentException("streamU isn't a sized and ordered stream");
        }

        if (streamTSpliterator.estimateSize() != streamUSpliterator.estimateSize()) {
            throw new IllegalArgumentException(msg("Expected equal size. streamT has estimatedSize {} where as streamU has estimatedSize {}",
                                                   streamTSpliterator.estimateSize(),
                                                   streamUSpliterator.estimateSize()));
        }
        var streamTIterator = Spliterators.iterator(streamTSpliterator);
        var streamUIterator = Spliterators.iterator(streamUSpliterator);
        Stream<R> zipStream = StreamSupport.stream(
                new Spliterators.AbstractSpliterator<>(Math.min(streamTSpliterator.estimateSize(),
                                                                streamUSpliterator.estimateSize()),
                                                       Spliterator.SIZED | Spliterator.ORDERED) {
                    @Override
                    public boolean tryAdvance(Consumer<? super R> consumer) {
                        var canAdvance = streamTIterator.hasNext() && streamUIterator.hasNext();
                        if (canAdvance) {
                            consumer.accept(zipFunction.apply(streamTIterator.next(), streamUIterator.next()));
                        }
                        return canAdvance;
                    }
                }, streamT.isParallel() || streamU.isParallel());

        return zipStream.onClose(streamT::close)
                        .onClose(streamU::close);
    }
}
