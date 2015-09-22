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

package net.cpollet.thorium.types;

import net.cpollet.thorium.data.method.Method;
import net.cpollet.thorium.data.method.Method;

/**
 * @author Christophe Pollet
 */
public interface Type {
    enum Id {
        VOID,
        BOOLEAN,
        INTEGER,
        FLOAT,
    }

    enum Nullable {
        YES, NO
    }

    static boolean isAssignableTo(Type destination, Type source) {
        if (destination == source) {
            return true;
        }

        if (destination == null || destination == Types.NULLABLE_VOID) {
            return true;
        }

        if (destination == Types.VOID && !source.isNullable()) {
            return true;
        }

        return source.isAssignableTo(destination);
    }

    Id id();

    boolean isNullable();

    boolean isAssignableTo(Type target);

    boolean isMethodDefined(String name, Type... parametersType);

    Method lookupMethod(String name, Type... parametersType);

    Type nullable();

    Type nonNullable();
}
