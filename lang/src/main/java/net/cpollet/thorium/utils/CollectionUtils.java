/*
 * Copyright 2015 Christophe Pollet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.cpollet.thorium.utils;

import java.util.Collection;
import java.util.stream.Stream;

/**
 * @author Christophe Pollet
 */
public class CollectionUtils {
    private CollectionUtils() {
        // nothing
    }

    public static String concat(Collection<?> collection) {
        return concat(collection, ", ");
    }

    public static String concat(Stream<?> stream) {
        return concat(stream, ", ");
    }

    public static String concat(Collection<?> collection, String separator) {
        return concat(collection.stream(), separator);
    }

    public static String concat(Stream<?> stream, String separator) {
        return stream.map(Object::toString).reduce("", (l, r) -> l + (l.isEmpty() ? "" : separator) + r);
    }
}
